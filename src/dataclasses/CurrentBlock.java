package dataclasses;

import java.util.List;

public class CurrentBlock extends Block {
    private final List<Transaction> pendingTransactions;

    public CurrentBlock(List<FileChunk> fileChunks, List<Transaction> pendingTransactions){
        super(fileChunks);
        this.pendingTransactions = pendingTransactions;
    }

    public void addTransaction(){

    }

    @Override
    public String computeHash() {
        return null;
    }
}
