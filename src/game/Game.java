package game;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private char round = 'w';
    private final PiecesLayout layout;
    private final List<Move> history = new ArrayList<>();
    private GameState state = GameState.PLAYING;
    private Move pendingMove;

    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    public char getTurn() {return round;}

    public GameState getState() {return state;}

    public int[] getKing(char color) {
        for (int i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (int j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                if (layout.getPiece(i, j) instanceof King && color == layout.getPiece(i, j).getColor()) {
                    return new int[] {i, j};
                }
            }
        }

        return null;
    }

    public List<int[]> getLegalMoves(int row, int col) {
        Piece piece = layout.getPiece(row, col);
        if (piece == null) {
            return new ArrayList<>();
        }
        List<int[]> legalMoves = new ArrayList<>();
        for (int[] m : piece.getPossibleMoves(row, col, layout)) {
            Move move = new Move(layout, row, col, m[0], m[1]);
            move.apply(layout);
            boolean safe = !isCheck(piece.getColor());
            move.undo(layout);
            if (safe) {
                legalMoves.add(m);
            }
        }
        return legalMoves;
    }

    public boolean canMove(int row, int col) {
        if (pendingMove != null) {
            return false;
        }
        Piece piece = layout.getPiece(row, col);
        return piece != null && piece.getColor() == getTurn();
    }

    public boolean isCheck(char color) {
        if (getKing(color) == null) {
            return false;
        }
        int row = getKing(color)[0];
        int col = getKing(color)[1];

        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                if (layout.getPiece(r, c) == null || layout.getPiece(r, c).getColor() == color) {
                    continue;
                } else {
                    List<int[]> possibleMoves = layout.getMovesFor(r, c);
                    for (int[] i : possibleMoves) {
                        if (i[0] == row && i[1] == col) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public boolean hasAnyLegalMove(char color) {
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                if (layout.getPiece(r, c) == null || layout.getPiece(r, c).getColor() != color) {
                    continue;
                } else {
                    if (!getLegalMoves(r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public GameState evaluateState(char color) {
        if (hasAnyLegalMove(color)) {
            return GameState.PLAYING;
        }
        return isCheck(color) ? GameState.CHECKMATE : GameState.STALEMATE;
    }

    public void promote(int row, int col, char PieceType) {
        Piece pawn = layout.getPiece(row, col);
        char color = pawn.getColor();

        switch (PieceType) {
            case 'Q':
                layout.setPiece(row, col, new Queen(color));
                break;
            case 'R':
                layout.setPiece(row, col, new Rook(color));
                break;
            case 'B':
                layout.setPiece(row, col, new Bishop(color));
                break;
            case 'N':
                layout.setPiece(row, col, new Knight(color));
                break;
        }
        history.add(pendingMove);
        pendingMove = null;

        if (round == 'w') {
            round = 'b';
        } else {
            round = 'w';
        }

        state = evaluateState(round);
    }


    public boolean needsPromotion(int row, int col) {
        Piece piece = layout.getPiece(row, col);

        return piece instanceof Pawn &&
                (row == 0 || row == PiecesLayout.BOARD_SIZE - 1);
    }

    public boolean tryMove(int selectedRow, int selectedCol, int toRow, int toCol) {

        if (pendingMove != null) {
            return false;
        }

        if (state != GameState.PLAYING || !canMove(selectedRow, selectedCol)) {
            return false;
        }

        List<int[]> possibleMoves = getLegalMoves(selectedRow, selectedCol);

        for (int[] i : possibleMoves) {

            if (toRow == i[0] && toCol == i[1]) {

                Move move = new Move(
                        layout,
                        selectedRow,
                        selectedCol,
                        toRow,
                        toCol
                );

                move.apply(layout);

                Piece piece = layout.getPiece(toRow, toCol);

                // Promotion required
                if (piece instanceof Pawn &&
                        (toRow == 0 || toRow == PiecesLayout.BOARD_SIZE - 1)) {

                    pendingMove = move;
                    return true;
                }

                // Normal move
                history.add(move);

                if (round == 'w') {
                    round = 'b';
                } else {
                    round = 'w';
                }

                state = evaluateState(round);

                return true;
            }
        }

        return false;
    }



}
