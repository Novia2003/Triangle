package com.example.triangle;

import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.List;

public class DrawingTriangle extends AbstractTriangleDrawer {

    public DrawingTriangle(ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        super(firstPoint, secondPoint, thirdPoint);
    }
    @Override
    public void actualDraw(GraphicsContext graphicsContext, List<ColorPoint> points) {
        //sortVertex();
        ColorPoint firstPoint = points.get(0);
        ColorPoint secondPoint = points.get(1);
        ColorPoint thirdPoint = points.get(2);

        double xAxisIncrement12 = getXAxisIncrement(firstPoint, secondPoint);
        double xAxisIncrement13 = getXAxisIncrement(firstPoint, thirdPoint);
        double xAxisIncrement23 = getXAxisIncrement(secondPoint, thirdPoint);

        double leftBoard = firstPoint.getX();
        double rightBoard = leftBoard;

        double firstLeftIncrement, firstRightIncrement, secondLeftIncrement, secondRightIncrement;

        if (xAxisIncrement13 > xAxisIncrement12) {
            firstLeftIncrement = xAxisIncrement12;
            firstRightIncrement = secondRightIncrement = xAxisIncrement13;
            secondLeftIncrement = xAxisIncrement23;
        } else {
            firstLeftIncrement = secondLeftIncrement = xAxisIncrement13;
            firstRightIncrement = xAxisIncrement12;
            secondRightIncrement = xAxisIncrement23;
        }

        drawPartTriangle(graphicsContext, firstPoint.getY(), secondPoint.getY() - 1, leftBoard, rightBoard,
                firstLeftIncrement, firstRightIncrement, firstPoint, secondPoint, thirdPoint);

        if (firstPoint.getY() == secondPoint.getY()) {
            leftBoard = firstPoint.getX();
            rightBoard = secondPoint.getX();
            secondLeftIncrement = xAxisIncrement13;
            secondRightIncrement = xAxisIncrement23;
        } else {
            int height = secondPoint.getY() - firstPoint.getY();
            leftBoard += height * firstLeftIncrement;
            rightBoard += height * firstRightIncrement;
        }

        drawPartTriangle(graphicsContext, secondPoint.getY(), thirdPoint.getY(), leftBoard, rightBoard,
                secondLeftIncrement, secondRightIncrement, firstPoint, secondPoint, thirdPoint);
    }
/*
    private void sortVertex() {
        List<ColorPoint> points = Arrays.asList(firstPoint, secondPoint, thirdPoint);
        points.sort((a, b) -> {
            if (a.getY() == b.getY()) {
                return a.getX() - b.getX();
            } else {
                return a.getY() - b.getY();
            }
        });

        firstPoint = points.get(0);
        secondPoint = points.get(1);
        thirdPoint = points.get(2);
    }*/

    private void drawPartTriangle(GraphicsContext graphicsContext, int startY, int endY, double leftBoard,
                                  double rightBoard, double leftIncrement, double rightIncrement,
                                  ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        for (int i = startY; i <= endY; i++) {

            for (int j = (int) leftBoard; j <= (int) rightBoard; j++) {
                graphicsContext.getPixelWriter().setColor(j, i, getInterpolationColor(j, i, firstPoint, secondPoint, thirdPoint));
            }

            leftBoard += leftIncrement;
            rightBoard += rightIncrement;
        }
    }

    private double getXAxisIncrement(ColorPoint firstPont, ColorPoint secondPoint) {
        if (firstPont.getY() == secondPoint.getY()) {
            return 0;
        } else  {
            double increment = secondPoint.getX() - firstPont.getX();
            increment /= secondPoint.getY() - firstPont.getY();
            return increment;
        }
    }

    private javafx.scene.paint.Color getInterpolationColor(int currentX, int currentY, ColorPoint firstPoint,
                                                           ColorPoint secondPoint, ColorPoint thirdPoint) {
        double alpha, beta;

        double numerator = firstPoint.getX() * (currentY - thirdPoint.getY()) +
                currentX * (thirdPoint.getY() - firstPoint.getY()) + thirdPoint.getX() * (firstPoint.getY() - currentY);

        double denominator = firstPoint.getX() * (secondPoint.getY() - thirdPoint.getY()) +
                secondPoint.getX() * (thirdPoint.getY() - firstPoint.getY()) + thirdPoint.getX() *
                (firstPoint.getY() - secondPoint.getY());

        beta = numerator / denominator;

        if (firstPoint.getX() == thirdPoint.getX()) {
            alpha = (currentY - thirdPoint.getY() + beta * (thirdPoint.getY() - secondPoint.getY())) /
                    (firstPoint.getY() - thirdPoint.getY());
        } else {
            alpha = (currentX - thirdPoint.getX() - beta * (secondPoint.getX() - thirdPoint.getX())) /
                    (firstPoint.getX() - thirdPoint.getX());
        }

        float r = (float) (alpha * firstPoint.getColor().getRed() + beta * secondPoint.getColor().getRed() +
                (1 - alpha - beta) * thirdPoint.getColor().getRed());
        r = getGoodValue(r);

        float g = (float) (alpha * firstPoint.getColor().getGreen() + beta * secondPoint.getColor().getGreen() +
                (1 - alpha - beta) * thirdPoint.getColor().getGreen());
        g = getGoodValue(g);

        float b = (float) (alpha * firstPoint.getColor().getBlue() + beta * secondPoint.getColor().getBlue() +
                (1 - alpha - beta) * thirdPoint.getColor().getBlue());
        b = getGoodValue(b);

        return new javafx.scene.paint.Color(r, g, b,1);
    }

    private float getGoodValue(float color) {
        if (color > 1) {
            return 1;
        }

        return Math.max(color, 0);
    }
}