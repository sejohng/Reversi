/*
Shijun Jiang
CSCI 4311 Socket Programming (Assignment 2)
Spring 2024 
Reversi-Client
*/

import java.io.*;
import java.net.*;

public class ReversiClient {
    private String hostName;
    private int portNumber;
    private String userName;
    

    public ReversiClient(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void startClient() {
        try (
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String fromServer, fromUser;

            while ((fromServer = in.readLine()) != null) {
                if (fromServer.startsWith("Your move")) {
                    System.out.println(fromServer); 
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        out.println(fromUser);
                    }
                } else {
                    System.out.println("Server: " + fromServer);
                    if (fromServer.equals("Enter your username:")) {
                        fromUser = stdIn.readLine();
                        this.userName = fromUser;
                        out.println(fromUser);
                    } else if (fromServer.startsWith("Invalid move") || fromServer.startsWith("It is not your turn")) {
                    }
                }
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage Example: java ReversiClient localhost 6666");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        ReversiClient client = new ReversiClient(hostName, portNumber);
        client.startClient();
    }
}
