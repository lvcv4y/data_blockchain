package dataclasses;

import java.util.List;

public class OldBlock extends Block {
    private final List<AddressBalance> balances;
    public final String minerAddress;

    public OldBlock(String minerAddress, List<FileChunk> fileChunks, List<AddressBalance> balances){
        super(fileChunks);
        this.minerAddress = minerAddress;
        this.balances = balances;
    }

    @Override
    public String computeHash() {
        return null;
    }
}
