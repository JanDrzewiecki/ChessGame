package game;

import pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;


public class ChessBoard extends JPanel {

    private final Image boardImage;
    private final PiecesLayout layout;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<int[]> possibleMoves = new ArrayList<>();
    private  Game game;
    private JPanel gameOverPanel;
    private Runnable mainMenuAction;


    public ChessBoard(PiecesLayout layout, Game game, Runnable mainMenuAction) {
        this.layout = layout;
        this.game = game;
        this.mainMenuAction = mainMenuAction;
        boardImage = new ImageIcon(getClass().getResource("/board/board_brown.png")).getImage();
        setPreferredSize(new Dimension(640, 640));
        setLayout(new OverlayLayout(this));



        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int size = Math.min(getWidth(), getHeight()); // zachowaj kwadrat
                int x = (getWidth() - size) / 2;              // wyśrodkuj w poziomie
                int y = (getHeight() - size) / 2;             // wyśrodkuj w pionie
                double cell = size / (double) PiecesLayout.BOARD_SIZE;
                int px = e.getX() - x;
                int py = e.getY() - y;
                int col = (int) (px / cell);
                int row = (int) (py / cell);
                int boardRow = whichCell(row, col)[0];
                int boardCol = whichCell(row, col)[1];
                if (px < 0 || py < 0 || !layout.isInside(boardRow, boardCol)) return;
                if (selectedRow == -1) {
                    if (game.canMove(boardRow, boardCol)) {
                        selectedRow = boardRow;
                        selectedCol = boardCol;
                        possibleMoves = game.getLegalMoves(boardRow, boardCol);
                        repaint();
                    }
                } else {
                    boolean moved = game.tryMove(
                            selectedRow, selectedCol, boardRow, boardCol
                    );

                    if (moved && game.needsPromotion(boardRow, boardCol)) {
                        showPromotionDialog(boardRow, boardCol);
                    }
                    selectedRow = -1;
                    selectedCol = -1;
                    possibleMoves = new ArrayList<>();
                    repaint();
                }
            }
        });
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

        // QUEEN
        String queenPath = color == 'w'
                ? "/pieces/wQ.png"
                : "/pieces/bQ.png";

        Image imageQ = new ImageIcon(
                getClass().getResource(queenPath)
        ).getImage();

        imageQ = imageQ.getScaledInstance(
                80, 80, Image.SCALE_SMOOTH
        );

        JButton queenButton = new JButton(
                new ImageIcon(imageQ)
        );


        // ROOK
        String rookPath = color == 'w'
                ? "/pieces/wR.png"
                : "/pieces/bR.png";

        Image imageR = new ImageIcon(
                getClass().getResource(rookPath)
        ).getImage();

        imageR = imageR.getScaledInstance(
                80, 80, Image.SCALE_SMOOTH
        );

        JButton rookButton = new JButton(
                new ImageIcon(imageR)
        );


        // BISHOP
        String bishopPath = color == 'w'
                ? "/pieces/wB.png"
                : "/pieces/bB.png";

        Image imageB = new ImageIcon(
                getClass().getResource(bishopPath)
        ).getImage();

        imageB = imageB.getScaledInstance(
                80, 80, Image.SCALE_SMOOTH
        );

        JButton bishopButton = new JButton(
                new ImageIcon(imageB)
        );


        // KNIGHT
        String knightPath = color == 'w'
                ? "/pieces/wN.png"
                : "/pieces/bN.png";

        Image imageN = new ImageIcon(
                getClass().getResource(knightPath)
        ).getImage();

        imageN = imageN.getScaledInstance(
                80, 80, Image.SCALE_SMOOTH
        );

        JButton knightButton = new JButton(
                new ImageIcon(imageN)
        );


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
            game.promote(row, col, 'Q');
            promotionPage.dispose();
            repaint();
        });

        rookButton.addActionListener(e -> {
            game.promote(row, col, 'R');
            promotionPage.dispose();
            repaint();
        });

        bishopButton.addActionListener(e -> {
            game.promote(row, col, 'B');
            promotionPage.dispose();
            repaint();
        });

        knightButton.addActionListener(e -> {
            game.promote(row, col, 'N');
            promotionPage.dispose();
            repaint();
        });


        promotionPage.setContentPane(promotionPanel);
        promotionPage.pack();
        promotionPage.setLocationRelativeTo(this);
        promotionPage.setVisible(true);
    }


    private void createGameOverPanel(String result, Runnable mainMenuAction) {

        gameOverPanel = new JPanel();
        gameOverPanel.setOpaque(false);


        gameOverPanel.setLayout(
                new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS)
        );

        JLabel resultLabel = new JLabel();
        resultLabel.setFont(
                new Font("SansSerif", Font.BOLD, 50)
        );
        resultLabel.setForeground(Color.RED);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setText(result);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        JButton mainMenu = new JButton("Main Menu");
        JButton exitGame = new JButton("Exit Game");

        buttons.add(mainMenu);
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(exitGame);

        gameOverPanel.add(Box.createVerticalGlue());
        gameOverPanel.add(resultLabel);
        gameOverPanel.add(Box.createVerticalStrut(30));
        gameOverPanel.add(buttons);
        gameOverPanel.add(Box.createVerticalGlue());

        gameOverPanel.setVisible(false);

        mainMenu.addActionListener(e -> {

            mainMenuAction.run();

        });
        exitGame.addActionListener(e -> System.exit(0));

        add(gameOverPanel);
    }

    public boolean isFliped() {
        if (game.getTurn() == 'b') {
            return true;
        }
        return false;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()); // zachowaj kwadrat
        int x = (getWidth() - size) / 2;              // wyśrodkuj w poziomie
        int y = (getHeight() - size) / 2;
        double cell = size / (double) PiecesLayout.BOARD_SIZE; // ułamkowe, żeby siatka pasowała do obrazka planszy
        g.drawImage(boardImage, x, y, size, size, this);
        if (selectedRow != -1) {
            g.setColor(new Color(128, 128, 128, 128));
            fillCell(g, x, y, cell, whichCell(selectedRow, selectedCol)[0],whichCell(selectedRow, selectedCol)[1]);
        }
        g.setColor(new Color(0, 200, 0, 120));           // półprzezroczysty zielony
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
        GameState state = game.getState();
        if (state == GameState.CHECKMATE) {
            createGameOverPanel("CHECKMATE", mainMenuAction);
            gameOverPanel.setVisible(true);

        } else if (state == GameState.STALEMATE) {
            createGameOverPanel("STALEMATE", mainMenuAction);
            gameOverPanel.setVisible(true);

        }
    }

}
