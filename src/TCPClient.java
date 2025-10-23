// Zadania 1, 3, 5
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
    public static void main(String[] args) {
        // ---------- Zadanie 3 ----------

        String host = null;
        int port = -1;

        File cfg = new File("config.txt");
        if (!cfg.exists()) {
            System.err.println("config.txt not found in working directory. Please create file with two lines: host and port.");
            System.err.println("Example:\n127.0.0.1\n5000");
            return;
        }

        try (BufferedReader cfgReader = new BufferedReader(new FileReader(cfg))) {
            host = cfgReader.readLine();
            String portLine = cfgReader.readLine();
            if (host != null) host = host.trim();
            if (portLine != null) portLine = portLine.trim();
            if (host == null || portLine == null) {
                System.err.println("config.txt is malformed. Expected two lines: host and port.");
                return;
            }
            port = Integer.parseInt(portLine);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading config.txt: " + e.getMessage());
            return;
        }

        System.out.println("Connecting to " + host + ":" + port);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {

            String serverLine;
            if ((serverLine = in.readLine()) != null) {
                System.out.println("Server: " + serverLine);
            }

            System.out.println("Type commands (STATS <text> | ANAGRAM <text> | DROP). Type EXIT to quit client.");

            while (true) {
                System.out.print("> ");
                String userLine = userIn.readLine();
                if (userLine == null) break;
                userLine = userLine.trim();
                if (userLine.equalsIgnoreCase("EXIT")) {
                    System.out.println("Client exiting (local).");
                    break;
                }
                if (userLine.isEmpty()) continue;

                out.println(userLine);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                System.out.println("Server: " + response);

                if (response.startsWith("BYE")) {
                    System.out.println("Server requested to close the connection.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

//STATS Hello123! → the server will return text statistics.
//
//ANAGRAM Hello → the server will return a shuffled (anagram) version of the text.
//
//DROP → the server will close the connection.
//
//EXIT → the client will close locally.