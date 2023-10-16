package execice7;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class JWEProcessor {
    public static String encryptAndWrap(String plaintext, String recipientCertificateFile) throws Exception {
        Provider provider = Security.getProvider("SUN");
        Security.addProvider(provider);

        // Carrega o certificado do destinatário a partir do arquivo
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        FileInputStream inputStream = new FileInputStream(recipientCertificateFile);
        X509Certificate recipientCertificate = (X509Certificate) certFactory.generateCertificate(inputStream);
        inputStream.close();

        // Gere uma chave simétrica para criptografar a mensagem
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        // Cifre a mensagem usando AES-GCM
        String ciphertext = AESEncryptation.encrypt(plaintext, secretKey);

        // Cifre a chave simétrica usando a chave pública do destinatário
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.WRAP_MODE, recipientCertificate.getPublicKey());
        byte[] wrappedKey = cipher.wrap(secretKey);

        // Crie o JWE JSON
        String jweHeader = "{\"alg\":\"RSA-OAEP\",\"enc\":\"A256GCM\"}";
        String jweEncryptedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(wrappedKey);
        String jweIV = Base64.getUrlEncoder().withoutPadding().encodeToString(secretKey.getEncoded());

        return jweHeader + "." + jweEncryptedKey + "." + jweIV + "." + ciphertext;
    }

    public static String unwrapAndDecrypt(String jwe, String recipientPrivateKeyFile) throws Exception {
        Provider provider = Security.getProvider("SUN");
        Security.addProvider(provider);

        // Divida o JWE em partes
        String[] parts = jwe.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("JWE não está no formato esperado.");
        }

        // Carregue a chave privada do destinatário a partir do arquivo
        FileInputStream privateKeyInputStream = new FileInputStream(recipientPrivateKeyFile);
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(privateKeyInputStream, "password".toCharArray());
        String alias = keystore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, "password".toCharArray());

        // Descriptografe a chave simétrica usando a chave privada
        byte[] wrappedKeyBytes = Base64.getUrlDecoder().decode(parts[1]);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        Key unwrappedKey = cipher.unwrap(wrappedKeyBytes, "AES", Cipher.SECRET_KEY);

        // Decifre a mensagem usando a chave simétrica
        SecretKey secretKey = (SecretKey) unwrappedKey;
        String ciphertext = parts[3];

        return AESEncryptation.decrypt(ciphertext, secretKey);
    }
}
