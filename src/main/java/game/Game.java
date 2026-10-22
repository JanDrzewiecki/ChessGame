package game;

import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.Rook;

import java.util.ArrayList;
import java.util.List;

/**
 * The rules and the state of a chess game: whose turn it is, the legal moves, check, checkmate
 * and stalemate.
 */
public class Game {

    private char round = 'w'; // The color whose turn it is: 'w' for white or 'b' for black.
    private final PiecesLayout layout; // The board.
    private final List<Move> history = new ArrayList<>(); // All moves played so far.
    private GameState state = GameState.PLAYING; // Whether the game is going on or finished.
    private Move pendingMove; // A pawn move waiting for the player to choose a promotion.

    /**
     * Create a game on a board.
     *
     * @param layout the board to play on
     */
    public Game(PiecesLayout layout) {
        this.layout = layout;
    }

    /**
     * Get the color whose turn it is.
     *
     * @return 'w' for white or 'b' for black
     */
    public char getTurn() {
        return round;
    }

    /**
     * Get the state of the game.
     *
     * @return the state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Set whose turn it is.
     *
     * @param round 'w' for white or 'b' for black
     * @throws IllegalArgumentException if the color is not 'w' or 'b'
     */
    public void setRound(char round) {
        if (round != 'w' && round != 'b') {
            throw new IllegalArgumentException("Round must be 'w' or 'b'");
        }

        this.round = round;
    }

    /**
     * Find the king of a color.
     *
     * @param color 'w' or 'b'
     * @return {row, column} of the king, or null if there is none
     */
    public int[] getKing(char color) {
        for (int i = 0; i < PiecesLayout.BOARD_SIZE; i++) {
            for (int j = 0; j < PiecesLayout.BOARD_SIZE; j++) {
                Piece piece = layout.getPiece(i, j);

                if (piece instanceof King && color == piece.getColor()) {
                    return new int[]{i, j};
                }
            }
        }

        return null;
    }

    /**
     * Get the legal moves of the piece on a square (castling and en passant included). A move is
     * legal if the own king is not in check afterwards.
     *
     * @param row the row of the piece
     * @param col the column of the piece
     * @return a list of {row, column} squares (empty if the square is empty)
     */
    public List<int[]> getLegalMoves(int row, int col) {
        Piece piece = layout.getPiece(row, col);

        if (piece == null) {
            return new ArrayList<>();
        }

        List<int[]> legalMoves = new ArrayList<>();

        for (int[] target : piece.getPossibleMoves(row, col, layout)) {
            Move move = new Move(layout, row, col, target[0], target[1], 'n');

            move.apply(layout);

            boolean safe = !isCheck(piece.getColor());

            move.undo(layout);

            if (safe) {
                legalMoves.add(target);
            }
        }

        if (piece instanceof King king) {
            legalMoves.addAll(getCastlingMoves(row, col, king.getColor()));
        }

        if (piece instanceof Pawn) {
            int[] enPassantTarget = getEnPassantMove(row, col);

            if (enPassantTarget.length != 0) {
                Move move = new Move(layout, row, col, enPassantTarget[0], enPassantTarget[1], 'n');

                move.apply(layout);

                boolean safe = !isCheck(piece.getColor());

                move.undo(layout);

                if (safe) {
                    legalMoves.add(enPassantTarget);
                }
            }
        }

        return legalMoves;
    }

    /**
     * Get the squares a king can castle to. The king and the rook must not have moved, the
     * squares between them must be empty and the king may not be in check or pass a square that
     * is attacked.
     *
     * @param kingsRow the row of the king
     * @param kingsCol the column of the king
     * @param color the color of the king
     * @return a list of {row, column} squares (empty if castling is not possible)
     */
    public List<int[]> getCastlingMoves(int kingsRow, int kingsCol, char color) {
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

        if (layout.getPiece(kingsRow, kingsCol + 1) == null
                && layout.getPiece(kingsRow, kingsCol + 2) == null
                && layout.getPiece(kingsRow, kingsCol + 3) instanceof Rook rook) {
            boolean safe = true;

            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol + i, 'n');

                    move.apply(layout);

                    if (isCheck(color)) {
                        safe = false;
                    }

                    move.undo(layout);
                }

