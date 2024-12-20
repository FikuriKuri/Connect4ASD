import java.awt.*;

public class Board {
    public static final int ROWS = 3; // Number of rows
    public static final int COLS = 3; // Number of columns
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS; // Width of the canvas
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS; // Height of the canvas

    Cell[][] cells; // 2D array of cells

    /** Constructor to initialize the game board */
    public Board() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col); // Initialize each cell
            }
        }
    }

    /**
     * Process a move by the current player and update the game state.
     *
     * @param seed the current player's seed
     * @param row  the row of the cell being played
     * @param col  the column of the cell being played
     * @return the updated game state
     */
    public State stepGame(Seed seed, int row, int col) {
        cells[row][col].content = seed; // Set the cell content to the player's seed
        if (hasWon(seed, row, col)) { // Check if the player has won
            return (seed == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else if (isDraw()) { // Check if the game is a draw
            return State.DRAW;
        }
        return State.PLAYING; // Otherwise, the game is still ongoing
    }

    /**
     * Check if the player has won after their move.
     *
     * @param seed the current player's seed
     * @param row  the row of the cell being played
     * @param col  the column of the cell being played
     * @return true if the player has won, false otherwise
     */
    private boolean hasWon(Seed seed, int row, int col) {
        // Check the row
        if (cells[row][0].content == seed && cells[row][1].content == seed && cells[row][2].content == seed) {
            return true;
        }
        // Check the column
        if (cells[0][col].content == seed && cells[1][col].content == seed && cells[2][col].content == seed) {
            return true;
        }
        // Check the main diagonal
        if (cells[0][0].content == seed && cells[1][1].content == seed && cells[2][2].content == seed) {
            return true;
        }
        // Check the opposite diagonal
        if (cells[0][2].content == seed && cells[1][1].content == seed && cells[2][0].content == seed) {
            return true;
        }
        return false; // No win found
    }

    /**
     * Check if the game is a draw.
     *
     * @return true if the game is a draw, false otherwise
     */
    private boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    return false; // If any cell is empty, it's not a draw
                }
            }
        }
        return true; // All cells are filled, and no winner found
    }

    /**
     * Paint the game board.
     *
     * @param g the Graphics object used for drawing
     */
    public void paint(Graphics g) {
        // Draw the grid lines
        g.setColor(Color.BLACK);
        for (int row = 1; row < ROWS; ++row) {
            g.drawLine(0, row * Cell.SIZE, CANVAS_WIDTH, row * Cell.SIZE); // Horizontal lines
        }
        for (int col = 1; col < COLS; ++col) {
            g.drawLine(col * Cell.SIZE, 0, col * Cell.SIZE, CANVAS_HEIGHT); // Vertical lines
        }

        // Ask each cell to paint itself
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);
            }
        }
    }
}
