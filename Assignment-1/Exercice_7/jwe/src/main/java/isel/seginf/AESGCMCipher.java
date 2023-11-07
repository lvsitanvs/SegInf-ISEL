package isel.seginf;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class AESGCMCipher {
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public static String encryptAESGMC(
            String plaintext,
            String additionalData,
            byte[] key,
            byte[] iv,
            int tagLengthBits
    ){
        try {

            // Parameters for the symmetric key
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLengthBits, iv);

            // Generates the symmetric key object
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // Initializes the cipher object with the symmetric key and the IV and additional data
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            cipher.updateAAD(additionalData.getBytes());

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
            byte[] tag = Arrays.copyOfRange(ciphertext, ciphertext.length - tagLengthBits / 8, ciphertext.length);
            String tagString = Base64.getEncoder().encodeToString(tag);
            String ciphertextString = Base64.getEncoder().encodeToString(ciphertext);

            return ciphertextString + "." + tagString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptAESGMC(
            String ciphertext,
            String additionalData,
            byte[] key,
            String iv,
            int tagLengthBits
    ){
        try {
            byte[] IVBytes = Base64.getDecoder().decode(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLengthBits, IVBytes);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            cipher.updateAAD(additionalData.getBytes());

            byte[] decodedText = Base64.getDecoder().decode(ciphertext);
            byte[] plaintext = cipher.doFinal(decodedText);

            return new String(plaintext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
