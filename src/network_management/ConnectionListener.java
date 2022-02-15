package network_management;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;

public class ConnectionListener {

    public static final int PORT = 8888;
    public static final int MAX_CONNECTION = 10;

    private static AsynchronousServerSocketChannel serverSocket;

    public static void initialize() throws IOException {
        if(serverSocket == null){
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", PORT));
            serverSocket.accept(null, new AcceptHandler());
        }
    }

    public static void shutdown(){
        try {
            serverSocket.close();
        } catch (IOException | NullPointerException ignored){
        }

        serverSocket = null;
    }

    private static class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>{
        @Override
        public void completed(AsynchronousSocketChannel socket, Object attachment) {
            // Someone is connecting to us.
            // We first need to ensure that they are not blacklisted.
            // Then that they are not already connected using that same address,
            // even though it should never happen.
            // Finally we can pass this connection to the ConnectionManager.

            List<AsynchronousSocketChannel> channels = ConnectionManager.getSocketChannels();

            // todo test if blacklisted : if so, close connection
            /*
            if(){
                ConnectionManager.closeConnection(socket); // to ensure we don't have any memory leaks
            } else

            */

            if(!channels.contains(socket)){
                // this connection is not already added
                ConnectionManager.addSocket(socket);
            }

            // loop : we keep on listening until we reach the maximum amount set.
            if(ConnectionManager.getSocketChannels().size() >= MAX_CONNECTION){
                // we shutdown the socket, until the ConnectionManager remove
                // a connection and re-initialize() it.
                shutdown();
            } else {
                serverSocket.accept(null, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // let's try to restart the server
            try {
                shutdown();
                ConnectionListener.initialize();
            } catch (IOException ignored){
                // still not working, we close and null the socket
                // todo log
                shutdown();
            }
        }
    }
}
