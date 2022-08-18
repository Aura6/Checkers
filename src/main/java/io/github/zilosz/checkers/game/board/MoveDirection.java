package io.github.zilosz.checkers.game.board;

import lombok.Getter;

public enum MoveDirection {
    UP_LEFT(-1, -1),
    UP_RIGHT(-1, 1),
    DOWN_LEFT(1, -1),
    DOWN_RIGHT(1, 1);

    @Getter private final Coordinate change;

    MoveDirection(int rowChange, int columnChange) {
        change = new Coordinate(rowChange, columnChange);
    }
}
