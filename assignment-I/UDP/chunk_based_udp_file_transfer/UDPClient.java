import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UDPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 2048;
    private static final int HEADER_SIZE = 8;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            System.out.print("Enter the filename to request: ");
            String fileName = scanner.nextLine();
            byte[] sendData = fileName.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            Map<Integer, byte[]> receivedChunks = new HashMap<>();
            int totalChunks = -1;
            byte[] receiveData = new byte[BUFFER_SIZE];

            clientSocket.setSoTimeout(5000);

            try {
                while (totalChunks == -1 || receivedChunks.size() < totalChunks) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);

                    ByteBuffer wrapped = ByteBuffer.wrap(receivePacket.getData(), 0, receivePacket.getLength());
                    int seq = wrapped.getInt();
                    int total = wrapped.getInt();

                    if (total == 0) {
                        System.out.println("Error: File not found or unavailable.");
                        return;
                    }

                    if (totalChunks == -1) {
                        totalChunks = total;
                    }

                    int dataLength = receivePacket.getLength() - HEADER_SIZE;
                    byte[] data = new byte[dataLength];
                    System.arraycopy(receivePacket.getData(), HEADER_SIZE, data, 0, dataLength);

                    receivedChunks.put(seq, data);
                }

                File outputFile = new File("downloaded_" + fileName);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        if (receivedChunks.containsKey(i)) {
                            fos.write(receivedChunks.get(i));
                        }
                    }
                }

                System.out.println("File received successfully and saved as: " + outputFile.getName());

            } catch (SocketTimeoutException e) {
                System.out.println("Error: Server timeout. Packets were lost.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}