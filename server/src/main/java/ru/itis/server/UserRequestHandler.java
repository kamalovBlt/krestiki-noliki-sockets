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
                if (gameLobby != null) {
                    gameLobby.setJoinedPlayer(socket);
                    MessageHandler.send(new Message("SG S", ""), socket);
                    MessageHandler.send(new Message("SG S", ""), gameLobby.getCreatorPlayer());
                }
                else {
                    MessageHandler.send(new Message("ER S", "Не найдено лобби с таким ID"), socket);
                }
            }
        }
    }

}
