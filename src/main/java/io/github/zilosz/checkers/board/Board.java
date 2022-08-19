package io.github.zilosz.checkers.board;

import io.github.zilosz.checkers.util.Coordinate;
import io.github.zilosz.checkers.util.MathUtils;
import io.github.zilosz.checkers.util.Team;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Board extends GridPane {

    private static final double TASKBAR_HEIGHT = 25;

    private final Canvas turnPieceCanvas;
    private final Label turnLabel;
    private final int rows;
    private final int columns;
    private final int pieceCount;
    private final boolean forceJump;
    private final double boxSize;
    @Getter @Setter private Box jumpSource;
    private Box[][] grid;
    private List<Box> playableBoxes;
    private HashMap<Team, Integer> piecesLeft;
    private final Hashtable<Box, Set<Box>> steps = new Hashtable<>();
    private final Hashtable<Box, Map<Box, Box>> jumps = new Hashtable<>();
    @Getter private Team goingTeam;

    public Board(HBox turnPane, Canvas turnPieceCanvas, Label turnLabel, int rows, int columns, int pieceCount, boolean forceJump) {
        this.turnPieceCanvas = turnPieceCanvas;
        this.turnLabel = turnLabel;
        this.rows = rows;
        this.columns = columns;
        this.pieceCount = pieceCount;
        this.forceJump = forceJump;

        setAlignment(Pos.CENTER);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double height = bounds.getHeight() - turnPane.getPrefHeight() - TASKBAR_HEIGHT;
        boxSize = Math.min(height / rows, bounds.getWidth() / columns);
    }

    public Team getOppositeTeam() {
        return goingTeam == Team.TOP ? Team.BOTTOM : Team.TOP;
    }

    private Stream<Box> getMovableBoxStream() {
        return playableBoxes.stream().filter(Box::hasPlayablePiece);
    }

    private void placePieces(Collection<Box> playableWithPiece, Team team) {
        playableWithPiece.stream().limit(pieceCount).forEach(box -> box.givePiece(new Piece(team)));
    }

    private void registerBoxMoves(Box box) {
        Pair<Set<Box>, Hashtable<Box, Box>> moves = box.getPossibleMoves();
        steps.put(box, moves.getKey());
        jumps.put(box, moves.getValue());
        moves.getValue().keySet().forEach(dest -> dest.updateMarkerColor(goingTeam.getPieceColor()));
    }

    public void registerMoves() {
        getMovableBoxStream().forEach(this::registerBoxMoves);
    }

    public void updateTurn(Team goingTeam) {
        this.goingTeam = goingTeam;

        turnPieceCanvas.getGraphicsContext2D().setFill(goingTeam.getPieceColor());
        turnPieceCanvas.getGraphicsContext2D().fillOval(0, 0, turnPieceCanvas.getWidth(), turnPieceCanvas.getHeight());

        resetMarkers();
        registerMoves();
    }

    public void resetMarkers() {
        jumps.values().stream().map(Map::keySet).flatMap(Collection::stream).forEach(box -> box.updateMarkerColor(null));
        steps.clear();
        jumps.clear();
    }

    public void setup() {
        playableBoxes = new ArrayList<>();
        Deque<Box> backwardsPlayableBoxes = new ArrayDeque<>();
        grid = new Box[rows][columns];

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < columns; c++) {
                Coordinate coordinate = new Coordinate(r, c);
                Box box = new Box(this, coordinate, boxSize);
                add(box, c, r);
                grid[r][c] = box;

                if (coordinate.isPlayable()) {
                    playableBoxes.add(box);
                    backwardsPlayableBoxes.addFirst(box);
                }
            }
        }

        placePieces(playableBoxes, Team.TOP);
        placePieces(backwardsPlayableBoxes, Team.BOTTOM);

        piecesLeft = new HashMap<>();
        piecesLeft.put(Team.TOP, pieceCount);
        piecesLeft.put(Team.BOTTOM, pieceCount);

        updateTurn(MathUtils.simulateProbability(0.5) ? Team.TOP : Team.BOTTOM);

        jumpSource = null;
    }

    public Box getBoxAtCoordinate(Coordinate coord) {
        return grid[coord.row()][coord.column()];
    }

    public boolean isCoordinateValid(Coordinate coord) {
        return MathUtils.isBetween(coord.row(), 0, rows - 1) && MathUtils.isBetween(coord.column(), 0, columns - 1);
    }

    public boolean canMove(Box from, Box to) {
        return jumps.get(from).containsKey(to) || (!forceJump || jumps.values().stream().allMatch(Map::isEmpty)) && steps.get(from).contains(to);
    }

    public Box getBoxJumpedOver(Box from, Box to) {
        return jumps.get(from).get(to);
    }

    public boolean canBoxBecomeKing(Box box) {
        return box.getPiece().getTeam() == Team.TOP && box.getCoordinate().row() == 0 || box.getCoordinate().row() == rows - 1;
    }

    public boolean checkGame() {
        Team opposite = getOppositeTeam();
        piecesLeft.put(opposite, piecesLeft.get(opposite) - 1);

        if (piecesLeft.get(opposite) > 0) return false;

        turnLabel.setTextFill(goingTeam.getPieceColor());
        turnLabel.setText("Winner:");

        return true;
    }
}
