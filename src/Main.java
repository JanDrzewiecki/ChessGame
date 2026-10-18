import game.ChessBoard;
import game.Game;
import game.Lessons;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;


public class Main {

    // Current PvP board (null when there is no PvP game in progress)
    private static final ChessBoard[] currentBoard = new ChessBoard[1];

    private static void startLesson(
            String difficulty,
            JPanel gamePanel,
            JPanel topPanel,
            CardLayout cardLayout,
            JPanel cards,
            JButton hintButton,
            ChessBoard[] currentLessonBoard

    ) {
        gamePanel.removeAll();
        currentBoard[0] = null;   // the PvP board was just removed
        hintButton.setVisible(true);

        ChessBoard lessonBoard = Lessons.createBoard(
                difficulty,

                // What happens when Main Menu is clicked
                () -> {
                    hintButton.setVisible(false);
                    gamePanel.removeAll();
                    gamePanel.add(topPanel, BorderLayout.NORTH);
                    gamePanel.revalidate();
                    gamePanel.repaint();
                    cardLayout.show(cards, "MENU");
                },

                // What happens when Next Puzzle is clicked
                () -> startLesson(
                        difficulty,
                        gamePanel,
                        topPanel,
                        cardLayout,
                        cards,
                        hintButton,
                        currentLessonBoard
                )
        );

        currentLessonBoard[0] = lessonBoard;

        hintButton.setVisible(true);

        gamePanel.add(topPanel, BorderLayout.NORTH);
        gamePanel.add(lessonBoard, BorderLayout.CENTER);

        gamePanel.revalidate();
        gamePanel.repaint();
        cardLayout.show(cards, "GAME");
    }

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
            ChessBoard[] currentLessonBoard = new ChessBoard[1];
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

            JButton easyButton = new JButton("Easy");
            JButton mediumButton = new JButton("Medium");
            JButton hardButton = new JButton("Hard");
            JButton mainMenuButton = new JButton("Main Menu");
            JPanel difficultyPanel = new JPanel();
            difficultyPanel.setLayout(
                    new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS)
            );
            JButton[] diffButtons = {easyButton, mediumButton, hardButton, mainMenuButton};

            JButton hintButton = new JButton("Hint");
            hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            hintButton.setPreferredSize(buttonSize);
            hintButton.setMaximumSize(maxButtonSize);
            hintButton.setVisible(false);

            hintButton.addActionListener(e -> {
                if (currentLessonBoard[0] != null) {
                    currentLessonBoard[0].activateHint();
                }
            });

            for (int i = 0; i < diffButtons.length; i++) {
                JButton button = diffButtons[i];
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setPreferredSize(buttonSize);
                button.setMaximumSize(maxButtonSize);
                button.setBorder(BorderFactory.createLineBorder(i == 0 ? Color.GRAY : Color.LIGHT_GRAY, 3, true));
            }

            difficultyPanel.add(Box.createVerticalGlue());
            difficultyPanel.add(easyButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(mediumButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(hardButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(mainMenuButton);
            difficultyPanel.add(Box.createVerticalGlue());
            lessonsButton.addActionListener(e -> {

                cardLayout.show(cards, "DIFFICULTY");


            });
            easyButton.addActionListener(e ->
                    startLesson("easy", gamePanel, topPanel, cardLayout, cards, hintButton, currentLessonBoard)
            );

            mediumButton.addActionListener(e ->
                    startLesson("medium", gamePanel, topPanel, cardLayout, cards, hintButton, currentLessonBoard)

            );

            hardButton.addActionListener(e ->
                    startLesson("difficult", gamePanel, topPanel, cardLayout, cards, hintButton, currentLessonBoard)
            );

            mainMenuButton.addActionListener(e -> {
                hintButton.setVisible(false);
                cardLayout.show(cards, "MENU");
            });


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

            JPanel topLeftPanel = new JPanel();
            topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            topLeftPanel.add(menuButton);
            topLeftPanel.add(hintButton);

            topPanel.add(topLeftPanel, BorderLayout.WEST);
            gamePanel.add(topPanel, BorderLayout.NORTH);

            cards.add(mainMenu, "MENU");
            cards.add(gamePanel, "GAME");
            cards.add(difficultyPanel, "DIFFICULTY");

            // =========================
            // BUTTON ACTIONS
            // =========================

            // Every new game gets a fresh layout, Game and board
            playerVsPlayerButton.addActionListener(e -> {
                hintButton.setVisible(false);
                if (currentBoard[0] != null) {
                    cardLayout.show(cards, "GAME");
                    return;
                }

                // Clear anything left over (e.g. a lesson board) before building the PvP screen
                gamePanel.removeAll();
                gamePanel.add(topPanel, BorderLayout.NORTH);

                PiecesLayout layout = new PiecesLayout();
                layout.setupStartPositions();
                Game game = new Game(layout);
                currentBoard[0] = new ChessBoard(
                        layout,
                        game,
                        () -> {
                            gamePanel.remove(currentBoard[0]);
                            currentBoard[0] = null;

                            gamePanel.revalidate();
                            gamePanel.repaint();

                            cardLayout.show(cards, "MENU");
                        }
                );
                gamePanel.add(currentBoard[0], BorderLayout.CENTER);
                gamePanel.revalidate();
                gamePanel.repaint();
                cardLayout.show(cards, "GAME");
            });

            // TODO: Player vs CPU listener goes here; start it with hintButton.setVisible(false);

            backToMainMenu.addActionListener(e -> {
                hintButton.setVisible(false);
                cardLayout.show(cards, "MENU");
            });
            forfeit.addActionListener(e -> {
                hintButton.setVisible(false);
                if (currentBoard[0] != null) {
                    gamePanel.remove(currentBoard[0]);
                    currentBoard[0] = null;
                }

                gamePanel.revalidate();
                gamePanel.repaint();

                cardLayout.show(cards, "MENU");
            });
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
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}