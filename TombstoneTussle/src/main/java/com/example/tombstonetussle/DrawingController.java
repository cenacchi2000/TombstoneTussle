package com.example.tombstonetussle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawingController {

    private DrawingModel drawingModel;
    private DrawingView drawingView;
    private GameController gameController;
    private GraphicsContext bgGc; // Graphics context for backgroundCanvas
    private GraphicsContext drawingGc; // Graphics context for drawingCanvas

    public DrawingController(DrawingModel model, DrawingView view, GameController gameController) {
        this.drawingModel = model;
        this.drawingView = view;
        this.gameController = gameController;
        bgGc = drawingView.getBackgroundCanvas().getGraphicsContext2D();
        drawingGc = drawingView.getDrawingCanvas().getGraphicsContext2D();

        bgGc.setStroke(Color.GRAY);
        bgGc.setLineWidth(2);

        // Drawing a fine grid
        drawGrid();

        // Event to handle drawing or erasing
        setupEventHandlers();
        // Setup the save event handler
        setupSaveEventHandler();
    }

    private void drawGrid() {
        int cellSize = 10;
        for (int i = 0; i < drawingView.getBackgroundCanvas().getWidth(); i += cellSize) {
            for (int j = 0; j < drawingView.getBackgroundCanvas().getHeight(); j += cellSize) {
                if (drawingModel.isInsideHumanFigure(drawingView, i, j)) {
                    bgGc.strokeRect(i, j, cellSize, cellSize);
                }
            }
        }
    }

    private void setupEventHandlers() {
        drawingView.getDrawingCanvas().setOnMouseDragged(event -> {
            int cellSize = 10;
            int cellX = (int) event.getX() / cellSize;
            int cellY = (int) event.getY() / cellSize;

            double x = cellX * cellSize;
            double y = cellY * cellSize;

            if (drawingModel.isInsideHumanFigure(drawingView, x, y)) {
                if (drawingView.getDrawButton().isSelected()) {
                    drawingGc.setFill(drawingView.getColorPicker().getValue());
                    drawingGc.fillRect(x, y, cellSize, cellSize);
                } else if (drawingView.getEraseButton().isSelected()) {
                    drawingGc.clearRect(x, y, cellSize, cellSize);
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

    private void setupSaveEventHandler() {
        drawingView.getSaveButton().setOnAction(event -> {
            drawingModel.getDrawingImage(drawingView.getDrawingCanvas(), drawingView.getBackgroundCanvas());
        });
    }


}
