package isel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddBlock {
    private  String filename;

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java -jar addblock.jar <origin> <destiny> <value> <filename>");
            return;
        }
        int origin = Integer.parseInt(args[0]);
        int dest = Integer.parseInt(args[1]);
        int value = Integer.parseInt(args[2]);
        String filename = args[3];

        // check if the file exists in filesystem
        File file = new File(filename);
        if (!file.exists()) {
            // create file
            Transaction genesisTransaction = new Transaction(-1,-1,-1.0);
            Block genesisBlock = new Block(genesisTransaction);
            String genesis = genesisBlock.setGenesisBlockHash(genesisBlock, "0x0");

            // write genesis block
            saveToFile(genesis, filename);
        } else {
            // read last block
            Block lastBlock = readLastBlock(filename);
            Transaction transaction = new Transaction(origin, dest, value);
            Block block = new Block(transaction);
            String data = block.getTransaction().toString() + lastBlock.getPreviousHash();
            String line = block.toString() + "," + block.calculateHash(data) + "\n";
            saveToFile(line, filename);
        }
    }

    private static void saveToFile(String block, String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(block + "\n");
        } catch (IOException | IOException e) {
            e.printStackTrace();
        }
    }
}