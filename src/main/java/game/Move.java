package game;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

/**
 * One move on the board. It can be applied and undone, including castling, en passant and
 * promotion.
 */
public class Move {

    private final int fromRow; // The row the piece moves from.
    private final int fromCol; // The column the piece moves from.
    private final int toRow; // The row the piece moves to.
    private final int toCol; // The column the piece moves to.
    private final Piece movedPiece; // The piece that moves.
    private final Piece capturedPiece; // The piece on the target square before the move, or null.
    private boolean wasMoved = false; // Whether the king or rook had already moved before.
    private Piece wasPassant; // The pawn captured en passant, or null.
    private boolean castled = false; // Whether this move also moved a rook (castling).
    private char promotionType; // The piece a pawn promotes to, or 'n' for no promotion.

    /**
     * Create a move. The pieces are read from the board at this moment.
     *
     * @param layout the board
     * @param fromRow the row of the moving piece
     * @param fromCol the column of the moving piece
     * @param toRow the row to move to
     * @param toCol the column to move to
     * @param promotionType Q, R, B or N for a promotion, or 'n' for no promotion
     */
    public Move(PiecesLayout layout, int fromRow, int fromCol, int toRow, int toCol,
            char promotionType) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movedPiece = layout.getPiece(fromRow, fromCol);
        this.capturedPiece = layout.getPiece(toRow, toCol);
        this.promotionType = promotionType;
    }

    /**
     * Do this move on the board (including the rook of castling or the pawn of en passant).
     *
     * @param layout the board
     */
    public void apply(PiecesLayout layout) {
        layout.setPiece(toRow, toCol, movedPiece);

        boolean diagonalStep = Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 1;
        boolean enPassant = diagonalStep && capturedPiece == null && movedPiece instanceof Pawn;

        if (enPassant) {
            wasPassant = layout.getPiece(fromRow, toCol);
            layout.setPiece(fromRow, fromCol, null);
            layout.setPiece(fromRow, toCol, null);
        } else {
            layout.setPiece(fromRow, fromCol, null);

            if (movedPiece instanceof Rook movedRook) {
                if (movedRook.hasMoved()) {
                    wasMoved = true;
                }

                movedRook.setMoved(true);
            } else if (movedPiece instanceof King movedKing) {
                if (fromRow == toRow && Math.abs(fromCol - toCol) == 2) {
                    castleRook(layout, movedKing);
                }

                if (movedKing.hasMoved()) {
                    wasMoved = true;
                }

                movedKing.setMoved(true);
            }
        }
    }

    // Move the rook next to the king when the king castles.
    private void castleRook(PiecesLayout layout, King king) {
        int rookFromCol = toCol > fromCol ? 7 : 0;
        int rookToCol = (fromCol + toCol) / 2;

        if (layout.getPiece(fromRow, rookFromCol) instanceof Rook rook
                && rook.getColor() == king.getColor()) {
            layout.setPiece(fromRow, rookToCol, rook);
            layout.setPiece(fromRow, rookFromCol, null);
            rook.setMoved(true);
            castled = true;
        }
    }

    /**
     * Take this move back on the board. The board must be as it was right after apply.
     *
     * @param layout the board
     */
    public void undo(PiecesLayout layout) {
        layout.setPiece(fromRow, fromCol, movedPiece);

        if (wasPassant != null) {
            layout.setPiece(fromRow, toCol, wasPassant);
        }

        layout.setPiece(toRow, toCol, capturedPiece);

        if (castled) {
            int rookFromCol = toCol > fromCol ? 7 : 0;
            int rookToCol = (fromCol + toCol) / 2;

            if (layout.getPiece(fromRow, rookToCol) instanceof Rook castledRook) {
                layout.setPiece(fromRow, rookFromCol, castledRook);
                layout.setPiece(fromRow, rookToCol, null);
                castledRook.setMoved(false);
            }

            castled = false;
        }

        if (movedPiece instanceof King movedKing) {
            movedKing.setMoved(wasMoved);
        } else if (movedPiece instanceof Rook movedRook) {
            movedRook.setMoved(wasMoved);
        }
    }

    /**
     * Replace the pawn on the target square by a new piece (call this after apply).
     *
     * @param layout the board
     * @param type Q, R, B or N
     */
    public void promoteTo(PiecesLayout layout, char type) {
        char color = layout.getPiece(toRow, toCol).getColor();

        switch (type) {
            case 'Q':
                layout.setPiece(toRow, toCol, new Queen(color));
                break;
            case 'R':
                layout.setPiece(toRow, toCol, new Rook(color));
                break;
            case 'B':
                layout.setPiece(toRow, toCol, new Bishop(color));
                break;
            case 'N':
                layout.setPiece(toRow, toCol, new Knight(color));
                break;
            default:
                break;
        }
    }

    /**
     * Get the row the piece moves from.
     *
     * @return the row
     */
    public int getFromRow() {
        return fromRow;
    }

    /**
     * Get the column the piece moves from.
     *
     * @return the column
     */
    public int getFromCol() {
        return fromCol;
    }

    /**
     * Get the row the piece moves to.
     *
     * @return the row
     */
    public int getToRow() {
        return toRow;
    }

    /**
     * Get the column the piece moves to.
     *
     * @return the column
     */
    public int getToCol() {
        return toCol;
    }

    /**
     * Get the piece that moves.
     *
     * @return the moved piece
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    /**
     * Get the piece that was on the target square before the move.
     *
     * @return the captured piece, or null if the square was empty
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Get the piece a pawn promotes to with this move.
     *
     * @return Q, R, B or N, or 'n' if this move has no promotion
     */
    public char getPromotionType() {
        return promotionType;
    }
}
