module io.github.zilosz.checkers {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    exports io.github.zilosz.checkers;
    opens io.github.zilosz.checkers.controllers to javafx.fxml;
    exports io.github.zilosz.checkers.util;
    exports io.github.zilosz.checkers.board;
}