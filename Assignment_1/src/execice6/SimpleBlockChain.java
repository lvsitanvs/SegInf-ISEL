package execice6;

import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

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
