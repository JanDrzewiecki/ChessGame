package app;

import bots.MiniMax;
import game.Game;
import game.PiecesLayout;
import lessons.Lessons;
import ui.BackgroundPanel;
import ui.ChessBoard;
import ui.MenuButton;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
 * The start of the chess game: builds the window with the menu and starts the games.
 */
public class Main {

    private static final Color BACKGROUND_COLOR = new Color(63, 63, 63); // The grey of the game.
    private static final String BACKGROUND_IMAGE = "/background/background.jpg"; // Menu background.

    // The current Player vs Player or Player vs CPU board (null when there is no such game).
    private static ChessBoard currentBoard;

    private static String currentMode = null; // Mode of currentBoard: "pvp" or "cpu".
    private static boolean isFullscreen = true; // Whether the window fills the whole screen.
    private static Rectangle previousBounds; // The window bounds before fullscreen.

    // Start a lesson with a random puzzle of a difficulty.
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
        currentBoard = null; // Any PvP or CPU board was just removed.
        currentMode = null;
        hintButton.setVisible(true);

        ChessBoard lessonBoard = Lessons.createBoard(
                difficulty,

                // What happens when Main Menu is clicked.
                () -> {
                    hintButton.setVisible(false);
                    gamePanel.removeAll();
                    gamePanel.add(topPanel, BorderLayout.NORTH);
                    gamePanel.revalidate();
                    gamePanel.repaint();
                    cardLayout.show(cards, "MENU");
                },

                // What happens when Next Puzzle is clicked.
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

    // Start (or resume) a Player vs Player or Player vs CPU game.
    private static void startVersus(
            boolean vsCpu,
            JPanel gamePanel,
            JPanel topPanel,
            CardLayout cardLayout,
            JPanel cards,
            int depth
    ) {
        String mode = vsCpu ? "cpu" : "pvp";

        // Resume the game in progress if it is the same mode.
        if (currentBoard != null && mode.equals(currentMode)) {
            cardLayout.show(cards, "GAME");

            return;
        }

        // Clear anything left over (a lesson board, or a game of the other mode).
        gamePanel.removeAll();
        gamePanel.add(topPanel, BorderLayout.NORTH);

        PiecesLayout layout = new PiecesLayout();

        layout.setupStartPositions();

        Game game = new Game(layout);
        char botsColor = 'n';
        MiniMax bot = null;

        if (vsCpu) {
            botsColor = new Random().nextBoolean() ? 'b' : 'w';
            bot = new MiniMax(botsColor, depth);
        }

        currentMode = mode;
        currentBoard = new ChessBoard(
                layout,
                game,
                () -> {
                    gamePanel.remove(currentBoard);
                    currentBoard = null;
                    currentMode = null;

                    gamePanel.revalidate();
                    gamePanel.repaint();

                    cardLayout.show(cards, "MENU");
                },
                botsColor,
                bot
        );

        gamePanel.add(currentBoard, BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
        cardLayout.show(cards, "GAME");
    }

    // Give menu buttons the same alignment and size.
    private static void styleMenuButtons(JButton[] buttons, Dimension size, Dimension maxSize) {
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];

            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(size);
            button.setMaximumSize(maxSize);
            button.setBorder(BorderFactory.createLineBorder(
                    i == 0 ? Color.GRAY : Color.LIGHT_GRAY, 3, true));
        }
    }

