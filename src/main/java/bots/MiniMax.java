package bots;

import game.Game;
import game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A bot that searches the moves ahead with minimax and alpha-beta pruning. It assumes both
 * players play their best move and picks the move with the best score for itself.
 */
public class MiniMax implements Bot {

    private static final int MATE_SCORE = 100000; // The score of a checkmate for the winner.
    private static final int OPENING_MOVES = 10; // Moves that count as opening.
    private static final int OPENING_TOLERANCE = 10; // In the opening, moves this close are equal.

    private char color; // The color the bot plays: 'w' or 'b'.
    private int depth; // The number of moves the bot looks ahead.

    /**
     * Create a bot.
     *
     * @param color the color the bot plays ('w' or 'b')
     * @param depth the number of moves to look ahead (the difficulty)
     */
    public MiniMax(char color, int depth) {
        this.color = color;
        this.depth = depth;
    }

    /**
     * Choose a move. In the opening the bot picks randomly among moves that are almost as good as
     * the best one, so the games do not all start the same way.
     *
     * @param game the game to choose a move in (the moves tried are undone again)
     * @return the move as {fromRow, fromCol, toRow, toCol}, or null if there is no legal move
     */
    @Override
    public int[] move(Game game) {
        List<Move> moves = game.getAllMoves(color);
        int bestValue = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Move move : moves) {
            game.makeMove(move);

            int value = searchScore(game, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

            bestValue = Math.max(bestValue, value);
            values.add(value);
            game.undoMove();
        }

        int tolerance = 0;

        if (game.getMovesCount() < OPENING_MOVES) {
            tolerance = OPENING_TOLERANCE;
        }

        for (int i = 0; i < moves.size(); i++) {
            if (values.get(i) >= bestValue - tolerance) {
                bestMoves.add(moves.get(i));
            }
        }

        if (bestMoves.isEmpty()) {
            return null;
        }

        Random random = new Random();
        Move chosen = bestMoves.get(random.nextInt(0, bestMoves.size()));

        return new int[]{
            chosen.getFromRow(), chosen.getFromCol(), chosen.getToRow(), chosen.getToCol()
        };
    }

    /**
     * Search the score of the current position, looking a number of moves ahead. The score is
     * from the point of view of the bot: the higher, the better for the bot. Branches that cannot
     * change the result are skipped (alpha-beta pruning).
     *
     * @param game the game, with the position to score
     * @param remainingDepth the number of moves left to look ahead
     * @param alpha the best score the bot is already sure to reach
     * @param beta the best score the opponent is already sure to reach
     * @return the score of the position
     */
    public int searchScore(Game game, int remainingDepth, int alpha, int beta) {
        char opponentColor;

        if (color == 'w') {
            opponentColor = 'b';
        } else {
            opponentColor = 'w';
        }

        if (remainingDepth == 0) {
            return game.getPoints(color) - game.getPoints(opponentColor);
        }

        List<Move> moves = game.getAllMoves(game.getTurn());

        if (moves.isEmpty()) {
            if (game.isCheck(color)) {
                return -MATE_SCORE;
            } else if (game.isCheck(opponentColor)) {
                return MATE_SCORE;
            }

            return 0;
        }

        boolean botsMove = game.getTurn() == color;
        int bestValue;

        if (botsMove) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        for (Move move : moves) {
            game.makeMove(move);

            if (botsMove) {
                bestValue = Math.max(bestValue,
                        searchScore(game, remainingDepth - 1, alpha, beta));
                alpha = Math.max(alpha, bestValue);
            } else {
                bestValue = Math.min(bestValue,
                        searchScore(game, remainingDepth - 1, alpha, beta));
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
