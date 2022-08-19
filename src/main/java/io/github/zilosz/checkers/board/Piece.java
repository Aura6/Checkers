package io.github.zilosz.checkers.board;

import io.github.zilosz.checkers.util.MoveDirection;
import io.github.zilosz.checkers.util.Team;
import lombok.Getter;

public class Piece {

    private static final double SIZE_RATIO = 0.85;

    @Getter private final Team team;
    private boolean isKing = false;

    public Piece(Team team) {
        this.team = team;
    }

    public void draw(Box box) {
        double min = (1 - SIZE_RATIO) / 2 * box.getWidth();
        double max = SIZE_RATIO * box.getWidth();
        box.getGraphicsContext2D().setFill(team.getPieceColor());
        box.getGraphicsContext2D().fillOval(min, min, max, max);

        if (isKing) {
            box.drawText("♛", team.getKingColor());
        }
    }

    public void drawKing(Box box) {
        box.drawText("♛", team.getKingColor());
        box.getGraphicsContext2D().setStroke(team.getKingColor());
    }

    public void makeKing(Box box) {
        isKing = true;
        drawKing(box);

    }

    public MoveDirection[] getMoveDirections() {
        return isKing ? MoveDirection.values() : team.getAllowedDirections();
    }
}
