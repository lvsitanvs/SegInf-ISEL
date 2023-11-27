package isel.seginf;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SSLClient {
    public static void SSLClient(String certificatePath, String host, Integer port) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLSocketFactory sslFactory = configureSSLSocketFactory(certificatePath);
            //HttpsURLConnection.getDefaultSSLSocketFactory();

        // print cipher suites available at the client
        String[] cipherSuites = sslFactory.getSupportedCipherSuites();
        for (int i=0; i<cipherSuites.length; ++i) {
            System.out.println("option " + i + " " + cipherSuites[i]);
        }

        // establish connection
        SSLSocket client = (SSLSocket) sslFactory.createSocket(host, port);
        System.out.println("Connection established!");
        client.startHandshake();
        System.out.println("Handshake done!");
        SSLSession session = client.getSession();
        System.out.println("Session established!");
        System.out.println("Cipher suite: " + session.getCipherSuite());
        System.out.println("Protocol version: " + session.getProtocol());
        System.out.println(session.getPeerCertificates()[0]);
    }

    private static SSLSocketFactory configureSSLSocketFactory(String certificatePath) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, FileNotFoundException {
        // Load server's certificate to KeyStore
        KeyStore ks = KeyStore.getInstance("PKCS12");
        // tansforma com keytool -importcert -file CA1.cer -keystore CA1.p12 -storetype PKCS12
        System.out.println(certificatePath);
        FileInputStream certInputStream = new FileInputStream(certificatePath);
        try {
            ks.load(certInputStream, "changeit".toCharArray());
            System.out.println("Certificate loaded!");
            ks.setCertificateEntry("server", java.security.cert.CertificateFactory.getInstance("X.509").generateCertificate(certInputStream));
            System.out.println("Certificate added to KeyStore!");
        } catch (Exception e) {
            System.out.println("Error loading certificate!");
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Create a TrustManager that trusts the server's certificate
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        System.out.println("TrustManagerFactory initialized!");

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        System.out.println("SSLContext initialized!");

        // Return the SSL Socket Factory
        return context.getSocketFactory();
    }
}
