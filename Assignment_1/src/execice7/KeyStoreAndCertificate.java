package execice7;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class KeyStoreAndCertificate {
    public static PublicKey getPublicKeyFromCertificate(String certificateFile) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream inputStream = new FileInputStream(certificateFile);
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        inputStream.close();
        return certificate.getPublicKey();
    }

    public static PrivateKey getPrivateKeyFromKeystore(String keystoreFile, String keystorePassword, String alias, String keyPassword) throws Exception {
        FileInputStream keystoreInputStream = new FileInputStream(keystoreFile);
        KeyStore keyStore = KeyStore.getInstance("JKS"); // You can use "PKCS12" for a PKCS12 keystore.
        keyStore.load(keystoreInputStream, keystorePassword.toCharArray());
        keystoreInputStream.close();

        KeyStore.PasswordProtection keyPasswordProtection = new KeyStore.PasswordProtection(keyPassword.toCharArray());
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPasswordProtection);
        return privateKeyEntry.getPrivateKey();
    }

    public static void main(String[] args) {
        try {
            // Obtenha a chave pública de um certificado X.509
            String certificateFile = "path/to/your/certificate.crt";
            PublicKey publicKey = getPublicKeyFromCertificate(certificateFile);
            System.out.println("Chave pública do certificado: " + publicKey);

            // Obtenha a chave privada de um Keystore
            String keystoreFile = "path/to/your/keystore.jks";
            String keystorePassword = "your_keystore_password";
            String alias = "your_alias";
            String keyPassword = "your_key_password";

            PrivateKey privateKey = getPrivateKeyFromKeystore(keystoreFile, keystorePassword, alias, keyPassword);
            System.out.println("Chave privada do Keystore: " + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
