# ChessGame

A desktop chess application built with **Java 21**, **Swing**, and **Maven**. It includes local two-player chess, a computer opponent powered by a minimax search algorithm, and chess puzzles with hints.

## Features

- **Player vs Player** — play against another person on the same computer.
- **Player vs CPU** — play against a computer opponent. Several difficulty levels control the search depth.
- **Lessons / Puzzles** — practise using chess puzzles loaded from a bundled Lichess CSV dataset.
- **Hints** — request help while solving a puzzle.
- **Chess interface** — graphical board, piece images, move feedback, and game controls.

## Requirements

- **JDK 21 or newer**
- **Maven 3.8+** (for building from the command line)
- A desktop environment capable of displaying Java Swing windows

You can also open the project in IntelliJ IDEA with a configured JDK 21+ and Maven support.

## Getting the project

Clone the repository, then enter the project directory:

```bash
git clone https://github.com/JanDrzewiecki/ChessGame
cd ChessGame
```

If you downloaded the source as a ZIP file, extract it and open the extracted project directory instead.

## Build

From the directory containing `pom.xml`, run:

```bash
mvn clean package
```

Maven compiles the source code, copies the application resources, and creates this executable JAR:

```text
target/chessgame-1.0.jar
```

## Run

After building, run the application with:

```bash
java -jar target/chessgame-1.0.jar
```

Alternatively, Maven can compile and launch the application directly:

```bash
mvn compile exec:java
```

To run it from IntelliJ IDEA, open the project, set the Project SDK to JDK 21 or newer, wait for Maven import to finish, and run `Main.java`.

## How to play

Start the application to open the main menu.
- Choose **Player vs Player** to play another person on the same computer. 
- Choose **Player vs CPU** to play against the computer, then select a difficulty. 
- Choose **Lessons** to select a puzzle difficulty and solve a chess puzzle. 
  - In puzzle mode, use **Hint** when you need assistance. 
- Use the in-game menu to return to the main menu or leave a game ('Forfeit' button ends the game, while 'Main Menu' button redirects you to the main menu, with the option to continue the game).

## Project structure

```text
ChessGame/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   ├── Main.java
│       │   ├── bots/
│       │   │   ├── Bot.java
│       │   │   └── MiniMax.java
│       │   ├── game/
│       │   │   ├── ChessBoard.java
│       │   │   ├── Game.java
│       │   │   ├── GameState.java
│       │   │   ├── Lessons.java
│       │   │   ├── Move.java
│       │   │   ├── PiecesLayout.java
│       │   │   ├── Puzzle.java
│       │   │   └── PuzzleLoader.java
│       │   ├── pieces/
│       │   │   ├── Piece.java
│       │   │   ├── Pawn.java
│       │   │   ├── Knight.java
│       │   │   ├── Bishop.java
│       │   │   ├── Rook.java
│       │   │   ├── Queen.java
│       │   │   └── King.java
│       │   └── ...
│       └── resources/
│           ├── background/
│           │   └── background.jpg
│           ├── board/
│           │   └── board_brown.png
│           ├── pieces/
│           └── puzzles/
│               └── lichess_puzzles_1000_random.csv
└── README.md
```

## Main components

- `Main` — creates the application window and main menu, and switches between game modes.
- `ChessBoard` — displays the board and handles board interaction.
- `Game` — manages chess gameplay and move logic.
- `PiecesLayout` and the classes in `pieces/` — represent the board layout and individual chess pieces.
- `Bot` — interface for a computer opponent.
- `MiniMax` — selects computer moves using minimax search with alpha-beta pruning.
- `Lessons`, `Puzzle`, and `PuzzleLoader` — manage puzzle mode, puzzle data, and puzzle interaction.

## Resources

Images and puzzle data are stored under `src/main/resources/` and are packaged into the application by Maven:

- `background/` — application background image.
- `board/` — chessboard image.
- `pieces/` — chess piece images.
- `puzzles/` — CSV file containing chess puzzles.

Keep these resources in their existing locations so the application can load them from the classpath. It is possible to replace the CSV file in /puzzles with another one if in need of more puzzles. 

## Troubleshooting

- **`mvn: command not found`** — install Maven, or use Maven support in IntelliJ IDEA.
- **Unsupported Java version / compilation errors** — check that both Maven and IntelliJ use JDK 21 or newer.
- **The window does not open** — run the application in a desktop environment with graphical display support; Swing applications generally cannot display a window in a headless environment.
- **Images or puzzles are missing** — check that files remain under `src/main/resources/` and that the project was built with `mvn clean package`.

## Authors

Jan **Drzewiecki**,
Jan **Krzywicki**