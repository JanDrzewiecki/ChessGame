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

    public void clearBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
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

    public void readFromRank(String rank, int rankNumber) {
        int col = 0;

        for (int i = 0; i < rank.length(); i++) {
            char fig = rank.charAt(i);

            if (Character.isDigit(fig)) {
                col += Character.getNumericValue(fig);
            } else {
                char color;
                if (Character.isUpperCase(fig)) {
                    color = 'w';
                } else {
                    color = 'b';
                }

                char piece = Character.toUpperCase(fig);

                board[rankNumber][col] = createPiece(color, piece);
                col++;
            }
        }
    }

    public void setupFromFEN(String fen) {
        String[] parts = fen.split(" ");
        String position = parts[0];

        for (int i = 0; i < 8; i++) {
            int slashIndex = position.indexOf('/');

            String rank;

            if (slashIndex != -1) {
                rank = position.substring(0, slashIndex);
                position = position.substring(slashIndex + 1);
            } else {
                rank = position;
            }

            readFromRank(rank, i);
        }
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
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
