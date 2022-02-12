package dataclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class Block extends Data {
    public static final String FILE_CHUNKS = "f_chunks";
    public static final String LAST_BLOC_HASH = "last_bloc_hash";

    @SerializedName(LAST_BLOC_HASH)
    protected final String lastBlocHash;

    @SerializedName(FILE_CHUNKS)
    protected final List<FileChunk> fileChunks;

    protected Block(String lastBlocHash, List<FileChunk> fileChunks) {
        this.lastBlocHash = lastBlocHash;
        this.fileChunks = fileChunks;
    }

    public List<FileChunk> getFileChunks(){
        return fileChunks;
    }
}
