package dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import utils.Cryptography;

import java.math.BigInteger;

public abstract class Data {
    public static final String HASH = "hash";

    @SerializedName(HASH)
    @Expose
    private BigInteger hash;

    abstract public BigInteger computeHash();

    public BigInteger getHash(){
        if(hash == null)
            hash = computeHash();

        return hash;
    }

    public String getStringHash(){
        return Cryptography.getHexFromHash(getHash());
    }
}
