package game;

import pieces.King;
import pieces.Piece;
import pieces.Rook;
import pieces.Knight;
import pieces.Bishop;
import pieces.Queen;
import pieces.Pawn;


import java.util.ArrayList;
import java.util.List;

public class Game {
    private char round = 'w';
    private final PiecesLayout layout;
    private final List<Move> history = new ArrayList<>();
    private GameState state = GameState.PLAYING;
    private Move pendingMove;

    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    public char getTurn() {return round;}

    public GameState getState() {return state;}

    public void setRound(char round) {
        if (round != 'w' && round != 'b') {
            throw new IllegalArgumentException("Round must be 'w' or 'b'");
        }
        this.round = round;
    }

    public int[] getKing(char color) {
        for (int i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (int j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                if (layout.getPiece(i, j) instanceof King && color == layout.getPiece(i, j).getColor()) {
                    return new int[]{i, j};
                }
            }
        }

        return null;
    }

    public List<int[]> getLegalMoves(int row, int col) {
        Piece piece = layout.getPiece(row, col);
        if (piece == null) {
            return new ArrayList<>();
        }
        List<int[]> legalMoves = new ArrayList<>();
        for (int[] m : piece.getPossibleMoves(row, col, layout)) {
            Move move = new Move(layout, row, col, m[0], m[1], 'n');
            move.apply(layout);
            boolean safe = !isCheck(piece.getColor());
            move.undo(layout);
            if (safe) {
                legalMoves.add(m);
            }
        }
        if (layout.getPiece(row, col) instanceof King king) {
            for (int[] m: casttlings(row, col, king.getColor())) {
                legalMoves.add(m);
            }
        }
        if (layout.getPiece(row, col) instanceof Pawn pawn) {
            int[] pass = enPassant(row, col);
            if (pass.length != 0) {
                Move move = new Move(layout, row, col, pass[0], pass[1], 'n');
                move.apply(layout);
                boolean safe = !isCheck(piece.getColor());
                move.undo(layout);
                if (safe) {
                    legalMoves.add(pass);
                }
            }
        }
        return legalMoves;
    }

