package ru.itis.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;


public class MainPageController {

    @FXML
    private StackPane rootPane;

    @FXML
    public void handleJoinButtonClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/play.fxml"));
        try {
            Parent load = fxmlLoader.load();
            rootPane.getChildren().add(load);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleCreateButtonClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/create.fxml"));
        try {
            Parent load = fxmlLoader.load();
            rootPane.getChildren().add(load);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
