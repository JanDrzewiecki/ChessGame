package ui;

import bots.Bot;
import game.Game;
import game.GameState;
import game.PiecesLayout;
import lessons.Lessons;
import pieces.Piece;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The window part that shows the chess board and handles the mouse. It is used for games between
 * two players, games against a bot and for lessons.
 */
public class ChessBoard extends JPanel {

    private final Image boardImage; // The picture of the empty board.
    private final PiecesLayout layout; // The pieces on the board.
    private int selectedRow = -1; // The row of the selected piece, or -1 if nothing is selected.
    private int selectedCol = -1; // The column of the selected piece, or -1.
    private List<int[]> possibleMoves = new ArrayList<>(); // Squares the selected piece can go to.
    private Game game; // The game that is shown.
    private JPanel gameOverPanel; // The panel shown when the game is over, or null.
    private Runnable mainMenuAction; // What to do when the player goes back to the main menu.

    // Player vs CPU ('n' means there is no bot).
    private char botsColor; // The color the bot plays: 'w', 'b' or 'n'.
    private Bot bot; // The bot, or null if there is no bot.
    private boolean botThinking = false; // Whether the bot is calculating a move right now.

    // Lessons.
    private Lessons lessons; // The lesson that is shown, or null if this is a normal game.
    private Runnable nextPuzzleAction; // What to do when the player wants the next puzzle.
    private Timer feedbackTimer; // Hides the "Wrong Move!" message after a short time.
    private boolean showWrongMove = false; // Whether the "Wrong Move!" message is shown.
    private String hintSquare = null; // The square to highlight as a hint, or null.
    private boolean puzzleSolvedShown = false; // Whether the "Puzzle Solved" panel was shown.

    /**
     * Create a board for a lesson.
     *
     * @param layout the pieces on the board
     * @param game the game of the lesson
     * @param mainMenuAction what to do when the player goes back to the main menu
     * @param nextPuzzleAction what to do when the player wants the next puzzle
     */
    public ChessBoard(PiecesLayout layout, Game game, Runnable mainMenuAction,
            Runnable nextPuzzleAction) {
        this(layout, game, mainMenuAction, 'n', null);
        this.nextPuzzleAction = nextPuzzleAction;
    }

