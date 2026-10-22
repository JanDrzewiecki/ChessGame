package ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A panel with a dark, rounded background.
 */
public class RoundedPanel extends JPanel {

    private final int arc; // The size of the rounded corners in pixels.

    /**
     * Create a panel.
     *
     * @param arc the size of the rounded corners in pixels
     */
    public RoundedPanel(int arc) {
        this.arc = arc;
        setOpaque(false);
        setAlignmentX(CENTER_ALIGNMENT); // OverlayLayout centers the card on the board.
        setAlignmentY(CENTER_ALIGNMENT);
    }

    /**
     * Get the biggest size: only as big as the content, so the board stays visible around it.
     *
     * @return the preferred size
     */
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    /**
     * Draw the rounded background and outline.
     *
     * @param graphics the graphics to draw on
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2d = (Graphics2D) graphics.create();

        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2d.setColor(new Color(0, 0, 0, 170)); // A black tint.
        graphics2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        graphics2d.setColor(new Color(255, 255, 255, 120)); // A thin light outline.
        graphics2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        graphics2d.dispose();

        super.paintComponent(graphics);
    }
}
