package game;

import pieces.*;

public class PiecesLayout {
    private final Piece[][] board = new Piece[8][8];

    public Piece createPiece (char color, char piece) {
        switch (piece) {
            case 'R':
                return new Rook(color);
            case 'N':
                return new Knight(color);
            case 'B':
                return new Bishop(color);
            case 'K':
                return new King(color);
            case 'Q':
                return new Queen(color);
            case 'P':
                return new Pawn(color);
        }
        return null;
    }

    public void setupStartPositions() {
        String piecesChars = "RNBQKBNR";
        char color;
        byte i = 0;
        while (i < 8) {
            for (byte j = 0; j < 8; j++) {
                if (i == 0 || i == 1) {
                    color = 'b';
                }
                else {
                    color = 'w';
                }
                if (i == 0 || i == 7) {
                    board[i][j] = createPiece(color, piecesChars.charAt(j));
                } else {
                    board[i][j] = createPiece(color, 'P');
                }
            }
            i++;
            if (i == 2) {
                i = 6;
            }
        }
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void movePiece(int currRow, int currCol, int toRow, int toCol) {
        board[toRow][toCol] = board[currRow][currCol];
        board[currRow][currCol] = null;
    }
}
