package bots;

import game.PiecesLayout;
import game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot {
    private char color;
    private PiecesLayout layout;
    private Game game;

    public RandomBot (char color, PiecesLayout layout, Game game) {
        this.color = color;
        this.layout = layout;
        this.game = game;
    }

    public int[] move() {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                if (layout.getPiece(r, c) != null && layout.getPiece(r, c).getColor() == color) {
                    List<int[]> possibleMoves = game.getLegalMoves(r, c);
                    for (int[] i : possibleMoves) {
                        moves.add(new int[]{r, c, i[0], i[1]});
                    }
                }
            }
        }
        if (moves.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return moves.get(random.nextInt(0, moves.size()));
    }
}
