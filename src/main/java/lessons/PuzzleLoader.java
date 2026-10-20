
package lessons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleLoader {

    private static final Random RANDOM = new Random();


    public static Puzzle loadRandomPuzzle(String difficulty) {
        int minRating;
        int maxRating;

        switch (difficulty.toLowerCase()) {
            case "easy":
                minRating = 0;
                maxRating = 1199;
                break;
            case "medium":
                minRating = 1200;
                maxRating = 1799;
                break;
            case "difficult":
                minRating = 1800;
                maxRating = 2400;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown difficulty: " + difficulty
                );
        }
        List<Puzzle> puzzles = new ArrayList<>();

        InputStream stream =
                PuzzleLoader.class.getResourceAsStream("/puzzles/lichess_puzzles_1000_random.csv");

        if (stream == null) {
            throw new IllegalStateException(
                    "Could not find /puzzles/lichess_puzzles_1000_random.csv in resources"
            );
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",", -1);

                if (columns.length < 4) {
                    continue;
                }

                String puzzleId = columns[0];
                String fen = columns[1];
                String moves = columns[2];

                int rating;
                try {
                    rating = Integer.parseInt(columns[3]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (rating >= minRating && rating <= maxRating) {
                    puzzles.add(new Puzzle(puzzleId, fen, moves, rating));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not read puzzles.csv", e);
        }

        if (puzzles.isEmpty()) {
            throw new IllegalStateException("No puzzles found in CSV");
        }


        return puzzles.get(RANDOM.nextInt(puzzles.size()));
    }
}
