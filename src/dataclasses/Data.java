package dataclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class Data {
    public static final String HASH = "hash";

    @SerializedName(HASH)
    @Expose
    protected final String hash;

    protected Data(){
        this.hash = computeHash();
    }

    abstract public String computeHash();

    public String getHash(){
        return hash;
    }
}
