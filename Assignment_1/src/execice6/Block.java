package execice6;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Block {
    private Transaction transaction;
    private String previousHash;
    private int origin;
    private int destiny;
    private double value;
    private String hash;

    public Block(Transaction transaction, String previousHash) {
        this.transaction = transaction;
        this.previousHash = previousHash;
    }

    public Block(int origin, int destiny, double value, String hash) {
        this.origin = origin;
        this.destiny = destiny;
        this.value = value;
        this.hash = hash;
    }

    public String calculateHash(String data) {
        String data = transaction.toString() + previousHash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder hashHex = new StringBuilder();
            for (byte b : hashBytes) {
                hashHex.append(String.format("%02x", b));
            }
            return hashHex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isValid(Block previousBlock) {
        String data = origin + "," + destiny + "," + value + "," + hash;
        return hash.equals(calculateHash(data)) && hash.equals(previousBlock.hash);
    }

    @Override
    public String toString() {
        return origin + "," + destiny + "," + value + "," + hash;
    }

    public String getHash() { return hash; }
}
