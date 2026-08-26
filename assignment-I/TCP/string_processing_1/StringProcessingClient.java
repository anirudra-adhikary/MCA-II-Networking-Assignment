import java.io.*;
import java.net.*;
import java.util.Scanner;

public class StringProcessingClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(hostname, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter a string to send to the server: ");
            String text = scanner.nextLine();

            // Send the string to the server
            out.println(text);

            // Read and print the server's response
            String response = in.readLine();
            System.out.println("\n--- Results from Server ---");
            System.out.println(response);

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}