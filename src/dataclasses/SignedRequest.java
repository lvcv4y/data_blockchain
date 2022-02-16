package dataclasses;

import com.google.gson.annotations.SerializedName;
import request.ErrorCodes;
import request.Request;
import request.RequestTypes;
import utils.Cryptography;

import java.math.BigInteger;
import java.util.List;

public class SignedRequest extends Request {
    public static final String SIGNATURE = "signature";
    public static final String AUTHOR_PUBLIC_KEY = "author_public_key";
    public static final String AUTHOR_IP = "author_ip";

    @SerializedName(SIGNATURE)
    private final String signature;

    @SerializedName(AUTHOR_PUBLIC_KEY)
    private final String authorPublicKey;

    @SerializedName(AUTHOR_IP)
    private final String authorIp;

    public SignedRequest(RequestTypes type, ErrorCodes errorCodes, List<Data> data,
                         String signature, String authorPublicKey, String authorIp){
        super(type, errorCodes, data);
        this.signature = signature;
        this.authorPublicKey = authorPublicKey;
        this.authorIp = authorIp;
    }

    public String getSignature() {
        return signature;
    }

    public String getAuthorPublicKey() {
        return authorPublicKey;
    }

    public String getAuthorIp() {
        return authorIp;
    }

    @Override
    public BigInteger computeHash(){
        // note : we do not take the signature field in consideration
        // as it is the object's hash signed by the author private key.
        final String toDigest = super.getHash() + "#" +
                authorPublicKey + "#" + authorIp;
        return Cryptography.getSHA256HashFromString(toDigest);
    }
}
