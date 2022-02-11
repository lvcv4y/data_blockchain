package dataclasses;

import java.util.List;

public abstract class Block extends Data {
    protected final List<FileChunk> fileChunks;

    protected Block(List<FileChunk> fileChunks) {
        this.fileChunks = fileChunks;
    }

    public List<FileChunk> getFileChunks(){
        return fileChunks;
    }
}
