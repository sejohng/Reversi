/*
Shijun Jiang
CSCI 4311 Socket Programming (Assignment 2)
Spring 2024 
Reversi-Server
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReversiServer {
    private static final int PORT = 6666;
    private ServerSocket serverSocket;
    private final ReversiGame game = new ReversiGame();
    private final Map<String, PrintWriter> players = new HashMap<>();
    private final Map<String, Character> playerColors = new HashMap<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public ReversiServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Reversi Server is listening on port " + PORT);
    }

    public void start() {
        try {
            while (true) {
                if (players.size() < 2) {
                    Socket socket = serverSocket.accept();
                    pool.execute(new PlayerHandler(socket));
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("Enter your username:");
                name = in.readLine();
                char assignedColor;
                synchronized (players) {
                    players.put(name, out);
                    if (players.size() == 1) {
                        assignedColor = 'B'; // Black for the 1st
                        playerColors.put(name, assignedColor);
                        out.println("Waiting for another player to join...");
                    } else {
                        assignedColor = 'W'; // White for the 2nd player
                        playerColors.put(name, assignedColor);
                        // Notify all players that the game starts
                        broadcast("Both players have joined. Let's start the game!");
                        broadcastInitialBoard();
                        promptNextPlayer(); 
                    }
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    synchronized (game) {
                        if (game.isGameOver()) {
                            // Game is over, broadcast the result
                            String result = game.determineWinner();
                            broadcast(result);
                            players.values().forEach(PrintWriter::close); // Break the loop and end the connection
                        } else {
                            char currentPlayerColor = game.getCurrentPlayer();
                            if (playerColors.get(name).equals(currentPlayerColor)) {
                                boolean validMove = game.performMove(inputLine.toUpperCase(), currentPlayerColor);
                                if (validMove) {
                                    broadcast("Move made by " + name + ": " + inputLine.toUpperCase());
                                    broadcast(game.getBoardState());
                                    if (game.isGameOver()) {
                                        String result = game.determineWinner();
                                        broadcast(result);
                                        break; 
                                    }
                                } else {
                                    out.println("Invalid move. Please try again.");
                                }
                            } else {
                                out.println("It's not your turn, please wait.");
                            }
                        }
                        promptNextPlayer(); 
                    }
                }                
            } catch (IOException e) {
                System.out.println("Exception in PlayerHandler: " + e.getMessage());
            } finally {
                if (name != null) {
                    players.remove(name);
                    playerColors.remove(name);
                    System.out.println(name + " is leaving");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket, what's going on??????");
                }
            }
        }
    }

    private void broadcastInitialBoard() {
        broadcast("Initial board:\n" + game.getBoardState());
    }

    private void broadcast(String message) {
        for (PrintWriter writer : players.values()) {
            writer.println(message);
        }
    }

    private void promptNextPlayer() {
        char nextPlayerColor = game.getCurrentPlayer();
            System.out.println("Prompting next player. Current player is: " + nextPlayerColor); 
            playerColors.forEach((playerName, color) -> {
                PrintWriter writer = players.get(playerName);
                if (color.equals(nextPlayerColor)) {
                    System.out.println("Prompting player " + playerName + " for their move.");
                    writer.println("Your move (" + (color == 'B' ? "Black" : "White") + "), format [A-H][1-8]:");
                } else {
                    System.out.println("Informing player " + playerName + " to wait.");
                    writer.println("Waiting for the other player to make a move...");
            }
        });
    }

    public static void main(String[] args) throws IOException {
        ReversiServer server = new ReversiServer();
        server.start();
    }
}
