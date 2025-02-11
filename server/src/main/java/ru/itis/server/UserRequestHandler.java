package ru.itis.server;

import ru.itis.protocol.Message;
import ru.itis.protocol.MessageHandler;

import java.net.Socket;

public class UserRequestHandler implements Runnable {

    private final Socket socket;

    public UserRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Message message;
        while ((message = MessageHandler.read(socket)) != null) {
            if (message.getType().equals("CL C")) {
                GameLobby gameLobby = new GameLobby();
                gameLobby.setCreatorPlayer(socket);
                int lobbyId = GameLobbies.getInstance().add(gameLobby);
                MessageHandler.send(new Message("CL S", String.valueOf(lobbyId)), socket);
            }
            if (message.getType().equals("JL C")) {
                int lobbyId = Integer.parseInt(message.getContent());
                GameLobby gameLobby = GameLobbies.getInstance().get(lobbyId);
                if (gameLobby != null && gameLobby.getCreatorPlayer() != null && gameLobby.getJoinedPlayer() == null) {
                    gameLobby.setJoinedPlayer(socket);
                    MessageHandler.send(new Message("SG S", ""), socket);
                    MessageHandler.send(new Message("SG S", ""), gameLobby.getCreatorPlayer());
                } else {
                    MessageHandler.send(new Message("ER S", "Ошибка при входе в лобби"), socket);
                }
            }
            if (message.getType().equals("M  C")) {
                String[] parts = message.getContent().split(" ");
                int lobbyId = Integer.parseInt(parts[0]);
                int playerId = Integer.parseInt(parts[1]);
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                GameLobby gameLobby = GameLobbies.getInstance().get(lobbyId);
                int[][] grid = gameLobby.getGrid();

                if (playerId == gameLobby.getCurrentMovePlayer() && grid[x][y] == 0) {
                    grid[x][y] = (playerId == 1) ? 1 : 2;

                    String gridString = convertGridToString(grid);
                    Message updateMessage = new Message("NB S", gridString);

                    MessageHandler.send(updateMessage, gameLobby.getCreatorPlayer());
                    MessageHandler.send(updateMessage, gameLobby.getJoinedPlayer());

                    String gameResult = checkGameOver(grid);
                    if (gameResult != null) {
                        Message endGameMessage = new Message("EG S", gameResult);
                        MessageHandler.send(endGameMessage, gameLobby.getCreatorPlayer());
                        MessageHandler.send(endGameMessage, gameLobby.getJoinedPlayer());
                        GameLobbies.getInstance().remove(lobbyId);
                    } else {
                        gameLobby.setCurrentMovePlayer(playerId == 1 ? 2 : 1);
                    }
                }
            }

        }
    }
    private String convertGridToString(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : grid) {
            for (int cell : row) {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    private String checkGameOver(int[][] grid) {

        for (int i = 0; i < 3; i++) {
            if (grid[i][0] != 0 && grid[i][0] == grid[i][1] && grid[i][0] == grid[i][2]) {
                return "WIN " + grid[i][0];
            }
        }

        for (int j = 0; j < 3; j++) {
            if (grid[0][j] != 0 && grid[0][j] == grid[1][j] && grid[0][j] == grid[2][j]) {
                return "WIN " + grid[0][j];
            }
        }

        if (grid[0][0] != 0 && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
            return "WIN " + grid[0][0];
        }
        if (grid[0][2] != 0 && grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0]) {
            return "WIN " + grid[0][2];
        }

        boolean isDraw = true;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 0) {
                    isDraw = false;
                    break;
                }
            }
        }
        return isDraw ? "DRAW" : null;
    }

}
