package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(char color) {
        super(color, 'P');
    }

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
            if (layout.isInside(row - 1, col - 1) &&
                    layout.getPiece(row - 1, col - 1) != null &&
                    layout.getPiece(row - 1, col - 1).getColor() == 'b') {
                possibleMoves.add(new int[]{row - 1, col - 1});
            }
            if (layout.isInside(row - 1, col + 1) &&
                    layout.getPiece(row - 1, col + 1) != null &&
                    layout.getPiece(row - 1, col + 1).getColor() == 'b') {
                possibleMoves.add(new int[]{row - 1, col + 1});
            }
        } else {
            if (layout.isInside(row + 1, col) && layout.getPiece(row + 1, col) == null) {
                possibleMoves.add(new int[]{row + 1, col});
                if (row == 1 && layout.getPiece(row + 2, col) == null) {
                    possibleMoves.add(new int[]{row + 2, col});
                }
            }
            if (layout.isInside(row + 1, col - 1) &&
                    layout.getPiece(row + 1, col - 1) != null &&
                    layout.getPiece(row + 1, col - 1).getColor() == 'w') {
                possibleMoves.add(new int[]{row + 1, col - 1});
            }
            if (layout.isInside(row + 1, col + 1) &&
                    layout.getPiece(row + 1, col + 1) != null &&
                    layout.getPiece(row + 1, col + 1).getColor() == 'w') {
                possibleMoves.add(new int[]{row + 1, col + 1});
            }
        }
        return possibleMoves;
    }
}
