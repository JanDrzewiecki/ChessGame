import javax.swing.*;
import java.awt.*;


public class ChessBoard {
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
                        button.setBorderPainted(false);   // bez obramówki
                        button.setFocusPainted(false);
                        button.setBackground(Color.white);
                        panel.add(button);
                    }
                    else {
                        JButton button = new JButton();
                        button.setBorderPainted(false);   // bez obramówki
                        button.setFocusPainted(false);
                        button.setBackground(Color.black);
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
