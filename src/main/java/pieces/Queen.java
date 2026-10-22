package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A queen, which moves any number of squares in a straight or diagonal line.
 */
public class Queen extends Piece {

    /**
     * Create a queen.
     *
     * @param color the color of the queen ('w' or 'b')
     */
    public Queen(char color) {
        super(color, 'Q');
    }

    /**
     * Get the squares this queen can move to.
     *
     * @param row the row of the queen
     * @param col the column of the queen
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int currentRow = row + direction[0];
            int currentCol = col + direction[1];

            while (layout.isInside(currentRow, currentCol)) {
                Piece other = layout.getPiece(currentRow, currentCol);

                if (other == null) {
                    possibleMoves.add(new int[]{currentRow, currentCol});
                } else {
                    if (other.getColor() != getColor()) {
                        possibleMoves.add(new int[]{currentRow, currentCol});
                    }

                    break;
                }

                currentRow += direction[0];
                currentCol += direction[1];
            }
        }

        return possibleMoves;
    }

    /**
     * Get the value of a queen.
     *
     * @return 900 points
     */
    @Override
    public int getPoint() {
        return 900;
    }

    /**
     * Get the letter of a queen.
     *
     * @return 'Q'
     */
    @Override
    public char getFigure() {
        return 'Q';
    }
}
