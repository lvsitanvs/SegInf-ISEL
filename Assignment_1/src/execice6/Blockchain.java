package execice6;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        String previousBlockHash = previousBlock.getHash();
        String data = transaction.toString() + previousBlockHash;
        String previousHash = previousBlock.calculateHash(data);
        Block newBlock = new Block(transaction, previousHash);
        chain.add(newBlock);
        saveToFile(newBlock);
    }

    private void saveToFile(Block block) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            String data = block.getTransaction().toString() + block.getPreviousHash();
            String line = block.toString() + "," + block.calculateHash(data) + "\n";
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
