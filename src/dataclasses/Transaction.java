package dataclasses;

import com.google.gson.annotations.SerializedName;

public class Transaction extends Data {
    public static final String FROM_ADDRESS = "from_addr";
    public static final String TO_ADDRESS = "to_addr";
    public static final String AMOUNT = "amount";
    public static final String SIGNATURE = "signature";

    @SerializedName(FROM_ADDRESS)
    private final String fromAddr;

    @SerializedName(TO_ADDRESS)
    private final String toAddr;

    @SerializedName(AMOUNT)
    private final double amount;

    @SerializedName(SIGNATURE)
    private final String signature; // hash of transaction signed with private key of fromAddr

    public Transaction(String fromAddr, String toAddr, double amount, String signature) throws NullPointerException {

        this.fromAddr = fromAddr;
        this.toAddr = toAddr;
        this.amount = amount;
        this.signature = signature;
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public String getToAddr() {
        return toAddr;
    }

    public double getAmount() {
        return amount;
    }

    public String getSignature(){
        return this.signature;
    }

    private String getSignature(String privateKey){
        // todo sign et return transaction hash
        return null;
    }

    public boolean isSignatureValid(){
        // todo verify that the signature field is the hash of the transaction
        // signed by the private key corresponding to the fromAddr one.
        return signature != null && fromAddr != null;
    }

    @Override
    public String computeHash() {
        // todo compute hash
        // note: do not use the signature field to compute the hash
        // as it is the signed hash of the transaction
        return "HASH";
    }
}
