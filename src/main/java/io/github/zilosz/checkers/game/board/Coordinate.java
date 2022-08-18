package io.github.zilosz.checkers.game.board;

import io.github.zilosz.checkers.util.MathUtils;

public record Coordinate(int row, int column) {

    public boolean isPlayable() {
        return MathUtils.isEven(row) == MathUtils.isEven(column);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(row + other.row(), column + other.column());
    }
}
