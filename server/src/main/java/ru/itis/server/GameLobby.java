package ru.itis.server;

import java.net.Socket;

public class GameLobby {

    private Socket creatorPlayer;
    private Socket joinedPlayer;

    private int currentMovePlayer = 1;
    private final int[][] grid = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
    };

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

    public int getCurrentMovePlayer() {
        return currentMovePlayer;
    }

    public void setCurrentMovePlayer(int currentMovePlayer) {
        this.currentMovePlayer = currentMovePlayer;
    }

    public int[][] getGrid() {
        return grid;
    }

}
