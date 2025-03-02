package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GameBoard extends JPanel implements ActionListener {
    private static final int COLS = 10, ROWS = 20;
    private static final int CELL_SIZE = 30;

    private Timer timer;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private int score = 0;

    // A queue to store upcoming blocks as specified
    private Queue<Block> blockQueue = new LinkedList<>();

    // A stack of rows to represent the game board as specified
    private Stack<GameBoardCell[]> boardStack = new Stack<>();

    private Block currentBlock;

    public GameBoard() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setBackground(Color.BLACK);

        // Initialize the board stack with empty rows
        initializeBoardStack();

        // Initialize the block queue with several blocks
        initializeBlockQueue();

        // Set the current block
        if (!blockQueue.isEmpty()) {
            currentBlock = blockQueue.poll();
            // Add a new block to the queue to replace the one we just took
            blockQueue.add(createRandomBlock());
        }

        timer = new Timer(500, this); // Controls falling speed
        timer.start();
    }

    private void initializeBoardStack() {
        // Clear the stack
        boardStack.clear();

        // Add empty rows to the stack (bottom to top)
        for (int i = 0; i < ROWS; i++) {
            GameBoardCell[] row = new GameBoardCell[COLS];
            for (int j = 0; j < COLS; j++) {
                row[j] = new GameBoardCell();
            }
            boardStack.push(row);
        }
    }

    private void initializeBlockQueue() {
        blockQueue.clear();

        // Add several random blocks to the queue
        for (int i = 0; i < 5; i++) {
            blockQueue.add(createRandomBlock());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver && !isPaused) {
            update();
            repaint();
        }
    }

    private void update() {
        if (currentBlock == null) {
            return;
        }

        // Try to move the block down
        if (canMove(currentBlock, currentBlock.getX(), currentBlock.getY() + 1)) {
            currentBlock.setY(currentBlock.getY() + 1);
        } else {
            // Block has landed, update the board state
            placeBlock();

            // Check for completed rows
            checkForCompletedRows();

            // Check for game over
            checkForGameOver();

            // Get the next block from the queue
            if (!blockQueue.isEmpty() && !isGameOver) {
                currentBlock = blockQueue.poll();
                // Add a new block to the queue
                blockQueue.add(createRandomBlock());
            }
        }
    }

    private void placeBlock() {
        int[][] shape = currentBlock.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    int x = currentBlock.getX() + j;
                    int y = currentBlock.getY() + i;

                    if (y >= 0 && y < ROWS && x >= 0 && x < COLS) {
                        // Update the cell in the stack at this position
                        GameBoardCell[] row = boardStack.get(ROWS - 1 - y);
                        row[x].setColor(currentBlock.getColor());
                    }
                }
            }
        }
    }

    private void checkForCompletedRows() {
        int rowsCleared = 0;

        // Check each row from bottom to top
        for (int i = 0; i < ROWS; i++) {
            GameBoardCell[] row = boardStack.get(i);
            boolean rowComplete = true;

            // Check if the row is complete
            for (int j = 0; j < COLS; j++) {
                if (!row[j].isFilled()) {
                    rowComplete = false;
                    break;
                }
            }

            if (rowComplete) {
                // Remove the completed row
                boardStack.remove(i);

                // Add a new empty row at the top
                GameBoardCell[] newRow = new GameBoardCell[COLS];
                for (int j = 0; j < COLS; j++) {
                    newRow[j] = new GameBoardCell();
                }
                boardStack.add(ROWS - 1, newRow);

                rowsCleared++;
                // Don't increment i since we've just shifted everything down
                i--;
            }
        }

        // Update score based on rows cleared
        if (rowsCleared > 0) {
            score += rowsCleared * rowsCleared * 100; // Exponential scoring for multiple rows
        }
    }

    private void checkForGameOver() {
        // Check if any cells in the top row are filled
        GameBoardCell[] topRow = boardStack.get(ROWS - 1);
        for (int j = 0; j < COLS; j++) {
            if (topRow[j].isFilled()) {
                isGameOver = true;
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Score: " + score,
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    private boolean canMove(Block block, int newX, int newY) {
        int[][] shape = block.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    int x = newX + j;
                    int y = newY + i;

                    // Check bounds
                    if (x < 0 || x >= COLS || y >= ROWS) {
                        return false;
                    }

                    // Check collision with other blocks (only if y is valid)
                    if (y >= 0) {
                        GameBoardCell[] row = boardStack.get(ROWS - 1 - y);
                        if (row[x].isFilled()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void moveLeft() {
        if (currentBlock != null && canMove(currentBlock, currentBlock.getX() - 1, currentBlock.getY())) {
            currentBlock.setX(currentBlock.getX() - 1);
            repaint();
        }
    }

    public void moveRight() {
        if (currentBlock != null && canMove(currentBlock, currentBlock.getX() + 1, currentBlock.getY())) {
            currentBlock.setX(currentBlock.getX() + 1);
            repaint();
        }
    }

    public void rotate() {
        if (currentBlock != null) {
            Block rotated = currentBlock.getClone();
            rotated.rotate();
            if (canMove(rotated, rotated.getX(), rotated.getY())) {
                currentBlock.rotate();
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                GameBoardCell cell = boardStack.get(ROWS - 1 - i)[j];
                drawCell(g, j, i, cell.getColor());
            }
        }

        // Draw the current block
        if (currentBlock != null) {
            int[][] shape = currentBlock.getShape();
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    if (shape[i][j] == 1) {
                        int x = currentBlock.getX() + j;
                        int y = currentBlock.getY() + i;
                        drawCell(g, x, y, currentBlock.getColor());
                    }
                }
            }
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    private void drawCell(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private Block createRandomBlock() {
        // Define Tetris shapes (I, O, T, L, J, S, Z)
        int[][][] shapes = {
                { { 1, 1, 1, 1 } }, // I
                { { 1, 1 }, { 1, 1 } }, // O
                { { 0, 1, 0 }, { 1, 1, 1 } }, // T
                { { 1, 0 }, { 1, 0 }, { 1, 1 } }, // L
                { { 0, 1 }, { 0, 1 }, { 1, 1 } }, // J
                { { 0, 1, 1 }, { 1, 1, 0 } }, // S
                { { 1, 1, 0 }, { 0, 1, 1 } } // Z
        };

        Color[] colors = {
                Color.CYAN, Color.YELLOW, Color.MAGENTA,
                Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED
        };

        int random = (int) (Math.random() * shapes.length);
        return new Block(shapes[random], colors[random]);
    }

    public Block getNextBlock() {
        return blockQueue.peek();
    }

    public void restart() {
        initializeBoardStack();
        initializeBlockQueue();
        currentBlock = blockQueue.poll();
        blockQueue.add(createRandomBlock());
        score = 0;
        isGameOver = false;
        timer.start();
        repaint();
    }

    public void togglePause() {
        isPaused = !isPaused;
    }
}