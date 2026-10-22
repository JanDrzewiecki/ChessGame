package ui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

/**
 * A panel with a picture as background, darkened a little.
 */
public class BackgroundPanel extends JPanel {

    private final Image image; // The background picture, or null if it could not be found.

    /**
     * Create a panel with a background picture.
     *
     * @param resourcePath the path of the picture in the resources, for example "/a/b.jpg"
     */
    public BackgroundPanel(String resourcePath) {
        URL url = getClass().getResource(resourcePath);

        image = (url != null) ? new ImageIcon(url).getImage() : null;
    }

    /**
     * Draw the picture, scaled to cover the whole panel, and a dark layer over it.
     *
     * @param graphics the graphics to draw on
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (image == null) {
            return; // Falls back to the normal grey background.
        }

        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);

        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        // Scale to cover the whole panel, keeping the aspect ratio, centered.
        double scale = Math.max(getWidth() / (double) imageWidth,
                getHeight() / (double) imageHeight);
        int scaledWidth = (int) Math.round(imageWidth * scale);
        int scaledHeight = (int) Math.round(imageHeight * scale);
        int x = (getWidth() - scaledWidth) / 2;
        int y = (getHeight() - scaledHeight) / 2;

        Graphics2D graphics2d = (Graphics2D) graphics.create();

        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.drawImage(image, x, y, scaledWidth, scaledHeight, this);
        graphics2d.setColor(new Color(0, 0, 0, 100));
        graphics2d.fillRect(0, 0, getWidth(), getHeight());
        graphics2d.dispose();
    }
}
