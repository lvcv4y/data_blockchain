package dataclasses;

public class FileChunk extends Data {

    public final String name; // name of the file ; might be an int, or a hash, not set for the moment
    public final String fileChunk;

    public FileChunk(String name, String fileChunk){
        this.name = name;
        this.fileChunk = fileChunk;
    }

    public String getName() {
        return name;
    }

    public String getFileChunk() {
        return fileChunk;
    }

    @Override
    public String computeHash() {
        return null;
    }
}
