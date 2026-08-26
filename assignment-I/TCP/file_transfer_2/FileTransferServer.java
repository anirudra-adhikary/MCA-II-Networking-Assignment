import java.io.*;
import java.net.*;

public class FileTransferServer {
    public static void main(String[] args) {
        int port = 5001;
        File serverDir = new File("server_files");
        if (!serverDir.exists()) serverDir.mkdir();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("File Transfer Server listening on port " + port + "...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream in = new DataInputStream(socket.getInputStream());
                     DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                    
                    System.out.println("\nClient connected!");
                
                    String fileName = in.readUTF();
                    File requestedFile = new File(serverDir, fileName);
                    
                    if (requestedFile.exists() && requestedFile.isFile()) {
                        out.writeUTF("SUCCESS"); 
                        out.writeLong(requestedFile.length());
                        
                        try (FileInputStream fileIn = new FileInputStream(requestedFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fileIn.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                        }
                        System.out.println("Sent file: '" + fileName + "'");
                    } else {
                        out.writeUTF("ERROR: The requested file '" + fileName + "' does not exist.");
                        System.out.println("Failed request: File '" + fileName + "' not found.");
                    }
                } catch (IOException e) {
                    System.err.println("Client communication error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}