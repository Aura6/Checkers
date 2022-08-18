package io.github.zilosz.checkers.game.board.piece;

import io.github.zilosz.checkers.game.board.Box;
import io.github.zilosz.checkers.game.board.MoveDirection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Piece {

    private static final double SIZE_RATIO = 0.85;

    @Getter private final TeamColor color;
    @Getter private boolean isKing = false;
    private List<MoveDirection> moveDirections = new ArrayList<>();

    public Piece(TeamColor color, MoveDirection... directions) {
        this.color = color;
        moveDirections.addAll(Arrays.stream(directions).toList());
    }

    public void draw(Box box) {
        double min = (1 - SIZE_RATIO) / 2 * box.getWidth();
        double max = SIZE_RATIO * box.getWidth();
        box.getGraphicsContext2D().setFill(color.piece());
        box.getGraphicsContext2D().fillOval(min, min, max, max);

        if (isKing) {
            box.getGraphicsContext2D().setStroke(color.king());
            box.drawText("♛", color.king());
        }
    }

    public void drawKing(Box box) {
        box.drawText("♛", color.king());
        box.getGraphicsContext2D().setStroke(color.king());
    }

    public void makeKing(Box box) {
        isKing = true;
        moveDirections = Arrays.stream(MoveDirection.values()).toList();
        drawKing(box);
    }

    public List<MoveDirection> getMoveDirections() {
        return new ArrayList<>(moveDirections);
    }
}
