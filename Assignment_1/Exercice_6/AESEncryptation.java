package Exercice7;

import java.security.Provider;
import java.security.Security;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.util.Base64;

public class AESEncryptation {
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        // Create a new instance of the Security.Provider class, passing in the name of the OpenJDK JCE provider.
        Provider provider = Security.getProvider("SUN");
        Security.addProvider(provider);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        byte[] iv = generateIV();
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        byte[] encryptedBytes = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryptedBytes, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryptedBytes, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Provider provider = Security.getProvider("SUN");
        Security.addProvider(provider);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] iv = new byte[GCM_TAG_LENGTH / 8];
        byte[] ciphertext = new byte[encryptedBytes.length - iv.length];

        System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);
        System.arraycopy(encryptedBytes, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] plaintextBytes = cipher.doFinal(ciphertext);

        return new String(plaintextBytes);
    }

    private static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[GCM_TAG_LENGTH / 8];
        random.nextBytes(iv);
        return iv;
    }
}
