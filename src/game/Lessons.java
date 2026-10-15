package game;

public class Lessons {

    private final String id;
    private final String fen;
    private final String[] moves;
    private final int rating;
    private final String themes;

    public Lessons(String id, String fen, String moves,
                  int rating, String themes) {
        this.id = id;
        this.fen = fen;
        this.moves = moves.split(" ");
        this.rating = rating;
        this.themes = themes;
    }

    public String getId() {
        return id;
    }

    public String getFen() {
        return fen;
    }

    public String[] getMoves() {
        return moves;
    }

    public int getRating() {
        return rating;
    }

    public String getThemes() {
        return themes;
    }
}