// Package declaration
package com.example.tombstonetussle;

// Import required classes for image processing, canvas drawing, and collections
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

// Public class named 'DrawingModel' to handle image drawing and manipulation
public class DrawingModel {
    // Instance variable to store the zombie image
    private Image zombieImage;

    // Default constructor for the class
    public DrawingModel() {
        // Load the zombie image from the specified location
        zombieImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/zombie.png"));
        // Load and add the default character to the list as the first item
        loadDefaultCharacter();
    }

    // Getter method for the zombie image
    public Image getZombieImage() {
        return zombieImage;
    }

    // Method to get the color of the pixel at a specified (x, y) coordinate in the zombie image
    public Color getPixelColor(double x, double y) {
        // Check if the coordinates are outside the bounds of the image, if so return a transparent color
        if (x < 0 || x >= zombieImage.getWidth() || y < 0 || y >= zombieImage.getHeight()) {
            return Color.TRANSPARENT;
        }
        // Obtain a PixelReader object from the zombie image
        PixelReader reader = zombieImage.getPixelReader();
        // Return the color of the pixel at the specified coordinates
        return reader.getColor((int) x, (int) y);
    }

    // List to store different character images
    private List<WritableImage> characters = new ArrayList<>();

    // Method to check if a point (x,y) is inside the human figure in the image
    public boolean isInsideHumanFigure(double x, double y) {
        int countInside = 0;  // Counter to track the number of pixels inside the human figure
        int totalPixels = 0;  // Counter to track the total number of pixels checked

        // Iterate over a 10x10 grid around the specified point
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // Get the color of the current pixel
                Color color = getPixelColor(x + i, y + j);
                // Check if the color is non-white (indicating it's part of the figure) and increment the counter
                if (color != null && color.getRed() < 0.9 && color.getGreen() < 0.9 && color.getBlue() < 0.9) {
                    countInside++;
                }
                totalPixels++;
            }
        }

        // Return true if at least 50% of the pixels in the grid are inside the human figure, otherwise return false
        return (double) countInside / totalPixels < 0.5;
    }

    // Method to obtain a combined image from the drawing canvas and background canvas
    public WritableImage getDrawingImage(Canvas drawingCanvas, Canvas backgroundCanvas) {
        // Create a new writable image of the size of the drawing canvas
        WritableImage writableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());

        // Capture image snapshots from both the drawing and background canvases
        WritableImage drawingWritableImage = new WritableImage((int) drawingCanvas.getWidth(), (int) drawingCanvas.getHeight());
        drawingCanvas.snapshot(null, drawingWritableImage);
        WritableImage bgWritableImage = new WritableImage((int) backgroundCanvas.getWidth(), (int) backgroundCanvas.getHeight());
        backgroundCanvas.snapshot(null, bgWritableImage);

        // Obtain PixelReaders for both captured images
        PixelReader bgPixelReader = bgWritableImage.getPixelReader();
        PixelReader drawingPixelReader = drawingWritableImage.getPixelReader();

        // Iterate over each pixel in the writable image
        for (int x = 0; x < writableImage.getWidth(); x++) {
            for (int y = 0; y < writableImage.getHeight(); y++) {
                // Get the colors from the background and drawing images at the current pixel
                Color bgColor = bgPixelReader.getColor(x, y);
                Color drawingColor = drawingPixelReader.getColor(x, y);

                // If the drawing pixel is not white, use its color in the writable image
                if (!drawingColor.equals(Color.WHITE)) {
                    writableImage.getPixelWriter().setColor(x, y, drawingColor);
                }
                // If the background pixel is not gray, use its color in the writable image
                else if (!(bgColor.getRed() > 0.1 && bgColor.getGreen() > 0.1 && bgColor.getBlue() > 0.1)) {
                    writableImage.getPixelWriter().setColor(x, y, bgColor);
                }
                // Otherwise, set the pixel in the writable image to transparent
                else {
                    writableImage.getPixelWriter().setColor(x, y, Color.TRANSPARENT);
                }
            }
        }

        // Return the combined writable image
        return writableImage;
    }

    // Private method to load the default character into the characters list
    private void loadDefaultCharacter() {
        // Load the default zombie character image from the specified location
        Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/zombieOriginal.png"));
        // Convert the default image into a writable image
        WritableImage writableDefault = new WritableImage(defaultImage.getPixelReader(),
                (int) defaultImage.getWidth(),
                (int) defaultImage.getHeight());
        // Add the writable image to the characters list
        characters.add(writableDefault);
    }

    // Method to save a writable image into the characters list
    public void save(WritableImage image) {
        this.characters.add(image);
    }

    // Method to get a character image from the characters list by its index
    public WritableImage getCharacter(int index) {
        if (index >= 0 && index < characters.size()) {
            return characters.get(index);
        }
        return null;
    }

    // Getter method for the entire list of character images
    public List<WritableImage> getAllCharacters() {
        return characters;
    }

}
