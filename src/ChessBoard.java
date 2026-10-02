import javax.swing.*;
import java.awt.*;


public class ChessBoard extends JPanel {

    private final Image boardImage;

    public ChessBoard() {
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
    }

}
