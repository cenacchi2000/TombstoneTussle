package com.example.tombstonetussle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameController extends Application {

    private GameState gameState; // The game state machine
    private GameView gameView;   // The game view
    private DrawingController drawingController; // The drawing controller

    // References to GameArea's MVC classes
    private GameAreaModel gameAreaModel;
    private GameAreaView gameAreaView;
    private GameAreaController gameAreaController;

    @Override
    public void start(Stage primaryStage) {
        gameState = new GameState(); // Initialize the state machine
        gameView = new GameView(this);   // Initialize the game view

        gameView.setupMainMenu(); // Set up the main menu
        setupEventHandlers(); // Set up the event handlers

        // Set up the stage
        Scene scene = new Scene(gameView.getRoot(), 600, 400);
        primaryStage.setTitle("Tombstone Tussle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Switch to the drawing screen
    public void switchToDrawingScreen() {
        if (gameState.getCurrentState() == GameState.State.MENU) {
            gameState.startDrawing(); // Update the game state to DRAWING
            DrawingModel drawingModel = new DrawingModel();
            DrawingView drawingView = new DrawingView();
            drawingController = new DrawingController(drawingModel, drawingView);
            gameView.getRoot().setCenter(drawingView);
        }
    }

    // Method to handle starting the GameArea
    private void startNewGameArea() {
        if (gameState.getCurrentState() == GameState.State.MENU) {
            gameState.startPlaying(); // Update the game state to PLAYING

            gameAreaModel = new GameAreaModel(GameAreaView.TILE_SIZE);
            gameAreaView = new GameAreaView(gameAreaModel);
            gameAreaController = new GameAreaController(gameAreaView, gameAreaModel);

            gameView.getRoot().setCenter(gameAreaView);
            gameAreaView.requestFocus();  // Request focus for the GameAreaView
        }
    }


    private void setupEventHandlers() {
        gameView.getEditButton().setOnAction(event -> switchToDrawingScreen());
        gameView.getNewGameButton().setOnAction(event -> startNewGameArea());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
