package game;

import pieces.Piece;
import bots.RandomBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;


public class ChessBoard extends JPanel {

    private final Image boardImage;
    private final PiecesLayout layout;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<int[]> possibleMoves = new ArrayList<>();
    private Game game;
    private JPanel gameOverPanel;
    private Runnable mainMenuAction;

    // Player vs CPU ('n' = no bot)
    private char botsColor;
    private RandomBot randomBot;

    // Lessons
    private Lessons lessons;
    private Runnable nextPuzzleAction;
    private Timer feedbackTimer;
    private boolean showWrongMove = false;
    private String hintSquare = null;
    private boolean puzzleSolvedShown = false;


    // Lessons constructor
    public ChessBoard(PiecesLayout layout, Game game, Runnable mainMenuAction, Runnable nextPuzzleAction) {
        this(layout, game, mainMenuAction, 'n', null);
        this.nextPuzzleAction = nextPuzzleAction;
    }

    // Player vs Player / Player vs CPU constructor
    public ChessBoard(PiecesLayout layout, Game game, Runnable mainMenuAction, char botsColor, RandomBot randomBot) {
        this.layout = layout;
        this.game = game;
        this.mainMenuAction = mainMenuAction;
        this.botsColor = botsColor;
        this.randomBot = randomBot;
        boardImage = new ImageIcon(getClass().getResource("/board/board_brown.png")).getImage();
        setPreferredSize(new Dimension(640, 640));
        setLayout(new OverlayLayout(this));


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (game.getTurn() == ChessBoard.this.botsColor) {
                    return;
                }
                int size = Math.min(getWidth(), getHeight()); // keep the board square
                int x = (getWidth() - size) / 2;              // center horizontally
                int y = (getHeight() - size) / 2;             // center vertically
                double cell = size / (double) PiecesLayout.BOARD_SIZE;
                int px = e.getX() - x;
                int py = e.getY() - y;
                int col = (int) (px / cell);
                int row = (int) (py / cell);
                int boardRow = whichCell(row, col)[0];
                int boardCol = whichCell(row, col)[1];
                if (px < 0 || py < 0 || !layout.isInside(boardRow, boardCol)) return;

                // Puzzle already solved -> ignore further clicks
                if (lessons != null && lessons.isSolved()) return;

                if (selectedRow == -1) {
                    if (game.canMove(boardRow, boardCol)) {
                        selectedRow = boardRow;
                        selectedCol = boardCol;
                        possibleMoves = game.getLegalMoves(boardRow, boardCol);
                        repaint();
                    }
                } else {
                    // Clicked the same square -> just deselect
                    if (boardRow == selectedRow && boardCol == selectedCol) {
                        selectedRow = -1;
                        selectedCol = -1;
                        possibleMoves = new ArrayList<>();
                        repaint();
                        return;
                    }

                    // Clicked another of your own pieces -> reselect instead of a failed/wrong move
                    if (game.canMove(boardRow, boardCol)) {
                        selectedRow = boardRow;
                        selectedCol = boardCol;
                        possibleMoves = game.getLegalMoves(boardRow, boardCol);
                        repaint();
                        return;
                    }

                    boolean moved;

                    if (lessons != null) {
                        moved = lessons.checkMove(
                                selectedRow, selectedCol,
                                boardRow, boardCol
                        );
                        hintSquare = null;
                        if (!moved) {
                            showWrongMove = true;
                            repaint();

                            if (feedbackTimer != null) {
                                feedbackTimer.stop();
                            }

                            feedbackTimer = new Timer(500, f -> {
                                showWrongMove = false;
                                repaint();
                            });

                            feedbackTimer.setRepeats(false);
                            feedbackTimer.start();
                        }
                    } else {
                        moved = game.tryMove(
                                selectedRow, selectedCol,
                                boardRow, boardCol
                        );
                    }

                    // In lessons, promotion is applied automatically from the puzzle's move
                    if (moved && lessons == null && game.needsPromotion(boardRow, boardCol)) {
                        showPromotionDialog(boardRow, boardCol);
                    }

                    if (lessons == null) {
                        botMove();
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
            }
        });
        if (botsColor == 'w') {
            botMove();
        }
    }

    public void botMove() {
        if (randomBot != null && game.getTurn() == botsColor) {
            int[] moves = randomBot.move();
            if (moves != null) {
                game.tryMove(moves[0], moves[1], moves[2], moves[3]);
                if (game.needsPromotion(moves[2], moves[3])) {
                    game.promote('Q');
                }
            }
        }
        repaint();
    }

    private JButton createPromotionButton(String path) {
        Image image = new ImageIcon(getClass().getResource(path)).getImage();
        image = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        return new JButton(new ImageIcon(image));
    }

    private void showPromotionDialog(int row, int col) {

        JFrame promotionPage = new JFrame("Promotion");
        promotionPage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel promotionPanel = new JPanel();
        promotionPanel.setLayout(
                new BoxLayout(promotionPanel, BoxLayout.X_AXIS)
        );

        Piece piece = layout.getPiece(row, col);
        char color = piece.getColor();

        JButton queenButton = createPromotionButton("/pieces/" + color + "Q.png");
        JButton rookButton = createPromotionButton("/pieces/" + color + "R.png");
        JButton bishopButton = createPromotionButton("/pieces/" + color + "B.png");
        JButton knightButton = createPromotionButton("/pieces/" + color + "N.png");

        // BUTTONS
        promotionPanel.add(queenButton);
        promotionPanel.add(Box.createHorizontalStrut(20));

        promotionPanel.add(rookButton);
        promotionPanel.add(Box.createHorizontalStrut(20));

        promotionPanel.add(bishopButton);
        promotionPanel.add(Box.createHorizontalStrut(20));

        promotionPanel.add(knightButton);


        // ACTIONS
        queenButton.addActionListener(e -> {
            game.promote('Q');
            botMove();
            promotionPage.dispose();
            repaint();
        });

        rookButton.addActionListener(e -> {
            game.promote('R');
            botMove();
            promotionPage.dispose();
            repaint();
        });

        bishopButton.addActionListener(e -> {
            game.promote('B');
            botMove();
            promotionPage.dispose();
            repaint();
        });

        knightButton.addActionListener(e -> {
            game.promote('N');
            botMove();
            promotionPage.dispose();
            repaint();
        });


        promotionPage.setContentPane(promotionPanel);
        promotionPage.pack();
        promotionPage.setLocationRelativeTo(this);
        promotionPage.setVisible(true);
    }


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

        mainMenu.addActionListener(e -> mainMenuAction.run());
        exitGame.addActionListener(e -> System.exit(0));

        add(gameOverPanel);
    }

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

        mainMenu.addActionListener(e -> mainMenuAction.run());
        nextPuzzle.addActionListener(e -> {
            if (nextPuzzleAction != null) {
                nextPuzzleAction.run();
            }
        });

        add(puzzlePanel);
        puzzlePanel.setVisible(true);
    }

    public boolean isFliped() {
        if (lessons != null) {
            // Flip based on the player's colour, not on whose turn it is
            return lessons.getPlayerColor() == 'b';
        }
        if (botsColor == 'w') {
            return true;   // player is black
        }
        if (botsColor == 'b') {
            return false;  // player is white
        }
        return game.getTurn() == 'b';   // Player vs Player: board follows the side to move
    }

    public int[] whichCell(int row, int col) {
        if (isFliped()) {
            return new int[]{7 - row, 7 - col};
        }
        return new int[]{row, col};
    }

    private void fillCell(Graphics g, int x, int y, double cell, int row, int col) {
        int left = x + (int) Math.round(col * cell);
        int top = y + (int) Math.round(row * cell);
        int right = x + (int) Math.round((col + 1) * cell);
        int bottom = y + (int) Math.round((row + 1) * cell);
        g.fillRect(left, top, right - left, bottom - top);
    }

    private void showHintSquare(String square, Graphics g) {
        int col = square.charAt(0) - 'a';
        int row = '8' - square.charAt(1);

        int[] screenCell = whichCell(row, col);

        int size = Math.min(getWidth(), getHeight());
        double cell = size / (double) PiecesLayout.BOARD_SIZE;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g.setColor(new Color(0, 175, 255, 120));
        fillCell(g, x, y, cell, screenCell[0], screenCell[1]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()); // keep the board square
        int x = (getWidth() - size) / 2;              // center horizontally
        int y = (getHeight() - size) / 2;             // center vertically
        double cell = size / (double) PiecesLayout.BOARD_SIZE; // fractional so the grid matches the board image
        g.drawImage(boardImage, x, y, size, size, this);
        if (selectedRow != -1) {
            g.setColor(new Color(128, 128, 128, 128));
            fillCell(g, x, y, cell, whichCell(selectedRow, selectedCol)[0], whichCell(selectedRow, selectedCol)[1]);
        }
        g.setColor(new Color(0, 200, 0, 120));           // semi-transparent green
        for (int[] m : possibleMoves) {
            fillCell(g, x, y, cell, whichCell(m[0], m[1])[0], whichCell(m[0], m[1])[1]);
        }
        for (byte i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (byte j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                Piece piece = layout.getPiece(i, j);
                if (piece != null) {
                    int left = x + (int) Math.round(whichCell(i, j)[1] * cell);
                    int top = y + (int) Math.round(whichCell(i, j)[0] * cell);
                    int right = x + (int) Math.round((whichCell(i, j)[1] + 1) * cell);
                    int bottom = y + (int) Math.round((whichCell(i, j)[0] + 1) * cell);
                    g.drawImage(piece.getIcon().getImage(), left, top, right - left, bottom - top, this);
                }
            }
        }

        // Game-over panel: only for normal games, and only created once
        GameState state = game.getState();
        if (lessons == null && gameOverPanel == null
                && (state == GameState.CHECKMATE || state == GameState.STALEMATE)) {
            createGameOverPanel(state == GameState.CHECKMATE ? "CHECKMATE" : "STALEMATE", mainMenuAction);
            gameOverPanel.setVisible(true);
            revalidate();
        }

        if (showWrongMove) {
            Graphics2D g2 = (Graphics2D) g.create();

            int rectWidth = 300;
            int rectHeight = 70;

            // Center the rectangle on the actual chessboard
            int rectX = x + (size - rectWidth) / 2;
            int rectY = y + (size - rectHeight) / 2;

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(
                    rectX, rectY,
                    rectWidth, rectHeight,
                    15, 15
            );

            // Center the text inside the rectangle
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.setColor(Color.RED);

            FontMetrics fm = g2.getFontMetrics();
            String message = "Wrong Move!";

            int textX = rectX + (rectWidth - fm.stringWidth(message)) / 2;
            int textY = rectY + (rectHeight - fm.getHeight()) / 2
                    + fm.getAscent();

            g2.drawString(message, textX, textY);
            g2.dispose();
        }
        if (hintSquare != null) {
            showHintSquare(hintSquare, g);
        }
    }

    public void setLessons(Lessons lessons) {
        this.lessons = lessons;
    }

    public void activateHint() {
        if (lessons == null) {
            return; // Hints only work in puzzles
        }

        hintSquare = lessons.getHint();
        repaint();
    }

    public void clearHint() {
        hintSquare = null;
        repaint();
    }
}
