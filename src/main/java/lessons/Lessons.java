package lessons;

import game.Game;
import game.PiecesLayout;
import ui.ChessBoard;

public class Lessons {

    private final Puzzle puzzle;
    private final Game game;
    private final char playerColor;
    public int moveIndex = 1;

    public Lessons(Puzzle puzzle, Game game, char playerColor) {
        this.puzzle = puzzle;
        this.game = game;
        this.playerColor = playerColor;
    }

    public char getPlayerColor() { return playerColor; }

    public static boolean applyMove(String move, Game game) {
        int fromCol = move.charAt(0) - 'a';
        int fromRow = '8' - move.charAt(1);
        int toCol = move.charAt(2) - 'a';
        int toRow = '8' - move.charAt(3);

        boolean success = game.tryMove(fromRow, fromCol, toRow, toCol);

        // Puzzle moves carry the promotion piece as a 5th char, e.g. "e7e8q"
        if (success && game.needsPromotion(toRow, toCol)) {
            char piece = move.length() == 5 ? Character.toUpperCase(move.charAt(4)) : 'Q';
            game.promote(piece);
        }
        return success;
    }

    public boolean checkMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isSolved()) return false;

        String[] moves = puzzle.getMoves();
        String correctMove = moves[moveIndex];
        String playerMove = game.moveToUCI(fromRow, fromCol, toRow, toCol);

        // Compare only from/to squares (ignore promotion suffix)
        if (!playerMove.equals(correctMove.substring(0, 4))) {
            return false;
        }

        if (!applyMove(correctMove, game)) {
            return false;
        }
        moveIndex++;

        // Opponent's reply, if one remains
        if (moveIndex < moves.length) {
            applyMove(moves[moveIndex], game);
            moveIndex++;
        }
        return true;
    }

    public String getHint() {
        String[] moves = puzzle.getMoves();
        if (moveIndex >= moves.length) return null;
        return moves[moveIndex].substring(0, 2);
    }

    public boolean isSolved() {
        return moveIndex >= puzzle.getMoves().length;
    }

    public static ChessBoard createBoard(String difficulty, Runnable mainMenuAction, Runnable nextPuzzleAction) {
        Puzzle puzzle = PuzzleLoader.loadRandomPuzzle(difficulty);

        PiecesLayout lessonLayout = new PiecesLayout();
        lessonLayout.setupFromFEN(puzzle.getFen());

        String[] moves = puzzle.getMoves();
        Game lessonGame = new Game(lessonLayout);

        // FEN side-to-move = the OPPONENT (they play moves[0]); the player is the other colour
        char sideToMove = puzzle.getFen().split(" ")[1].charAt(0);
        lessonGame.setRound(sideToMove);
        applyMove(moves[0], lessonGame);

        char playerColor = (sideToMove == 'w') ? 'b' : 'w';
        Lessons lessons = new Lessons(puzzle, lessonGame, playerColor);

        ChessBoard lessonBoard = new ChessBoard(lessonLayout, lessonGame, mainMenuAction, nextPuzzleAction);
        lessonBoard.setLessons(lessons);
        return lessonBoard;
    }
}