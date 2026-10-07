package game;

import pieces.Piece;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final Piece movedPiece;
    private final Piece capturedPiece;

    public Move(PiecesLayout layout, int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movedPiece = layout.getPiece(fromRow, fromCol);
        this.capturedPiece = layout.getPiece(toRow, toCol);
    }

    public void apply(PiecesLayout layout) {
        layout.setPiece(toRow, toCol, movedPiece);
        layout.setPiece(fromRow, fromCol, null);
    }

    public void undo(PiecesLayout layout) {
        layout.setPiece(fromRow, fromCol, movedPiece);
        layout.setPiece(toRow, toCol, capturedPiece);
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
