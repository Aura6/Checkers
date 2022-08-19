package io.github.zilosz.checkers.util;

import javafx.scene.paint.Color;
import lombok.Getter;

public enum Team {
    TOP(Color.RED, Color.GOLD, MoveDirection.DOWN_LEFT, MoveDirection.DOWN_RIGHT),
    BOTTOM(Color.BLUE, Color.WHITE, MoveDirection.UP_LEFT, MoveDirection.UP_RIGHT);

    @Getter private final Color pieceColor;
    @Getter private final Color kingColor;
    @Getter private final MoveDirection[] allowedDirections;

    Team(Color pieceColor, Color kingColor, MoveDirection... allowedDirections) {
        this.pieceColor = pieceColor;
        this.kingColor = kingColor;
        this.allowedDirections = allowedDirections;
    }
}