    /**
     * Start the chess game: build the window with all screens and show it.
     *
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");
            Image icon = Toolkit.getDefaultToolkit()
                    .getImage(Main.class.getResource("/pieces/wP.png"));

            frame.setIconImage(icon);

            // CardLayout lets us switch between screens.
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);

            // =========================
            // MAIN MENU
            // =========================
            JPanel mainMenu = new BackgroundPanel(BACKGROUND_IMAGE);

            mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.Y_AXIS));

            JButton playerVsPlayerButton = new MenuButton("Player vs Player");
            JButton playerVsCpuButton = new MenuButton("Player vs CPU");
            JButton lessonsButton = new MenuButton("Lessons");
            JButton exitButton = new MenuButton("Exit");
            ChessBoard[] currentLessonBoard = new ChessBoard[1];
            Dimension buttonSize = new Dimension(200, 60);
            Dimension maxButtonSize = new Dimension(450, 90);
            JButton[] menuButtons = {
                playerVsPlayerButton, playerVsCpuButton, lessonsButton, exitButton
            };

            styleMenuButtons(menuButtons, buttonSize, maxButtonSize);

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

            gamePanel.setBackground(BACKGROUND_COLOR);
            topPanel.setBackground(BACKGROUND_COLOR);

            // The difficulty levels of the lessons.
            JButton easyButton = new MenuButton("Easy");
            JButton mediumButton = new MenuButton("Medium");
            JButton hardButton = new MenuButton("Hard");
            JButton mainMenuButton = new MenuButton("Main Menu");
            JPanel difficultyPanel = new BackgroundPanel(BACKGROUND_IMAGE);
            JButton[] diffButtons = {easyButton, mediumButton, hardButton, mainMenuButton};

            easyButton.setForeground(Color.GREEN);
            mediumButton.setForeground(Color.YELLOW);
            hardButton.setForeground(Color.RED);
            difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));

            // The difficulty levels of the bot (separate buttons, because a component can only
            // have one parent).
            JButton veryEasyButton = new MenuButton("Very easy");
            JButton cpuEasyButton = new MenuButton("Easy");
            JButton cpuMediumButton = new MenuButton("Medium");
            JButton cpuHardButton = new MenuButton("Hard");
            JButton veryHardButton = new MenuButton("Very hard");
            JButton cpuMainMenuButton = new MenuButton("Main Menu");
            JPanel difficultyPanelForBot = new BackgroundPanel(BACKGROUND_IMAGE);
            JButton[] diffButtonsForBot = {
                veryEasyButton, cpuEasyButton, cpuMediumButton, cpuHardButton, veryHardButton,
                cpuMainMenuButton
            };

            veryEasyButton.setForeground(Color.GREEN);
            cpuEasyButton.setForeground(Color.GREEN);
            cpuMediumButton.setForeground(Color.YELLOW);
            cpuHardButton.setForeground(Color.RED);
            veryHardButton.setForeground(Color.RED);
            difficultyPanelForBot.setLayout(
                    new BoxLayout(difficultyPanelForBot, BoxLayout.Y_AXIS));

            JButton hintButton = new MenuButton("Hint");
            Dimension hintButtonSize = new Dimension(100, 30);

            hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            hintButton.setPreferredSize(hintButtonSize);
            hintButton.setMaximumSize(maxButtonSize);
            hintButton.setVisible(false);

            hintButton.addActionListener(event -> {
                if (currentLessonBoard[0] != null) {
                    currentLessonBoard[0].activateHint();
                }
            });

            styleMenuButtons(diffButtons, buttonSize, maxButtonSize);
            styleMenuButtons(diffButtonsForBot, buttonSize, maxButtonSize);

            difficultyPanel.add(Box.createVerticalGlue());
            difficultyPanel.add(easyButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(mediumButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(hardButton);
            difficultyPanel.add(Box.createVerticalStrut(20));
            difficultyPanel.add(mainMenuButton);
            difficultyPanel.add(Box.createVerticalGlue());

            lessonsButton.addActionListener(event -> cardLayout.show(cards, "DIFFICULTY"));

            easyButton.addActionListener(event -> startLesson("easy", gamePanel, topPanel,
                    cardLayout, cards, hintButton, currentLessonBoard));
            mediumButton.addActionListener(event -> startLesson("medium", gamePanel, topPanel,
                    cardLayout, cards, hintButton, currentLessonBoard));
            hardButton.addActionListener(event -> startLesson("difficult", gamePanel, topPanel,
                    cardLayout, cards, hintButton, currentLessonBoard));

            mainMenuButton.addActionListener(event -> {
                hintButton.setVisible(false);
                cardLayout.show(cards, "MENU");
            });

            difficultyPanelForBot.add(Box.createVerticalGlue());

            for (int i = 0; i < diffButtonsForBot.length; i++) {
                difficultyPanelForBot.add(diffButtonsForBot[i]);
                difficultyPanelForBot.add(Box.createVerticalStrut(20));
            }

            difficultyPanelForBot.add(Box.createVerticalGlue());

            // The level 1 to 5 is the number of moves the bot looks ahead.
            veryEasyButton.addActionListener(event ->
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 1));
            cpuEasyButton.addActionListener(event ->
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 2));
            cpuMediumButton.addActionListener(event ->
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 3));
            cpuHardButton.addActionListener(event ->
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 4));
            veryHardButton.addActionListener(event ->
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 5));
            cpuMainMenuButton.addActionListener(event -> cardLayout.show(cards, "MENU"));

            JButton menuButton = new MenuButton("☰");
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem backToMainMenu = new JMenuItem("Main Menu");
            JMenuItem forfeit = new JMenuItem("Forfeit");
            JMenuItem exit = new JMenuItem("Exit");

            popupMenu.setBackground(BACKGROUND_COLOR);
            backToMainMenu.setBackground(BACKGROUND_COLOR);
            forfeit.setBackground(BACKGROUND_COLOR);
            exit.setBackground(BACKGROUND_COLOR);
            backToMainMenu.setForeground(Color.WHITE);
            forfeit.setForeground(Color.WHITE);
            exit.setForeground(Color.WHITE);

            popupMenu.add(backToMainMenu);
            popupMenu.add(forfeit);
            popupMenu.addSeparator();
            popupMenu.add(exit);

            menuButton.addActionListener(event ->
                    popupMenu.show(menuButton, 0, menuButton.getHeight()));

            JPanel topLeftPanel = new JPanel();

            topLeftPanel.setBackground(BACKGROUND_COLOR);
            topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            topLeftPanel.add(menuButton);
            topLeftPanel.add(hintButton);

            topPanel.add(topLeftPanel, BorderLayout.WEST);
            gamePanel.add(topPanel, BorderLayout.NORTH);

            cards.add(mainMenu, "MENU");
            cards.add(gamePanel, "GAME");
            cards.add(difficultyPanel, "DIFFICULTY");
            cards.add(difficultyPanelForBot, "CPU_DIFFICULTY");

            // =========================
            // BUTTON ACTIONS
            // =========================
            playerVsPlayerButton.addActionListener(event ->
                    startVersus(false, gamePanel, topPanel, cardLayout, cards, 0));

            // A game against the bot in progress is resumed, otherwise the level is chosen.
            playerVsCpuButton.addActionListener(event -> {
                if (currentBoard != null && "cpu".equals(currentMode)) {
                    startVersus(true, gamePanel, topPanel, cardLayout, cards, 0);
                } else {
                    cardLayout.show(cards, "CPU_DIFFICULTY");
                }
            });

            backToMainMenu.addActionListener(event -> {
                hintButton.setVisible(false);
                cardLayout.show(cards, "MENU");
            });

            forfeit.addActionListener(event -> {
                hintButton.setVisible(false);

                if (currentBoard != null) {
                    gamePanel.remove(currentBoard);
                    currentBoard = null;
                    currentMode = null;
                }

                gamePanel.revalidate();
                gamePanel.repaint();

                cardLayout.show(cards, "MENU");
            });

            exit.addActionListener(event -> System.exit(0));
            exitButton.addActionListener(event -> System.exit(0));

            // =========================
            // FRAME
            // =========================
            frame.add(cards);
            frame.setPreferredSize(new Dimension(640, 700));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.pack();

            GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();

            frame.setBounds(screen.getDefaultConfiguration().getBounds());
            setupFullscreen(frame);
            frame.setVisible(true);
        });
    }

    // Let the player switch between fullscreen and a normal window with the F11 key.
    private static void setupFullscreen(JFrame frame) {
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");

        frame.getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
            /** Switch between fullscreen and a normal window. */
            @Override
            public void actionPerformed(ActionEvent event) {
                GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice();

                if (!isFullscreen) {
                    previousBounds = frame.getBounds();

                    frame.dispose();
                    frame.setUndecorated(true);
                    frame.setBounds(screen.getDefaultConfiguration().getBounds());
                    frame.setVisible(true);

                    isFullscreen = true;
                } else {
                    frame.dispose();
                    frame.setUndecorated(false);

                    if (previousBounds != null) {
                        frame.setBounds(previousBounds);
                    } else {
                        frame.setSize(640, 700);
                        frame.setLocationRelativeTo(null);
                    }

                    frame.setVisible(true);
                    isFullscreen = false;
                }
            }
        });
    }
}
