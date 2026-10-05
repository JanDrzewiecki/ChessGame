package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen (char color) {
        super(color, 'Q');
    }
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : directions) {
            int r = row + d[0];
            int c = col + d[1];

            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece other = layout.getPiece(r, c);
                if (other == null) {
                    possibleMoves.add(new int[]{r, c});
                } else {
                    if (other.getColor() != getColor()) {
                        possibleMoves.add(new int[]{r, c});
                    }
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }

        return possibleMoves;
    }
}
