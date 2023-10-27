package execice6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class Block1 {
    private int origin;
    private int destiny;
    private double value;
    private String hash;

    public Block1(int origin, int destiny, double value, String hash) {
        this.origin = origin;
        this.destiny = destiny;
        this.value = value;
        this.hash = hash;
    }

    public String calculateHashVerification() {
        String data = origin + "," + destiny + "," + value + "," + hash;
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

    public boolean isValid(Block1 previousBlock) {
        return hash.equals(calculateHashVerification()) && hash.equals(previousBlock.hash);
    }

    @Override
    public String toString() {
        return origin + "," + destiny + "," + value + "," + hash;
    }
}

public class VerifyBlockChain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso correto: verifychain <filename>");
            return;
        }

        String filename = args[0];
        List<Block1> blockchain = readBlockchainFromFile(filename);

        if (blockchain != null && validateBlockchain(blockchain)) {
            System.out.println("Cadeia de blocos válida:");
            for (Block1 block : blockchain) {
                System.out.println(block.toString());
            }
        } else {
            System.out.println("Cadeia de blocos inválida.");
        }
    }

    private static List<Block1> readBlockchainFromFile(String filename) {
        List<Block1> blockchain = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int origin = Integer.parseInt(parts[0]);
                    int destiny = Integer.parseInt(parts[1]);
                    double value = Double.parseDouble(parts[2]);
                    String hash = parts[3];
                    blockchain.add(new Block1(origin, destiny, value, hash));
                } else {
                    System.out.println("Formato inválido no arquivo.");
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo.");
            e.printStackTrace();
            return null;
        }
        return blockchain;
    }

    private static boolean validateBlockchain(List<Block1> blockchain) {
        for (int i = 1; i < blockchain.size(); i++) {
            Block1 currentBlock = blockchain.get(i);
            Block1 previousBlock = blockchain.get(i - 1);

            if (!currentBlock.isValid(previousBlock)) {
                return false;
            }
        }
        return true;
    }
}