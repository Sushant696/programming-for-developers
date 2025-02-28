package QuestionThree.tetrisGUI.tetrisGUI.src.main.java.view;

import javax.swing.*;
import java.awt.*;

public class NextBlockPanel extends JPanel {
    private static final int CELL_SIZE = 20;
    private Block nextBlock;

    public NextBlockPanel() {
        setPreferredSize(new Dimension(6 * CELL_SIZE, 6 * CELL_SIZE));
        setBorder(BorderFactory.createTitledBorder("Next Block"));
        setBackground(Color.DARK_GRAY);
    }

    public void setNextBlock(Block nextBlock) {
        this.nextBlock = nextBlock;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nextBlock != null) {
            // Center the block in the panel
            int[][] shape = nextBlock.getShape();
            int centerX = (getWidth() - shape[0].length * CELL_SIZE) / 2;
            int centerY = (getHeight() - shape.length * CELL_SIZE) / 2;

            g.setColor(nextBlock.getColor());
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    if (shape[i][j] == 1) {
                        g.fillRect(centerX + j * CELL_SIZE,
                                centerY + i * CELL_SIZE,
                                CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(centerX + j * CELL_SIZE,
                                centerY + i * CELL_SIZE,
                                CELL_SIZE, CELL_SIZE);
                        g.setColor(nextBlock.getColor());
                    }
                }
            }
        }
    }
}