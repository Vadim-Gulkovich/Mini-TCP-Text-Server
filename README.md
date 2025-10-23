# Mini TCP Text Server

A simple Java client–server application demonstrating basic TCP socket communication. The server accepts multiple clients and processes text commands over the network.

## Features

- **Configurable Port:** The server can run on any port specified as a command-line argument.  
- **Network Communication:** The client connects to the server using IP and port read from a `config.txt` file.  

### Supported Commands

- `STATS <text>` — returns text statistics (number of lowercase, uppercase, digits, and other characters).  
- `ANAGRAM <text>` — returns a shuffled (anagram) version of the input text.  
- `DROP` — closes the connection with the server.  
- `EXIT` — closes the client locally.  

- **Multithreaded Server:** Handles multiple clients simultaneously.

## How to Run

1. Compile both files:  
   ```bash
   javac TCPServer.java TCPClient.java

2. Run the server:
    ```bash
    java TCPServer 5000
3. Create a file named config.txt: 
    ```bash
    127.0.0.1
    5000
4. Run the client:  
    ```bash
    java TCPClient
5. Type commands such as:
   ```bash
    STATS Hello123!
    ANAGRAM Hello
    DROP
    EXIT

Example Output:
    Server: WELCOME: connected to TCPServer.
    STATS Hello123!
    Server: STATS_RESULT: lower=5 upper=1 digits=3 others=1
    ANAGRAM Hello
    Server: ANAGRAM_RESULT: leHol
