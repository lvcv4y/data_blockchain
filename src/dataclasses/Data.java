package dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class Data {
    public static final String HASH = "hash";

    @SerializedName(HASH)
    @Expose
    private String hash;

    abstract public String computeHash();

    public String getHash(){
        if(hash == null)
            hash = computeHash();

        return hash;
    }
}
