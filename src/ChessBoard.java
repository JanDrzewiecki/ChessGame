import javax.swing.*;
import java.awt.*;


public class ChessBoard {
    private static Color mix(Color a, Color b, double t) {
        return new Color(
                (int) (a.getRed()   * (1 - t) + b.getRed()   * t),
                (int) (a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) (a.getBlue()  * (1 - t) + b.getBlue()  * t)
        );
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("chess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel board = new JPanel(new GridBagLayout());
            JPanel panel = new JPanel(new GridLayout(8, 8)) {
                @Override
                public Dimension getPreferredSize() {
                    Container parent = getParent();
                    int size = Math.min(parent.getWidth(), parent.getHeight());
                    if (size == 0) size = 640;   // przy pierwszym pack() rodzic nie ma jeszcze rozmiaru
                    return new Dimension(size, size);
                }
            };
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++) {
                    if ((j + i) % 2 == 0) {
                        JButton button = new JButton();
                        button.setContentAreaFilled(false);
                        button.setOpaque(true);              // ale tło z setBackground dalej jest malowane
                        button.setBorderPainted(false);
                        button.setFocusPainted(false);
                        button.setBackground(Color.white);
                        Color base = button.getBackground();
                        Color pressed = mix(base, Color.GRAY, 0.4);   // 40% szarości

                        button.getModel().addChangeListener(e ->
                                button.setBackground(button.getModel().isPressed() ? pressed : base)
                        );
                        panel.add(button);

                    }
                    else {
                        JButton button = new JButton();
                        button.setContentAreaFilled(false);
                        button.setOpaque(true);              // ale tło z setBackground dalej jest malowane
                        button.setBorderPainted(false);
                        button.setFocusPainted(false);
                        button.setBackground(Color.black);
                        Color base = button.getBackground();
                        Color pressed = mix(base, Color.GRAY, 0.4);   // 40% szarości

                        button.getModel().addChangeListener(e ->
                                button.setBackground(button.getModel().isPressed() ? pressed : base)
                        );
                        panel.add(button);

                    }

                }

            }
            board.add(panel);
            frame.add(board);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
