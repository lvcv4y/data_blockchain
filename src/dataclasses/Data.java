package dataclasses;

import com.google.gson.annotations.SerializedName;

public abstract class Data {
    public static final String HASH = "hash";

    @SerializedName(HASH)
    protected final String hash;

    protected Data(){
        this.hash = computeHash();
    }

    abstract public String computeHash();

    public String getHash(){
        return hash;
    }
}
