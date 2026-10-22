package lessons;

import game.Game;
import game.PiecesLayout;
import ui.ChessBoard;

/**
 * A lesson: the player solves a puzzle by playing the right moves.
 */
public class Lessons {

    private final Puzzle puzzle; // The puzzle that is being solved.
    private final Game game; // The game of the lesson.
    private final char playerColor; // The color of the player: 'w' or 'b'.
    public int moveIndex = 1; // The index in the puzzle solution of the move the player must play.

    /**
     * Create a lesson.
     *
     * @param puzzle the puzzle to solve
     * @param game the game of the lesson
     * @param playerColor the color of the player ('w' or 'b')
     */
    public Lessons(Puzzle puzzle, Game game, char playerColor) {
        this.puzzle = puzzle;
        this.game = game;
        this.playerColor = playerColor;
    }

    /**
     * Get the color of the player.
     *
     * @return 'w' or 'b'
     */
    public char getPlayerColor() {
        return playerColor;
    }

    /**
     * Play a move given in UCI notation, for example "e2e4".
     *
     * @param move the move in UCI notation (a fifth character is the promotion piece)
     * @param game the game to play the move in
     * @return true if the move was legal and played
     */
    public static boolean applyMove(String move, Game game) {
        int fromCol = move.charAt(0) - 'a';
        int fromRow = '8' - move.charAt(1);
        int toCol = move.charAt(2) - 'a';
        int toRow = '8' - move.charAt(3);
        boolean success = game.tryMove(fromRow, fromCol, toRow, toCol);

        // Puzzle moves carry the promotion piece as a 5th char, e.g. "e7e8q".
        if (success && game.needsPromotion(toRow, toCol)) {
            char piece = 'Q';

            if (move.length() == 5) {
                piece = Character.toUpperCase(move.charAt(4));
            }

            game.promote(piece);
        }

        return success;
    }

    /**
     * Check a move of the player against the puzzle solution. If it is right, it is played
     * followed by the reply of the opponent.
     *
     * @param fromRow the row of the piece the player moves
     * @param fromCol the column of the piece the player moves
     * @param toRow the row the player moves to
     * @param toCol the column the player moves to
     * @return true if the move is the next move of the solution
     */
    public boolean checkMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isSolved()) {
            return false;
        }

        String[] moves = puzzle.getMoves();
        String correctMove = moves[moveIndex];
        String playerMove = game.moveToUCI(fromRow, fromCol, toRow, toCol);

        // Compare only the from and to squares (ignore the promotion suffix).
        if (!playerMove.equals(correctMove.substring(0, 4))) {
            return false;
        }

        if (!applyMove(correctMove, game)) {
            return false;
        }

        moveIndex++;

        // The reply of the opponent, if one remains.
        if (moveIndex < moves.length) {
            applyMove(moves[moveIndex], game);
            moveIndex++;
        }

        return true;
    }

    /**
     * Get a hint: the square of the piece to move next.
     *
     * @return the square, for example "e2", or null if the puzzle is solved
     */
    public String getHint() {
        String[] moves = puzzle.getMoves();

        if (moveIndex >= moves.length) {
            return null;
        }

        return moves[moveIndex].substring(0, 2);
    }

    /**
     * Check if all moves of the puzzle have been played.
     *
     * @return true if the puzzle is solved
     */
    public boolean isSolved() {
        return moveIndex >= puzzle.getMoves().length;
    }

    /**
     * Create a board for a new lesson with a random puzzle of a difficulty.
     *
     * @param difficulty "easy", "medium" or "difficult"
     * @param mainMenuAction what to do when the player goes back to the main menu
     * @param nextPuzzleAction what to do when the player asks for the next puzzle
     * @return the board of the lesson
     */
    public static ChessBoard createBoard(String difficulty, Runnable mainMenuAction,
            Runnable nextPuzzleAction) {
        Puzzle puzzle = PuzzleLoader.loadRandomPuzzle(difficulty);
        PiecesLayout lessonLayout = new PiecesLayout();

        lessonLayout.setupFromFEN(puzzle.getFen());

        String[] moves = puzzle.getMoves();
        Game lessonGame = new Game(lessonLayout);

        // The FEN side to move is the opponent (they play moves[0]); the player is the other color.
        char sideToMove = puzzle.getFen().split(" ")[1].charAt(0);

        lessonGame.setRound(sideToMove);
        applyMove(moves[0], lessonGame);

        char playerColor = 'w';

        if (sideToMove == 'w') {
            playerColor = 'b';
        }

        Lessons lessons = new Lessons(puzzle, lessonGame, playerColor);
        ChessBoard lessonBoard = new ChessBoard(lessonLayout, lessonGame, mainMenuAction,
                nextPuzzleAction);

        lessonBoard.setLessons(lessons);

        return lessonBoard;
    }
}
