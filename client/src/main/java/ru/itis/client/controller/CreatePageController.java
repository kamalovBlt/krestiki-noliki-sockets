package ru.itis.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import ru.itis.client.Client;
import ru.itis.protocol.Message;
import ru.itis.protocol.MessageHandler;

import java.io.IOException;

public class CreatePageController {

    @FXML
    private Text lobbyId;

    @FXML
    private StackPane rootPane;

    @FXML
    private void initialize() {
        new Thread(() -> {
            MessageHandler.send(new Message("CL C", ""), Client.getInstance().getSocket());
            Message message = MessageHandler.read(Client.getInstance().getSocket());
            if (message != null && message.getType().equals("CL S")) {
                String lobbyId = message.getContent();
                Client.getInstance().setCurrentGameId(Integer.parseInt(lobbyId));
                Client.getInstance().setCurrentGameRole(1);
                Platform.runLater(() -> this.lobbyId.setText(lobbyId));
            }
            new Thread(() -> {
                Message gameStartedMessage = MessageHandler.read(Client.getInstance().getSocket());
                if (gameStartedMessage != null && gameStartedMessage.getType().equals("SG S")) {
                    Platform.runLater(() -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
                        try {
                            Parent load = fxmlLoader.load();
                            rootPane.getChildren().add(load);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }).start();
        }).start();

    }
}
