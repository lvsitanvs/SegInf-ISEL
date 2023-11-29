package isel.seginf;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

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
        // Load the server's certificate to KeyStore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        CertificateFactory cf = null;
        try {
            ks.load(null, null);
        } catch (IOException | CertificateException e) {
            System.out.println(e.getMessage());
        }

        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.getMessage();
        }

        try (FileInputStream certificateInputStream = new FileInputStream(certificatePath)) {
            if (cf != null) {
                Certificate cert = cf.generateCertificate(certificateInputStream);
                ks.setCertificateEntry("ca", cert);
            }
            
        } catch (IOException | CertificateException e) {
            System.out.println(e.getMessage());
        }

        // Create a TrustManager that trusts the server's certificate
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        // Create an SSLContext that uses the TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Use the SSLContext to create an SSLSocketFactory
        return sslContext.getSocketFactory();
    }
}
