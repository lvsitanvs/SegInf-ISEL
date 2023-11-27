package isel.seginf;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class SSLClient {
    public static void SSLClient(String host, Integer port) throws IOException {
        SSLSocketFactory sslFactory =
            HttpsURLConnection.getDefaultSSLSocketFactory();

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
}
