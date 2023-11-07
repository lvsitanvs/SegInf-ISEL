package isel.seginf;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


/* This class verifies the integrity of the blockchain.
 *  The blockchain is stored in a file.
 *  The program will read the file and verify the integrity of the blockchain.
 *  It will print the number of transactions in the blockchain and if it is valid or not. */
public class VerifyChain {
    public static void main(String[] args) {
        // inform user about usage ----------------------------------------------------------------
        if (args.length != 1) {
            System.out.println("Usage: java -jar verifychain.jar <filename>");
            return;
        }

        // get arguments --------------------------------------------------------------------------
        String filename = args[0];

        // read file ------------------------------------------------------------------------------
        File file = new File(filename);

        // check if file exists -------------------------------------------------------------------
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        // inform user of the number of transactions in the blockchain ----------------------------
        int numberOfBlocks = getNumberOfBlocks(filename);
        System.out.println("Number of transactions in blockchain: " + numberOfBlocks);

        // start verification ---------------------------------------------------------------------
        verifyBlockchain(filename);
    }

    // get number of blocks ----------------------------------------------------------------------
    private static int getNumberOfBlocks(String filename) {
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
            lineNumberReader.skip(Long.MAX_VALUE);
            int numberOfLines = lineNumberReader.getLineNumber();

            return numberOfLines - 1;  // subtract 1 because of genesis block
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    // verify blockchain -------------------------------------------------------------------------
    private static void verifyBlockchain(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            int lineCounter = 0;
            String previousHash = "0x0";
            Boolean valid = true;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineCounter++;
                if (lineCounter == 1) {
                    if (!verifyGenesisBlock(line)) {
                        System.out.println("Genesis block is invalid.");
                        valid = false;
                        break;
                    }
                    previousHash = calculateHash(line);
                }
                else {
                    if (!verifyBlock(line, previousHash)) {
                        int previousLine = lineCounter - 1;
                        System.out.println("Blockchain is invalid at transaction " + previousLine + ".");
                        valid = false;
                        break;
                    }
                    previousHash = calculateHash(line);
                }
            }
            if (valid) {
                System.out.println("Blockchain is valid.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // verify genesis block ---------------------------------------------------------------------
    private static Boolean verifyGenesisBlock(String line) {
        String[] block = line.split(",");
        if (block.length != 4) {
            return false;
        }
        if (!block[0].equals("-1") || !block[1].equals("-1") || !block[2].equals("-1")) {
            return false;
        }
        return block[3].equals("0x0");
    }

    // verify block -----------------------------------------------------------------------------
    private static Boolean verifyBlock(String line, String previousHash) {
        String[] block = line.split(",");
        if (block.length != 4) {
            return false;
        }
        if (block[0].equals("-1") || block[1].equals("-1") || block[2].equals("-1")) {
            return false;
        }
        return block[3].equals(previousHash);
    }

    // calculate hash ----------------------------------------------------------------------------
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