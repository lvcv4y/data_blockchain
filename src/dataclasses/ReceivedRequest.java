package dataclasses;

import request.Request;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;


public class ReceivedRequest extends Data {
    // used to memorize received requests, to limit spam on the network

    // number of minutes after which we "forget" this request instance
    public static final int TIMEOUT = 10;

    private final Request request;
    private final InetSocketAddress senderAddress;
    private LocalDateTime time;
    private int counter;


    public ReceivedRequest(Request r, InetSocketAddress senderAddress, LocalDateTime time) {
        this.request = r;
        this.senderAddress = senderAddress;
        this.time = time;
        counter = 1;
    }

    public void incrementCounter(){
        counter++;
        time = LocalDateTime.now();
    }

    public int getCounter(){
        return counter;
    }

    public boolean hasExpired(){
        return LocalDateTime.now().isAfter(time.plusMinutes(TIMEOUT));
    }

    public Request getRequest() {
        return request;
    }

    public InetSocketAddress getSenderAddress() {
        return senderAddress;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String computeHash() {
        // todo compute hash
        // note : do not take in consideration the counter and time field
        // as this hash will be used to compare two instances
        // and time and counter should not be taken in consideration
        // in this comparison.
        // note : use r.getHash() to compute this one more efficiently
        return "HASH";
    }
}
