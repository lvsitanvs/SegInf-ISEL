import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

class Transaction {
    private int origin;
    private int destiny;
    private double value;

    public Transaction(int origin, int destiny, double value) {
        this.origin = origin;
        this.destiny = destiny;
        this.value = value;
    }

    @Override
    public String toString() {
        return origin + "," + destiny + "," + value;
    }
}

class Block {
    private Transaction transaction;
    private String previousHash;

    public Block(Transaction transaction, String previousHash) {
        this.transaction = transaction;
        this.previousHash = previousHash;
    }

    public String calculateHash() {
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
}

class Blockchain {
    private List<Block> chain;
    private String filename;

    public Blockchain(String filename) {
        this.chain = new ArrayList<>();
        this.filename = filename;
        // Add the genesis block
        Transaction genesisTransaction = new Transaction(-1, -1, -1.0);
        Block genesisBlock = new Block(genesisTransaction, "0x0");
        chain.add(genesisBlock);
    }

    public void addBlock(Transaction transaction) {
        Block previousBlock = chain.get(chain.size() - 1);
        String previousHash = previousBlock.calculateHash();
        Block newBlock = new Block(transaction, previousHash);
        chain.add(newBlock);
        saveToFile(newBlock);
    }

    private void saveToFile(Block block) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            String line = block.toString() + "," + block.calculateHash() + "\n";
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class SimpleBlockChain {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Uso correto: addblock <origem> <destino> <valor> <filename>");
            return;
        }

        int origin = Integer.parseInt(args[0]);
        int destiny = Integer.parseInt(args[1]);
        double value = Double.parseDouble(args[2]);
        String filename = args[3];

        Transaction transaction = new Transaction(origin, destiny, value);
        Blockchain blockchain = new Blockchain(filename);
        blockchain.addBlock(transaction);
        System.out.println("Bloco adicionado com sucesso à cadeia.");
    }
}
