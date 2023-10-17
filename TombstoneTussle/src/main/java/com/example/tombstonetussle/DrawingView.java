// Package declaration
package com.example.tombstonetussle;

// Importing necessary JavaFX classes for UI components, layout, and image processing
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

// The class that acts as the view for the drawing functionality
public class DrawingView extends BorderPane {

    // Instance variables for UI components and data
    private Canvas backgroundCanvas; // Canvas for displaying the grid and the human contour
    private Canvas drawingCanvas; // Canvas where the player can draw
    private GameController gameController; // Controller for the game functionality
    private ColorPicker colorPicker; // UI component for color selection
    private ToggleButton drawButton; // Button for activating the drawing mode
    private ToggleButton eraseButton; // Button for activating the erasing mode
    private Button saveButton; // Button to save the drawing
    private Image zombieImage; // Image of the zombie
    private Label backArrowLabel; // Label to represent a back arrow for navigation
    private DrawingModel drawingModel; // Reference to the model that contains the business logic related to drawing

    // Constructor to initialize the view with the drawing model and game controller
    public DrawingView(DrawingModel drawingModel, GameController gameController) {
        this.drawingModel = drawingModel;
        this.gameController = gameController;

        // Setting up the back arrow for navigation
        backArrowLabel = new Label("⬅️");
        backArrowLabel.setFont(new Font(24));
        backArrowLabel.setId("backArrow");
        // Adding an event handler to handle the back navigation when the back arrow is clicked
        backArrowLabel.setOnMouseClicked(event -> gameController.handleBackFromDrawing());
        setTop(backArrowLabel);

        // Initializing the color picker with a default value
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);

        // Initializing the canvases
        backgroundCanvas = new Canvas(500, 500);
        drawingCanvas = new Canvas(500, 500);

        // Getting the zombie image from the drawing model
        zombieImage = drawingModel.getZombieImage();

        // Calculations to properly scale and center the zombie image on the canvas
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

        // Drawing the scaled and centered zombie image on the background canvas
        backgroundCanvas.getGraphicsContext2D().drawImage(zombieImage,
                (canvasWidth - newWidth) / 2, (canvasHeight - newHeight) / 2, newWidth, newHeight);

        // Initializing the draw and erase buttons
        drawButton = new ToggleButton("✎");
        eraseButton = new ToggleButton("Eraser");

        // Initializing the save button and creating a VBox for the right side controls
        saveButton = new Button("Save");
        VBox rightControls = new VBox(10, colorPicker, drawButton, eraseButton, saveButton);
        setRight(rightControls);

        // Creating a StackPane to overlay the drawing canvas on top of the background canvas
        StackPane stackPane = new StackPane(backgroundCanvas, drawingCanvas);
        setCenter(stackPane);
    }

    // Getter methods for various components
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

    public Button getSaveButton() {
        return saveButton;
    }

    // Method to clear the drawing canvas
    public void clearCanvas() {
        drawingCanvas.getGraphicsContext2D().clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
    }
}
