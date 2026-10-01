import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class NameServer {
    private static final int PORT = 9876;
    private static final Map<String, String> dnsTable = new HashMap<>();

    static {
        dnsTable.put("server1", "192.168.1.10");
        dnsTable.put("server2", "192.168.1.20");
        dnsTable.put("database", "10.0.0.5");
        dnsTable.put("router", "192.168.1.1");
        dnsTable.put("localhost", "127.0.0.1");
    }

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Name Service running on port " + PORT + "...");
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String hostName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress clientIP = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Lookup request for: '" + hostName + "' from " + clientIP);

                String ipAddress = dnsTable.getOrDefault(hostName, "NOT FOUND");
                byte[] sendData = ipAddress.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}