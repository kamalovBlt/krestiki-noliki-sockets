package ru.itis.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameLobbies {

    private volatile static GameLobbies instance;
    private final Map<Integer, GameLobby> lobbies;

    private GameLobbies() {
        this.lobbies = new ConcurrentHashMap<>();
    }

    public static GameLobbies getInstance() {
        if (instance == null) {
            synchronized (GameLobbies.class) {
                if (instance == null) {
                    instance = new GameLobbies();
                }
            }
        }
        return instance;
    }

    public Map<Integer, GameLobby> getLobbies() {
        return lobbies;
    }

    public int add(GameLobby lobby) {
        int id = 1;
        while (lobbies.containsKey(id)) {
            id++;
        }
        lobbies.put(id, lobby);
        return id;
    }

    public GameLobby get(int id) {
        return lobbies.get(id);
    }

    public void remove(int lobbyId) {
        lobbies.remove(lobbyId);
    }
}
