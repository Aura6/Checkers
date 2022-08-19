package io.github.zilosz.checkers.controllers;

import io.github.zilosz.checkers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final double PIECES_TO_GRID_SIZE_RATIO = 0.1875;
    private static final int PIECE_MAX_THRESHOLD = 20;

    @FXML private Slider columnSlider;
    @FXML private Slider rowSlider;
    @FXML private Slider pieceSlider;
    @FXML private Label columnLabel;
    @FXML private Label rowLabel;
    @FXML private Label pieceLabel;
    @FXML private AnchorPane homePane;
    @FXML private ToggleGroup forceJumpGroup;

    @FXML
    void startGame() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        int rows = (int) rowSlider.getValue();
        int columns = (int) columnSlider.getValue();
        int pieceCount = (int) pieceSlider.getValue();
        controller.createBoard(rows, columns, pieceCount, forceJumpGroup.getToggles().get(0).isSelected());
        homePane.getScene().setRoot(root);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnSlider.valueProperty().addListener((obs, old, val) -> updateSizeSlider(columnLabel, val, rowSlider));
        rowSlider.valueProperty().addListener((obs, old, val) -> updateSizeSlider(rowLabel, val, columnSlider));
        pieceSlider.valueProperty().addListener(((obs, old, val) -> updateSliderLabel(pieceLabel, val)));
    }

    private void updateSliderLabel(Label label, Number value) {
        label.setText(String.valueOf(value.intValue()));
    }

    private void updateSizeSlider(Label sizeLabel, Number changedNumber, Slider otherSizeSlider) {
        double changedSize = changedNumber.doubleValue();
        double otherSize = otherSizeSlider.valueProperty().doubleValue();
        double pieceMax = Math.floor(PIECES_TO_GRID_SIZE_RATIO * changedSize * otherSize);
        pieceSlider.setMax(pieceMax);

        int majorTick = (int) (pieceMax / PIECE_MAX_THRESHOLD + 1);
        int minorTick = majorTick - 1;
        pieceSlider.setMajorTickUnit(majorTick);
        pieceSlider.setMinorTickCount(minorTick);

        updateSliderLabel(sizeLabel, changedNumber);
        updateSliderLabel(pieceLabel, pieceSlider.getValue());
    }
}
