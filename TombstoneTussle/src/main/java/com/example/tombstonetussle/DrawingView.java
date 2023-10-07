package com.example.tombstonetussle;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class DrawingView extends BorderPane {

    private Canvas backgroundCanvas; // For grid and human contour
    private Canvas drawingCanvas; // For player drawing
    private ColorPicker colorPicker;
    private ToggleButton drawButton;
    private ToggleButton eraseButton;
    private Image zombieImage;

    public DrawingView() {
        // Setup the color picker
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);

        // Setup the canvases
        backgroundCanvas = new Canvas(500, 500);
        drawingCanvas = new Canvas(500, 500);

        zombieImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/zombie.png"));

        double canvasWidth = backgroundCanvas.getWidth();
        double canvasHeight = backgroundCanvas.getHeight();

        double imageWidth = zombieImage.getWidth();
        double imageHeight = zombieImage.getHeight();

        double aspectRatio = imageWidth / imageHeight;
        double newWidth = canvasWidth;
        double newHeight = canvasWidth / aspectRatio;

        if (newHeight > canvasHeight) {
            newHeight = canvasHeight;
            newWidth = canvasHeight * aspectRatio;
        }

        backgroundCanvas.getGraphicsContext2D().drawImage(zombieImage,
                (canvasWidth - newWidth) / 2, (canvasHeight - newHeight) / 2, newWidth, newHeight);

        // Drawing and erasing buttons
        drawButton = new ToggleButton("✎");
        eraseButton = new ToggleButton("Eraser");

        VBox rightControls = new VBox(10, colorPicker, drawButton, eraseButton);
        setRight(rightControls);

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

    public Color getPixelColor(double x, double y) {
        if (x < 0 || x >= zombieImage.getWidth() || y < 0 || y >= zombieImage.getHeight()) {
            return Color.TRANSPARENT;
        }
        PixelReader reader = zombieImage.getPixelReader();
        return reader.getColor((int) x, (int) y);
    }

}
