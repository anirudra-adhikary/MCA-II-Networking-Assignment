import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class NameClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            byte[] receiveData = new byte[1024];

            while (true) {
                System.out.print("Enter hostname to lookup (or 'exit' to quit): ");
                String hostName = scanner.nextLine();

                if ("exit".equalsIgnoreCase(hostName)) {
                    break;
                }

                byte[] sendData = hostName.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.setSoTimeout(3000); 
                
                try {
                    clientSocket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Result: " + response + "\n");
                } catch (Exception e) {
                    System.out.println("Result: Request timed out.\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}