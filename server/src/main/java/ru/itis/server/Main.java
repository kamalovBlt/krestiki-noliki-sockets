package ru.itis.server;

public class Main {
    public static void main(String[] args) {
        new Thread(new WaitClient(22222)).start();
    }
}
