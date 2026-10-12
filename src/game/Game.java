package game;

import pieces.King;
import pieces.Piece;
import pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private char round = 'w';
    private final PiecesLayout layout;
    private final List<Move> history = new ArrayList<>();
    private GameState state = GameState.PLAYING;

    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    public char getTurn() {
        return round;
    }

    public GameState getState() {
        return state;
    }

    public int[] getKing(char color) {
        for (int i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (int j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                if (layout.getPiece(i, j) instanceof King && color == layout.getPiece(i, j).getColor()) {
                    return new int[]{i, j};
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
        if (layout.getPiece(row, col) instanceof King king) {
            for (int[] m: casttlings(row, col, king.getColor())) {
                legalMoves.add(m);
            }
        }
        return legalMoves;
    }

    public List<int[]> casttlings(int kingsRow, int kingsCol, char color) {
        List<int[]> castles = new ArrayList<>();
        if (!(layout.getPiece(kingsRow, kingsCol) instanceof King king)) {
            return castles;
        }
        if (isCheck(color)) {
            return castles;
        }
        if (king.hasMoved() || kingsCol != 4) {
            return castles;
        }
        if (layout.getPiece(kingsRow, kingsCol + 1) == null &&
            layout.getPiece(kingsRow, kingsCol + 2) == null &&
            layout.getPiece(kingsRow, kingsCol + 3) instanceof Rook rook) {
            boolean cast = true;
            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol + i);
                    move.apply(layout);
                    if (isCheck(color)) {
                        cast = false;
                    }
                    move.undo(layout);
                }
                if (cast) {
                    castles.add(new int[]{kingsRow, kingsCol + 2});
                }
            }
        }
        if (layout.getPiece(kingsRow, kingsCol - 1) == null &&
            layout.getPiece(kingsRow, kingsCol - 2) == null &&
            layout.getPiece(kingsRow, kingsCol - 3) == null &&
            layout.getPiece(kingsRow, kingsCol - 4) instanceof Rook rook) {
            boolean cast = true;
            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol - i);
                    move.apply(layout);
                    if (isCheck(color)) {
                        cast = false;
                    }
                    move.undo(layout);
                }
                if (cast) {
                    castles.add(new int[]{kingsRow, kingsCol - 2});
                }
            }
        }
        return castles;
    }



    public boolean canMove(int row, int col) {
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

    public boolean tryMove(int selectedRow, int selectedCol, int toRow, int toCol) {
        if (state != GameState.PLAYING || !canMove(selectedRow, selectedCol)) {
            return false;
        }
        List<int[]> possibleMoves = getLegalMoves(selectedRow, selectedCol);
        for (int[] i : possibleMoves) {
            if (toRow == i[0] && toCol == i[1]) {
                Move move = new Move(layout, selectedRow, selectedCol, toRow, toCol);
                if (layout.getPiece(selectedRow, selectedCol) instanceof King king) {
                    if (toRow == selectedRow && toCol == selectedCol + 2) {
                        Move rooksMove = new Move(layout, selectedRow, selectedCol + 3, selectedRow, selectedCol + 1);
                        rooksMove.apply(layout);
                    } else if (toRow == selectedRow && toCol == selectedCol - 2) {
                        Move rooksMove = new Move(layout, selectedRow, selectedCol - 4, selectedRow, selectedCol - 1);
                        rooksMove.apply(layout);

                    }
                }
                move.apply(layout);
                history.add(move);
                if(round == 'w') {
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
