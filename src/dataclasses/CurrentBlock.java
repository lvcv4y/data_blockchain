package dataclasses;

import com.google.gson.annotations.SerializedName;
import utils.Cryptography;

import java.math.BigInteger;
import java.util.List;

public class CurrentBlock extends Block {
    public static final String PENDING_TRANSACTIONS = "pending_transactions";

    @SerializedName(PENDING_TRANSACTIONS)
    private final List<Transaction> pendingTransactions;

    public CurrentBlock(String lastBlocHash, List<FileChunk> fileChunks, List<Transaction> pendingTransactions){
        super(lastBlocHash, fileChunks);
        this.pendingTransactions = pendingTransactions;
    }

    public void addTransaction(Transaction t){
        pendingTransactions.add(t);
    }

    public void deleteTransaction(Transaction t){
        pendingTransactions.remove(t);
    }

    public List<Transaction> getTransactions(){
        return pendingTransactions;
    }

    @Override
    public BigInteger computeHash() {
        final String filesHash = Cryptography.getHexFromHash(Cryptography.getSHA256HashUsingMerkelTree(fileChunks));
        final String transactionsHash = Cryptography.getHexFromHash(
                Cryptography.getSHA256HashUsingMerkelTree(pendingTransactions)
        );

        return Cryptography.getSHA256HashFromString(lastBlocHash + "#" + filesHash + "#" + transactionsHash);
    }
}