    public List<int[]> casttlings(int kingsRow, int kingsCol, char color) {
        List<int[]> castles = new ArrayList<>();
        if (!(layout.getPiece(kingsRow, kingsCol) instanceof King king)) {
            return castles;
        }
        if (isCheck(color)) {
            return castles;
        }
        if (king.hasMoved() || kingsCol != 4) {
            return castles;
        }
        if (layout.getPiece(kingsRow, kingsCol + 1) == null &&
                layout.getPiece(kingsRow, kingsCol + 2) == null &&
                layout.getPiece(kingsRow, kingsCol + 3) instanceof Rook rook) {
            boolean cast = true;
            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol + i, 'n');
                    move.apply(layout);
                    if (isCheck(color)) {
                        cast = false;
                    }
                    move.undo(layout);
                }
                if (cast) {
                    castles.add(new int[]{kingsRow, kingsCol + 2});
                }
            }
        }
        if (layout.getPiece(kingsRow, kingsCol - 1) == null &&
                layout.getPiece(kingsRow, kingsCol - 2) == null &&
                layout.getPiece(kingsRow, kingsCol - 3) == null &&
                layout.getPiece(kingsRow, kingsCol - 4) instanceof Rook rook) {
            boolean cast = true;
            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol - i, 'n');
                    move.apply(layout);
                    if (isCheck(color)) {
                        cast = false;
                    }
                    move.undo(layout);
                }
                if (cast) {
                    castles.add(new int[]{kingsRow, kingsCol - 2});
                }
            }
        }
        return castles;
    }

    public int[] enPassant(int row, int col) {
        if (!(layout.getPiece(row, col) instanceof Pawn pawn1)) {
            return new int[]{};
        }
        if (pawn1.getColor() == 'w') {
            if (layout.isInside(row, col + 1)) {
                if (layout.getPiece(row, col + 1) instanceof Pawn pawn2) {
                    if (pawn2.getColor() != pawn1.getColor()) {
                        if (history.isEmpty()) {
                            return new int[]{};
                        }
                        Move move = history.getLast();
                        if (move.getToRow() == row && move.getToCol() == col + 1) {
                            if (move.getMovedPiece() instanceof Pawn) {
                                if (move.getToRow() - move.getFromRow() == 2) {
                                    return new int[]{row - 1, col + 1};
                                }
                            }
                        }
                    }
                }
            }
            if (layout.isInside(row, col - 1)) {
                if (layout.getPiece(row, col - 1) instanceof Pawn pawn2) {
                    if (pawn2.getColor() != pawn1.getColor()) {
                        if (history.isEmpty()) {
                            return new int[]{};
                        }
                        Move move = history.getLast();
                        if (move.getToRow() == row && move.getToCol() == col - 1) {
                            if (move.getMovedPiece() instanceof Pawn) {
                                if (move.getToRow() - move.getFromRow() == 2) {
                                    return new int[]{row - 1, col - 1};
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (layout.isInside(row, col + 1)) {
                if (layout.getPiece(row, col + 1) instanceof Pawn pawn2) {
                    if (pawn2.getColor() != pawn1.getColor()) {
                        if (history.isEmpty()) {
                            return new int[]{};
                        }
                        Move move = history.getLast();
                        if (move.getToRow() == row && move.getToCol() == col + 1) {
                            if (move.getMovedPiece() instanceof Pawn) {
                                if (move.getFromRow() - move.getToRow() == 2) {
                                    return new int[]{row + 1, col + 1};
                                }
                            }
                        }
                    }
                }
            }
            if (layout.isInside(row, col - 1)) {
                if (layout.getPiece(row, col - 1) instanceof Pawn pawn2) {
                    if (pawn2.getColor() != pawn1.getColor()) {
                        if (history.isEmpty()) {
                            return new int[]{};
                        }
                        Move move = history.getLast();
                        if (move.getToRow() == row && move.getToCol() == col - 1) {
                            if (move.getMovedPiece() instanceof Pawn) {
                                if (move.getFromRow() - move.getToRow() == 2) {
                                    return new int[]{row + 1, col - 1};
                                }
                            }
                        }
                    }
                }
            }
        }
        return new int[]{};
    }

    public boolean canMove(int row, int col) {
        if (pendingMove != null) {
            return false;
        }
        Piece piece = layout.getPiece(row, col);
        return piece != null && piece.getColor() == getTurn();
    }

    public boolean isCheck(char color) {
        if (getKing(color) == null) {
            return false;
        }
        int row = getKing(color)[0];
        int col = getKing(color)[1];

        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                if (layout.getPiece(r, c) == null || layout.getPiece(r, c).getColor() == color) {
                    continue;
                } else {
                    List<int[]> possibleMoves = layout.getMovesFor(r, c);
                    for (int[] i : possibleMoves) {
                        if (i[0] == row && i[1] == col) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public boolean hasAnyLegalMove(char color) {
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                if (layout.getPiece(r, c) == null || layout.getPiece(r, c).getColor() != color) {
                    continue;
                } else {
                    if (!getLegalMoves(r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public GameState evaluateState(char color) {
        if (hasAnyLegalMove(color)) {
            return GameState.PLAYING;
        }
        return isCheck(color) ? GameState.CHECKMATE : GameState.STALEMATE;
    }

    public void promote(char PieceType) {
        if (pendingMove != null) {
            pendingMove.promoteTo(layout, PieceType);
            history.add(pendingMove);
            pendingMove = null;

            if (round == 'w') {
                round = 'b';
            } else {
                round = 'w';
            }

            state = evaluateState(round);
        }
    }


    public boolean needsPromotion(int row, int col) {
        Piece piece = layout.getPiece(row, col);

        return piece instanceof Pawn &&
                (row == 0 || row == PiecesLayout.BOARD_SIZE - 1);
    }

    public boolean tryMove(int selectedRow, int selectedCol, int toRow, int toCol) {
        if (pendingMove != null) {
            return false;
        }

        if (state != GameState.PLAYING || !canMove(selectedRow, selectedCol)) {
            return false;
        }
        List<int[]> possibleMoves = getLegalMoves(selectedRow, selectedCol);
        for (int[] i : possibleMoves) {
            if (toRow == i[0] && toCol == i[1]) {
                Move move = new Move(layout, selectedRow, selectedCol, toRow, toCol, 'n');


                move.apply(layout);

                Piece piece = layout.getPiece(toRow, toCol);

                // Promotion required
                if (piece instanceof Pawn &&
                        (toRow == 0 || toRow == PiecesLayout.BOARD_SIZE - 1)) {

                    pendingMove = move;
                    return true;
                }

                // Normal move
                history.add(move);
                if(round == 'w') {
                    round = 'b';
                } else {
                    round = 'w';
                }
                state = evaluateState(round);
                return true;
            }
        }
        return false;
    }

    public void makeMove(Move move) {
        move.apply(layout);
        if (move.getPromotionType() != 'n') {
            move.promoteTo(layout, move.getPromotionType());
        }
        history.add(move);
        if (round == 'w') {
            round = 'b';
        } else {
            round = 'w';
        }
    }

    public void undoMove() {
        if (!history.isEmpty()) {
            Move move = history.getLast();
            history.remove(history.size() - 1);
            move.undo(layout);
            if (round == 'w') {
                round = 'b';
            } else {
                round = 'w';
            }
        }
    }

    public List<Move> getAllMoves(char color) {
        List<Move> allMoves = new ArrayList<>();
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);
                if (piece != null && piece.getColor() == color) {
                    for (int[] m: getLegalMoves(r, c)) {
                        if (piece instanceof Pawn && (m[0] == 0 || m[0] == 7)) {
                            allMoves.add(new Move(layout, r, c, m[0], m[1], 'Q'));
                            allMoves.add(new Move(layout, r, c, m[0], m[1], 'N'));
                            allMoves.add(new Move(layout, r, c, m[0], m[1], 'R'));
                            allMoves.add(new Move(layout, r, c, m[0], m[1], 'B'));

                        } else {
                            allMoves.add(new Move(layout, r, c, m[0], m[1], 'n'));
                        }
                    }
                }
            }
        }
        return allMoves;
    }

    public int getPoints(char color) {
        int sum = 0;
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);
                if (piece != null && piece.getColor() == color) {
                    sum += piece.getPoint();
                }
            }
        }
        return sum;
    }

    // for lessons: converts board coordinates to a UCI square pair, e.g. "e2e4"
    public String moveToUCI(int fromRow, int fromCol, int toRow, int toCol) {
        char fromFile = (char) ('a' + fromCol);
        char fromRank = (char) ('8' - fromRow);
        char toFile = (char) ('a' + toCol);
        char toRank = (char) ('8' - toRow);
        return "" + fromFile + fromRank + toFile + toRank;
    }
}
