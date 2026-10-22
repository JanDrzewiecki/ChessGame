package bots;

import game.Game;

/**
 * A computer player that can choose a move in a given game.
 */
public interface Bot {

    /**
     * Choose a move for the player whose turn it is.
     *
     * @param game the game to choose a move in (the bot may try moves on it, but must undo them)
     * @return the move as {fromRow, fromCol, toRow, toCol}, or null if there is no legal move
     */
    int[] move(Game game);
}
