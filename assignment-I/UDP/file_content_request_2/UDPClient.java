import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class UDPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9876;
    private static final int MAX_BUFFER_SIZE = 65507; 

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            System.out.print("Enter the filename to request: ");
            String fileName = scanner.nextLine();
            byte[] sendData = fileName.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[MAX_BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            clientSocket.setSoTimeout(5000); 
            
            try {
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                
                System.out.println("\n--- Server Response ---");
                System.out.println(response);
                System.out.println("-----------------------");
                
            } catch (SocketTimeoutException e) {
                System.out.println("Error: Server timeout. The request or response packet may have been lost.");
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}