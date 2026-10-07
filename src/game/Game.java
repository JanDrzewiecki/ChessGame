package game;

import pieces.King;
import pieces.Piece;

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
