package com.example.tombstonetussle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameController extends Application {

    private GameState gameState; // The game state machine
    private GameView gameView;   // The game view
    private DrawingController drawingController; // The drawing controller

    // References to GameArea's MVC classes
    private GameAreaModel gameAreaModel;
    private GameAreaView gameAreaView;
    private GameAreaController gameAreaController;

    private char[][] maze;
    private int rows;
    private int columns;


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
            gameAreaView = new GameAreaView(gameAreaModel, maze); // Pass the maze to GameAreaView
            gameAreaController = new GameAreaController(gameAreaView, gameAreaModel, this);

            gameView.getRoot().setCenter(gameAreaView);
            gameAreaView.requestFocus(); // Request focus for the GameAreaView
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

    public void selectMaze(String mazeName) {
        if (mazeName.equals("maze1")) {
            Maze1 maze1 = new Maze1();
            maze1.generateMazeDesign(); // Generate the maze design for M1
            maze = maze1.getMaze(); // Get the generated maze
        } else if (mazeName.equals("maze2")) {
            // Generate maze2
        } else if (mazeName.equals("maze3")) {
            // Generate maze3
        }
        // Add more conditions for other maze options if needed

        // After setting the maze, start the new game area
        startNewGameArea();
    }



    public char[][] generateMaze() {
        // Initialize the maze with walls
        initializeMaze();

        // Start the maze generation from the top-left corner
        generate(0, 0);

        return maze;
    }

    public void MazeGenerator(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.maze = new char[rows][columns];
    }



    private void initializeMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                maze[i][j] = '#'; // Wall
            }
        }
    }

    private void generate(int row, int column) {
        maze[row][column] = '.'; // Passage

        // Get a random order of directions
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);

        for (Direction direction : directions) {
            int newRow = row + direction.getRowOffset();
            int newColumn = column + direction.getColumnOffset();

            if (isValid(newRow, newColumn) && maze[newRow][newColumn] == '#') {
                int wallRow = row + direction.getRowOffset() / 2;
                int wallColumn = column + direction.getColumnOffset() / 2;
                maze[wallRow][wallColumn] = '.'; // Passage

                generate(newRow, newColumn);
            }
        }
    }

    private boolean isValid(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

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


    public static void main(String[] args) {
        launch(args);
    }
}
