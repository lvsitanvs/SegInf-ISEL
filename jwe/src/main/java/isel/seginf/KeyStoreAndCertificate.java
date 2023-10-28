package isel.seginf;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.*;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class KeyStoreAndCertificate {
    public static PublicKey getPublicKeyFromCertificate(String certificateFile, Boolean printInfo) throws Exception {
        // Generate object for X.509 certificate
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        // Instantiates a stream for reading the certificate file
        FileInputStream inputStream = new FileInputStream(certificateFile);

        // Generate the certificate object from the stream
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);

        inputStream.close();

        // Get public key from certificate
        PublicKey publicKey = certificate.getPublicKey();

        // Converts the publicKey to RSAPublicKey.
        KeyFactory factory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pkRSA = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);

        if (printInfo) {
            System.out.println("SubjectDN: " + certificate.getSubjectX500Principal());
            System.out.println("IssuerDN: " + certificate.getIssuerX500Principal());
            System.out.println("Type: " + certificate.getType());
            System.out.println("Version: " + certificate.getVersion());
            System.out.println("SigAlgName: " + certificate.getSigAlgName());
            System.out.println("NotBefore: " + certificate.getNotBefore());
            System.out.println("NotAfter: " + certificate.getNotAfter());
            System.out.println("SerialNumber: " + certificate.getSerialNumber());
            System.out.println();
            System.out.println("PublicKey: " + certificate.getPublicKey());
        }
        if (verifyCertificate(certificateFile)) {
            System.out.println("Certificate is valid");
        } else {
            System.out.println("Certificate is not valid");
            return null;
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKeyFromKeystore(
            String keystoreFile,
            String keystorePassword,
            String alias,
            String keyPassword,
            String keyStoreInstance,
            Boolean printInfo
    ) throws Exception {
        // Instantiates a stream for reading the keystore file
        FileInputStream keystoreInputStream = new FileInputStream(keystoreFile);

        // Generate the keystore object from the stream
        KeyStore keyStore = KeyStore.getInstance(keyStoreInstance);
        keyStore.load(keystoreInputStream, keystorePassword.toCharArray());

        keystoreInputStream.close();

        // Get private key from keystore
        KeyStore.PasswordProtection keyPasswordProtection = new KeyStore.PasswordProtection(keyPassword.toCharArray());
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPasswordProtection);
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        if (printInfo) {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias1 = aliases.nextElement();
                System.out.println("Alias: " + alias1);

                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias1);

                System.out.println("SubjectDN: " + certificate.getSubjectX500Principal());
                System.out.println("IssuerDN: " + certificate.getIssuerX500Principal());

                PublicKey publicKey = certificate.getPublicKey();
                System.out.println("PublicKey: " + publicKey);
            }
        }
        return privateKey;
    }

    private static Boolean verifyCertificate(String certificateFile) {
        ArrayList<X509Certificate> certList = new ArrayList<>();
        System.out.println(certificateFile);
        String filename = certificateFile.split("/")[2];
        String numSerie = filename.split("_")[1];
        // Adicionar os certificados
        switch (numSerie) {
            case "1.cer":
                certList.add(0, getCertificate(certificateFile));
                certList.add(1, getCertificate("certificates-keys/intermediates/CA1-int.cer"));
                certList.add(2, getCertificate("certificates-keys/trust-anchors/CA1.cer"));
                break;
            case "2.cer":
                certList.add(0, getCertificate(certificateFile));
                certList.add(1, getCertificate("certificates-keys/intermediates/CA2-int.cer"));
                certList.add(2, getCertificate("certificates-keys/trust-anchors/CA2.cer"));
                break;
            default:
                System.out.println("Invalid certificate");
                return false;
        }

        Set<TrustAnchor> trustAnchorSet = new HashSet<>();
        for (X509Certificate x509Certificate : certList) {
            trustAnchorSet.add(new TrustAnchor(x509Certificate, null));
        }

        try {
            PKIXParameters params = new PKIXParameters(trustAnchorSet);
            params.setRevocationEnabled(false);
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            CertPath certPath = CertificateFactory.getInstance("X.509").generateCertPath(certList);
            cpv.validate(certPath, params);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static X509Certificate getCertificate(String certificateFile) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream inputStream = new FileInputStream(certificateFile);
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            inputStream.close();
            return certificate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            // Get Public Key from X.509 certificate
            String certificateFile = "certificates-keys/end-entities/Alice_1.cer";
            PublicKey publicKey = getPublicKeyFromCertificate(certificateFile, true);
            System.out.println("Public Key from certificate: " + publicKey);

            // Get Private Key from Keystore
            String keystoreFile = "certificates-keys/pfx/Alice_1.pfx";
            String keystorePassword = "changeit";
            String alias = "1";
            String keyPassword = "changeit";
            String keyStoreInstance = "PKCS12";  // Can use "JKS" for a JKS keystore.

            PrivateKey privateKey = getPrivateKeyFromKeystore(keystoreFile, keystorePassword, alias, keyPassword, keyStoreInstance, true);
            System.out.println("Private Key from Keystore: " + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
