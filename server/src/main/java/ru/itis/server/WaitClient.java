package ru.itis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WaitClient implements Runnable {

    private final int port;

    public WaitClient(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new UserRequestHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
