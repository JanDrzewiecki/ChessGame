package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A king, which moves one square in any direction.
 */
public class King extends Piece {

    private boolean moved = false; // Whether this king has moved (needed for castling).

    /**
     * Create a king.
     *
     * @param color the color of the king ('w' or 'b')
     */
    public King(char color) {
        super(color, 'K');
    }

    /**
     * Get the squares this king can step to (castling is added by the game).
     *
     * @param row the row of the king
     * @param col the column of the king
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] steps = {{1, 0}, {-1, 0}, {1, -1}, {1, 1}, {0, 1}, {-1, 1}, {-1, -1}, {0, -1}};

        for (int[] step : steps) {
            int targetRow = row + step[0];
            int targetCol = col + step[1];

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
     * Check if this king has moved.
     *
     * @return true if the king has moved
     */
    public boolean hasMoved() {
        return moved;
    }

    /**
     * Set whether this king has moved.
     *
     * @param moved true if the king has moved
     */
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    /**
     * Get the value of a king (it cannot be captured, so it has no value).
     *
     * @return 0 points
     */
    @Override
    public int getPoint() {
        return 0;
    }

    /**
     * Get the letter of a king.
     *
     * @return 'K'
     */
    @Override
    public char getFigure() {
        return 'K';
    }
}
