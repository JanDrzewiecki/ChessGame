package game;

import pieces.*;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private boolean wasMoved = false;
    private Piece wasPassant;
    private boolean castled = false;

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
        if (Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 1 && capturedPiece == null && movedPiece instanceof Pawn pawn) {
            if (toCol == fromCol + 1) {
                wasPassant = layout.getPiece(fromRow, toCol);
                layout.setPiece(fromRow, fromCol, null);
                layout.setPiece(fromRow, fromCol + 1, null);
            } else {
                wasPassant = layout.getPiece(fromRow, toCol);
                layout.setPiece(fromRow, fromCol, null);
                layout.setPiece(fromRow, fromCol - 1, null);
            }
        }
        else {
            layout.setPiece(fromRow, fromCol, null);
            if (movedPiece instanceof Rook r) {
                if (r.hasMoved()) {
                    wasMoved = true;
                }
                r.setMoved(true);
            } else if (movedPiece instanceof King k) {
                if (fromRow == toRow && Math.abs(fromCol - toCol) == 2) {
                    int rookFromCol = toCol > fromCol ? 7 : 0;
                    int rookToCol = (fromCol + toCol) / 2;
                    if (layout.getPiece(fromRow, rookFromCol) instanceof Rook rook && rook.getColor() == k.getColor()) {
                        layout.setPiece(fromRow, rookToCol, rook);
                        layout.setPiece(fromRow, rookFromCol, null);
                        rook.setMoved(true);
                        castled = true;
                    }
                }
                if (k.hasMoved()) {
                    wasMoved = true;
                }
                k.setMoved(true);
            }
        }
    }

    public void undo(PiecesLayout layout) {
        layout.setPiece(fromRow, fromCol, movedPiece);
        if (wasPassant != null) {
            layout.setPiece(fromRow, toCol, wasPassant);
        }
        layout.setPiece(toRow, toCol, capturedPiece);
        if (castled) {
            int rookFromCol = toCol > fromCol ? 7 : 0;
            int rookToCol = (fromCol + toCol) / 2;
            if (layout.getPiece(fromRow, rookToCol) instanceof Rook rook) {
                layout.setPiece(fromRow, rookFromCol, rook);
                layout.setPiece(fromRow, rookToCol, null);
                rook.setMoved(false);
            }
            castled = false;
        }
        if (movedPiece instanceof King king) {
            king.setMoved(wasMoved);
        } else if (movedPiece instanceof Rook rook) {
            rook.setMoved(wasMoved);
        }
    }

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
        }    }

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
