// Zadania: 1, 2, 4, 5

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TCPServer {

    public static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        // ---------- Zadanie ----------
        int port = DEFAULT_PORT;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port argument, using default " + DEFAULT_PORT);
            }
        }

        String ipInfo;
        try {
            InetAddress localAddr = InetAddress.getLocalHost();
            ipInfo = localAddr.getHostAddress();
        } catch (UnknownHostException e) {
            ipInfo = "UnknownHost";
        }

        System.out.println("Server starting on IP: " + ipInfo + " Port: " + port);

        // Zadanie 5
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening: " + serverSocket);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted: " + clientSocket.getRemoteSocketAddress());

                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    // ---------- Zadanie 4 ----------
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final Random random = new Random();

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)
            ) {
                out.println("WELCOME: connected to TCPServer. Available commands: STATS <text> | ANAGRAM <text> | DROP");
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    System.out.println("From " + socket.getRemoteSocketAddress() + " -> " + line);

                    String command;
                    String argument = "";
                    int spaceIdx = line.indexOf(' ');
                    if (spaceIdx >= 0) {
                        command = line.substring(0, spaceIdx).toUpperCase(Locale.ROOT);
                        argument = line.substring(spaceIdx + 1);
                    } else {
                        command = line.toUpperCase(Locale.ROOT);
                    }

                    switch (command) {
                        case "STATS":

                            String stats = statsForText(argument);
                            out.println("STATS_RESULT: " + stats);
                            break;

                        case "ANAGRAM":

                            String anagram = anagramOf(argument);
                            out.println("ANAGRAM_RESULT: " + anagram);
                            break;

                        case "DROP":
                            out.println("BYE: closing connection");
                            System.out.println("Client requested DROP: " + socket.getRemoteSocketAddress());
                            socket.close();
                            return;

                        default:
                            out.println("ERROR: invalid command. Use STATS <text> | ANAGRAM <text> | DROP");
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed / exception for " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
            } finally {
                try {
                    if (!socket.isClosed()) socket.close();
                } catch (IOException ignored) {}
                System.out.println("Closed connection: " + socket.getRemoteSocketAddress());
            }
        }

        private String statsForText(String text) {
            if (text == null) text = "";
            int lower = 0, upper = 0, digits = 0, others = 0;
            for (char c : text.toCharArray()) {
                if (Character.isLowerCase(c)) lower++;
                else if (Character.isUpperCase(c)) upper++;
                else if (Character.isDigit(c)) digits++;
                else others++;
            }
            return String.format("lower=%d upper=%d digits=%d others=%d", lower, upper, digits, others);
        }

        private String anagramOf(String text) {
            if (text == null) return "";
            List<Character> list = new ArrayList<>();
            for (char c : text.toCharArray()) list.add(c);
            Collections.shuffle(list, random);
            StringBuilder sb = new StringBuilder();
            for (char c : list) sb.append(c);
            return sb.toString();
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