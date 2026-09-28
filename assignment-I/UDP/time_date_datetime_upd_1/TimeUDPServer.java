import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUDPServer {
    
    private static final int PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
    
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running and listening on port " + PORT + "...");

            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String request = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim().toUpperCase();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                
                System.out.println("Received request: '" + request + "' from " + clientAddress + ":" + clientPort);

                String response;
                switch (request) {
                    case "DATE":
                        response = LocalDate.now().toString(); 
                        break;
                    case "TIME":
                        response = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        break;
                    case "DATETIME":
                        response = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        break;
                    default:
                        response = "ERROR: Invalid command. Please use DATE, TIME, or DATETIME.";
                }

                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}