    /**
     * Create a board for a game between two players or against a bot.
     *
     * @param layout the pieces on the board
     * @param game the game to show
     * @param mainMenuAction what to do when the player goes back to the main menu
     * @param botsColor the color the bot plays ('w' or 'b'), or 'n' if there is no bot
     * @param bot the bot, or null if there is no bot
     */
    public ChessBoard(PiecesLayout layout, Game game, Runnable mainMenuAction, char botsColor,
            Bot bot) {
        this.layout = layout;
        this.game = game;
        this.mainMenuAction = mainMenuAction;
        this.botsColor = botsColor;
        this.bot = bot;
        boardImage = new ImageIcon(getClass().getResource("/board/board_brown.png")).getImage();
        setBackground(new Color(63, 63, 63));
        setOpaque(true);
        setPreferredSize(new Dimension(640, 640));
        setLayout(new OverlayLayout(this));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleMousePressed(event);
            }
        });

        if (botsColor == 'w') {
            playBotMove();
        }
    }

    // Handle a click on the board: select a piece, deselect it or play a move.
    private void handleMousePressed(MouseEvent event) {
        if (game.getTurn() == botsColor) {
            return;
        }

        int size = Math.min(getWidth(), getHeight()); // Keep the board square.
        int x = (getWidth() - size) / 2; // Center horizontally.
        int y = (getHeight() - size) / 2; // Center vertically.
        double cell = size / (double) PiecesLayout.BOARD_SIZE;
        int px = event.getX() - x;
        int py = event.getY() - y;
        int col = (int) (px / cell);
        int row = (int) (py / cell);
        int[] boardCell = getScreenCell(row, col);
        int boardRow = boardCell[0];
        int boardCol = boardCell[1];

        if (px < 0 || py < 0 || !layout.isInside(boardRow, boardCol)) {
            return;
        }

        // Puzzle already solved: ignore further clicks.
        if (lessons != null && lessons.isSolved()) {
            return;
        }

        if (selectedRow == -1) {
            if (game.canMove(boardRow, boardCol)) {
                selectedRow = boardRow;
                selectedCol = boardCol;
                possibleMoves = game.getLegalMoves(boardRow, boardCol);
                repaint();
            }

            return;
        }

        // Clicked the same square: just deselect.
        if (boardRow == selectedRow && boardCol == selectedCol) {
            selectedRow = -1;
            selectedCol = -1;
            possibleMoves = new ArrayList<>();
            repaint();

            return;
        }

        // Clicked another of your own pieces: reselect instead of a failed or wrong move.
        if (game.canMove(boardRow, boardCol)) {
            selectedRow = boardRow;
            selectedCol = boardCol;
            possibleMoves = game.getLegalMoves(boardRow, boardCol);
            repaint();

            return;
        }

        boolean moved;

        if (lessons != null) {
            moved = lessons.checkMove(selectedRow, selectedCol, boardRow, boardCol);
            hintSquare = null;

            if (!moved) {
                showWrongMoveMessage();
            }
        } else {
            moved = game.tryMove(selectedRow, selectedCol, boardRow, boardCol);
        }

        // In lessons, promotion is applied automatically from the move of the puzzle.
        if (moved && lessons == null && game.needsPromotion(boardRow, boardCol)) {
            showPromotionDialog(boardRow, boardCol);
        }

        if (lessons == null) {
            playBotMove();
        }

        selectedRow = -1;
        selectedCol = -1;
        possibleMoves = new ArrayList<>();

        if (moved && lessons != null && lessons.isSolved() && !puzzleSolvedShown) {
            puzzleSolvedShown = true;
            createPuzzleCompletedPanel(mainMenuAction);
            revalidate();
        }

        repaint();
    }

    // Show the "Wrong Move!" message for half a second.
    private void showWrongMoveMessage() {
        showWrongMove = true;
        repaint();

        if (feedbackTimer != null) {
            feedbackTimer.stop();
        }

        feedbackTimer = new Timer(500, event -> {
            showWrongMove = false;
            repaint();
        });

        feedbackTimer.setRepeats(false);
        feedbackTimer.start();
    }

    /**
     * Let the bot play a move, if it is the turn of the bot. The bot calculates on a copy of the
     * game in a background thread, so the window keeps responding.
     */
    public void playBotMove() {
        if (bot == null || botThinking || game.getTurn() != botsColor
                || game.getState() != GameState.PLAYING) {
            return;
        }

        botThinking = true;

        Game gameCopy = game.copy();

        new SwingWorker<int[], Void>() {
            /** Calculate the move of the bot (runs in a background thread). */
            @Override
            protected int[] doInBackground() {
                return bot.move(gameCopy);
            }

            /** Play the calculated move on the board (runs in the window thread). */
            @Override
            protected void done() {
                botThinking = false;

                try {
                    int[] move = get();

                    if (move != null && game.getTurn() == botsColor) {
                        boolean moved = game.tryMove(move[0], move[1], move[2], move[3]);

                        if (moved && game.needsPromotion(move[2], move[3])) {
                            game.promote('Q');
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                repaint();
            }
        }.execute();
    }

    // Create a button with the picture of a piece.
    private JButton createPromotionButton(String path) {
        Image image = new ImageIcon(getClass().getResource(path)).getImage();

        image = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        return new PromotionButton(new ImageIcon(image));
    }

    // Show a window to choose the piece a pawn is promoted to.
    private void showPromotionDialog(int row, int col) {
        JDialog promotionPage = new JDialog(SwingUtilities.getWindowAncestor(this), "Promotion");

        promotionPage.setUndecorated(true);
        promotionPage.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        promotionPage.setAlwaysOnTop(true);

        JPanel promotionPanel = new JPanel();

        promotionPanel.setBackground(new Color(95, 95, 95));
        promotionPanel.setLayout(new BoxLayout(promotionPanel, BoxLayout.X_AXIS));

        Piece piece = layout.getPiece(row, col);
        char color = piece.getColor();
        JButton queenButton = createPromotionButton("/pieces/" + color + "Q.png");
        JButton rookButton = createPromotionButton("/pieces/" + color + "R.png");
        JButton bishopButton = createPromotionButton("/pieces/" + color + "B.png");
        JButton knightButton = createPromotionButton("/pieces/" + color + "N.png");

        promotionPanel.add(queenButton);
        promotionPanel.add(Box.createHorizontalStrut(20));
        promotionPanel.add(rookButton);
        promotionPanel.add(Box.createHorizontalStrut(20));
        promotionPanel.add(bishopButton);
        promotionPanel.add(Box.createHorizontalStrut(20));
        promotionPanel.add(knightButton);

        queenButton.addActionListener(event -> choosePromotion('Q', promotionPage));
        rookButton.addActionListener(event -> choosePromotion('R', promotionPage));
        bishopButton.addActionListener(event -> choosePromotion('B', promotionPage));
        knightButton.addActionListener(event -> choosePromotion('N', promotionPage));

        promotionPage.setContentPane(promotionPanel);
        promotionPage.pack();
        promotionPage.setLocationRelativeTo(this);
        promotionPage.setVisible(true);
    }

    // Promote the pawn to the chosen piece, close the window and let the bot move.
    private void choosePromotion(char pieceType, JDialog promotionPage) {
        game.promote(pieceType);
        playBotMove();
        promotionPage.dispose();
        repaint();
    }

    // Create the panel that is shown when the game is over.
    private void createGameOverPanel(String result, Runnable mainMenuAction) {
        gameOverPanel = new RoundedPanel(60);
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel resultLabel = new JLabel();

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        resultLabel.setForeground(Color.RED);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setText(result);

        JPanel buttons = new JPanel();

        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton mainMenu = new MenuButton("Main Menu");
        JButton exitGame = new MenuButton("Exit Game");

        mainMenu.setPreferredSize(new Dimension(150, 45));
        exitGame.setPreferredSize(new Dimension(150, 45));

        buttons.add(mainMenu);
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(exitGame);

        gameOverPanel.add(resultLabel);
        gameOverPanel.add(Box.createVerticalStrut(25));
        gameOverPanel.add(buttons);
        gameOverPanel.setVisible(false);

        mainMenu.addActionListener(event -> mainMenuAction.run());
        exitGame.addActionListener(event -> System.exit(0));

        add(gameOverPanel);
    }

    // Create the panel that is shown when a puzzle is solved.
    private void createPuzzleCompletedPanel(Runnable mainMenuAction) {
        JPanel puzzlePanel = new RoundedPanel(60);

        puzzlePanel.setLayout(new BoxLayout(puzzlePanel, BoxLayout.Y_AXIS));
        puzzlePanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel completedTextLabel = new JLabel();

        completedTextLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        completedTextLabel.setForeground(Color.GREEN);
        completedTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completedTextLabel.setText("Puzzle Solved");

        JPanel buttons = new JPanel();

        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton nextPuzzle = new MenuButton("Next Puzzle");
        JButton mainMenu = new MenuButton("Main Menu");

        nextPuzzle.setPreferredSize(new Dimension(175, 45));
        mainMenu.setPreferredSize(new Dimension(175, 45));

        buttons.add(nextPuzzle);
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(mainMenu);

        puzzlePanel.add(completedTextLabel);
        puzzlePanel.add(Box.createVerticalStrut(25));
        puzzlePanel.add(buttons);

        mainMenu.addActionListener(event -> mainMenuAction.run());
        nextPuzzle.addActionListener(event -> {
            if (nextPuzzleAction != null) {
                nextPuzzleAction.run();
            }
        });

        add(puzzlePanel);
        puzzlePanel.setVisible(true);
    }

    /**
     * Check if the board is shown upside down (the black pieces at the bottom).
     *
     * @return true if the board is flipped
     */
    public boolean isFlipped() {
        if (lessons != null) {
            // Flip based on the color of the player, not on whose turn it is.
            return lessons.getPlayerColor() == 'b';
        }

        if (botsColor == 'w') {
            return true; // The player is black.
        }

        if (botsColor == 'b') {
            return false; // The player is white.
        }

        return game.getTurn() == 'b'; // Player vs Player: the board follows the side to move.
    }

    /**
     * Convert between a square of the board and the square on the screen. The board is rotated
     * by 180 degrees when it is flipped, so the same conversion works in both directions.
     *
     * @param row the row to convert
     * @param col the column to convert
     * @return {row, column} of the converted square
     */
    public int[] getScreenCell(int row, int col) {
        if (isFlipped()) {
            return new int[]{7 - row, 7 - col};
        }

        return new int[]{row, col};
    }

    // Fill one square of the board with the current color of the graphics.
    private void fillCell(Graphics graphics, int x, int y, double cell, int row, int col) {
        int left = x + (int) Math.round(col * cell);
        int top = y + (int) Math.round(row * cell);
        int right = x + (int) Math.round((col + 1) * cell);
        int bottom = y + (int) Math.round((row + 1) * cell);

        graphics.fillRect(left, top, right - left, bottom - top);
    }

    // Highlight the square of the hint.
    private void showHintSquare(String square, Graphics graphics) {
        int col = square.charAt(0) - 'a';
        int row = '8' - square.charAt(1);
        int[] screenCell = getScreenCell(row, col);
        int size = Math.min(getWidth(), getHeight());
        double cell = size / (double) PiecesLayout.BOARD_SIZE;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        graphics.setColor(new Color(0, 175, 255, 120));
        fillCell(graphics, x, y, cell, screenCell[0], screenCell[1]);
    }

    /**
     * Draw the board, the selected square, the possible moves, the pieces and the messages.
     *
     * @param graphics the graphics to draw on
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int size = Math.min(getWidth(), getHeight()); // Keep the board square.
        int x = (getWidth() - size) / 2; // Center horizontally.
        int y = (getHeight() - size) / 2; // Center vertically.
        // The cell size is fractional so the grid matches the board image.
        double cell = size / (double) PiecesLayout.BOARD_SIZE;

        graphics.drawImage(boardImage, x, y, size, size, this);

        if (selectedRow != -1) {
            int[] selectedCell = getScreenCell(selectedRow, selectedCol);

            graphics.setColor(new Color(128, 128, 128, 128));
            fillCell(graphics, x, y, cell, selectedCell[0], selectedCell[1]);
        }

        graphics.setColor(new Color(0, 200, 0, 120)); // Semi-transparent green.

        for (int[] target : possibleMoves) {
            int[] targetCell = getScreenCell(target[0], target[1]);

            fillCell(graphics, x, y, cell, targetCell[0], targetCell[1]);
        }

        drawPieces(graphics, x, y, cell);
        showGameOverPanelIfNeeded();

        if (showWrongMove) {
            drawWrongMoveMessage(graphics, x, y, size);
        }

        if (hintSquare != null) {
            showHintSquare(hintSquare, graphics);
        }
    }

    // Draw all pieces on their squares.
    private void drawPieces(Graphics graphics, int x, int y, double cell) {
        for (int i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (int j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                Piece piece = layout.getPiece(i, j);

                if (piece != null) {
                    int[] screenCell = getScreenCell(i, j);
                    int left = x + (int) Math.round(screenCell[1] * cell);
                    int top = y + (int) Math.round(screenCell[0] * cell);
                    int right = x + (int) Math.round((screenCell[1] + 1) * cell);
                    int bottom = y + (int) Math.round((screenCell[0] + 1) * cell);

                    graphics.drawImage(piece.getIcon().getImage(), left, top, right - left,
                            bottom - top, this);
                }
            }
        }
    }

    // Show the game-over panel: only for normal games, and only once.
    private void showGameOverPanelIfNeeded() {
        GameState state = game.getState();

        if (lessons == null && gameOverPanel == null
                && (state == GameState.CHECKMATE || state == GameState.STALEMATE)) {
            String result = "STALEMATE";

            if (state == GameState.CHECKMATE) {
                result = "CHECKMATE";
            }

            createGameOverPanel(result, mainMenuAction);
            gameOverPanel.setVisible(true);
            revalidate();
        }
    }

    // Draw the "Wrong Move!" message in the middle of the board.
    private void drawWrongMoveMessage(Graphics graphics, int x, int y, int size) {
        Graphics2D graphics2d = (Graphics2D) graphics.create();
        int rectWidth = 300;
        int rectHeight = 70;

        // Center the rectangle on the actual chessboard.
        int rectX = x + (size - rectWidth) / 2;
        int rectY = y + (size - rectHeight) / 2;

        graphics2d.setColor(new Color(0, 0, 0, 150));
        graphics2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 15, 15);

        // Center the text inside the rectangle.
        graphics2d.setFont(new Font("Arial", Font.BOLD, 26));
        graphics2d.setColor(Color.RED);

        FontMetrics metrics = graphics2d.getFontMetrics();
        String message = "Wrong Move!";
        int textX = rectX + (rectWidth - metrics.stringWidth(message)) / 2;
        int textY = rectY + (rectHeight - metrics.getHeight()) / 2 + metrics.getAscent();

        graphics2d.drawString(message, textX, textY);
        graphics2d.dispose();
    }

    /**
     * Show this board as a lesson.
     *
     * @param lessons the lesson to show
     */
    public void setLessons(Lessons lessons) {
        this.lessons = lessons;
    }

    /**
     * Highlight the piece to move next (only in lessons).
     */
    public void activateHint() {
        if (lessons == null) {
            return; // Hints only work in puzzles.
        }

        hintSquare = lessons.getHint();
        repaint();
    }

    /**
     * Remove the hint highlight.
     */
    public void clearHint() {
        hintSquare = null;
        repaint();
    }
}
