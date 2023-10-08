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
        primaryStage.setMaximized(true); // Start the window maximized
        primaryStage.setResizable(false); // Prevent the player from resizing the window
        primaryStage.show();
    }

    // Switch to the drawing screen
    public void switchToDrawingScreen() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startDrawing(); // Update the game state to DRAWING
            gameView.updateButtonVisibility(); // Update button visibility based on the new state
            DrawingModel drawingModel = new DrawingModel();
            DrawingView drawingView = new DrawingView(this);
            drawingController = new DrawingController(drawingModel, drawingView, this);
            gameView.getRoot().setCenter(drawingView);
        }
    }

    // Method to handle starting the GameArea
    private void startNewGameArea() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startPlaying(); // Update the game state to PLAYING

            gameAreaModel = new GameAreaModel(GameAreaView.TILE_SIZE);
            gameAreaView = new GameAreaView(gameAreaModel);
            gameAreaController = new GameAreaController(gameAreaView, gameAreaModel, this);

            gameView.getRoot().setCenter(gameAreaView);
            gameAreaView.requestFocus();  // Request focus for the GameAreaView
            gameView.updateButtonVisibility(); // Update button visibility based on the new state
        }
    }


    private void setupEventHandlers() {
        gameView.getEditButton().setOnAction(event -> switchToDrawingScreen());
        gameView.getNewGameButton().setOnAction(event -> startNewGameArea());
    }

    public boolean isNewGameButtonVisible() {
        return gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED;
    }


    public boolean isContinueButtonVisible() {
        return gameState.getCurrentState() == GameState.State.PAUSED;
    }

    // Method to handle back action from GameArea and return to main menu
    public void handleBackToMainMenu() {
        gameState.pauseGame();
        gameView.setupMainMenu();
    }

    public void handleBackFromDrawing(){
        gameState.goToPreviousState();
        gameView.setupMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
