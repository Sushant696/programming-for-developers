package QuestionThree.tetrisGUI.tetrisGUI.src.main.java.view;

import javax.swing.*;
import java.awt.*;

public class TetrisGame extends JFrame {
    private GameBoard gameBoard;
    private NextBlockPanel nextBlockPanel;
    private Timer updateTimer;

    public TetrisGame() {
        setTitle("Tetris with Queue and Stack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        initUI();

        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        gameBoard = new GameBoard();

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Next block preview
        nextBlockPanel = new NextBlockPanel();
        sidePanel.add(nextBlockPanel);

        // Control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton leftButton = new JButton("Left");
        JButton rightButton = new JButton("Right");
        JButton rotateButton = new JButton("Rotate");

        leftButton.addActionListener(e -> gameBoard.moveLeft());
        rightButton.addActionListener(e -> gameBoard.moveRight());
        rotateButton.addActionListener(e -> gameBoard.rotate());

        controlPanel.add(leftButton);
        controlPanel.add(rightButton);
        controlPanel.add(rotateButton);

        sidePanel.add(controlPanel);

        // Game control buttons
        JPanel gameControlPanel = new JPanel();
        gameControlPanel.setLayout(new GridLayout(2, 1, 5, 5));

        JButton newGameButton = new JButton("New Game");
        JButton pauseButton = new JButton("Pause");

        newGameButton.addActionListener(e -> gameBoard.restart());
        pauseButton.addActionListener(e -> {
            gameBoard.togglePause();
            if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pause");
            }
        });

        gameControlPanel.add(newGameButton);
        gameControlPanel.add(pauseButton);

        sidePanel.add(gameControlPanel);

        // Layout
        setLayout(new BorderLayout());
        add(gameBoard, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        // Update timer for next block preview
        updateTimer = new Timer(100, e -> nextBlockPanel.setNextBlock(gameBoard.getNextBlock()));
        updateTimer.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TetrisGame game = new TetrisGame();
            game.setVisible(true);
        });
    }
}