package isel.seginf;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyEncryption {
    public static String encryptSymmetricKey(SecretKey secretKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKeyBytes = cipher.doFinal(secretKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKeyBytes);
    }

    public static SecretKey decryptSymmetricKey(String encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    public static void main(String[] args) {
        System.out.println();
        try {
            // Generate a  symmetric AES key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

            // Generate RSA key pair (public and private key)
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Encrypt the symmetric key using RSA public key
            String encryptedKey = encryptSymmetricKey(secretKey, publicKey);

            // Print the encrypted key (in the exercise, send it to the recipient)
            System.out.println("Encrypted Symmetric Key: " + encryptedKey);

            // Decrypt the symmetric key using RSA private key
            SecretKey decryptedKey = decryptSymmetricKey(encryptedKey, privateKey);

            // Test encrypting and decrypting a text using the symmetric key
            String plaintext = "Text to be encrypted";
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, decryptedKey);
            byte[] encryptedBytes = aesCipher.doFinal(plaintext.getBytes());

            aesCipher.init(Cipher.DECRYPT_MODE, decryptedKey);
            byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);
            String decryptedText = new String(decryptedBytes);

            System.out.println("Plain text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

