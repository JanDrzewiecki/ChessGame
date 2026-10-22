package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A knight, which jumps in an L shape.
 */
public class Knight extends Piece {

    /**
     * Create a knight.
     *
     * @param color the color of the knight ('w' or 'b')
     */
    public Knight(char color) {
        super(color, 'N');
    }

    /**
     * Get the squares this knight can jump to.
     *
     * @param row the row of the knight
     * @param col the column of the knight
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] jumps = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] jump : jumps) {
            int targetRow = row + jump[0];
            int targetCol = col + jump[1];

            if (!layout.isInside(targetRow, targetCol)) {
                continue;
            }

            Piece other = layout.getPiece(targetRow, targetCol);

            if (other == null || other.getColor() != getColor()) {
                possibleMoves.add(new int[]{targetRow, targetCol});
            }
        }

        return possibleMoves;
    }

    /**
     * Get the value of a knight.
     *
     * @return 300 points
     */
    @Override
    public int getPoint() {
        return 300;
    }

    /**
     * Get the letter of a knight.
     *
     * @return 'N'
     */
    @Override
    public char getFigure() {
        return 'N';
    }
}
