package dataclasses;

import com.google.gson.annotations.SerializedName;
import utils.Cryptography;

import java.math.BigInteger;
import java.util.List;

public class OldBlock extends Block {

    public static final String BALANCES = "balances";
    public static final String MINER_ADDRESS = "miner_address";
    public static final String MINER_SIGNATURE = "miner_signature";

    @SerializedName(BALANCES)
    private final List<AddressBalance> balances;

    @SerializedName(MINER_ADDRESS)
    public final String minerAddress;

    @SerializedName(MINER_SIGNATURE)
    public final String minerSignature;

    public OldBlock(String lastBlocHash, String minerAddress, String minerSignature,
                    List<FileChunk> fileChunks, List<AddressBalance> balances){
        super(lastBlocHash, fileChunks);
        this.minerAddress = minerAddress;
        this.minerSignature = minerSignature;
        this.balances = balances;
    }

    public List<AddressBalance> getBalances(){
        return balances;
    }

    @Override
    public BigInteger computeHash() {
        // note : we do not take the minerSignature field in consideration
        // as it is the object's hash signed by the miner.
        final String filesHash = Cryptography.getHexFromHash(Cryptography.getSHA256HashUsingMerkelTree(fileChunks));
        final String balancesHash = Cryptography.getHexFromHash(
                Cryptography.getSHA256HashUsingMerkelTree(balances)
        );

        return Cryptography.getSHA256HashFromString(lastBlocHash + "#" + filesHash + "#" + balancesHash);
    }
}
