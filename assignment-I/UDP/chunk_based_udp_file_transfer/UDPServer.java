import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPServer {
    private static final int PORT = 9876;
    private static final int BLOCK_SIZE = 1024;
    private static final int HEADER_SIZE = 8;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            log("Server initialized and listening on port " + PORT);
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress clientIP = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                log("Request received from " + clientIP + ":" + clientPort + " for file: '" + fileName + "'");

                File file = new File(fileName);

                if (file.exists() && !file.isDirectory()) {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    int totalChunks = (int) Math.ceil((double) fileBytes.length / BLOCK_SIZE);
                    
                    log("File found. Size: " + fileBytes.length + " bytes. Total chunks: " + totalChunks);
                    log("Starting transfer...");

                    for (int i = 0; i < totalChunks; i++) {
                        int offset = i * BLOCK_SIZE;
                        int length = Math.min(BLOCK_SIZE, fileBytes.length - offset);
                        byte[] chunkData = new byte[HEADER_SIZE + length];

                        ByteBuffer.wrap(chunkData, 0, 4).putInt(i);
                        ByteBuffer.wrap(chunkData, 4, 4).putInt(totalChunks);
                        System.arraycopy(fileBytes, offset, chunkData, HEADER_SIZE, length);

                        DatagramPacket sendPacket = new DatagramPacket(chunkData, chunkData.length, clientIP, clientPort);
                        serverSocket.send(sendPacket);
                        
                        updateStatusViewer(i + 1, totalChunks);
                        
                        Thread.sleep(1); 
                    }
                    System.out.println(); 
                    log("Transfer completed successfully for: " + fileName + "\n");
                } else {
                    log("Error: File '" + fileName + "' not found.");
                    
                    byte[] errorData = new byte[HEADER_SIZE];
                    ByteBuffer.wrap(errorData, 0, 4).putInt(0);
                    ByteBuffer.wrap(errorData, 4, 4).putInt(0);
                    DatagramPacket errorPacket = new DatagramPacket(errorData, errorData.length, clientIP, clientPort);
                    serverSocket.send(errorPacket);
                }
            }
        } catch (Exception e) {
            log("Server Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void log(String message) {
        String timestamp = dateFormat.format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }

    private static void updateStatusViewer(int currentChunk, int totalChunks) {
        int percent = (int) ((currentChunk * 100.0f) / totalChunks);
        int barLength = 50;
        int progress = (int) ((currentChunk * (float) barLength) / totalChunks);
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                bar.append("=");
            } else if (i == progress) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ").append(percent).append("% (").append(currentChunk).append("/").append(totalChunks).append(")");
        
        System.out.print("\r" + bar.toString());
    }
}