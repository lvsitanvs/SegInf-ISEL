package isel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private Transaction transaction;;
    private String hash;

    public Block(Transaction transaction) {
        this.transaction = transaction;
        this.hash = calculateHash(transaction.toString() + previousHash);
    }

    public String setGenesisBlockHash(Block block, String hash) {
        return block.transaction.getTransaction() + ";" +hash;
    }

    public String calculateHash(String data) {
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
        }
        return null;
    }
}
