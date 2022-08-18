module io.github.zilosz.checkers {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    exports io.github.zilosz.checkers;
    exports io.github.zilosz.checkers.home;
    opens io.github.zilosz.checkers.home.controller to javafx.fxml;
    opens io.github.zilosz.checkers.game.controller to javafx.fxml;
    exports io.github.zilosz.checkers.game.board;
    exports io.github.zilosz.checkers.game.board.piece;
}