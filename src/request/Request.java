package request;

import com.google.gson.annotations.SerializedName;
import dataclasses.Data;
import dataclasses.ReceivedRequest;
import utils.Cryptography;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Request extends Data {

    public static final String TYPE = "type";
    public static final String ERROR_CODE = "error_code";
    public static final String DATA = "data";

    @SerializedName(TYPE)
    private final RequestTypes type;

    @SerializedName(ERROR_CODE)
    private final ErrorCodes error;

    @SerializedName(DATA)
    private final List<Data> data;

    public Request(RequestTypes type, ErrorCodes error, List<Data> data){
        this.type = type;
        this.error = error;

        if(data == null)
            this.data = new ArrayList<>();
        else
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

    public RequestTypes getType() {
        return type;
    }

    public ErrorCodes getError() {
        return error;
    }

    public List<Data> getData() {
        return data;
    }

    public void addData(Data d){
        data.add(d);
    }

    public ReceivedRequest wrap(InetSocketAddress from, LocalDateTime time){
        return new ReceivedRequest(this, from, time);
    }

    @Override
    public BigInteger computeHash() {
        final String dataHash = Cryptography.getHexFromHash(Cryptography.getSHA256HashUsingMerkelTree(data));
        final String otherFieldsHash = Cryptography.getHexFromHash(
                Cryptography.getSHA256HashFromString(type + "#" + error.toString())
        );

        return Cryptography.getSHA256HashFromString(otherFieldsHash + "#" + dataHash);
    }
}
