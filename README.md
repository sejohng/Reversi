# Reversi
This project implements a server-client version of Reversi (also known as “Othello”), allowing two players to play against each other over the Internet. Reversi is a strategy board game in which players flip over their opponent’s pieces to occupy most positions on the board. Game logic is concentrated on the server side, including initializing the board, verifying legal moves, performing piece flips, and updating game state. The client is responsible for collecting the player’s operational input and displaying the current game status. The server controls the game process, such as taking turns, determining game end conditions, and announcing the winner.
