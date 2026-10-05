package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{
    public Knight (char color) {
        super(color, 'N');
    }
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] jumps = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] i : jumps) {
            int r = row + i[0];
            int c = col + i[1];

            if (r < 0 || r >= 8 || c < 0 || c >= 8) {
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
