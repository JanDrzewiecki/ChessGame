package game;

import pieces.*;

import java.util.ArrayList;
import java.util.List;

public class PiecesLayout {
    public static final int BOARD_SIZE = 8;
    private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];

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
        while (i < BOARD_SIZE) {
            for (byte j = 0; j < BOARD_SIZE; j++) {
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

    public boolean isInside(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    public List<int[]> getMovesFor(int row, int col) {
        if (getPiece(row, col) == null) {
            return new ArrayList<>();
        }
        return getPiece(row, col).getPossibleMoves(row, col, this);
    }
}
