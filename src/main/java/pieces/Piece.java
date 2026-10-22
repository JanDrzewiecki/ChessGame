package pieces;

import game.PiecesLayout;

import javax.swing.ImageIcon;
import java.util.List;

/**
 * A chess piece with a color and a figure, which knows how it moves.
 */
public abstract class Piece {

    private char color; // The color of the piece: 'w' for white, 'b' for black.
    private char figure; // The letter of the piece: K, Q, R, B, N or P.

    /**
     * Create a piece.
     *
     * @param color the color of the piece ('w' or 'b')
     * @param figure the letter of the piece
     */
    public Piece(char color, char figure) {
        this.color = color;
        this.figure = figure;
    }

    /**
     * Get the picture of this piece.
     *
     * @return the icon of the piece
     */
    public ImageIcon getIcon() {
        String path = "/pieces/" + this.color + Character.toUpperCase(this.figure) + ".png";

        return new ImageIcon(getClass().getResource(path));
    }

    /**
     * Get the color of this piece.
     *
     * @return 'w' for white or 'b' for black
     */
    public char getColor() {
        return color;
    }

    /**
     * Get the squares this piece can move to, without checking if the own king stays safe.
     *
     * @param row the row of the piece
     * @param col the column of the piece
     * @param layout the board
     * @return a list of {row, column} squares
     */
    public abstract List<int[]> getPossibleMoves(int row, int col, PiecesLayout layout);

    /**
     * Get the value of this piece in points.
     *
     * @return the value of the piece
     */
    public abstract int getPoint();

    /**
     * Get the letter of this piece.
     *
     * @return K, Q, R, B, N or P
     */
    public abstract char getFigure();
}
