package ru.itis.server;

import java.net.Socket;

public class GameLobby {

    private Socket creatorPlayer;
    private Socket joinedPlayer;

    public Socket getCreatorPlayer() {
        return creatorPlayer;
    }

    public Socket getJoinedPlayer() {
        return joinedPlayer;
    }

    public void setCreatorPlayer(Socket creatorPlayer) {
        this.creatorPlayer = creatorPlayer;
    }

    public void setJoinedPlayer(Socket joinedPlayer) {
        this.joinedPlayer = joinedPlayer;
    }

}
