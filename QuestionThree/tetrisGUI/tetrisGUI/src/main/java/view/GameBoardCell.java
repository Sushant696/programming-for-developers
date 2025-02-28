package QuestionThree.tetrisGUI.tetrisGUI.src.main.java.view;

import java.awt.Color;

public class GameBoardCell {
    private boolean filled;
    private Color color;

    public GameBoardCell() {
        filled = false;
        color = Color.BLACK;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        if (color != Color.BLACK) {
            filled = true;
        } else {
            filled = false;
        }
    }

    public void clear() {
        filled = false;
        color = Color.BLACK;
    }
}
