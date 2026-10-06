import game.ChessBoard;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;

public class MainMenu {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Chess");

            // CardLayout lets us switch between screens
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);

            // =========================
            JPanel mainMenu = new JPanel();
            mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.Y_AXIS));
            JButton playerVsPlayerButton = new JButton("Player vs Player");
            JButton playerVsCpuButton = new JButton("Player vs CPU");
            JButton lessonsButton = new JButton("Lessons");
            JButton exitButton = new JButton("Exit");

            playerVsPlayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerVsCpuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            lessonsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            Dimension buttonSize = new Dimension(200, 60);
            Dimension maxButtonSize = new Dimension(450, 90);

            playerVsPlayerButton.setPreferredSize(buttonSize);
            playerVsPlayerButton.setMaximumSize(maxButtonSize);

            playerVsPlayerButton.setBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 3, true)
            );

            playerVsCpuButton.setPreferredSize(buttonSize);
            playerVsCpuButton.setMaximumSize(maxButtonSize);

            playerVsCpuButton.setBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true)
            );

            lessonsButton.setPreferredSize(buttonSize);
            lessonsButton.setMaximumSize(maxButtonSize);

            lessonsButton.setBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true)
            );

            exitButton.setPreferredSize(buttonSize);
            exitButton.setMaximumSize(maxButtonSize);
            exitButton.setBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true)
            );


            mainMenu.add(Box.createVerticalGlue());
            mainMenu.add(playerVsPlayerButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(playerVsCpuButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(lessonsButton);
            mainMenu.add(Box.createVerticalStrut(20));
            mainMenu.add(exitButton);
            mainMenu.add(Box.createVerticalGlue());



            //CHESS

            PiecesLayout layout = new PiecesLayout();
            layout.setupStartPositions();

            ChessBoard chessBoard = new ChessBoard(layout);

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

            menuButton.addActionListener(e -> {
                popupMenu.show(menuButton, 0, menuButton.getHeight());
            });

            topPanel.add(menuButton, BorderLayout.WEST);

            gamePanel.add(topPanel, BorderLayout.NORTH);
            gamePanel.add(chessBoard);


            // Add both screens
            cards.add(mainMenu, "MENU");
            cards.add(gamePanel, "GAME");

            // =========================
            // BUTTON ACTIONS
            // =========================

            playerVsPlayerButton.addActionListener(e -> {
                cardLayout.show(cards, "GAME");
            });

            backToMainMenu.addActionListener(e -> {
                cardLayout.show(cards, "MENU");
            });

            exit.addActionListener(e -> {
                System.exit(0);

            });

            exitButton.addActionListener(e -> {

                System.exit(0);
            });

            // =========================
            // FRAME
            // =========================

            frame.add(cards);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}