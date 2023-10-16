package execice7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class AESEncryptationTest {
    private SecretKey secretKey;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        secretKey = keyGenerator.generateKey();
    }

    @Test
    public void testEncryptWhenValidInputThenReturnEncryptedString() throws Exception {
        String plaintext = "This is a test string";

        String encryptedText = AESEncryptation.encrypt(plaintext, secretKey);

        assertNotNull(encryptedText);
        assertNotEquals(plaintext, encryptedText);
    }

    @Test
    public void testEncryptWhenNullPlaintextThenThrowException() {
        assertThrows(NullPointerException.class, () -> AESEncryptation.encrypt(null, secretKey));
    }

    @Test
    public void testDecryptWhenValidInputThenReturnDecryptedString() throws Exception {
        String plaintext = "This is a test string";

        String encryptedText = AESEncryptation.encrypt(plaintext, secretKey);
        String decryptedText = AESEncryptation.decrypt(encryptedText, secretKey);

        assertNotNull(decryptedText);
        assertEquals(plaintext, decryptedText);
    }

    @Test
    public void testDecryptWhenNullEncryptedStringThenThrowException() {
        assertThrows(NullPointerException.class, () -> AESEncryptation.decrypt(null, secretKey));
    }
}
