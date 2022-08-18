package io.github.zilosz.checkers.game.board;

import io.github.zilosz.checkers.game.board.piece.Piece;
import io.github.zilosz.checkers.game.board.piece.TeamColor;
import io.github.zilosz.checkers.util.MathUtils;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Board extends GridPane {

    private static final TeamColor YOUR_COLOR = new TeamColor(Color.WHITE, Color.RED);
    private static final TeamColor ENEMY_COLOR = new TeamColor(Color.BLUE, Color.WHITE);

    private static final double TASKBAR_HEIGHT = 25;

    private final HBox turnPane;
    private final Canvas turnPieceCanvas;
    private final Label turnLabel;
    private final int rows;
    private final int columns;
    private final int pieceCount;
    private final double boxSize;
    @Getter private TeamColor turnColor;
    @Getter @Setter private Box jumpSource;
    private Box[][] grid;
    private List<Box> playableBoxes;
    private HashMap<TeamColor, Integer> piecesLeft;
    private final Hashtable<Box, Set<Box>> steps = new Hashtable<>();
    private final Hashtable<Box, Map<Box, Box>> jumps = new Hashtable<>();

    public Board(HBox turnPane, Canvas turnPieceCanvas, Label turnLabel, int rows, int columns, int pieceCount) {
        this.turnPane = turnPane;
        this.turnPieceCanvas = turnPieceCanvas;
        this.turnLabel = turnLabel;
        this.rows = rows;
        this.columns = columns;
        this.pieceCount = pieceCount;

        setAlignment(Pos.CENTER);
        setGridLinesVisible(true);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double height = bounds.getHeight() - turnPane.getPrefHeight() - TASKBAR_HEIGHT;
        boxSize = Math.min(height / rows, bounds.getWidth() / columns);
    }

    private Stream<Box> getMovableBoxStream() {
        return playableBoxes.stream().filter(Box::hasPlayablePiece);
    }

    private void registerBoxMoves(Box box) {
        Pair<Set<Box>, Hashtable<Box, Box>> moves = box.getPossibleMoves();
        steps.put(box, moves.getKey());
        jumps.put(box, moves.getValue());
        moves.getValue().keySet().forEach(dest -> dest.updateMarkerColor(turnColor.piece()));
    }

    private void placePieces(List<Box> playable, TeamColor color, MoveDirection... directions) {
        playable.stream().limit(pieceCount).forEach(box -> box.givePiece(new Piece(color, directions)));
    }

    public void registerMoves() {
        getMovableBoxStream().forEach(this::registerBoxMoves);
    }

    public void updateTurn(TeamColor turnColor) {
        this.turnColor = turnColor;
        turnPieceCanvas.getGraphicsContext2D().setFill(turnColor.piece());
        turnPieceCanvas.getGraphicsContext2D().fillOval(0, 0, turnPieceCanvas.getWidth(), turnPieceCanvas.getHeight());
        resetMarkers();
        registerMoves();
    }

    public TeamColor getOppositeColor() {
        return turnColor == YOUR_COLOR ? ENEMY_COLOR : YOUR_COLOR;
    }

    public void resetMarkers() {
        jumps.values().stream().map(Map::keySet).flatMap(Collection::stream).forEach(box -> box.updateMarkerColor(null));
        steps.clear();
        jumps.clear();
    }

    public void setup() {
        playableBoxes = new ArrayList<>();
        grid = new Box[rows][columns];

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < columns; c++) {
                Coordinate coordinate = new Coordinate(r, c);
                Box box = new Box(this, coordinate, boxSize);
                add(box, c, r);
                grid[r][c] = box;

                if (coordinate.isPlayable()) {
                    playableBoxes.add(box);
                }
            }
        }

        placePieces(playableBoxes, ENEMY_COLOR, MoveDirection.DOWN_LEFT, MoveDirection.DOWN_RIGHT);

        List<Box> boxes = new ArrayList<>(playableBoxes);
        Collections.reverse(boxes);
        placePieces(boxes, YOUR_COLOR, MoveDirection.UP_LEFT, MoveDirection.UP_RIGHT);

        piecesLeft = new HashMap<>();
        piecesLeft.put(YOUR_COLOR, pieceCount);
        piecesLeft.put(ENEMY_COLOR, pieceCount);

        updateTurn(MathUtils.testProbability(0.5) ? YOUR_COLOR : ENEMY_COLOR);

        jumpSource = null;
    }

    public Box getBoxAtCoordinate(Coordinate coord) {
        return grid[coord.row()][coord.column()];
    }

    public boolean isCoordinateValid(Coordinate coord) {
        return MathUtils.isBetween(coord.row(), 0, rows - 1) && MathUtils.isBetween(coord.column(), 0, columns - 1);
    }

    public boolean canMove(Box from, Box to) {
        return jumps.get(from).containsKey(to) || jumps.values().stream().allMatch(Map::isEmpty) && steps.get(from).contains(to);
    }

    public Box getBoxJumpedOver(Box from, Box to) {
        return jumps.get(from).get(to);
    }

    public boolean canBoxBecomeKing(Box box) {
        return box.getPiece().getColor() == YOUR_COLOR && box.getCoordinate().row() == 0 || box.getCoordinate().row() == rows - 1;
    }

    public boolean checkGame() {
        TeamColor key = getOppositeColor();
        piecesLeft.put(key, piecesLeft.get(key) - 1);
        boolean valid = piecesLeft.get(key) > 0;

        if (valid) return false;

        turnPane.getChildren().remove(turnPieceCanvas);
        turnLabel.setTextFill(turnColor.piece());
        turnLabel.setText(turnColor == YOUR_COLOR ? "You win!" : "You lost!");
        turnColor = null;

        return false;
    }
}
