// Package declaration
package com.example.tombstonetussle;

// Importing necessary classes for drawing operations and image processing
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// The class that acts as the controller for the drawing operations
public class DrawingController {

    // Instance variables
    private DrawingModel drawingModel;  // Model that contains the business logic related to drawing
    private DrawingView drawingView;  // View that represents the UI for the drawing functionality
    private GameController gameController;  // Controller for the game functionality
    private GraphicsContext bgGc;  // Graphics context for the background canvas
    private GraphicsContext drawingGc;  // Graphics context for the drawing canvas

    // Constructor to initialize the controller with the model, view, and game controller
    public DrawingController(DrawingModel model, DrawingView view, GameController gameController) {
        this.drawingModel = model;
        this.drawingView = view;
        this.gameController = gameController;

        // Getting graphics contexts for both canvases from the drawing view
        bgGc = drawingView.getBackgroundCanvas().getGraphicsContext2D();
        drawingGc = drawingView.getDrawingCanvas().getGraphicsContext2D();

        // Setting the color and line width for the grid
        bgGc.setStroke(Color.GRAY);
        bgGc.setLineWidth(2);

        // Drawing a fine grid on the background canvas
        drawGrid();

        // Setting up event handlers for drawing and erasing
        setupEventHandlers();

        // Setting up the save event handler
        setupSaveEventHandler();
    }

    // Method to draw a grid on the background canvas
    private void drawGrid() {
        int cellSize = 10;
        for (int i = 0; i < drawingView.getBackgroundCanvas().getWidth(); i += cellSize) {
            for (int j = 0; j < drawingView.getBackgroundCanvas().getHeight(); j += cellSize) {
                // Drawing a rectangle only if the current cell is inside a human figure
                if (drawingModel.isInsideHumanFigure(i, j)) {
                    bgGc.strokeRect(i, j, cellSize, cellSize);
                }
            }
        }
    }

    // Method to set up event handlers for drawing and erasing
    private void setupEventHandlers() {
        // Event handler for when the mouse is dragged on the drawing canvas
        drawingView.getDrawingCanvas().setOnMouseDragged(event -> {
            int cellSize = 10;
            int cellX = (int) event.getX() / cellSize;
            int cellY = (int) event.getY() / cellSize;

            double x = cellX * cellSize;
            double y = cellY * cellSize;

            // Check if the current cell is inside a human figure
            if (drawingModel.isInsideHumanFigure(x, y)) {
                // If the draw button is selected, fill the cell with the chosen color
                if (drawingView.getDrawButton().isSelected()) {
                    drawingGc.setFill(drawingView.getColorPicker().getValue());
                    drawingGc.fillRect(x, y, cellSize, cellSize);
                }
                // If the erase button is selected, clear the cell
                else if (drawingView.getEraseButton().isSelected()) {
                    drawingGc.clearRect(x, y, cellSize, cellSize);
                }
            }
        });

        // Event handlers to ensure that only one of the draw or erase buttons is selected at a time
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

    // Method to set up the save event handler
    private void setupSaveEventHandler() {
        // Event handler for when the save button is clicked
        drawingView.getSaveButton().setOnAction(event -> {
            // Capture the current drawing as an image
            WritableImage currentImage = drawingModel.getDrawingImage(drawingView.getDrawingCanvas(), drawingView.getBackgroundCanvas());
            // Save the captured image to the model
            drawingModel.save(currentImage);
            // Set the current character index in the game controller to the index of the last saved character
            gameController.setCurrentCharacterIndex(drawingModel.getAllCharacters().size() - 1);
            // Return to the main menu after saving
            gameController.handleBackFromDrawing();
        });
    }
}
