package dataclasses;

import com.google.gson.annotations.SerializedName;

public class AddressBalance extends Data {

    public static final String ADDRESS = "address";
    public static final String BALANCE = "balance";

    @SerializedName(ADDRESS)
    private final String address;

    @SerializedName(BALANCE)
    private final double balance;

    public AddressBalance(String address, double balance){
        this.address = address;
        this.balance = balance;
    }

    public String getAddress(){
        return address;
    }

    public double getBalance(){
        return balance;
    }

    @Override
    public String computeHash() {
        // todo compute hash
        return "HASH";
    }
}
