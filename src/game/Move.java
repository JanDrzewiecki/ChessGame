package game;

import pieces.King;
import pieces.Piece;
import pieces.Rook;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private boolean wasMoved = false;

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
        if (movedPiece instanceof Rook r) {
            if(r.hasMoved()) {
                wasMoved = true;
            }
            r.setMoved(true);
        } else if (movedPiece instanceof King k) {
            if (k.hasMoved()) {
                wasMoved = true;
            }
            k.setMoved(true);
        }
    }

    public void undo(PiecesLayout layout) {
        layout.setPiece(fromRow, fromCol, movedPiece);
        layout.setPiece(toRow, toCol, capturedPiece);
        if (movedPiece instanceof King king) {
            king.setMoved(wasMoved);
        } else if (movedPiece instanceof Rook rook) {
            rook.setMoved(wasMoved);
        }
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
