import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");
            frame.add(new ChessBoard());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();                      // dopasuj okno do szachownicy
            frame.setLocationRelativeTo(null); // wyśrodkuj na ekranie
            frame.setVisible(true);
        });
    }

}
