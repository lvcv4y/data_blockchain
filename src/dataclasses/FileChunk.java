package dataclasses;

import com.google.gson.annotations.SerializedName;
import utils.Cryptography;

import java.math.BigInteger;

public class FileChunk extends Data {
    public static final String NAME = "name";
    public static final String FILE_DATA = "file_data";

    @SerializedName(NAME)
    public final String name; // signed hash of filechunk, so we can identify the author of the file

    @SerializedName(FILE_DATA)
    public final String data;

    public FileChunk(String name, String data){
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    @Override
    public BigInteger computeHash() {
        // we don't take "name field in consideration", as
        // it is already the data hash signed by the author.
        return Cryptography.getSHA256HashFromString(data);
    }
}
