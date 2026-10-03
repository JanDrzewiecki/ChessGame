import game.ChessBoard;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");
            PiecesLayout layout = new PiecesLayout();
            layout.setupStartPositions();
            frame.add(new ChessBoard(layout));


            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();                      // dopasuj okno do szachownicy
            frame.setLocationRelativeTo(null); // wyśrodkuj na ekranie
            frame.setVisible(true);
        });
    }

}
