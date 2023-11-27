package isel.seginf;

public class Main {
    public static void main(String[] args) {

        System.out.println("Connecting to server!");
        try {
            SSLClient.SSLClient("localhost", 4433);
        } catch (Exception e) {
            System.out.println("Error connecting to server!");
            e.printStackTrace();
        }
    }
}