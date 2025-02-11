package ru.itis.client;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private static volatile Client instance;

    private final static String HOST = "localhost";
    private final static int PORT = 22222;
    private int currentGameId;
    private int currentGameRole;

    private final Socket socket;

    public Socket getSocket() {
        return socket;
    }

    private Client () {
        try {

            socket = new Socket(HOST, PORT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Client getInstance () {
        if (instance == null) {
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client();
                }
            }
        }
        return instance;
    }

    public int getCurrentGameId() {
        return currentGameId;
    }

    public int getCurrentGameRole() {
        return currentGameRole;
    }

    public void setCurrentGameRole(int currentGameRole) {
        this.currentGameRole = currentGameRole;
    }

    public void setCurrentGameId(int currentGameId) {
        this.currentGameId = currentGameId;
    }
}
