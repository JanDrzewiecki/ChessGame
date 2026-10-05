package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece{
    public Rook(char color) {
        super(color, 'R');
    }

    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        List<int[]> possibleMoves = new ArrayList<>();

        // w dół
        for (int i = row + 1; i < 8; i++) {
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

        // w górę
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

        // w prawo
        for (int j = col + 1; j < 8; j++) {
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

        // w lewo
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
}
