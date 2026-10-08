import game.ChessBoard;
import game.Game;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");

            // CardLayout lets us switch between screens
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);

            // =========================
            // MAIN MENU
            // =========================
            JPanel mainMenu = new JPanel();
            mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.Y_AXIS));
            JButton playerVsPlayerButton = new JButton("Player vs Player");
            JButton playerVsCpuButton = new JButton("Player vs CPU");
            JButton lessonsButton = new JButton("Lessons");
            JButton exitButton = new JButton("Exit");

            Dimension buttonSize = new Dimension(200, 60);
            Dimension maxButtonSize = new Dimension(450, 90);

            JButton[] menuButtons = {playerVsPlayerButton, playerVsCpuButton, lessonsButton, exitButton};
            for (int i = 0; i < menuButtons.length; i++) {
                JButton button = menuButtons[i];
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setPreferredSize(buttonSize);
                button.setMaximumSize(maxButtonSize);
                button.setBorder(BorderFactory.createLineBorder(i == 0 ? Color.GRAY : Color.LIGHT_GRAY, 3, true));
            }

            mainMenu.add(Box.createVerticalGlue());
            mainMenu.add(playerVsPlayerButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(playerVsCpuButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(lessonsButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(exitButton);
            mainMenu.add(Box.createVerticalGlue());

            // =========================
            // GAME SCREEN
            // =========================
            JPanel gamePanel = new JPanel(new BorderLayout());
            JPanel topPanel = new JPanel(new BorderLayout());

            JButton menuButton = new JButton("☰");
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem backToMainMenu = new JMenuItem("Main Menu");
            JMenuItem forfeit = new JMenuItem("Forfeit");
            JMenuItem exit = new JMenuItem("Exit");

            popupMenu.add(backToMainMenu);
            popupMenu.add(forfeit);
            popupMenu.addSeparator();
            popupMenu.add(exit);

            menuButton.addActionListener(e -> popupMenu.show(menuButton, 0, menuButton.getHeight()));

            topPanel.add(menuButton, BorderLayout.WEST);
            gamePanel.add(topPanel, BorderLayout.NORTH);

            cards.add(mainMenu, "MENU");
            cards.add(gamePanel, "GAME");

            // =========================
            // BUTTON ACTIONS
            // =========================
            ChessBoard[] currentBoard = new ChessBoard[1];

            // Every new game gets a fresh layout, Game and board
            playerVsPlayerButton.addActionListener(e -> {
                if (currentBoard[0] != null) {
                    gamePanel.remove(currentBoard[0]);
                }
                PiecesLayout layout = new PiecesLayout();
                layout.setupStartPositions();
                Game game = new Game(layout);
                currentBoard[0] = new ChessBoard(layout, game);
                gamePanel.add(currentBoard[0], BorderLayout.CENTER);
                gamePanel.revalidate();
                gamePanel.repaint();
                cardLayout.show(cards, "GAME");
            });

            backToMainMenu.addActionListener(e -> cardLayout.show(cards, "MENU"));
            forfeit.addActionListener(e -> cardLayout.show(cards, "MENU"));
            exit.addActionListener(e -> System.exit(0));
            exitButton.addActionListener(e -> System.exit(0));

            // =========================
            // FRAME
            // =========================
            frame.add(cards);
            frame.setPreferredSize(new Dimension(640, 700));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
