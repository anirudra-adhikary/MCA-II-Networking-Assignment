import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CommandServer {
    public static void main(String[] args) {
        int port = 5002;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Command Server listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                String clientIp = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                System.out.println("\n--- New Client Connected ---");
                System.out.println("Client IP: " + clientIp);
                System.out.println("Client Port: " + clientPort);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request;
                    boolean keepRunning = true;

                    while (keepRunning && (request = in.readLine()) != null) {
                        request = request.trim();
                        System.out.println("Received from [" + clientIp + ":" + clientPort + "]: " + request);

                        if (request.equalsIgnoreCase("TIME")) {
                            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            out.println(time);
                        } else if (request.equalsIgnoreCase("DATE")) {
                            String date = LocalDate.now().toString();
                            out.println(date);
                        } else if (request.equalsIgnoreCase("HOSTNAME")) {
                            try {
                                String hostname = InetAddress.getLocalHost().getHostName();
                                out.println(hostname);
                            } catch (UnknownHostException e) {
                                out.println("ERROR: Unable to resolve hostname");
                            }
                        } else if (request.toUpperCase().startsWith("ECHO ")) {
                            String message = request.substring(5);
                            out.println(message);
                        } else if (request.equalsIgnoreCase("QUIT")) {
                            out.println("CONNECTION_TERMINATED");
                            keepRunning = false;
                        } else {
                            out.println("ERROR: Invalid command");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Connection dropped by client: " + e.getMessage());
                } finally {
                    System.out.println("Client disconnected. Returning to wait for next client...");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}