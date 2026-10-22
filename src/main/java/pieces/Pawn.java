package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A pawn, which moves forward and captures diagonally.
 */
public class Pawn extends Piece {

    /**
     * Create a pawn.
     *
     * @param color the color of the pawn ('w' or 'b')
     */
    public Pawn(char color) {
        super(color, 'P');
    }

    /**
     * Get the squares this pawn can move to (en passant is added by the game).
     *
     * @param row the row of the pawn
     * @param col the column of the pawn
     * @param layout the board
     * @return a list of {row, column} squares
     */
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();

        if (getColor() == 'w') {
            if (layout.isInside(row - 1, col) && layout.getPiece(row - 1, col) == null) {
                possibleMoves.add(new int[]{row - 1, col});

                if (row == 6 && layout.getPiece(row - 2, col) == null) {
                    possibleMoves.add(new int[]{row - 2, col});
                }
            }

            if (layout.isInside(row - 1, col - 1)
                    && layout.getPiece(row - 1, col - 1) != null
                    && layout.getPiece(row - 1, col - 1).getColor() == 'b') {
                possibleMoves.add(new int[]{row - 1, col - 1});
            }

            if (layout.isInside(row - 1, col + 1)
                    && layout.getPiece(row - 1, col + 1) != null
                    && layout.getPiece(row - 1, col + 1).getColor() == 'b') {
                possibleMoves.add(new int[]{row - 1, col + 1});
            }
        } else {
            if (layout.isInside(row + 1, col) && layout.getPiece(row + 1, col) == null) {
                possibleMoves.add(new int[]{row + 1, col});

                if (row == 1 && layout.getPiece(row + 2, col) == null) {
                    possibleMoves.add(new int[]{row + 2, col});
                }
            }

            if (layout.isInside(row + 1, col - 1)
                    && layout.getPiece(row + 1, col - 1) != null
                    && layout.getPiece(row + 1, col - 1).getColor() == 'w') {
                possibleMoves.add(new int[]{row + 1, col - 1});
            }

            if (layout.isInside(row + 1, col + 1)
                    && layout.getPiece(row + 1, col + 1) != null
                    && layout.getPiece(row + 1, col + 1).getColor() == 'w') {
                possibleMoves.add(new int[]{row + 1, col + 1});
            }
        }

        return possibleMoves;
    }

    /**
     * Get the value of a pawn.
     *
     * @return 100 points
     */
    @Override
    public int getPoint() {
        return 100;
    }

    /**
     * Get the letter of a pawn.
     *
     * @return 'P'
     */
    @Override
    public char getFigure() {
        return 'P';
    }
}
