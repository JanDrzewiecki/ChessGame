import game.ChessBoard;
import game.Game;
import game.GameState;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");

            PiecesLayout layout = new PiecesLayout();
            layout.setupStartPositions();
            Game game = new Game(layout);

            ChessBoard chessBoard = new ChessBoard(layout, game);

            // Panel for buttons
            JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 15));

            JButton playerVsPlayerButton = new JButton("Player vs Player");
            JButton playerVsCpuButton = new JButton("Player vs CPU");
            JButton lessonsButton = new JButton("Lessons");
            JButton exitButton = new JButton("Exit");

            buttonPanel.add(playerVsPlayerButton);
            buttonPanel.add(playerVsCpuButton);
            buttonPanel.add(lessonsButton);
            buttonPanel.add(exitButton);

            // Padding around the buttons
            buttonPanel.setBorder(
                    BorderFactory.createEmptyBorder(30, 10, 200, 10)
            );

            // Main panel containing board + buttons
            JPanel mainPanel = new JPanel(new BorderLayout());

            mainPanel.add(buttonPanel, BorderLayout.WEST);
            mainPanel.add(chessBoard, BorderLayout.CENTER);

            frame.add(mainPanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            exitButton.addActionListener(e -> {
                exitButton.setOpaque(true);
                exitButton.setBackground(Color.RED);
                exitButton.setEnabled(false);
                Timer timer = new Timer(50, event -> {
                    System.exit(0);
                });
                timer.setRepeats(false);
                timer.start();
            });
        });
    }
}