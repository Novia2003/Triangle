package com.example.triangle;

import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.List;

public class DrawingTriangle2 extends AbstractTriangleDrawer {
    public DrawingTriangle2(ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        super(firstPoint, secondPoint, thirdPoint);
    }

    @Override
    protected void actualDraw(GraphicsContext graphicsContext, List<ColorPoint> points) {
        ColorPoint firstPoint = points.get(0);
        ColorPoint secondPoint = points.get(1);
        ColorPoint thirdPoint = points.get(2);

        double xAxisIncrement12 = getXAxisIncrement(firstPoint, secondPoint);
        double xAxisIncrement13 = getXAxisIncrement(firstPoint, thirdPoint);

        int[] firstLeftBoard, firstRightBoard, secondLeftBoard, secondRightBoard;

        if (firstPoint.getY() != secondPoint.getY()) {
            ColorPoint auxiliaryPoint = getAuxiliaryPoint(firstPoint, secondPoint, thirdPoint);

            if (xAxisIncrement13 > xAxisIncrement12) {
                firstLeftBoard = drawBresenhamLine(firstPoint, secondPoint, true);
                firstRightBoard = drawBresenhamLine(firstPoint, auxiliaryPoint, false);
                secondRightBoard = drawBresenhamLine(auxiliaryPoint, thirdPoint, false);
                secondLeftBoard = drawBresenhamLine(secondPoint, thirdPoint, true);
            } else {
                firstLeftBoard = drawBresenhamLine(firstPoint, auxiliaryPoint, true);
                secondLeftBoard = drawBresenhamLine(auxiliaryPoint, thirdPoint, true);
                firstRightBoard = drawBresenhamLine(firstPoint, secondPoint, false);
                secondRightBoard = drawBresenhamLine(secondPoint, thirdPoint, false);
            }

            drawPartTriangle(graphicsContext, firstPoint.getY(), firstLeftBoard, firstRightBoard,
                    firstPoint, secondPoint, thirdPoint);
        } else {
            secondLeftBoard = drawBresenhamLine(firstPoint, thirdPoint, true);
            secondRightBoard = drawBresenhamLine(secondPoint, thirdPoint, false);
        }

        drawPartTriangle(graphicsContext, secondPoint.getY(), secondLeftBoard, secondRightBoard,
                firstPoint, secondPoint, thirdPoint);
    }

    private ColorPoint getAuxiliaryPoint(ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        int dy12 = secondPoint.getY() - firstPoint.getY();
        int dy13 = thirdPoint.getY() - firstPoint.getY();
        int dx13 = thirdPoint.getX() - firstPoint.getX();
        int x = dy12 * dx13 / dy13 + firstPoint.getX();

        return new ColorPoint(x, secondPoint.getY(), null);
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

    private int[] drawBresenhamLine(ColorPoint firstPoint, ColorPoint secondPoint, boolean isLeftBoard) {
        int x, y, deltaX, deltaY, additionX, additionY, incrementX, incrementY, error, errorIncrease, errorDecrease;
        int initialHeight = firstPoint.getY();

        deltaX = secondPoint.getX() - firstPoint.getX();
        deltaY = secondPoint.getY() - initialHeight;

        int[] array = new int[deltaY + 1];
        Arrays.fill(array, -1);

        incrementX = Integer.compare(deltaX, 0);
        incrementY = Integer.compare(deltaY, 0);

        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);

        if (deltaX > deltaY) {
            additionX = incrementX;
            additionY = 0;
            errorIncrease = deltaX;
            errorDecrease = deltaY;
        } else {
            additionX = 0;
            additionY = incrementY;
            errorIncrease = deltaY;
            errorDecrease = deltaX;
        }

        error = errorIncrease / 2;
        x = firstPoint.getX();
        y = initialHeight;
        array[0] = x;

        for (int i = 0; i < errorIncrease; i++) {
            error -= errorDecrease;

            if (error < 0) {
                error += errorIncrease;
                x += incrementX;
                y += incrementY;
            } else {
                x += additionX;
                y += additionY;
            }

            int index = y - initialHeight;

            if (array[index] != -1) {
                if (array[index] > x && isLeftBoard) { // для левой
                    array[index] = x;
                }
            } else {
                array[index] = x;
            }
        }

        return array;
    }

