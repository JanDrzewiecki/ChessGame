package bots;

import game.Game;
import game.Move;

import java.util.List;
import java.util.Random;

public class RandomBot implements Bot {
    private char color;

    public RandomBot (char color) {
        this.color = color;
    }
    @Override
    public int[] move(Game game) {
        List<Move> moves = game.getAllMoves(color);
        if (moves.isEmpty()) {
            return null;
        }
        Random random = new Random();
        Move move = moves.get(random.nextInt(0, moves.size()));
        return new int[]{move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol()};
    }
}
