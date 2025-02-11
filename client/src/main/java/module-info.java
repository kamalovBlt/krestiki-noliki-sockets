module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires protocol;
    exports ru.itis.client.controller;
    opens ru.itis.client.controller;
    exports ru.itis.client;
    opens ru.itis.client;
}