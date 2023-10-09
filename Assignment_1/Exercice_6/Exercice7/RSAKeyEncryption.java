import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
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
        try {
            // Generate a  symmetric AES key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

            // Gere um par de chaves RSA (chave pública e privada)
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Cifre a chave simétrica usando a chave pública RSA
            String encryptedKey = encryptSymmetricKey(secretKey, publicKey);

            // Imprima a chave cifrada (em uma implementação real, você a enviaria ao destinatário)
            System.out.println("Chave simétrica cifrada: " + encryptedKey);

            // Decifre a chave simétrica usando a chave privada RSA
            SecretKey decryptedKey = decryptSymmetricKey(encryptedKey, privateKey);

            // Teste cifrando e decifrando um texto usando a chave simétrica
            String plaintext = "Texto a ser cifrado";
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, decryptedKey);
            byte[] encryptedBytes = aesCipher.doFinal(plaintext.getBytes());

            aesCipher.init(Cipher.DECRYPT_MODE, decryptedKey);
            byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);
            String decryptedText = new String(decryptedBytes);

            System.out.println("Texto decifrado: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