    private void drawPartTriangle(GraphicsContext graphicsContext, int startY, int[] leftArray, int[] rightArray,
                                  ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        for (int i = 0; i < leftArray.length; i++) {
            int leftBoard = leftArray[i];
            int rightBoard = rightArray[i];
            int y = i + startY;

            for (int x = leftBoard; x <= rightBoard; x++) {
                graphicsContext.getPixelWriter().setColor(x, y, getInterpolationColor(x, y, firstPoint, secondPoint, thirdPoint));
            }
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

/*
public class DrawingTriangle2 extends AbstractTriangleDrawer {
    public DrawingTriangle2(ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        super(firstPoint, secondPoint, thirdPoint);
    }

    @Override
    protected void actualDraw(GraphicsContext graphicsContext, List<ColorPoint> points) {
        ColorPoint firstPoint = points.get(0);
        ColorPoint secondPoint = points.get(1);
        ColorPoint thirdPoint = points.get(2);

        int deltaX12 = secondPoint.getX() - firstPoint.getX();
        int deltaY12 = secondPoint.getY() - firstPoint.getY();
        int deltaX13 = thirdPoint.getX() - firstPoint.getX();
        int deltaY13 = thirdPoint.getY() - firstPoint.getY();
        int deltaX23 = thirdPoint.getX() - secondPoint.getX();
        int deltaY23 = thirdPoint.getY() - secondPoint.getY();


        double xAxisIncrement12 = getXAxisIncrement(deltaX12, deltaY12);
        double xAxisIncrement13 = getXAxisIncrement(deltaX13, deltaY13);

        int firstLeftDeltaX, firstRightDeltaX, firstLeftDeltaY, firstRightDeltaY, secondLeftDeltaX, secondRightDeltaX,
                secondLeftDeltaY, secondRightDeltaY;

        if (xAxisIncrement13 > xAxisIncrement12) {
            firstLeftDeltaX = deltaX12;
            firstLeftDeltaY = deltaY12;
            firstRightDeltaX = secondRightDeltaX = deltaX13;
            firstRightDeltaY = secondRightDeltaY = deltaY13;
            secondLeftDeltaX = deltaX23;
            secondLeftDeltaY = deltaY23;
        } else {
            firstLeftDeltaX = secondLeftDeltaX = deltaX13;
            firstLeftDeltaY = secondLeftDeltaY = deltaY13;
            firstRightDeltaX = deltaX12;
            firstRightDeltaY = deltaY12;
            secondRightDeltaX = deltaX23;
            secondRightDeltaY = deltaY23;
        }

        int currentY = firstPoint.getY();
        int leftBoard = firstPoint.getX();
        int rightBoard = leftBoard;
        graphicsContext.getPixelWriter().setColor(leftBoard, currentY,
                getInterpolationColor(leftBoard, currentY, firstPoint, secondPoint, thirdPoint));


        int errorLeft, errorRight, errorAdditionLeft, errorAdditionRight, errorDecreaseLeft, errorDecreaseRight;

        boolean firstLeftDeltaXBigger = Math.abs(firstLeftDeltaX) > firstLeftDeltaY;
        boolean firstRightDeltaXBigger = Math.abs(firstRightDeltaX) > firstRightDeltaY;

        if (firstLeftDeltaXBigger) {
            errorAdditionLeft = Math.abs(firstLeftDeltaX);
            errorDecreaseLeft = firstLeftDeltaY;
        } else {
            errorAdditionLeft = firstLeftDeltaY;
            errorDecreaseLeft = Math.abs(firstLeftDeltaX);
        }

        errorLeft = errorAdditionLeft / 2;
        int firstLeftIncrement = Integer.compare(firstLeftDeltaX, 0);

        if (firstRightDeltaXBigger) {
            errorAdditionRight = Math.abs(firstRightDeltaX);
            errorDecreaseRight = firstRightDeltaY;
        } else {
            errorAdditionRight = firstRightDeltaY;
            errorDecreaseRight = Math.abs(firstRightDeltaX);
        }



        errorRight = errorAdditionRight / 2;
        int firstRightIncrement = Integer.compare(firstRightDeltaX, 0);

        while (currentY < secondPoint.getY()) {
            errorLeft -= errorDecreaseLeft;

            if (errorLeft < 0) {
                errorLeft += errorAdditionLeft;
                leftBoard += firstLeftIncrement;
            } else {
                if (errorDecreaseLeft != 0) {
                    while (errorLeft >= 0) {
                        leftBoard += (firstLeftDeltaXBigger) ? firstLeftIncrement : 0;
                        errorLeft -= errorDecreaseLeft;
                    }
                    errorLeft += errorDecreaseLeft;
                }
            }

            errorRight -= errorDecreaseRight;

            if (errorRight < 0) {
                errorRight += errorAdditionRight;
                rightBoard += firstRightIncrement;
            } else {
                if (errorDecreaseRight != 0) {
                    while (errorRight >= 0) {
                        rightBoard += (firstRightDeltaXBigger) ? firstRightIncrement : 0;
                        errorRight -= errorDecreaseRight;
                    }
                    errorRight += errorDecreaseRight;
                }
            }


            currentY++;

            for (int x = leftBoard; x <= rightBoard; x++) {
                graphicsContext.getPixelWriter().setColor(x, currentY,
                        getInterpolationColor(x, currentY, firstPoint, secondPoint, thirdPoint));
            }
        }

        if (firstPoint.getY() == secondPoint.getY()) {
            leftBoard = firstPoint.getX();
            rightBoard = secondPoint.getX();
            secondLeftDeltaX = deltaX13;
            secondLeftDeltaY = deltaY13;
            secondRightDeltaX = deltaX23;
            secondRightDeltaY = deltaY23;
        }

        boolean secondLeftDeltaXBigger = Math.abs(secondLeftDeltaX) > secondLeftDeltaY;
        boolean secondRightDeltaXBigger = Math.abs(secondRightDeltaX) > secondRightDeltaY;

        if (secondLeftDeltaXBigger) {
            errorAdditionLeft = Math.abs(secondLeftDeltaX);
            errorDecreaseLeft = secondLeftDeltaY;
        } else {
            errorAdditionLeft = secondLeftDeltaY;
            errorDecreaseLeft = Math.abs(secondLeftDeltaX);
        }

        errorLeft = errorAdditionLeft / 2;
        int secondLeftIncrement = Integer.compare(secondLeftDeltaX, 0);

        if (secondRightDeltaXBigger) {
            errorAdditionRight = Math.abs(secondRightDeltaX);
            errorDecreaseRight = secondRightDeltaY;
        } else {
            errorAdditionRight = secondRightDeltaY;
            errorDecreaseRight = Math.abs(secondRightDeltaX);
        }

        errorRight = errorAdditionRight / 2;
        int secondRightIncrement = Integer.compare(secondRightDeltaX, 0);

        while (currentY <= thirdPoint.getY()) {
            errorLeft -= errorDecreaseLeft;

            if (errorLeft < 0) {
                errorLeft += errorAdditionLeft;
                leftBoard += secondLeftIncrement;
            } else {
                if (errorDecreaseLeft != 0) {
                    while (errorLeft >= 0) {
                        leftBoard += (secondLeftDeltaXBigger) ? secondLeftIncrement : 0;
                        errorLeft -= errorDecreaseLeft;
                    }
                    errorLeft += errorDecreaseLeft;
                }
            }

            errorRight -= errorDecreaseRight;

            if (errorRight < 0) {
                errorRight += errorAdditionRight;
                rightBoard += secondRightIncrement;
            } else {
                if (errorDecreaseRight != 0) {
                    while (errorRight >= 0) {
                        rightBoard += (secondRightDeltaXBigger) ? secondRightIncrement : 0;
                        errorRight -= errorDecreaseRight;
                    }
                    errorRight += errorDecreaseRight;
                }
            }

            currentY++;

            for (int x = leftBoard; x <= rightBoard; x++) {
                graphicsContext.getPixelWriter().setColor(x, currentY,
                        getInterpolationColor(x, currentY, firstPoint, secondPoint, thirdPoint));
            }
        }
    }

    private void drawPartTriangle(GraphicsContext graphicsContext, int leftBoard, int rightBoard, int y,
                                  ColorPoint firstPoint, ColorPoint secondPoint, ColorPoint thirdPoint) {
        for (int j = leftBoard; j <= rightBoard; j++) {
            graphicsContext.getPixelWriter().setColor(j, y, getInterpolationColor(j, y, firstPoint, secondPoint, thirdPoint));
        }
    }

    private double getXAxisIncrement(int deltaX, int deltaY) {
        if (deltaY == 0) {
            return 0;
        } else  {
            double increment = deltaX;
            increment /= deltaY;
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
 */