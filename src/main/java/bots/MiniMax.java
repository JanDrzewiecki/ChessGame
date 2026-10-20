package bots;

import game.Game;
import game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniMax implements Bot{
    private char color;
    private int depth;

    public MiniMax (char color, int depth) {
        this.color = color;
        this.depth = depth;
    }
    @Override
    public int[] move(Game game) {
        List<Move> moves = game.getAllMoves(color);
        int bestValue = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Move m : moves) {
            game.makeMove(m);
            int value = minimax(game, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            bestValue = Math.max(bestValue, value);
            values.add(value);
            game.undoMove();
        }
        int tolerance = game.getMovesCount() < 10 ? 10 : 0;
        for (int i = 0; i < moves.size(); i++) {
            if (values.get(i) >= bestValue - tolerance) {
                bestMoves.add(moves.get(i));
            }
        }
        if (bestMoves.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(0, bestMoves.size());
        return new int[]{bestMoves.get(index).getFromRow(), bestMoves.get(index).getFromCol(), bestMoves.get(index).getToRow(), bestMoves.get(index).getToCol()};
    }

    public int minimax(Game game, int depth, int alpha, int beta) {
        int bestValue;
        char oponentsColor;
        if (color == 'w') {
            oponentsColor = 'b';
        } else {
            oponentsColor = 'w';
        }
        if (depth == 0) {
            return game.getPoints(color) - game.getPoints(oponentsColor);
        }
        List<Move> moves = game.getAllMoves(game.getTurn());
        if (moves.isEmpty()) {
            if (game.isCheck(color)) {
                return -100000;
            } else if (game.isCheck(oponentsColor)) {
                return 100000;
            }
            return 0;
        }
        boolean botsMove = game.getTurn() == color;
        if (botsMove) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }
        for (Move m : moves) {
            game.makeMove(m);
            if (botsMove) {
                bestValue = Math.max(bestValue, minimax(game, depth - 1, alpha, beta));
                alpha = Math.max(alpha, bestValue);
            } else {
                bestValue = Math.min(bestValue, minimax(game, depth - 1, alpha, beta));
                beta = Math.min(beta, bestValue);
            }
            game.undoMove();
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }

}
