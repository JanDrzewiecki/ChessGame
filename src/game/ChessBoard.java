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
                int cell = size / PiecesLayout.BOARD_SIZE;
                int px = e.getX() - x;
                int py = e.getY() - y;
                int col = px / cell;
                int row = py / cell;
                if (px < 0 || py < 0 || !layout.isInside(row, col)) return;
                if (selectedRow == -1) {
                    if (game.canMove(row, col)) {
                        selectedRow = row;
                        selectedCol = col;
                        possibleMoves = game.getLegalMoves(row, col);
                        repaint();
                    }
                } else {
                    game.tryMove(selectedRow, selectedCol, row, col);
                    selectedRow = -1;
                    selectedCol = -1;
                    possibleMoves = new ArrayList<>();
                    repaint();
                }
            }
        });
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()); // zachowaj kwadrat
        int x = (getWidth() - size) / 2;              // wyśrodkuj w poziomie
        int y = (getHeight() - size) / 2;
        int cell = size / PiecesLayout.BOARD_SIZE;
        g.drawImage(boardImage, x, y, size, size, this);
        if (selectedRow != -1) {
            g.setColor(new Color(128, 128, 128, 128));
            g.fillRect(x + selectedCol * cell, y + selectedRow * cell, cell, cell);
        }
        g.setColor(new Color(0, 200, 0, 120));           // półprzezroczysty zielony
        for (int[] m : possibleMoves) {
            g.fillRect(x + m[1] * cell, y + m[0] * cell, cell, cell);
        }
        for (byte i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (byte j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                Piece piece = layout.getPiece(i, j);
                if (piece != null) {
                    g.drawImage(piece.getIcon().getImage(), x + j * cell, y + i * cell, cell, cell, this);
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
