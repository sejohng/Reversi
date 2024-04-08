/*
Shijun Jiang
CSCI 4311 Socket Programming (Assignment 2)
Spring 2024 
ReversiLogic
*/

public class ReversiGame {
    private char[][] board;
    public static final int SIZE = 8;
    private char currentPlayer;
    private int scoreBlack = 2; 
    private int scoreWhite = 2; 

    public ReversiGame() {
        board = new char[SIZE][SIZE];
        initBoard();
        currentPlayer = 'B';
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = ' ';
            }
        }
        board[SIZE/2 - 1][SIZE/2 - 1] = 'W';
        board[SIZE/2][SIZE/2] = 'W';
        board[SIZE/2 - 1][SIZE/2] = 'B';
        board[SIZE/2][SIZE/2 - 1] = 'B';
        
    }

    public boolean isValidMove(int row, int column, char color) {
        if (board[row][column] != ' ') {
//          System.out.println("Move invalid: Target position is not empty."); // Debug
            return false;
        }
        
        char opponentColor = (color == 'B') ? 'W' : 'B';
        boolean validMove = false;
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        
        for (int[] d : directions) {
            int x = row + d[0];
            int y = column + d[1];
            boolean foundOpponent = false;
            
            while (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == opponentColor) {
                foundOpponent = true;
                x += d[0];
                y += d[1];
            }
            
            if (foundOpponent && x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == color) {
                validMove = true;
                break;
            } else if (foundOpponent) { // Adding debug information if a direction has opponent pieces but not valid
                System.out.println("Direction (" + d[0] + ", " + d[1] + ") has opponent's pieces but not valid.");
            }
        }
        
        if (!validMove) {
            System.out.println("Move (" + (char)('A' + column) + (row + 1) + ") by " + color + " is invalid."); // Debug info
        }
        
        return validMove;
    }
    
    
    public void makeMove(int row, int column, char color) {
        if (!isValidMove(row, column, color)) return; 
        
        flipDiscs(row, column, color);
        board[row][column] = color;
        updateScores();
        switchPlayer();
    }
    
    private void flipDiscs(int row, int column, char color) {
        char opponentColor = (color == 'B') ? 'W' : 'B';
                int[] directions = {-1, 0, 1};
        
                for (int dRow : directions) {
                    for (int dCol : directions) {
                        if (dRow == 0 && dCol == 0) continue;
                        
                        int currentRow = row + dRow, currentCol = column + dCol;
                        boolean foundOpponent = false;
                        
                        while (currentRow >= 0 && currentRow < SIZE && currentCol >= 0 && currentCol < SIZE && board[currentRow][currentCol] == opponentColor) {
                            foundOpponent = true;
                            currentRow += dRow;
                            currentCol += dCol;
                        }
                        
                        if (foundOpponent && currentRow >= 0 && currentRow < SIZE && currentCol >= 0 && currentCol < SIZE && board[currentRow][currentCol] == color) {
                            while (!(currentRow == row && currentCol == column)) {
                                board[currentRow][currentCol] = color;
                                currentRow -= dRow;
                                currentCol -= dCol;
                            }
                        }
                    }
                }
            }
    private void updateScores() {
        int black = 0, white = 0;
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        if (board[i][j] == 'B') black++;
                        else if (board[i][j] == 'W') white++;
                    }
                }
                scoreBlack = black;
                scoreWhite = white;
            }

    public boolean isGameOver() {
        //  If the board is full, the game is over.
        boolean isFull = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == ' ') {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        if (isFull) return true;
        
        // Check if there is a valid move
        boolean hasValidMoveBlack = false;
        boolean hasValidMoveWhite = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j, 'B')) {
                    hasValidMoveBlack = true;
                }
                if (isValidMove(i, j, 'W')) {
                    hasValidMoveWhite = true;
                }
                if (hasValidMoveBlack || hasValidMoveWhite) {
                    return false; // As long as one party has a valid move, the game continues
                }
            }
        }
        
        // If none of them move effectively, the game is over
        return true;
    }
    

    public static void main(String[] args) {
        ReversiGame game = new ReversiGame();
//      game.printBoard();
    }
    
    public String getBoardState() {
        StringBuilder sb = new StringBuilder();
        sb.append("    A   B   C   D   E   F   G   H  \n");
        for (int i = 0; i < SIZE; i++) {
            sb.append(" ").append(i + 1).append(" "); 
            for (int j = 0; j < SIZE; j++) {
                sb.append("| ").append(board[i][j]).append(" ");
            }
            sb.append("|"); 
            if (i < SIZE - 1) {
                sb.append("\n   --------------------------------\n");
            }
        }
        sb.append("\n   --------------------------------\n"); 
        return sb.toString();
    }
    
    
    public char getCurrentPlayer() {
        return currentPlayer;
    }
    
    public boolean performMove(String columnRow, char color) {
        if (columnRow.length() < 2) {
            System.out.println("Move input is too short."); // Debug info
            return false; 
        }
        
        int column = columnRow.toUpperCase().charAt(0) - 'A'; 
        int row = Character.getNumericValue(columnRow.charAt(1)) - 1; 
        
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            System.out.println("Move is out of bounds.");
            return false;
        }
        
        if (isValidMove(row, column, color)) {
            makeMove(row, column, color);
            return true; 
        } else {
//          System.out.println("Move (" + columnRow + ") is invalid."); // Debug info
            return false;
        }
    }
    
    
    public void switchPlayer() {
        System.out.println("Before switch, current player is: " + currentPlayer); 
        currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
        System.out.println("After switch, current player is: " + currentPlayer); 
    }
    
    public String determineWinner() {
        if (scoreBlack > scoreWhite) {
            return "Congratulations! Black wins with " + scoreBlack + " to " + scoreWhite + " points. Game is over!";
        } else if (scoreWhite > scoreBlack) {
            return "Congratulations! White wins with " + scoreWhite + " to " + scoreBlack + " points. Game is over!";
        } else {
            return "It's a draw. Both players have " + scoreBlack + " points. Game is over!";
        }
    }
    
    
}
