package io.github.zilosz.checkers.game.board;

import io.github.zilosz.checkers.game.board.piece.Piece;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import lombok.Getter;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Optional;
import java.util.Set;

public class Box extends Canvas {

    private static final Color PLAYABLE_COLOR = Color.BLACK;
    private static final Color PLAYABLE_FOCUS_COLOR = Color.GRAY;
    private static final Color USELESS_COLOR = Color.RED;
    private static final double FONT_TO_SIZE_RATIO = 0.8;
    private static final double TEXT_HEIGHT_OFFSET = -5;

    private final Board board;
    @Getter private final Coordinate coordinate;
    private final double size;
    @Getter private Piece piece;
    private boolean isFocused = false;
    private Color backColor;
    private Color markerColor;

    public Box(Board board, Coordinate coordinate, double size) {
        this.board = board;
        this.coordinate = coordinate;
        this.size = size;

        setWidth(size);
        setHeight(size);

        if (coordinate.isPlayable()) {
            updateBackColor(PLAYABLE_COLOR);

            setOnMouseEntered(this::onMouseEnter);
            setOnMouseClicked(this::onMouseClick);
            setOnMouseExited(this::onMouseExit);

        } else {
            updateBackColor(USELESS_COLOR);
        }

        getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
        getGraphicsContext2D().setTextBaseline(VPos.CENTER);
    }

    public void updateMarkerColor(Color markerColor) {
        this.markerColor = markerColor;
        draw();
    }

    private void updateBackColor(Color backColor) {
        this.backColor = backColor;
        draw();
    }

    private void draw() {
        getGraphicsContext2D().setFill(backColor);
        getGraphicsContext2D().fillRect(0, 0, size, size);

        if (piece != null) {
            piece.draw(this);
        }

        if (markerColor != null) {
            drawText("◎", markerColor);
        }
    }

    public void drawText(String text, Color color) {
        getGraphicsContext2D().setFont(new Font(size * FONT_TO_SIZE_RATIO));
        getGraphicsContext2D().setStroke(color);
        getGraphicsContext2D().strokeText(text, size / 2, size / 2 + TEXT_HEIGHT_OFFSET);
    }

    public boolean hasPlayablePiece() {
        return piece != null && piece.getColor().equals(board.getTurnColor());
    }

    private void highlight() {
        isFocused = true;
        updateBackColor(PLAYABLE_FOCUS_COLOR);
    }

    private void unhighlight() {
        isFocused = false;
        updateBackColor(PLAYABLE_COLOR);
    }

    private void highlightIfIsDestination(Box source) {

        if (board.canMove(source, this)) {
            highlight();
        }
    }

    private void onMouseEnter(MouseEvent event) {
        Optional<Box> jumpSource = Optional.ofNullable(board.getJumpSource());

        if (piece == null) {
            jumpSource.ifPresent(this::highlightIfIsDestination);

        } else if (jumpSource.isEmpty() && hasPlayablePiece()) {
            highlight();
        }
    }

    private void onMouseClick(MouseEvent event) {

        if (isFocused) {

            if (piece == null) {
                board.getJumpSource().transferPiece(this);
                unhighlight();

            } else {

                if (this == board.getJumpSource()) {
                    board.setJumpSource(null);
                    updateBackColor(PLAYABLE_COLOR);

                } else {
                    board.setJumpSource(this);
                    updateBackColor(PLAYABLE_FOCUS_COLOR);
                }
            }
        }
    }

    private void onMouseExit(MouseEvent event) {

        if (isFocused && this != board.getJumpSource()) {
            unhighlight();
        }
    }

    public void givePiece(Piece piece) {
        this.piece = piece;
        draw();
    }

    private void destroyPiece() {
        piece = null;
        draw();
    }

    private void transferPiece(Box destination) {
        destination.givePiece(piece);

        if (board.canBoxBecomeKing(destination)) {
            piece.makeKing(destination);
        }

        destroyPiece();
        unhighlight();

        board.setJumpSource(null);

        if (destination.isJumpDestination()) {
            board.getBoxJumpedOver(this, destination).destroyPiece();

            if (board.checkGame()) {
                return;
            }
        }

        if (!destination.isJumpDestination() || destination.getPossibleMoves().getValue().isEmpty()) {
            board.updateTurn(board.getOppositeColor());

        } else if (destination.isJumpDestination()) {
            board.resetMarkers();
            board.registerMoves();
        }
    }

    private boolean isJumpDestination() {
        return markerColor != null;
    }

    public Pair<Set<Box>, Hashtable<Box, Box>> getPossibleMoves() {
        Set<Box> steps = new HashSet<>();
        Hashtable<Box, Box> jumps = new Hashtable<>();

        for (MoveDirection direction : piece.getMoveDirections()) {
            Coordinate oneAwayCoord = coordinate.add(direction.getChange());

            if (board.isCoordinateValid(oneAwayCoord)) {
                Box oneAwayBox = board.getBoxAtCoordinate(oneAwayCoord);

                if (oneAwayBox.getPiece() == null) {
                    steps.add(oneAwayBox);

                } else if (oneAwayBox.getPiece().getColor() != piece.getColor()) {
                    Coordinate twoAwayCoord = oneAwayCoord.add(direction.getChange());

                    if (board.isCoordinateValid(twoAwayCoord)) {
                        Box twoAwayBox = board.getBoxAtCoordinate(twoAwayCoord);

                        if (twoAwayBox.getPiece() == null) {
                            jumps.put(twoAwayBox, oneAwayBox);
                        }
                    }
                }
            }
        }

        if (!jumps.isEmpty()) {
            steps.clear();
        }

        return new Pair<>(steps, jumps);
    }
}
