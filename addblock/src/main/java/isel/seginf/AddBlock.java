package isel.seginf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/* This class adds a new block to the blockchain.
*  The block is created from the arguments passed to the program.
*
*  The arguments are:
*   - origin: the origin account id
*   - destiny: the destiny account id
*   - value: the value to transfer
*   - filename: the file where the blockchain is stored

*  The program will create the file if it does not exist with a genesis block (transaction 0).
*  It will calculate the hash of the previous block and use it as the hash of the new block.
*  At last, it will append the new block to the file.  */
public class AddBlock {
    public static void main(String[] args) {
        // Inform user about usage ----------------------------------------------------------------
        if (args.length != 4) {
            System.out.println("Usage: java -jar addblock.jar <origin> <destiny> <value> <filename>");
            return;
        }
        // Get arguments --------------------------------------------------------------------------
        int origin = Integer.parseInt(args[0]);
        int dest = Integer.parseInt(args[1]);
        float value = Float.parseFloat(args[2]);
        String filename = args[3];
        // Create transaction ---------------------------------------------------------------------
        String transaction = origin + "," + dest + "," + value;
        File file = new File(filename);

        if (!file.exists()) {                            // check if the file exists in filesystem
            saveToFile("-1,-1,-1,0x0", filename);  // create file and write genesis block
        }
        String line = readLastBlock(filename);           // read last block from file
        String hash = calculateHash(line);               // calculate last block hash
        String newBlock = transaction + "," + hash;      // create new block
        saveToFile(newBlock, filename);                  // save block to file
    }

    // Save block to file -------------------------------------------------------------------------
    private static void saveToFile(String block, String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(block + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read last block from file ------------------------------------------------------------------
    private static String readLastBlock(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String line = "";
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
            }
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Calculate hash from block ------------------------------------------------------------------
    private static String calculateHash(String line) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(line.getBytes());
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