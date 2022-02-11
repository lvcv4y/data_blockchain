package dataclasses;

public abstract class Data {
    protected final String hash;

    protected Data(){
        this.hash = computeHash();
    }

    abstract public String computeHash();

    public String getHash(){
        return hash;
    }
}
