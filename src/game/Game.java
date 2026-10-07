package game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private char round;
    private final PiecesLayout layout;
    private final List<Move> history = new ArrayList<>();

    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    public char getTurn() {return round;}

    public List<int[]> getLegalMoves(int row, int col) {
        if (layout.getPiece(row, col) == null) {
            return new ArrayList<>();
        }
        return layout.getPiece(row, col).getPossibleMoves(row, col, layout);
    }

    public boolean canMove(int row, int col) {
        if (this.layout.getPiece(row, col).getColor() == getTurn()) {
            return true;
        }
        return false;
    }

    public boolean tryMove(int fromRow, int fromCol, int toRow, int toCol) {
        List<int[]> possibleMoves = this.layout.getPiece(fromRow, fromCol).getPossibleMoves(fromRow, fromCol, layout);
        for (int[] i : possibleMoves) {
            if (toRow == i[0] && toCol == i[1]) {
                return false;
            }
        }
        return true;
    }

}
