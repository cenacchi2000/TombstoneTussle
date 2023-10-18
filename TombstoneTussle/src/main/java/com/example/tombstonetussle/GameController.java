package com.example.tombstonetussle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.*;

// Class that controls the game logic and interactions
public class GameController extends Application {
    // Index of the currently selected character for drawing
    private int currentCharacterIndex = 0;
    // Singleton instance for tracking the game's state
    GameState gameState = GameState.getInstance();
    // View for the game
    private GameView gameView;
    private int m= 10;
    private int n= 10;
    // Controller for drawing characters
    private DrawingController drawingController;

    // References to GameArea's MVC classes
    private GameAreaModel gameAreaModel;
    private GameAreaView gameAreaView;
    private GameAreaController gameAreaController;
    // MVC references for drawing functionality
    private DrawingModel drawingModel;
    private DrawingView drawingView;
    // 2D char array to represent the maze
    private char[][] maze;
    // Number of rows and columns for the maze
    private int rows;
    private int columns;

    // Constructor for GameController
    public GameController() {
        this.drawingModel = new DrawingModel();
        int startX = 8;
        int startY = 9;

        // Initialize the game view with this controller as a reference
        gameView = new GameView(this);
    }

    // Start method called when the application is launched
    @Override
    public void start(Stage primaryStage) {
        // Initialize the game view with this controller as a reference
        gameView = new GameView(this);

        // Set up the main menu and event handlers
        gameView.setupMainMenu();
        setupEventHandlers();

        // Configure and display the primary stage
        Scene scene = new Scene(gameView.getRoot(), 1500, 900);
        scene.getStylesheets().addAll(this.getClass().getResource("gameMenu.css").toExternalForm());
        primaryStage.setTitle("Tombstone Tussle");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Method to switch to the drawing screen
    public void switchToDrawingScreen() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startDrawing();
            gameView.updateButtonVisibility();
            if (drawingView == null) {
                drawingView = new DrawingView(drawingModel, this);
                drawingController = new DrawingController(drawingModel, drawingView, this);
            }
            gameView.getRoot().setCenter(drawingView);
        }
    }

    // Method to initialize a new GameArea
    private void startNewGameArea() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startPlaying();

            gameAreaModel = new GameAreaModel(GameAreaView.TILE_SIZE, GameAreaView.W, GameAreaView.H);
            WritableImage selectedCharacter = gameView.getCharacterImage();

            List<EnemyModel> enemyModels = new ArrayList<>();
            for(int i = 0; i < 4; i++) {
                enemyModels.add(new EnemyModel(GameAreaView.TILE_SIZE, gameAreaModel.getMaze1()));
            }
            gameAreaView = new GameAreaView(gameAreaModel, selectedCharacter, maze, enemyModels);
            gameAreaController = new GameAreaController(gameAreaView, gameAreaModel, this);

            gameView.getRoot().getRight().setVisible(true);

            gameView.getRoot().setCenter(gameAreaView);
            gameAreaView.requestFocus();
            gameView.updateButtonVisibility();
        }
    }

    // Set up event handlers for buttons
    private void setupEventHandlers() {
        gameView.getEditButton().setOnAction(event -> switchToDrawingScreen());
        gameView.getNewGameButton().setOnAction(event -> startNewGameArea());
    }

    // Check if the new game button should be visible
    public boolean isNewGameButtonVisible() {
        return gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED;
    }


    // Method to handle going back to the main menu
    public void handleBackToMainMenu() {
        gameState.pauseGame();
        gameView.setupMainMenu();
        gameView.getMenu().setVisible(false);
        gameView.resetMenu();
    }

    // Handle back action from Drawing and return to main menu
    public void handleBackFromDrawing(){
        gameState.goToMenu();
        gameView.setupMainMenu();
        if (!drawingModel.getAllCharacters().isEmpty()) {
            WritableImage currentCharacter = drawingModel.getCharacter(currentCharacterIndex);
            gameView.setCharacterImage(currentCharacter);
        } else {
            gameView.setDefaultCharacterImage();
        }
        drawingView.clearCanvas();
    }

    // Switch to the previous character in the drawing model
    public void previousCharacter() {
        if (currentCharacterIndex > 0) {
            currentCharacterIndex--;
            updateCharacterView();
        }
    }

    // Switch to the next character in the drawing model
    public void nextCharacter() {
        if (currentCharacterIndex < drawingModel.getAllCharacters().size() - 1) {
            currentCharacterIndex++;
            updateCharacterView();
        }
    }

    // Update the character displayed in the view
    private void updateCharacterView() {
        WritableImage currentCharacter;
        if (currentCharacterIndex >= 0 && currentCharacterIndex < drawingModel.getAllCharacters().size()) {
            currentCharacter = drawingModel.getCharacter(currentCharacterIndex);
        } else {
            currentCharacter = gameView.getDefaultCharacterImage();
        }
        gameView.setCharacterImage(currentCharacter);
    }

    // Set the current character index
    public void setCurrentCharacterIndex(int index) {
        this.currentCharacterIndex = index;
    }

    // Recursive method to generate the maze
    private void generate(int row, int column) {
        maze[row][column] = '.';

        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);

        for (Direction direction : directions) {
            int newRow = row + direction.getRowOffset();
            int newColumn = column + direction.getColumnOffset();

            if (isValid(newRow, newColumn) && maze[newRow][newColumn] == '#') {
                int wallRow = row + direction.getRowOffset() / 2;
                int wallColumn = column + direction.getColumnOffset() / 2;
                maze[wallRow][wallColumn] = '.';

                generate(newRow, newColumn);
            }
        }
    }

    // Check if a given row and column is valid for maze generation
    private boolean isValid(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    // Get the scene for the game (currently returns null)
    public Node getScene() {
        Node o = null;
        return o;
    }

    // Enum for possible directions in maze generation
    private enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        private final int rowOffset;
        private final int columnOffset;

        Direction(int rowOffset, int columnOffset) {
            this.rowOffset = rowOffset;
            this.columnOffset = columnOffset;
        }

        public int getRowOffset() {
            return rowOffset;
        }

        public int getColumnOffset() {
            return columnOffset;
        }
    }

    // Main method to launch the game application
    public static void main(String[] args) {
        launch(args);
    }
}
