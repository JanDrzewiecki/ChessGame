import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;

public class TestGUI {
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Chess");
        JPanel menu = new JPanel();
        JLabel title = new JLabel("Chess Game");
        title.setFont(new Font("Arial", Font.BOLD, 30));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);

        menu.add(title);
        frame.setContentPane(menu);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

