package view;

import java.awt.Color;

public class Block {
    private int[][] shape;
    private Color color;
    private int x, y;

    public Block(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        x = 4;
        y = 0;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Block getClone() {
        // Deep copy the shape
        int[][] shapeCopy = new int[shape.length][];
        for (int i = 0; i < shape.length; i++) {
            shapeCopy[i] = shape[i].clone();
        }

        Block clone = new Block(shapeCopy, color);
        clone.setX(x);
        clone.setY(y);
        return clone;
    }

    public void rotate() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                rotated[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }
}
