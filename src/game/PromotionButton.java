package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PromotionButton extends JButton {

    private boolean hovered = false;

    public PromotionButton(ImageIcon icon) {
        super(icon);
        setContentAreaFilled(false);   // we paint the background ourselves
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }

            @Override
            public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int alpha = getModel().isPressed() ? 220 : hovered ? 190 : 150;   // black tint
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.setColor(new Color(255, 255, 255, 120));                       // thin light outline
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();

        super.paintComponent(g);   // draws the white text
    }
}
