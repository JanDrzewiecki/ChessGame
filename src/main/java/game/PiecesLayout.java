package game;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

import java.util.ArrayList;
import java.util.List;

/**
 * The chess board: an 8 by 8 grid of squares, each empty or holding a piece.
 */
public class PiecesLayout {

    public static final int BOARD_SIZE = 8; // The number of rows and columns of the board.

    private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE]; // The pieces by row, column.

    /**
     * Create a new piece.
     *
     * @param color the color of the piece ('w' or 'b')
     * @param figure the letter of the piece: R, N, B, K, Q or P
     * @return the new piece, or null if the letter is unknown
     */
    public Piece createPiece(char color, char figure) {
        switch (figure) {
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
            default:
                return null;
        }
    }

    /**
     * Remove all pieces from the board.
     */
    public void clearBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
    }

    /**
     * Put all pieces on their start squares.
     */
    public void setupStartPositions() {
        String backRank = "RNBQKBNR";
        char color;
        byte i = 0;

        while (i < BOARD_SIZE) {
            for (byte j = 0; j < BOARD_SIZE; j++) {
                if (i == 0 || i == 1) {
                    color = 'b';
                } else {
                    color = 'w';
                }

                if (i == 0 || i == 7) {
                    board[i][j] = createPiece(color, backRank.charAt(j));
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

    /**
     * Read one rank (row) of a FEN position and put its pieces on the board.
     *
     * @param rank the rank in FEN notation, for example "rnbqkbnr"
     * @param rankNumber the row of the board to fill (0 is the top)
     */
    public void readFromRank(String rank, int rankNumber) {
        int col = 0;

        for (int i = 0; i < rank.length(); i++) {
            char symbol = rank.charAt(i);

            if (Character.isDigit(symbol)) {
                col += Character.getNumericValue(symbol);
            } else {
                char color;

                if (Character.isUpperCase(symbol)) {
                    color = 'w';
                } else {
                    color = 'b';
                }

                char figure = Character.toUpperCase(symbol);

                board[rankNumber][col] = createPiece(color, figure);
                col++;
            }
        }
    }

    /**
     * Set up the board from a FEN string.
     *
     * @param fen the position in FEN notation
     */
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

        applyCastlingRights(parts.length > 2 ? parts[2] : "-");
    }

    // Kings and rooks count as "moved" unless the FEN castling field says castling is possible.
    private void applyCastlingRights(String rights) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];

                if (piece instanceof King king) {
                    boolean atHome = (king.getColor() == 'w' && row == 7 && col == 4)
                            || (king.getColor() == 'b' && row == 0 && col == 4);
                    boolean canCastle;

                    if (king.getColor() == 'w') {
                        canCastle = rights.contains("K") || rights.contains("Q");
                    } else {
                        canCastle = rights.contains("k") || rights.contains("q");
                    }

                    king.setMoved(!(atHome && canCastle));
                } else if (piece instanceof Rook rook) {
                    boolean canCastle = false;

                    if (rook.getColor() == 'w' && row == 7) {
                        canCastle = (col == 7 && rights.contains("K"))
                                || (col == 0 && rights.contains("Q"));
                    } else if (rook.getColor() == 'b' && row == 0) {
                        canCastle = (col == 7 && rights.contains("k"))
                                || (col == 0 && rights.contains("q"));
                    }

                    rook.setMoved(!canCastle);
                }
            }
        }
    }

    /**
     * Get the piece on a square.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return the piece, or null if the square is empty
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Put a piece on a square (or empty the square with null).
     *
     * @param row the row of the square
     * @param col the column of the square
     * @param piece the piece to put there, or null
     */
    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    /**
     * Check if a square is on the board.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return true if the square is on the board
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Get the squares the piece on a square can move to, without checking for check.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return a list of {row, column} squares (empty if the square is empty)
     */
    public List<int[]> getMovesFor(int row, int col) {
        if (getPiece(row, col) == null) {
            return new ArrayList<>();
        }

        return getPiece(row, col).getPossibleMoves(row, col, this);
    }

    /**
     * Get the ring of a square, counted from the edge of the board.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return 1 for the outer edge up to 4 for the four central squares
     */
    public int getCircle(int row, int col) {
        return 1 + Math.min(Math.min(row, 7 - row), Math.min(col, 7 - col));
    }

    /**
     * Make an independent copy of this board (new pieces on the same squares).
     *
     * @return the copy
     */
    public PiecesLayout copy() {
        PiecesLayout copyOfLayout = new PiecesLayout();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece original = getPiece(row, col);

                if (original == null) {
                    copyOfLayout.setPiece(row, col, null);
                } else if (original instanceof King originalKing) {
                    King king = (King) createPiece(originalKing.getColor(), 'K');

                    copyOfLayout.setPiece(row, col, king);

                    if (originalKing.hasMoved()) {
                        king.setMoved(true);
                    }
                } else if (original instanceof Rook originalRook) {
                    Rook rook = (Rook) createPiece(originalRook.getColor(), 'R');

                    copyOfLayout.setPiece(row, col, rook);

                    if (originalRook.hasMoved()) {
                        rook.setMoved(true);
                    }
                } else {
                    Piece piece = createPiece(original.getColor(), original.getFigure());

                    copyOfLayout.setPiece(row, col, piece);
                }
            }
        }

        return copyOfLayout;
    }
}
