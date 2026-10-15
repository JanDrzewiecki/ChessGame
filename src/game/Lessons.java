
package game;

import javax.swing.*;

public class Lessons {

    public static void open() {

        Puzzle puzzle = new Puzzle(
                "00sHx",
                "q3k1nr/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                "e8d7 a2e6 d7d8 f7f8",
                1760
        );

        PiecesLayout lessonLayout = new PiecesLayout();
        lessonLayout.setupFromFEN(puzzle.getFen());

        String[] moves = puzzle.getMoves();

        Game lessonGame = new Game(lessonLayout);

        JFrame lessonsWindow = new JFrame("Lessons");
        lessonsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Apply the initial setup move from the Lichess puzzle
        lessonGame.setRound('b');
        applyMove(moves[0], lessonGame);

        ChessBoard lessonBoard = new ChessBoard(
                lessonLayout,
                lessonGame,
                lessonsWindow::dispose
        );

        lessonsWindow.add(lessonBoard);
        lessonsWindow.pack();
        lessonsWindow.setLocationRelativeTo(null);
        lessonsWindow.setVisible(true);
    }

    public static void applyMove(String move, Game game) {
        int fromCol = move.charAt(0) - 'a';
        int fromRow = '8' - move.charAt(1);
        int toCol = move.charAt(2) - 'a';
        int toRow = '8' - move.charAt(3);

        boolean success = game.tryMove(
                fromRow, fromCol, toRow, toCol
        );
    }
}
