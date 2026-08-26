import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CommandClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5002;

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server. Available commands: TIME, DATE, HOSTNAME, ECHO <msg>, QUIT");

            while (true) {
                System.out.print("\nEnter command: ");
                String command = scanner.nextLine();

                out.println(command);

                String response = in.readLine();
                
                if (response == null) {
                    System.out.println("Server closed the connection unexpectedly.");
                    break;
                }

                System.out.println("Server Response: " + response);

                if (command.equalsIgnoreCase("QUIT") && response.equals("CONNECTION_TERMINATED")) {
                    System.out.println("Disconnecting from server...");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}