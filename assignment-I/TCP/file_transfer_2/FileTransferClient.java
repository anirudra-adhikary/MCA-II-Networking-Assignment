import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileTransferClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;

        try (Socket socket = new Socket(host, port);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter the filename to request from server: ");
            String fileName = scanner.nextLine();
            out.writeUTF(fileName);

            String response = in.readUTF();
            
            if (response.equals("SUCCESS")) {
                long fileSize = in.readLong();
                System.out.println("Downloading file (" + fileSize + " bytes)...");
                
                File saveDir = new File("client_files");
                if (!saveDir.exists()) saveDir.mkdir();
                
                File newFile = new File(saveDir, "downloaded_" + fileName);
                
                try (FileOutputStream fileOut = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;
                    
                    while (totalRead < fileSize && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }
                }
                System.out.println("Success! File saved to: " + newFile.getPath());
            } else {
                System.out.println("\n--- Server Error ---");
                System.out.println(response);
            }

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }
}