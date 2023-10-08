package com.example.tombstonetussle;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

public class DrawingModel {

    // This method checks if the point (x,y) is inside any of the body parts.
    public boolean isInsideHumanFigure(DrawingView view, double x, double y) {
        int countInside = 0;
        int totalPixels = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Color color = view.getPixelColor(x + i, y + j);
                if (color != null && color.getRed() < 0.9 && color.getGreen() < 0.9 && color.getBlue() < 0.9) {
                    countInside++;
                }
                totalPixels++;
            }
        }

        return (double) countInside / totalPixels < 0.5;  // at least 50% of the pixels of the cell must be inside
    }

    public WritableImage getDrawingImage(Canvas drawingCanvas, Canvas backgroundCanvas) {
        WritableImage writableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());

        // Capture image snapshot from canvas as WritableImage
        WritableImage drawingWritableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());
        drawingCanvas.snapshot(null, drawingWritableImage);
        WritableImage bgWritableImage = new WritableImage((int) backgroundCanvas.getWidth(), (int) backgroundCanvas.getHeight());
        backgroundCanvas.snapshot(null, bgWritableImage);

        // Get PixelReader from WritableImages
        PixelReader bgPixelReader = bgWritableImage.getPixelReader();
        PixelReader drawingPixelReader = drawingWritableImage.getPixelReader();

        // Combine background and drawing canvas
        for (int x = 0; x < writableImage.getWidth(); x++) {
            for (int y = 0; y < writableImage.getHeight(); y++) {
                Color bgColor = bgPixelReader.getColor(x, y);
                Color drawingColor = drawingPixelReader.getColor(x, y);

                // If the drawing color is different from white (or any default background color), use it
                if (!drawingColor.equals(Color.WHITE)) {
                    writableImage.getPixelWriter().setColor(x, y, drawingColor);
                }
                // If it is NOT the gray grid, use the background color
                else if (!(bgColor.getRed() > 0.1 && bgColor.getGreen() > 0.1 && bgColor.getBlue() > 0.1)) {
                    writableImage.getPixelWriter().setColor(x, y, bgColor);
                }
                // Otherwise, set transparent
                else {
                    writableImage.getPixelWriter().setColor(x, y, Color.TRANSPARENT);
                }
            }
        }

        return writableImage;
    }


}
