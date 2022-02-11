package request;

import dataclasses.Data;

import java.util.List;

public class Request {

    public final RequestTypes type;

    public final ErrorCodes error;

    public final List<Data> data;

    public Request(RequestTypes type, ErrorCodes error, List<Data> data){
        this.type = type;
        this.error = error;
        this.data = data;
    }

    public Request(RequestTypes type){
        this(type, ErrorCodes.NO_ERROR, null);
    }

    public Request(RequestTypes type, ErrorCodes error){
        this(type, error, null);
    }

    public Request(RequestTypes type, List<Data> data){
        this(type, ErrorCodes.NO_ERROR, data);
    }
}
