package pieces;

import game.PiecesLayout;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    public King (char color) {
        super(color, 'K');
    }
    @Override
    public List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout) {
        return new ArrayList<>();
    }
}
