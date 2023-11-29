package isel.seginf;

public class Main {
    public static void main(String[] args) {
        String HOST = "www.secure-server.edu";
        Integer PORT = 4433;
        String CERTIFICATE_PATH = "src/main/resources/ca.jks";
        System.out.println("Connecting to server!");
        try {
            SSLClient.SSLClient(CERTIFICATE_PATH, HOST, PORT);
        } catch (Exception e) {
            System.out.println("Error connecting to server!");
            e.printStackTrace();
        }
    }
}