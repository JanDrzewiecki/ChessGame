package game;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private final Image image;

    public BackgroundPanel(String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        image = (url != null) ? new ImageIcon(url).getImage() : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;   // falls back to the normal grey background

        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        if (iw <= 0 || ih <= 0) return;

        // Scale to cover the whole panel, keeping the aspect ratio, centered
        double scale = Math.max(getWidth() / (double) iw, getHeight() / (double) ih);
        int w = (int) Math.round(iw * scale);
        int h = (int) Math.round(ih * scale);
        int x = (getWidth() - w) / 2;
        int y = (getHeight() - h) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, x, y, w, h, this);
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}