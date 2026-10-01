import javax.swing.*;
import java.awt.*;


public class ChessBoard {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("chess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(8, 8));
            for (int i = 0; i < 8; i++){
                if (i % 2 == 0) {
                    JButton button = new JButton();
                    button.setBackground(Color.white);
                    panel.add(button);
                }
                else {
                    JButton button = new JButton();

                    button.setBackground(Color.black);
                    panel.add(button);
                }
                for (int j = 1; j < 8; j++) {
                    if (j % 2 == 0) {
                        JButton button = new JButton();
                        button.setBackground(Color.white);
                        panel.add(button);
                    }
                    else {
                        JButton button = new JButton();

                        button.setBackground(Color.black);
                        panel.add(button);
                    }
                }

            }
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
