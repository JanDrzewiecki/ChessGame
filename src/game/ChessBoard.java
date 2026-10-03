package game;

import pieces.Piece;

import javax.swing.*;
import java.awt.*;


public class ChessBoard extends JPanel {

    private final Image boardImage;
    private final PiecesLayout layout;


    public ChessBoard(PiecesLayout layout) {
        this.layout = layout;
        boardImage = new ImageIcon(getClass().getResource("/board/board_brown.png")).getImage();
        setPreferredSize(new Dimension(640, 640));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()); // zachowaj kwadrat
        int x = (getWidth() - size) / 2;              // wyśrodkuj w poziomie
        int y = (getHeight() - size) / 2;             // wyśrodkuj w pionie
        g.drawImage(boardImage, x, y, size, size, this);
        int cell = size / 8;
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                Piece piece = layout.getPiece(i, j);
                if (piece != null) {
                    g.drawImage(piece.getIcon().getImage(), x + j * cell, y + i * cell, cell, cell, this);
                }
            }
        }
    }

}
