package dataclasses;

public class AddressBalance extends Data {

    private final String address;
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
        return null;
    }
}
