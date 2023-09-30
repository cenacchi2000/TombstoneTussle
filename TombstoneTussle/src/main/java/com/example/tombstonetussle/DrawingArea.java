package com.example.tombstonetussle;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DrawingArea extends BorderPane {

    private Canvas backgroundCanvas; // For grid and human contour
    private Canvas drawingCanvas; // For player drawing
    private ColorPicker colorPicker;
    private ToggleButton drawButton;
    private ToggleButton eraseButton;
    private GraphicsContext bgGc; // Graphics context for backgroundCanvas
    private GraphicsContext drawingGc; // Graphics context for drawingCanvas

    public DrawingArea() {
        // Setup the color picker
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK); // Default color

        // Drawing and erasing buttons
        drawButton = new ToggleButton("✎");
        eraseButton = new ToggleButton("Eraser");

        ToggleGroup group = new ToggleGroup();
        drawButton.setToggleGroup(group);
        eraseButton.setToggleGroup(group);
        drawButton.setSelected(true); // Default is drawing

        VBox rightControls = new VBox(10, colorPicker, drawButton, eraseButton);
        setRight(rightControls);

        // Setup the canvases
        backgroundCanvas = new Canvas(500, 500);
        drawingCanvas = new Canvas(500, 500);

        bgGc = backgroundCanvas.getGraphicsContext2D();
        drawingGc = drawingCanvas.getGraphicsContext2D();

        bgGc.setStroke(Color.BLACK);
        bgGc.setLineWidth(2);

        // Drawing a fine grid and an human contour
        drawGrid();
        drawHumanContour();

        StackPane stackPane = new StackPane(backgroundCanvas, drawingCanvas);
        setCenter(stackPane);

        // Event to handle drawing or erasing
        drawingCanvas.setOnMouseDragged(event -> {
            double size = 5.0; // Brush size
            double x = event.getX() - size / 2;
            double y = event.getY() - size / 2;

            if (isInsideHumanFigure(x, y) && drawButton.isSelected()) {
                drawingGc.setFill(colorPicker.getValue());
                drawingGc.fillRect(x, y, size, size);
            } else if (isInsideHumanFigure(x, y) && eraseButton.isSelected()) {
                drawingGc.clearRect(x, y, size, size);
            }
        });
    }

    private void drawGrid() {
        for (int i = 0; i < backgroundCanvas.getWidth(); i += 10) {
            for (int j = 0; j < backgroundCanvas.getHeight(); j += 10) {
                if (isInsideHumanFigure(i, j)) {
                    bgGc.strokeRect(i, j, 10, 10);
                }
            }
        }
    }

    //TO RE-DO
    private void drawHumanContour() {
        bgGc.strokeOval(225, 50, 50, 50); // Head
        bgGc.strokeRect(240, 100, 20, 100); // Body
        bgGc.strokeRect(240, 200, 10, 100); // Left leg
        bgGc.strokeRect(250, 200, 10, 100); // Right leg
        bgGc.strokeRect(210, 110, 30, 10); // Left arm
        bgGc.strokeRect(260, 110, 30, 10); // Right arm
    }

    //TO RE-DO
    private boolean isInsideHumanFigure(double x, double y) {
        // This method checks if the point (x,y) is inside any of the body parts.
        boolean insideHead = (x - 250) * (x - 250) + (y - 75) * (y - 75) <= 25 * 25;
        boolean insideBody = x >= 240 && x <= 260 && y >= 100 && y <= 200;
        boolean insideLeftLeg = x >= 240 && x <= 250 && y >= 200 && y <= 300;
        boolean insideRightLeg = x >= 250 && x <= 260 && y >= 200 && y <= 300;
        boolean insideLeftArm = x >= 210 && x <= 240 && y >= 110 && y <= 120;
        boolean insideRightArm = x >= 260 && x <= 290 && y >= 110 && y <= 120;

        return insideHead || insideBody || insideLeftLeg || insideRightLeg || insideLeftArm || insideRightArm;
    }
}
