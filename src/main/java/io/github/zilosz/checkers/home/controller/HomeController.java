package io.github.zilosz.checkers.home.controller;

import io.github.zilosz.checkers.game.controller.GameController;
import io.github.zilosz.checkers.home.LayoutType;
import io.github.zilosz.checkers.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final double PIECES_TO_GRID_SIZE_RATIO = 0.1875;

    @FXML private Slider columnSlider;
    @FXML private Slider rowSlider;
    @FXML private Slider pieceSlider;
    @FXML private Label columnLabel;
    @FXML private Label rowLabel;
    @FXML private Label pieceLabel;
    @FXML private AnchorPane homePane;
    @FXML private ComboBox<LayoutType> layoutChooser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnSlider.valueProperty().addListener((obs, old, val) -> updateSizeSlider(columnLabel, val, rowSlider));
        rowSlider.valueProperty().addListener((obs, old, val) -> updateSizeSlider(rowLabel, val, columnSlider));
        pieceSlider.valueProperty().addListener(((obs, old, val) -> updateSliderLabel(pieceLabel, val)));

        layoutChooser.valueProperty().addListener(((obs, old, val) -> onChangeLayoutType(val)));
        List<LayoutType> layouts = Arrays.stream(LayoutType.values()).toList();
        layoutChooser.setItems(FXCollections.observableList(layouts));
        layoutChooser.setValue(LayoutType.EVEN);
    }

    @FXML
    void startGame() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        int rows = (int) rowSlider.getValue();
        int columns = (int) columnSlider.getValue();
        int pieceCount = (int) pieceSlider.getValue();
        controller.createBoard(rows, columns, pieceCount);
        homePane.getScene().setRoot(root);
    }

    private void updateSizeSlider(Label sizeLabel, Number changedNumber, Slider otherSizeSlider) {
        double changedSize = changedNumber.doubleValue();

        if (layoutChooser.getValue().getValidator().test(changedSize)) {
            double otherSize = otherSizeSlider.valueProperty().doubleValue();
            pieceSlider.setMax(Math.floor(PIECES_TO_GRID_SIZE_RATIO * changedSize * otherSize));
            updateSliderLabel(pieceLabel, pieceSlider.getValue());
            updateSliderLabel(sizeLabel, changedNumber);
        }
    }

    private void updateSliderLabel(Label label, Number value) {
        label.setText(String.valueOf(value.intValue()));
    }

    private void setSizeParameters(LayoutType layoutType, Slider slider, Label label) {
        updateSliderLabel(label, layoutType.getValue());
        slider.setValue(layoutType.getValue());
        slider.setMin(layoutType.getMin());
        slider.setMax(layoutType.getMax());
    }

    private void onChangeLayoutType(LayoutType layoutType) {
        setSizeParameters(layoutType, columnSlider, columnLabel);
        setSizeParameters(layoutType, rowSlider, rowLabel);
    }
}
