package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King (char color) {
        super(color, 'K');
    }
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] moves = {{1, 0}, {-1, 0}, {1, -1}, {1, 1}, {0, 1}, {-1, 1}, {-1, -1}, {0, -1}};
        for (int[] i : moves) {
            int r = row + i[0];
            int c = col + i[1];
            if (!layout.isInside(r, c)) {
                continue;
            }

            Piece other = layout.getPiece(r, c);
            if (other == null || other.getColor() != getColor()) {
                possibleMoves.add(new int[]{r, c});
            }
        }
        return possibleMoves;
    }
}
