package io.github.zilosz.checkers.controllers;

import io.github.zilosz.checkers.Main;
import io.github.zilosz.checkers.board.Board;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class GameController {

    @FXML private Label turnLabel;
    @FXML private VBox container;
    @FXML private HBox turnPane;
    @FXML private Canvas turnPieceCanvas;
    private Board board;

    @FXML
    void goHome() throws IOException {
        container.getScene().setRoot(new FXMLLoader(Main.class.getResource("home.fxml")).load());
    }

    @FXML
    void reset() {
        turnLabel.setTextFill(Color.BLACK);
        turnLabel.setText("Turn:");
        board.getChildren().clear();
        board.setup();
    }

    public void createBoard(int rows, int columns, int pieceCount, boolean forceJump) {
        board = new Board(turnPane, turnPieceCanvas, turnLabel, rows, columns, pieceCount, forceJump);
        board.setup();
        container.getChildren().add(board);
    }
}
