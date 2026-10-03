package pieces;
import javax.swing.*;
import java.awt.*;

public class Piece {
    private char color;
    private char figure;

    public Piece(char color, char figure) {
        this.color = color;
        this.figure = figure;
    }

    public ImageIcon getIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/pieces/" + this.color + Character.toUpperCase(this.figure) + ".png" ));
        return icon;
    }

}
