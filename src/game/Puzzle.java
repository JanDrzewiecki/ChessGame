package game;

public class Puzzle {

    private final String puzzleId;
    private final String fen;
    private final String moves;
    private final int rating;

    public Puzzle(String puzzleId, String fen, String moves, int rating) {
        this.puzzleId = puzzleId;
        this.fen = fen;
        this.moves = moves;
        this.rating = rating;
    }

    public String getPuzzleId() {
        return this.puzzleId;
    }

    public String getFen() {
        return this.fen;
    }

    public String[] getMoves() {
        return this.moves.split(" ");
    }

    public int getRating() {
        return this.rating;
    }
}
