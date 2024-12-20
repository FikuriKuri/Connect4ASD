/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - Student ID - Student Name 1
 * 2 - Student ID - Student Name 2
 * 3 - Student ID - Student Name 3
 */
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class Main extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants for drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Game components
    private Board board; // Game board
    private State currentState; // Current state of the game
    private Seed currentPlayer; // Current player
    private JLabel statusBar; // Status bar to display messages
    private int crossWins = 0;
    private int noughtWins = 0;


    /**
     * Constructor to setup the UI and game components
     */
    public Main() {
        // Add mouse listener for player interaction
//        String[] options = {"Player vs Player", "Player vs AI"};
//        int choice = JOptionPane.showOptionDialog(null, "Choose game mode:", "Game Mode",
//                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
//        isPlayerVsAI = (choice == 1); // If "Player vs AI" is selected
//
//        if (isPlayerVsAI) {
//            aiPlayer = new AIPlayer(Seed.NOUGHT); // AI is 'O'
//        }
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);
                        if (currentPlayer == Seed.CROSS) {
                            SoundEffect.chick.play(); //Suara player 1
                        } else if (currentPlayer == Seed.NOUGHT) {
                            SoundEffect.dog_barking.play(); //suara player 2
                        }
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // Set up status bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2));

        initGame();
        newGame();
    }

    /**
     * Initialize the game components
     */
    private void initGame() {
        board = new Board();
    }

    /**
     * Start a new game
     */
    private void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        showStartingPlayerDialog();
    }
    private void showStartingPlayerDialog() {
        String[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(this,
                "Who will start first?",
                "Choose Starting Player",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]); // Default selection is "X"

        // Set the starting player based on the choice
        if (choice == 0) { // If player chooses "X"
            currentPlayer = Seed.CROSS;
        } else { // If player chooses "O"
            currentPlayer = Seed.NOUGHT;
        }
        currentState = State.PLAYING;
    }

    /**
     * Custom painting for the game board
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            SoundEffect.game_draw.play();
        } else {
            statusBar.setForeground(Color.RED);
            statusBar.setText((currentState == State.CROSS_WON) ? "'X' Won!" : "'O' Won!");
            SoundEffect.game_over.play();
        }
        statusBar.setText(String.format("'X' Wins: %d | 'O' Wins: %d | %s",
                crossWins, noughtWins, (currentState == State.PLAYING ?
                        ((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn") :
                        (currentState == State.DRAW ? "It's a Draw! Click to play again." :
                                ((currentState == State.CROSS_WON) ? "'X' Won!" : "'O' Won!")))));


        if (currentState == State.CROSS_WON) {
            crossWins++;
        } else if (currentState == State.NOUGHT_WON) {
            noughtWins++;
        }
    }


    /**
     * Main method to launch the game
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new Main());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}