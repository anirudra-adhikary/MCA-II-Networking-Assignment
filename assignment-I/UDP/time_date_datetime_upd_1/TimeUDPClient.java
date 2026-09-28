import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TimeUDPClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {
             
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            byte[] receiveBuffer = new byte[1024];

            System.out.println("Enter command (DATE, TIME, DATETIME) or 'QUIT' to exit:");

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }

                byte[] sendData = command.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server Response: " + response);
            }
            
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}