import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConcurrentCommandServer {
    public static void main(String[] args) {
        int port = 5003;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Concurrent Command Server listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
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
                    out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } else if (request.equalsIgnoreCase("DATE")) {
                    out.println(LocalDate.now().toString());
                } else if (request.equalsIgnoreCase("HOSTNAME")) {
                    try {
                        out.println(InetAddress.getLocalHost().getHostName());
                    } catch (UnknownHostException e) {
                        out.println("ERROR: Unable to resolve hostname");
                    }
                } else if (request.toUpperCase().startsWith("ECHO ")) {
                    out.println(request.substring(5));
                } else if (request.equalsIgnoreCase("QUIT")) {
                    out.println("CONNECTION_TERMINATED");
                    keepRunning = false;
                } else {
                    out.println("ERROR: Invalid command");
                }
            }
        } catch (IOException e) {
            System.out.println("Connection dropped by client [" + clientIp + ":" + clientPort + "]");
        } finally {
            System.out.println("Client [" + clientIp + ":" + clientPort + "] disconnected.");
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}