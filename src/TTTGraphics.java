import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.sound.sampled.*;
/**
 * Tic-Tac-Toe: Two-player Graphics version with Simple-OO in one class
 */
public class TTTGraphics extends JFrame {
    private void playSound(String soundFile) {
        try {
            // Lokasi file suara
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the game board
    public static final int ROWS = 6;  // ROWS x COLS cells
    public static final int COLS = 7;

    // Define named constants for the drawing graphics
    public static final int CELL_SIZE = 80; // cell width/height (square)
    public static final int BOARD_WIDTH  = CELL_SIZE * COLS; // the drawing canvas
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 10;                  // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static final int CELL_PADDING = CELL_SIZE / 5;
    public static final int DISC_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
    public static final Color COLOR_BG = Color.WHITE;  // background
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_GRID   = Color.LIGHT_GRAY;  // grid lines
    public static final Color COLOR_RED  = new Color(211, 45, 65);  // Red disc
    public static final Color COLOR_YELLOW = new Color(255, 223, 0);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);
    private int redWins = 0;    // Jumlah kemenangan pemain merah
    private int yellowWins = 0; // Jumlah kemenangan pemain kuning
    // This enum (inner class) contains the various states of the game
    public enum State {
        PLAYING, DRAW, RED_WON, YELLOW_WON
    }
    private State currentState;  // the current game state

    // This enum (inner class) is used for:
    // 1. Player: CROSS, NOUGHT
    // 2. Cell's content: CROSS, NOUGHT and NO_SEED
    public enum Seed {
        RED, YELLOW, NO_SEED
    }
    private Seed currentPlayer; // the current player
    private Seed[][] board;     // Game board of ROWS-by-COLS cells

    // UI Components
    private GamePanel gamePanel; // Drawing canvas (JPanel) for the game board
    private JLabel statusBar;  // Status Bar

    /** Constructor to setup the game and the GUI components */
    public TTTGraphics() {
        // Initialize the game objects
        initGame();

        // Set up GUI components
        gamePanel = new GamePanel();  // Construct a drawing canvas (a JPanel)
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int col = mouseX / CELL_SIZE; // Determine the column clicked
                int row = -1; // Initialize row variable

                if (currentState == State.PLAYING) {
                    if (col >= 0 && col < COLS) {
                        // Find the lowest empty cell in the column
                        for (int r = ROWS - 1; r >= 0; r--) {
                            if (board[r][col] == Seed.NO_SEED) {
                                board[r][col] = currentPlayer; // Make a move
                                currentState = stepGame(currentPlayer, r, col); // Update state
                                row = r; // Store the row where move was made
                                break;
                            }
                        }

                        // Add this block to notify the user if the column is full
                        if (row == -1) { // No valid row found
                            statusBar.setForeground(Color.RED);
                            statusBar.setText("Invalid Move! Column is full. Try another.");
                        } else if (currentState == State.PLAYING) { // Game continues
                            currentPlayer = (currentPlayer == Seed.RED) ? Seed.YELLOW : Seed.RED;
                        }
                    }
                } else { // Game over
                    newGame(); // Start a new game
                }
                repaint(); // Refresh UI
            }
        });


                                       // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("       ");
        statusBar.setFont(FONT_STATUS);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(COLOR_BG_STATUS);

        // Set up content pane
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Tic Tac Toe");
        setVisible(true);  // show this JFrame

        newGame();
    }

    /** Initialize the Game (run once) */
    public void initGame() {
        board = new Seed[ROWS][COLS]; // allocate array
    }

    /** Reset the game-board contents and the status, ready for new game */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.RED;    // cross always plays first
        currentState = State.PLAYING; // reset to playing state
        playSound("src/chick.wav");
        repaint();
    }


    /**
     *  The given player makes a move on (selectedRow, selectedCol).
     *  Update cells[selectedRow][selectedCol]. Compute and return the
     *  new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        // Check for 4-in-a-line on the rowSelected
        int count = 0;
        for (int col = 0; col < COLS; ++col) {
            if (board[rowSelected][col] == theSeed) {
                ++count;
                if (count == 4) return true;  // found
            } else {
                count = 0; // reset and count again if not consecutive
            }
        }

        count = 0;
        for (int row = 0; row < ROWS; ++row) {
            if (board[row][colSelected] == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check diagonal (top-left to bottom-right)
        count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = rowSelected + i;
            int c = colSelected + i;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

// Check diagonal (bottom-left to top-right)
        count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = rowSelected + i;
            int c = colSelected - i;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Update game board
        board[selectedRow][selectedCol] = player;
        playSound("src/dog.wav");
        boolean winStatus = hasWon(player,selectedRow, selectedCol);

        // Compute and return the new game state
        if (winStatus) {
            playSound("src/chick.wav");
            return (player == Seed.RED) ? State.RED_WON : State.YELLOW_WON;
        } else {
            // Nobody win. Check for DRAW (all cells occupied) or PLAYING.
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (board[row][col] == Seed.NO_SEED) {
                        return State.PLAYING; // still have empty cells
                    }
                }
            }
            playSound("src/game_draw.wav");
            return State.DRAW; // no empty cell, it's a draw
        }
    }

    /**
     *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
     */
    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L; // to prevent serializable warning

        @Override
        public void paintComponent(Graphics g) {  // Callback via repaint()
            super.paintComponent(g);
            setBackground(COLOR_BG);  // set its background color

            // Draw the grid lines
            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the Seeds of all the cells if they are not empty
            // Use Graphics2D which allows us to set the pen's stroke
            Graphics2D g2d = (Graphics2D) g;
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x = col * CELL_SIZE + CELL_PADDING;
                    int y = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Seed.RED) {
                        g2d.setColor(COLOR_RED);
                        g2d.fillOval(x, y, DISC_SIZE, DISC_SIZE);
                    } else if (board[row][col] == Seed.YELLOW) {
                        g2d.setColor(COLOR_YELLOW);
                        g2d.fillOval(x, y, DISC_SIZE, DISC_SIZE);
                    }
                }
            }

            // Print status message
            if (currentState == State.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                statusBar.setText((currentPlayer == Seed.RED) ? "RED's Turn" : "YELLOW's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click to play again");
            } else if (currentState == State.RED_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'RED' Won! Click to play again");
            } else if (currentState == State.YELLOW_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'YELLOW' Won! Click to play again");
            }
        }
    }

    /** The entry main() method */
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TTTGraphics(); // Let the constructor do the job
            }
        });
    }
}