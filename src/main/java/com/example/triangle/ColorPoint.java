package com.example.triangle;

import javafx.scene.paint.Color;

public class ColorPoint {
    private final int x;
    private final int y;
    private final javafx.scene.paint.Color color;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public javafx.scene.paint.Color getColor() {
        return color;
    }

    public ColorPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
