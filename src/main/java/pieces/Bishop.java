package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A bishop, which moves any number of squares diagonally.
 */
public class Bishop extends Piece {

    /**
     * Create a bishop.
     *
     * @param color the color of the bishop ('w' or 'b')
     */
    public Bishop(char color) {
        super(color, 'B');
    }

    /**
     * Get the squares this bishop can move to.
     *
     * @param row the row of the bishop
     * @param col the column of the bishop
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

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
     * Get the value of a bishop.
     *
     * @return 300 points
     */
    @Override
    public int getPoint() {
        return 300;
    }

    /**
     * Get the letter of a bishop.
     *
     * @return 'B'
     */
    @Override
    public char getFigure() {
        return 'B';
    }
}
