import java.io.*;
import java.net.*;

public class StringProcessingServer {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port + "...");
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String input = in.readLine();
                if (input != null) {
                    System.out.println("Received from client: " + input);

                    int length = input.length();
                    int vowels = 0, consonants = 0, digits = 0;

                    String lowerInput = input.toLowerCase();
                    for (int i = 0; i < length; i++) {
                        char ch = lowerInput.charAt(i);

                        if (ch >= 'a' && ch <= 'z') {
                            if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                                vowels++;
                            } else {
                                consonants++;
                            }
                        } else if (Character.isDigit(ch)) {
                            digits++;
                        }
                    }

                    // Format the result and send it back to the client
                    String response = String.format("Length: %d, Vowels: %d, Consonants: %d, Digits: %d", 
                                                    length, vowels, consonants, digits);
                    out.println(response);
                }
                
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}