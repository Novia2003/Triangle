package com.example.triangle;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private final int WIDTH = 1500;
    private final int HEIGHT = 800;

    @FXML
    private Canvas canvas = new Canvas(HEIGHT, WIDTH);
    private GraphicsContext graphicsContext;

    private ColorPoint firstPoint = new ColorPoint(600, 300, javafx.scene.paint.Color.BLUE);
    private ColorPoint secondPoint = new ColorPoint(700, 200, javafx.scene.paint.Color.GREEN);
    private ColorPoint thirdPoint = new ColorPoint(700, 500, javafx.scene.paint.Color.RED);

    private int selectedVertex = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicsContext = canvas.getGraphicsContext2D();
        repaint();
    }


    private void repaint() {
        paint(graphicsContext);
    }

    @FXML
    private void onMousePressed(MouseEvent mouseEvent) {
        int currentX = (int) mouseEvent.getX();
        int currentY = (int) mouseEvent.getY();

        int errorRate = 3;

        if (Math.abs(currentX - firstPoint.getX()) < errorRate && Math.abs(currentY - firstPoint.getY()) < errorRate) {
            selectedVertex = 1;
        } else {
            if (Math.abs(currentX - secondPoint.getX()) < errorRate && Math.abs(currentY - secondPoint.getY()) < errorRate) {
                selectedVertex = 2;
            } else {
                if (Math.abs(currentX - thirdPoint.getX()) < errorRate && Math.abs(currentY - thirdPoint.getY()) < errorRate) {
                    selectedVertex = 3;
                } else {
                    selectedVertex = 0;
                }
            }
        }
    }

    @FXML
    private void onMouseDragged(MouseEvent mouseEvent) {
        int currentX = (int) mouseEvent.getX();
        int currentY = (int) mouseEvent.getY();

        switch (selectedVertex) {
            case (1) -> firstPoint = new ColorPoint(currentX, currentY, firstPoint.getColor());
            case (2) -> secondPoint = new ColorPoint(currentX, currentY, secondPoint.getColor());
            case (3) -> thirdPoint = new ColorPoint(currentX, currentY, thirdPoint.getColor());
        }

        repaint();
    }

    protected void paint(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        ITriangleDrawer triangle = new DrawingTriangle2(firstPoint, secondPoint, thirdPoint);
        triangle.drawTriangle(graphicsContext);
    }
}