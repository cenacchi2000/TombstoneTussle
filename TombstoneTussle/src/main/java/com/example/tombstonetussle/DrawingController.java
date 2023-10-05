
package com.example.tombstonetussle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DrawingController {

    private DrawingModel drawingModel;
    private DrawingView drawingView;
    private GraphicsContext bgGc; // Graphics context for backgroundCanvas
    private GraphicsContext drawingGc; // Graphics context for drawingCanvas

    public DrawingController(DrawingModel model, DrawingView view) {
        this.drawingModel = model;
        this.drawingView = view;
        bgGc = drawingView.getBackgroundCanvas().getGraphicsContext2D();
        drawingGc = drawingView.getDrawingCanvas().getGraphicsContext2D();

        bgGc.setStroke(Color.BLACK);
        bgGc.setLineWidth(2);

        // Drawing a fine grid and a human contour
        drawGrid();
        drawHumanContour();

        // Event to handle drawing or erasing
        setupEventHandlers();
    }

    private void drawGrid() {
        for (int i = 0; i < drawingView.getBackgroundCanvas().getWidth(); i += 10) {
            for (int j = 0; j < drawingView.getBackgroundCanvas().getHeight(); j += 10) {
                if (drawingModel.isInsideHumanFigure(i, j)) {
                    bgGc.strokeRect(i, j, 10, 10);
                }
            }
        }
    }

    private void drawHumanContour() {
        bgGc.strokeOval(225, 50, 50, 50); // Head
        bgGc.strokeRect(240, 100, 20, 100); // Body
        bgGc.strokeRect(240, 200, 10, 100); // Left leg
        bgGc.strokeRect(250, 200, 10, 100); // Right leg
        bgGc.strokeRect(210, 110, 30, 10); // Left arm
        bgGc.strokeRect(260, 110, 30, 10); // Right arm
    }

    private void setupEventHandlers() {
        drawingView.getDrawingCanvas().setOnMouseDragged(event -> {
            double size = 5.0; // Brush size
            double x = event.getX() - size / 2;
            double y = event.getY() - size / 2;

            if (drawingModel.isInsideHumanFigure(x, y)) {
                if (drawingView.getDrawButton().isSelected()) {
                    drawingGc.setFill(drawingView.getColorPicker().getValue());
                    drawingGc.fillRect(x, y, size, size);
                } else if (drawingView.getEraseButton().isSelected()) {
                    drawingGc.clearRect(x, y, size, size);
                }
            }
        });

        drawingView.getDrawButton().setOnAction(event -> {
            if (drawingView.getDrawButton().isSelected()) {
                drawingView.getEraseButton().setSelected(false);
            }
        });

        drawingView.getEraseButton().setOnAction(event -> {
            if (drawingView.getEraseButton().isSelected()) {
                drawingView.getDrawButton().setSelected(false);
            }
        });
    }

}
