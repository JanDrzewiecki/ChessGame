package pieces;
import game.PiecesLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class Piece {
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

    public char getColor() {
        return color;
    }

    public abstract List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout);
}
