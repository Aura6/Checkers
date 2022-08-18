package io.github.zilosz.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("home.fxml")).load()));
        stage.setTitle("Checkers");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}