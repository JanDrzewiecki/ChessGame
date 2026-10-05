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
        return new ArrayList<>();
    }
}
