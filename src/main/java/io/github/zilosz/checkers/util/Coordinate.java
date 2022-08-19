package io.github.zilosz.checkers.util;

public record Coordinate(int row, int column) {

    public boolean isPlayable() {
        return MathUtils.isEven(row) == MathUtils.isEven(column);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(row + other.row(), column + other.column());
    }
}
