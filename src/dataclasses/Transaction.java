package dataclasses;

public class Transaction extends Data {
    public final String fromAddr;
    public final String toAddr;

    public final double amount;

    public Transaction(String fromAddr, String toAddr, double amount) {
        this.fromAddr = fromAddr;
        this.toAddr = toAddr;
        this.amount = amount;
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

    @Override
    public String computeHash() {
        return null;
    }
}
