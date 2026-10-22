package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A rook, which moves any number of squares horizontally or vertically.
 */
public class Rook extends Piece {

    private boolean moved = false; // Whether this rook has moved (needed for castling).

    /**
     * Create a rook.
     *
     * @param color the color of the rook ('w' or 'b')
     */
    public Rook(char color) {
        super(color, 'R');
    }

    /**
     * Get the squares this rook can move to.
     *
     * @param row the row of the rook
     * @param col the column of the rook
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();

        // Down
        for (int i = row + 1; i < PiecesLayout.BOARD_SIZE; i++) {
            Piece other = layout.getPiece(i, col);

            if (other == null) {
                possibleMoves.add(new int[]{i, col});
            } else {
                if (other.getColor() != getColor()) {
                    possibleMoves.add(new int[]{i, col});
                }

                break;
            }
        }

        // Up
        for (int i = row - 1; i >= 0; i--) {
            Piece other = layout.getPiece(i, col);

            if (other == null) {
                possibleMoves.add(new int[]{i, col});
            } else {
                if (other.getColor() != getColor()) {
                    possibleMoves.add(new int[]{i, col});
                }

                break;
            }
        }

        // Right
        for (int j = col + 1; j < PiecesLayout.BOARD_SIZE; j++) {
            Piece other = layout.getPiece(row, j);

            if (other == null) {
                possibleMoves.add(new int[]{row, j});
            } else {
                if (other.getColor() != getColor()) {
                    possibleMoves.add(new int[]{row, j});
                }

                break;
            }
        }

        // Left
        for (int j = col - 1; j >= 0; j--) {
            Piece other = layout.getPiece(row, j);

            if (other == null) {
                possibleMoves.add(new int[]{row, j});
            } else {
                if (other.getColor() != getColor()) {
                    possibleMoves.add(new int[]{row, j});
                }

                break;
            }
        }

        return possibleMoves;
    }

    /**
     * Check if this rook has moved.
     *
     * @return true if the rook has moved
     */
    public boolean hasMoved() {
        return moved;
    }

    /**
     * Set whether this rook has moved.
     *
     * @param moved true if the rook has moved
     */
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    /**
     * Get the value of a rook.
     *
     * @return 500 points
     */
    @Override
    public int getPoint() {
        return 500;
    }

    /**
     * Get the letter of a rook.
     *
     * @return 'R'
     */
    @Override
    public char getFigure() {
        return 'R';
    }
}
