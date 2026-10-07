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
    private final Game game;


    public ChessBoard(PiecesLayout layout, Game game) {
        this.layout = layout;
        this.game = game;
        boardImage = new ImageIcon(getClass().getResource("/board/board_brown.png")).getImage();
        setPreferredSize(new Dimension(640, 640));
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
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, size / 10));
            String text = "CHECKMATE";
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + size / 2;
            g.drawString(text, tx, ty);
        } else if (state == GameState.STALEMATE) {
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, size / 10));
            String text = "STALEMATE";
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + size / 2;
            g.drawString(text, tx, ty);

        }
    }

}
