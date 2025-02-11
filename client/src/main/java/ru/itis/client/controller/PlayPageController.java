package ru.itis.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import ru.itis.client.Client;
import ru.itis.protocol.Message;
import ru.itis.protocol.MessageHandler;

import java.io.IOException;

public class PlayPageController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Text error;

    @FXML
    private TextField idField;

    @FXML
    public void handleJoinButtonClick(ActionEvent actionEvent) {
        String text = idField.getText().trim();

        if (text.isEmpty()) {
            error.setText("Введите число");
            return;
        }

        try {
            int id = Integer.parseInt(text);
            MessageHandler.send(new Message("JL C", String.valueOf(id)), Client.getInstance().getSocket());
            new Thread(() -> {
                Message read;
                while ((read = MessageHandler.read(Client.getInstance().getSocket())) != null) {
                    if (read.getType().equals("SG S")) {
                        Client.getInstance().setCurrentGameId(id);
                        Client.getInstance().setCurrentGameRole(2);
                        Platform.runLater(() -> {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/game.fxml"));
                            try {
                                Parent load = fxmlLoader.load();
                                rootPane.getChildren().add(load);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        break;
                    }
                    else {
                        error.setText(read.getContent());
                    }
                }
            }).start();
        } catch (NumberFormatException e) {
            error.setText("Введите число");
        }
    }

}
