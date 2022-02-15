package threads;

import com.google.gson.JsonParseException;
import dataclasses.ReceivedRequest;
import request.JsonConverter;
import request.Request;
import request.RequestTypes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class ConnectionManager {

    private static final List<AsynchronousSocketChannel> socketChannels = new ArrayList<>();
    private static final List<RequestListener> listeners = new ArrayList<>();
    private static final HashMap<String, ReceivedRequest> receivedRequestMap = new HashMap<>();

    public static boolean registerListener(RequestListener listener){
        return listeners.add(listener);
    }

    public static boolean unregisterListener(RequestListener listener){
        return listeners.remove(listener);
    }

    public static void addSocket(AsynchronousSocketChannel socketChannel) {
        ReceiveAttachment a = new ReceiveAttachment(1024, socketChannel);
        synchronized (socketChannels){
            socketChannels.add(socketChannel);
        }
        socketChannel.read(a.buffer, a, new ReceiveHandler());
    }

    public static void answer(Request req, InetSocketAddress to){
        sendRequestIf(req, to::equals);
    }

    public static void broadcastRequest(Request req){
        sendRequestIf(req, (ignored) -> true);
    }

    public static void sendRequestIf(Request req, Predicate<InetSocketAddress> condition){
        String json = JsonConverter.serialize(req);
        ByteBuffer buffer = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));

        synchronized (socketChannels) {
            for (AsynchronousSocketChannel s : socketChannels) {
                try {
                    if (condition.test((InetSocketAddress) s.getRemoteAddress())) {
                        WriteAttachment attachment = new WriteAttachment(s);
                        s.write(buffer, attachment, new WriteHandler());
                    }

                } catch (IOException | ClassCastException e) {
                    // ClassCastException means that s.getRemoteAddress() is not InetSocketAddress
                    // meaning that s is not connected and bounded.
                    closeConnection(s);
                }
            }
        }
    }

    public static void handleReceivedRequest(InetSocketAddress from, Request req){

        ReceivedRequest received = new ReceivedRequest(req, from, LocalDateTime.now());
        ReceivedRequest oldReceivedRequest = receivedRequestMap.get(received.getHash());

        if(oldReceivedRequest != null) {
            // we have already received this request from this client
            // to prevent spam and DoS attacks, we keep track of how
            // many time each request is received from a same client.
            // if it goes up to a certain amount, we blacklist the client.

            if (oldReceivedRequest.hasExpired()) {
                receivedRequestMap.replace(received.getHash(), received);
            } else {
                oldReceivedRequest.incrementCounter();
            }

            /*
            if (oldReceivedRequest.getCounter() > 5){
                // todo blacklist client
            }
            */

        } else {
            receivedRequestMap.put(received.getHash(), received);
        }


        // todo create a working thread pool to do
        // both of those tasks ?
        // (especially the second one)

        // ping all the listeners
        synchronized (listeners){
            for(RequestListener l : listeners){
                l.onRequestReceived(from, req);
            }
        }

        if(req.getType() == RequestTypes.ECHO)
            broadcastRequest(new Request(RequestTypes.ECHO_RESPONSE));

        // purge the whole map to avoid memory leaks.
        // Note : we do NOT forget the request even though the sender
        // has disconnected, to avoid bypass.
        for(ReceivedRequest r : receivedRequestMap.values()){
            if(r.hasExpired())
                receivedRequestMap.remove(r.getHash());
        }
    }

    public static List<AsynchronousSocketChannel> getSocketChannels(){
        synchronized (socketChannels){
            // duplicating the list, so the object instance
            // will not be modified, no matter what the
            // calling flux do (avoid synchronization problem).
            return new ArrayList<>(socketChannels);
        }
    }

    public static void closeConnection(AsynchronousSocketChannel s) {
        try {
            s.close();
        } catch (IOException ignored) {
        }

        synchronized (socketChannels) {
            socketChannels.remove(s);

            if(socketChannels.size() < ConnectionListener.MAX_CONNECTION){
                // we can accept a new connection
                try {
                    ConnectionListener.initialize();
                } catch (IOException ignored) {
                    // todo log
                }

            }
        }
    }

    // inner classes

    public interface RequestListener {
        void onRequestReceived(InetSocketAddress from, Request req);
    }

    // Receive handler and attachment

    private static class ReceiveAttachment {
        private ByteBuffer buffer;
        private final AsynchronousSocketChannel socket;
        private final int bufferSize;

        public ReceiveAttachment(int bufferSize, AsynchronousSocketChannel socket){
            this.bufferSize = bufferSize;
            this.buffer = ByteBuffer.allocate(bufferSize);
            this.socket = socket;
        }

        public void enlargeBuffer(int sizeToAdd){
            ByteBuffer copy = buffer.duplicate();
            buffer = ByteBuffer.allocate(bufferSize + sizeToAdd);
            buffer.put(copy);
        }
    }

    private static class ReceiveHandler implements CompletionHandler<Integer, ReceiveAttachment> {
        @Override
        public void completed(Integer result, ReceiveAttachment attachment) {

            if(result == -1) { // end of file
                closeConnection(attachment.socket);
                return;
            }


            ByteBuffer b = attachment.buffer;
            int lastChar = b.position();

            if(b.getChar(lastChar) == '\0'){
                // since the last char is null, the request is complete :
                // there is no byte left in the stream that belongs to
                // the string corresponding to the json of the request
                String jsonRequest = new String(b.array(), StandardCharsets.UTF_8).trim();

                try {
                    Request request = JsonConverter.deserialize(jsonRequest);
                    handleReceivedRequest((InetSocketAddress) attachment.socket.getRemoteAddress(), request);
                } catch (JsonParseException | NullPointerException ignored){
                    // we simply return if the json request is malformed
                    // (if we can't deserialize it)
                    return;
                } catch (IOException e){
                    // cannot get the remote address of the socket :
                    // probably a connection error, so we close it.
                    e.printStackTrace();
                    closeConnection(attachment.socket);
                    return;
                }

                attachment.buffer.clear();

            } else {
                // the string is not complete : we are missing data ; so :
                // we enlarge the buffer, and read again to get the missing data
                attachment.enlargeBuffer(1024);
            }

            attachment.socket.read(attachment.buffer, attachment, this);
        }

        @Override
        public void failed(Throwable exc, ReceiveAttachment attachment) {
            // failed to read : close the connection
            closeConnection(attachment.socket);
        }
    }

    // Writing handler and attachment

    private static class WriteAttachment {
        private final AsynchronousSocketChannel socket;

        public WriteAttachment(AsynchronousSocketChannel socket){
            this.socket = socket;
        }
    }

    private static class WriteHandler implements CompletionHandler<Integer, WriteAttachment> {
        @Override
        public void completed(Integer result, WriteAttachment attachment) {
            if(result == -1){ // end of file
                closeConnection(attachment.socket);
            }
        }

        @Override
        public void failed(Throwable exc, WriteAttachment attachment) {
            // failed to connect : close the connection
            closeConnection(attachment.socket);
        }
    }
}
