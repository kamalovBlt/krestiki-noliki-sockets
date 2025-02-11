package ru.itis.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ru.itis.client.Client;
import ru.itis.client.util.Coordinate;
import ru.itis.client.util.FieldValue;
import ru.itis.protocol.Message;
import ru.itis.protocol.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    @FXML
    private StackPane rootPane;

    private Map<Coordinate, FieldValue> fieldValueMap;

    @FXML
    public void initialize() {
        fieldValueMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fieldValueMap.put(new Coordinate(i, j), FieldValue.NULL);
            }
        }
        render();
        new Thread(new WaitServerAnswer()).start();
    }

    private void render() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(300, 300);

        for (int i = 0; i < 3; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(100));
            grid.getRowConstraints().add(new RowConstraints(100));
        }

        for (Map.Entry<Coordinate, FieldValue> entry : fieldValueMap.entrySet()) {
            int x = entry.getKey().getX();
            int y = entry.getKey().getY();
            FieldValue value = entry.getValue();

            StackPane cell = new StackPane();
            cell.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

            Button button = createCellButton(x, y, value);
            cell.getChildren().add(button);

            grid.add(cell, x, y);
        }

        rootPane.getChildren().clear();
        rootPane.getChildren().add(grid);
        StackPane.setAlignment(grid, Pos.CENTER);
    }

    private Button createCellButton(int x, int y, FieldValue value) {
        Button button = new Button();
        button.setStyle("-fx-font-size: 24; -fx-background-color: transparent;");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        switch (value) {
            case CROSS -> button.setText("X");
            case ZERO -> button.setText("O");
            case NULL -> button.setText("");
        }

        button.setOnAction(event -> handleCellClick(x, y));
        return button;
    }

    private void handleCellClick(int x, int y) {
        Coordinate coord = new Coordinate(x, y);
        if (fieldValueMap.get(coord) == FieldValue.NULL) {
            MessageHandler.send(new Message(
                    "M  C",
                    Client.getInstance().getCurrentGameId() + " "
                            + Client.getInstance().getCurrentGameRole() + " "
                            + x + " " + y
            ), Client.getInstance().getSocket());
        }
    }

    private class WaitServerAnswer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Message message = MessageHandler.read(Client.getInstance().getSocket());
                    if (message != null && message.getType().equals("NB S")) {
                        String grid = message.getContent();
                        processGridString(grid);
                    }
                    if (message != null && message.getType().equals("EG S")) {
                        String content = message.getContent();
                        showGameResult(content);
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void processGridString(String grid) {
            if (grid.length() != 9) {
                System.err.println("Invalid grid length: " + grid.length());
                return;
            }

            for (int i = 0; i < grid.length(); i++) {
                int x = i / 3;
                int y = i % 3;
                char c = grid.charAt(i);

                FieldValue value;
                switch (c) {
                    case '0' -> value = FieldValue.NULL;
                    case '1' -> value = FieldValue.CROSS;
                    case '2' -> value = FieldValue.ZERO;
                    default -> throw new IllegalArgumentException("Invalid grid value: " + c);
                }

                fieldValueMap.put(new Coordinate(x, y), value);
            }

            Platform.runLater(GameController.this::render);
        }
    }

    private void showGameResult(String result) {
        Platform.runLater(() -> {

            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.9);");
            resultLabel.setAlignment(Pos.CENTER);
            resultLabel.setPrefSize(300, 300);

            switch (result) {
                case "DRAW" -> {
                    resultLabel.setText("Ничья!");
                    resultLabel.setTextFill(Color.DARKBLUE);
                }
                case "WIN 1", "WIN 2" -> {
                    int winner = Integer.parseInt(result.split(" ")[1]);
                    if (winner == Client.getInstance().getCurrentGameRole()) {
                        resultLabel.setText("Вы победили!");
                        resultLabel.setTextFill(Color.GREEN);
                    } else {
                        resultLabel.setText("Вы проиграли!");
                        resultLabel.setTextFill(Color.RED);
                    }
                }
            }

            rootPane.getChildren().add(resultLabel);

            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> {
                Platform.runLater(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
                    try {
                        Parent load = fxmlLoader.load();
                        rootPane.getChildren().add(load);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            delay.play();
        });
    }
}