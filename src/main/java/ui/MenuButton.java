package ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A button of the menu: a dark, rounded button that gets darker when the mouse is over it.
 */
public class MenuButton extends JButton {

    private boolean hovered = false; // Whether the mouse is over the button.

    /**
     * Create a button.
     *
     * @param text the text on the button
     */
    public MenuButton(String text) {
        super(text);
        setContentAreaFilled(false); // We paint the background ourselves.
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            /** Darken the button while the mouse is over it. */
            @Override
            public void mouseEntered(MouseEvent event) {
                hovered = true;
                repaint();
            }

            /** Show the normal button again when the mouse leaves it. */
            @Override
            public void mouseExited(MouseEvent event) {
                hovered = false;
                repaint();
            }
        });
    }

    /**
     * Draw the rounded background and outline, then the text.
     *
     * @param graphics the graphics to draw on
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2d = (Graphics2D) graphics.create();

        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int alpha = 150; // How dark the button is (0 is see-through, 255 is solid).

        if (getModel().isPressed()) {
            alpha = 220;
        } else if (hovered) {
            alpha = 190;
        }

        graphics2d.setColor(new Color(0, 0, 0, alpha));
        graphics2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        graphics2d.setColor(new Color(255, 255, 255, 120)); // A thin light outline.
        graphics2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        graphics2d.dispose();

        super.paintComponent(graphics); // Draws the white text.
    }
}
