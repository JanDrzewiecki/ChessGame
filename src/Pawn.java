import javax.swing.*;
import java.awt.*;

public class Pawn {
    boolean isWhite;
    private Image image;

    public void getImage() {
        if (isWhite) {
            image = new ImageIcon("src/assets/white-pawn.png").getImage();
        } else {
            image = new ImageIcon("src/assets/black-pawn.png").getImage();
        }
    }

}
