package lessons;

/**
 * A chess puzzle: a start position and the moves that solve it.
 */
public class Puzzle {

    private final String puzzleId; // The identifier of the puzzle.
    private final String fen; // The start position in FEN notation.
    private final String moves; // The moves of the solution in UCI notation, separated by spaces.
    private final int rating; // The difficulty rating of the puzzle.

    /**
     * Create a puzzle.
     *
     * @param puzzleId the identifier of the puzzle
     * @param fen the start position in FEN notation
     * @param moves the moves of the solution in UCI notation, separated by spaces
     * @param rating the difficulty rating
     */
    public Puzzle(String puzzleId, String fen, String moves, int rating) {
        this.puzzleId = puzzleId;
        this.fen = fen;
        this.moves = moves;
        this.rating = rating;
    }

    /**
     * Get the identifier of this puzzle.
     *
     * @return the identifier
     */
    public String getPuzzleId() {
        return this.puzzleId;
    }

    /**
     * Get the start position of this puzzle.
     *
     * @return the position in FEN notation
     */
    public String getFen() {
        return this.fen;
    }

    /**
     * Get the moves of the solution.
     *
     * @return the moves in UCI notation
     */
    public String[] getMoves() {
        return this.moves.split(" ");
    }

    /**
     * Get the difficulty rating of this puzzle.
     *
     * @return the rating
     */
    public int getRating() {
        return this.rating;
    }
}
