package com.example.triangle;

import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractTriangleDrawer implements ITriangleDrawer {
    private ColorPoint firstPoint;
    private ColorPoint secondPoint;
    private ColorPoint thirdPoint;
    public AbstractTriangleDrawer(ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.thirdPoint = thirdPoint;
    }

    protected List<ColorPoint> getSortedPoints() {
        List<ColorPoint> points = Arrays.asList(firstPoint, secondPoint, thirdPoint);
        points.sort((a, b) -> {
            if (a.getY() == b.getY()) {
                return a.getX() - b.getX();
            } else {
                return a.getY() - b.getY();
            }
        });
        return points;
    }

    @Override
    public void drawTriangle(GraphicsContext graphicsContext) {

        actualDraw(graphicsContext, getSortedPoints());
    }

    protected abstract void actualDraw(GraphicsContext graphicsContext, List<ColorPoint> points);
}
