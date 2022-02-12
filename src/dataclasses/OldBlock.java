package dataclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OldBlock extends Block {

    public static final String BALANCES = "balances";
    public static final String MINER_ADDRESS = "miner_address";

    @SerializedName(BALANCES)
    private final List<AddressBalance> balances;

    @SerializedName(MINER_ADDRESS)
    public final String minerAddress;

    public OldBlock(String lastBlocHash, String minerAddress, List<FileChunk> fileChunks, List<AddressBalance> balances){
        super(lastBlocHash, fileChunks);
        this.minerAddress = minerAddress;
        this.balances = balances;
    }

    public List<AddressBalance> getBalances(){
        return balances;
    }

    @Override
    public String computeHash() {
        //todo compute hash
        return "HASH";
    }
}
