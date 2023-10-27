package isel;

public class Transaction {
    private int origin;
    private int destiny;
    private double value;

    public Transaction(int origin, int destiny, double value) {
        this.origin = origin;
        this.destiny = destiny;
        this.value = value;
    }

    public String getTransaction() {
        return this.toString();
    }

    @Override
    public String toString() {
        return origin + "," + destiny + "," + value;
    }
}
