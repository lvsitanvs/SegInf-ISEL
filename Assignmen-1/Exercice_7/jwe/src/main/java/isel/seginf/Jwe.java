package isel.seginf;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import static isel.seginf.AESGCMCipher.decryptAESGMC;
import static isel.seginf.AESGCMCipher.encryptAESGMC;

public class Jwe {
    private static final int GCM_TAG_LENGTH = 128;

    public static void main(String[] args) throws Exception {
        // inform user about the usage of the program
        if (args.length != 3) {
            usage();
            return;
        }

        String command = args[0];
        String string = args[1];
        String certificate = args[2];

        // switch between the two commands
        switch (command) {
            case "enc" -> {
                PublicKey publicKey = KeyStoreAndCertificate.getPublicKeyFromCertificate(certificate, false);
                String encryptedText = encrypt(string, publicKey);
                if (encryptedText != null) {
                    System.out.println("Encrypted text: " + encryptedText);
                } else {
                    System.out.println("Error encrypting text");
                }
            }
            case "dec" -> {
                PrivateKey privateKey = KeyStoreAndCertificate.getPrivateKeyFromKeystore(
                        certificate,
                        "changeit",
                        "1",
                        "changeit",
                        "PKCS12",
                        false);
                String decryptedText = decrypt(string, privateKey);

                if (decryptedText != null) {
                    System.out.println("Decrypted text: " + decryptedText);
                } else {
                    System.out.println("Error decrypting text");
                }
            }
            default -> {
                System.out.println("Unknown command: " + command);
                usage();
            }
        }
    }
    private static void usage() {
        System.out.println("Usage: java -jar jwe.jar <command> <string> <certificate>");
        System.out.println("Commands:");
        System.out.println("  enc <some string> <recipient certificate>");
        System.out.println("  dec <jwe string> <recipient pfx>");
    }

    private static String encrypt(String plaintext, PublicKey publicKey) {
        System.out.println("Encrypting string: " + plaintext);
        System.out.println("Using certificate: " + publicKey);
        try {
            // Cipher with AES/GCM/NoPadding algorithm
            String header = "{\"alg\":\"RSA-OAEP\",\"enc\":\"A256GCM\"}";
            String protectedHeader = Base64.getEncoder().encodeToString(header.getBytes());
            byte[] byteIV = generateIV();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecretKey randomKey = keyGenerator.generateKey();
            String result = encryptAESGMC(plaintext, protectedHeader, randomKey.getEncoded(), byteIV, GCM_TAG_LENGTH);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] encryptedKeyBytes = cipher.wrap(randomKey);


            // Return JSON Web Encryption object ------------------------------------
            // BASE64URL(UTF8(JWE Protected Header)) || '.' ||
            // BASE64URL(JWE Encrypted Key) || '.' ||
            // BASE64URL(JWE Initialization Vector) || '.' ||
            // BASE64URL(JWE Ciphertext) || '.' ||
            // BASE64URL(JWE Authentication Tag)
            String encryptedKey = Base64.getEncoder().encodeToString(encryptedKeyBytes);
            String InitializationVector = Base64.getEncoder().encodeToString(byteIV);
            assert result != null;
            String ciphertext = result.split("\\.") [0];
            String authenticationTag = result.split("\\.") [1];

            return protectedHeader + "." + encryptedKey + "." + InitializationVector + "." + ciphertext + "." + authenticationTag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decrypt(String encryptedString, PrivateKey secretKey) {
        String[] parts = encryptedString.split("\\.");
        String additionalData = parts[0];
        byte[] encryptedKey = Base64.getDecoder().decode(parts[1]);
        String InitializationVector = parts[2];
        String ciphertext = parts[3];

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Unwrap the key
            cipher.init(Cipher.UNWRAP_MODE, secretKey);
            SecretKey originalKey = (SecretKey) cipher.unwrap(encryptedKey, "AES", Cipher.SECRET_KEY);

            // Decrypt the cipherText
            return decryptAESGMC(ciphertext, additionalData, originalKey.getEncoded(), InitializationVector, GCM_TAG_LENGTH);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[GCM_TAG_LENGTH / 8];
        random.nextBytes(iv);
        return iv;
    }
}