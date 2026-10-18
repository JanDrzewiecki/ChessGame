package game;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private final int arc;

    public RoundedPanel(int arc) {
        this.arc = arc;
        setOpaque(false);
        setAlignmentX(CENTER_ALIGNMENT);   // OverlayLayout centers the card on the board
        setAlignmentY(CENTER_ALIGNMENT);
    }

    // Only as big as its content, so the board stays visible around it
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 170));                    // black tint
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        g2.setColor(new Color(255, 255, 255, 120));              // thin light outline
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();

        super.paintComponent(g);
    }
}