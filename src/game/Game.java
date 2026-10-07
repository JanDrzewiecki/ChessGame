package game;

import pieces.King;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private char round = 'w';
    private final PiecesLayout layout;
    private final List<Move> history = new ArrayList<>();

    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    public char getTurn() {return round;}

    public int[] getKing(char color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
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
        Piece piece = layout.getPiece(row, col);
        return piece != null && piece.getColor() == getTurn();
    }

    public boolean isCheck(char color) {
        if (getKing(color) == null) {
            return false;
        }
        int row = getKing(color)[0];
        int col = getKing(color)[1];

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
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

    public boolean tryMove(int selectedRow, int selectedCol, int toRow, int toCol) {
        if (!canMove(selectedRow, selectedCol)) {
            return false;
        }
        List<int[]> possibleMoves = getLegalMoves(selectedRow, selectedCol);
        for (int[] i : possibleMoves) {
            if (toRow == i[0] && toCol == i[1]) {
                Move move = new Move(layout, selectedRow, selectedCol, toRow, toCol);
                move.apply(layout);
                history.add(move);
                if(round == 'w') {
                    round = 'b';
                } else {
                    round = 'w';
                }
                return true;
            }
        }
        return false;
    }

}
