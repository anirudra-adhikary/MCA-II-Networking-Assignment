import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;

public class UDPServer {
    private static final int PORT = 9876;
    private static final int MAX_UDP_PAYLOAD = 65507;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT + "...");
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress clientIP = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                
                System.out.println("Client requested file: " + fileName);

                File file = new File(fileName);
                byte[] sendData;

                if (file.exists() && !file.isDirectory()) {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    
                    if (fileBytes.length > MAX_UDP_PAYLOAD) {
                         sendData = "Error: File is too large to fit in a single UDP datagram.".getBytes();
                    } else {
                         sendData = fileBytes;
                    }
                } else {
                    sendData = "Error: File not found or unavailable.".getBytes();
                }

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}