
package com.example.tombstonetussle;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DrawingView extends BorderPane {

    private Canvas backgroundCanvas; // For grid and human contour
    private Canvas drawingCanvas; // For player drawing
    private ColorPicker colorPicker;
    private ToggleButton drawButton;
    private ToggleButton eraseButton;

    public DrawingView() {
        // Setup the color picker
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK); // Default color

        // Drawing and erasing buttons
        drawButton = new ToggleButton("✎");
        eraseButton = new ToggleButton("Eraser");

        VBox rightControls = new VBox(10, colorPicker, drawButton, eraseButton);
        setRight(rightControls);

        // Setup the canvases
        backgroundCanvas = new Canvas(500, 500);
        drawingCanvas = new Canvas(500, 500);

        StackPane stackPane = new StackPane(backgroundCanvas, drawingCanvas);
        setCenter(stackPane);
    }

    public Canvas getBackgroundCanvas() {
        return backgroundCanvas;
    }

    public Canvas getDrawingCanvas() {
        return drawingCanvas;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public ToggleButton getDrawButton() {
        return drawButton;
    }

    public ToggleButton getEraseButton() {
        return eraseButton;
    }
}