                if (safe) {
                    castles.add(new int[]{kingsRow, kingsCol + 2});
                }
            }
        }

        if (layout.getPiece(kingsRow, kingsCol - 1) == null
                && layout.getPiece(kingsRow, kingsCol - 2) == null
                && layout.getPiece(kingsRow, kingsCol - 3) == null
                && layout.getPiece(kingsRow, kingsCol - 4) instanceof Rook rook) {
            boolean safe = true;

            if (rook.getColor() == color && !rook.hasMoved()) {
                for (int i = 1; i < 3; i++) {
                    Move move = new Move(layout, kingsRow, kingsCol, kingsRow, kingsCol - i, 'n');

                    move.apply(layout);

                    if (isCheck(color)) {
                        safe = false;
                    }

                    move.undo(layout);
                }

                if (safe) {
                    castles.add(new int[]{kingsRow, kingsCol - 2});
                }
            }
        }

        return castles;
    }

    /**
     * Get the square a pawn can capture to en passant. This is only possible right after an
     * enemy pawn moved two squares forward and stands next to this pawn.
     *
     * @param row the row of the pawn
     * @param col the column of the pawn
     * @return {row, column} of the square to move to, or an empty array if there is none
     */
    public int[] getEnPassantMove(int row, int col) {
        if (!(layout.getPiece(row, col) instanceof Pawn pawn) || history.isEmpty()) {
            return new int[]{};
        }

        Move lastMove = history.getLast();
        boolean white = pawn.getColor() == 'w';
        int forward = white ? -1 : 1;

        for (int side = 1; side >= -1; side -= 2) {
            int enemyCol = col + side;

            if (layout.isInside(row, enemyCol)
                    && layout.getPiece(row, enemyCol) instanceof Pawn enemy
                    && enemy.getColor() != pawn.getColor()
                    && lastMove.getToRow() == row
                    && lastMove.getToCol() == enemyCol
                    && lastMove.getMovedPiece() instanceof Pawn) {
                int steps = lastMove.getToRow() - lastMove.getFromRow();

                if (steps * forward == -2) {
                    return new int[]{row + forward, enemyCol};
                }
            }
        }

        return new int[]{};
    }

    /**
     * Check if the player to move may pick up the piece on a square.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return true if the piece belongs to the player whose turn it is
     */
    public boolean canMove(int row, int col) {
        if (pendingMove != null) {
            return false;
        }

        Piece piece = layout.getPiece(row, col);

        return piece != null && piece.getColor() == getTurn();
    }

    /**
     * Check if the king of a color is attacked.
     *
     * @param color 'w' or 'b'
     * @return true if the king is in check (false if there is no such king)
     */
    public boolean isCheck(char color) {
        int[] kingSquare = getKing(color);

        if (kingSquare == null) {
            return false;
        }

        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);

                if (piece == null || piece.getColor() == color) {
                    continue;
                }

                for (int[] target : layout.getMovesFor(r, c)) {
                    if (target[0] == kingSquare[0] && target[1] == kingSquare[1]) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if a color has at least one legal move.
     *
     * @param color 'w' or 'b'
     * @return true if there is a legal move
     */
    public boolean hasAnyLegalMove(char color) {
        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);

                if (piece == null || piece.getColor() != color) {
                    continue;
                }

                if (!getLegalMoves(r, c).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the state of the game for the player who has to move.
     *
     * @param color the color that has to move
     * @return PLAYING, CHECKMATE (no legal move and in check) or STALEMATE (no legal move)
     */
    public GameState evaluateState(char color) {
        if (hasAnyLegalMove(color)) {
            return GameState.PLAYING;
        }

        return isCheck(color) ? GameState.CHECKMATE : GameState.STALEMATE;
    }

    /**
     * Finish a pawn move that reached the last row, by choosing the new piece.
     *
     * @param pieceType Q, R, B or N
     */
    public void promote(char pieceType) {
        if (pendingMove != null) {
            pendingMove.promoteTo(layout, pieceType);
            history.add(pendingMove);
            pendingMove = null;
            switchTurn();
            state = evaluateState(round);
        }
    }

    /**
     * Check if the piece on a square is a pawn that reached the last row.
     *
     * @param row the row of the square
     * @param col the column of the square
     * @return true if the pawn must be promoted
     */
    public boolean needsPromotion(int row, int col) {
        Piece piece = layout.getPiece(row, col);

        return piece instanceof Pawn && (row == 0 || row == PiecesLayout.BOARD_SIZE - 1);
    }

    /**
     * Play a move for the player whose turn it is, if it is legal. A pawn that reaches the last
     * row waits for {@link #promote(char)} before the turn changes.
     *
     * @param selectedRow the row of the piece to move
     * @param selectedCol the column of the piece to move
     * @param toRow the row to move to
     * @param toCol the column to move to
     * @return true if the move was played
     */
    public boolean tryMove(int selectedRow, int selectedCol, int toRow, int toCol) {
        if (pendingMove != null) {
            return false;
        }

        if (state != GameState.PLAYING || !canMove(selectedRow, selectedCol)) {
            return false;
        }

        for (int[] target : getLegalMoves(selectedRow, selectedCol)) {
            if (toRow == target[0] && toCol == target[1]) {
                Move move = new Move(layout, selectedRow, selectedCol, toRow, toCol, 'n');

                move.apply(layout);

                if (needsPromotion(toRow, toCol)) {
                    pendingMove = move;

                    return true;
                }

                history.add(move);
                switchTurn();
                state = evaluateState(round);

                return true;
            }
        }

        return false;
    }

    /**
     * Play a move without any checks, for searching (see {@link #undoMove()}). A promotion is
     * done if the move has one.
     *
     * @param move the move to play
     */
    public void makeMove(Move move) {
        move.apply(layout);

        if (move.getPromotionType() != 'n') {
            move.promoteTo(layout, move.getPromotionType());
        }

        history.add(move);
        switchTurn();
    }

    /**
     * Take back the last move played with {@link #makeMove(Move)}.
     */
    public void undoMove() {
        if (!history.isEmpty()) {
            Move move = history.getLast();

            history.remove(history.size() - 1);
            move.undo(layout);
            switchTurn();
        }
    }

    // Give the turn to the other player.
    private void switchTurn() {
        if (round == 'w') {
            round = 'b';
        } else {
            round = 'w';
        }
    }

    /**
     * Get all legal moves of a color. A pawn move to the last row gives four moves, one for each
     * piece it can promote to.
     *
     * @param color 'w' or 'b'
     * @return a list of moves
     */
    public List<Move> getAllMoves(char color) {
        List<Move> allMoves = new ArrayList<>();

        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);

                if (piece == null || piece.getColor() != color) {
                    continue;
                }

                for (int[] target : getLegalMoves(r, c)) {
                    if (piece instanceof Pawn && (target[0] == 0 || target[0] == 7)) {
                        allMoves.add(new Move(layout, r, c, target[0], target[1], 'Q'));
                        allMoves.add(new Move(layout, r, c, target[0], target[1], 'N'));
                        allMoves.add(new Move(layout, r, c, target[0], target[1], 'R'));
                        allMoves.add(new Move(layout, r, c, target[0], target[1], 'B'));
                    } else {
                        allMoves.add(new Move(layout, r, c, target[0], target[1], 'n'));
                    }
                }
            }
        }

        return allMoves;
    }

    /**
     * Get the score of a color: the value of its pieces plus a bonus for pieces near the centre.
     *
     * @param color 'w' or 'b'
     * @return the score in points
     */
    public int getPoints(char color) {
        int sum = 0;

        for (int r = 0; r < PiecesLayout.BOARD_SIZE; r++) {
            for (int c = 0; c < PiecesLayout.BOARD_SIZE; c++) {
                Piece piece = layout.getPiece(r, c);

                if (piece != null && piece.getColor() == color) {
                    sum += piece.getPoint() + layout.getCircle(r, c) * 10;
                }
            }
        }

        return sum;
    }

    /**
     * Set the state of the game.
     *
     * @param newState the new state
     */
    public void setState(GameState newState) {
        state = newState;
    }

    /**
     * Add moves to the history of this game.
     *
     * @param moves the moves to add
     */
    public void setHistory(List<Move> moves) {
        history.addAll(moves);
    }

    /**
     * Set the pawn move that waits for a promotion.
     *
     * @param move the move, or null
     */
    public void setPendingMove(Move move) {
        pendingMove = move;
    }

    /**
     * Get the number of moves played.
     *
     * @return the number of moves
     */
    public int getMovesCount() {
        return history.size();
    }

    /**
     * Make an independent copy of this game (own board, shared played moves).
     *
     * @return the copy
     */
    public Game copy() {
        Game gameCopy = new Game(layout.copy());

        gameCopy.setRound(getTurn());
        gameCopy.setState(state);
        gameCopy.setHistory(history);
        gameCopy.setPendingMove(pendingMove);

        return gameCopy;
    }

    /**
     * Convert board coordinates to a move in UCI notation (used by the lessons).
     *
     * @param fromRow the row of the start square
     * @param fromCol the column of the start square
     * @param toRow the row of the target square
     * @param toCol the column of the target square
     * @return the move as text, for example "e2e4"
     */
    public String moveToUCI(int fromRow, int fromCol, int toRow, int toCol) {
        char fromFile = (char) ('a' + fromCol);
        char fromRank = (char) ('8' - fromRow);
        char toFile = (char) ('a' + toCol);
        char toRank = (char) ('8' - toRow);

        return "" + fromFile + fromRank + toFile + toRank;
    }
}
