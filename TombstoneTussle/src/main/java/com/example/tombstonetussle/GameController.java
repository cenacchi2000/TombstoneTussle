package com.example.tombstonetussle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameController extends Application {
    private int currentCharacterIndex = 0;
    private NPCCharacter npcCharacter;
    private GameState gameState;
    private GameView gameView;


    private int m= 10;
    private int n= 10;
    private DrawingController drawingController;

    // Reference to GameArea's MVC classes
    private GameAreaModel gameAreaModel;
    private GameAreaView gameAreaView;
    private GameAreaController gameAreaController;
    private DrawingModel drawingModel;
    private DrawingView drawingView;
    private MenuAreaController menuAreaController;
    private char[][] maze;
    private int rows;
    private int columns;

    // Declare playerModel field
    private GameAreaModel playerModel;

    public GameController() {
        this.drawingModel = new DrawingModel();
        int startX = 8;
        int startY = 9;
        npcCharacter = new NPCCharacter(startX, startY);

        // Set the reference to this GameController in the GameView constructor
        gameView = new GameView(this);
    }



    @Override
    public void start(Stage primaryStage) {
        gameState = new GameState(); // Initialize the state machine
        gameView = new GameView(this);   // Initialize the game view

        gameView.setupMainMenu(); // Set up the main menu
        setupEventHandlers(); // Set up the event handlers


        // Set up the stage
        Scene scene = new Scene(gameView.getRoot(), 600, 400);
        scene.getStylesheets().addAll(this.getClass().getResource("gameMenu.css").toExternalForm());
        primaryStage.setTitle("Tombstone Tussle");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Start the window maximized
        // Temporarily set
        //primaryStage.setResizable(false); // Prevent the player from resizing the window
        primaryStage.show();

    }

    // Switch to the drawing screen
    public void switchToDrawingScreen() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startDrawing(); // Update the game state to DRAWING
            gameView.updateButtonVisibility(); // Update button visibility based on the new state
            if (drawingView == null) {
                drawingView = new DrawingView(drawingModel, this);
                drawingController = new DrawingController(drawingModel, drawingView, this);
            }
            gameView.getRoot().setCenter(drawingView);
        }
    }

    // Method to handle starting the GameArea
    private void startNewGameArea() {
        if (gameState.getCurrentState() == GameState.State.MENU || gameState.getCurrentState() == GameState.State.PAUSED) {
            gameState.startPlaying(); // Update the game state to PLAYING

            gameAreaModel = new GameAreaModel(GameAreaView.TILE_SIZE, GameAreaView.W, GameAreaView.H);
            WritableImage selectedCharacter = gameView.getCharacterImage();
            gameAreaView = new GameAreaView(gameAreaModel, selectedCharacter, npcCharacter, maze);
            // Pass the maze to GameAreaView
            gameAreaController = new GameAreaController(gameAreaView, gameAreaModel, this);

            AnchorPane menu = null;
            try {
                menu = FXMLLoader.load(getClass().getResource("menuArea.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //gameView.getRoot().setRight(new Button("Test"));
            gameView.getRoot().setRight(menu);
            System.out.println(menu.getClass());
            gameView.getRoot().setCenter(gameAreaView); // change the center of the borderpane to the maze
            gameAreaView.requestFocus(); // Request focus for the GameAreaView
            gameView.updateButtonVisibility(); // Update button visibility based on the new state
        }
    }

    public void gameLoop() {
        // Update player's position and game logic
        // Add your player character logic here, such as handling input and updating its position.
        // Example: playerController.updatePosition();

        // Update NPC's position


        // Repeat the loop
        // Use a game loop mechanism like JavaFX's AnimationTimer to continuously call the gameLoop method.
        // Example:
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameLoop(); // Call the game loop recursively to keep the game running.
            }
        }.start();
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
        if (!drawingModel.getAllCharacters().isEmpty()) {
            WritableImage currentCharacter = drawingModel.getCharacter(currentCharacterIndex);
            gameView.setCharacterImage(currentCharacter);
        } else {
            gameView.setDefaultCharacterImage();  // If no custom characters, set the default one
        }
        drawingView.clearCanvas();  // Clear the drawing canvas when going back
    }

    public void previousCharacter() {
        if (currentCharacterIndex > 0) {
            currentCharacterIndex--;
            updateCharacterView();
        }
    }

    public void nextCharacter() {
        if (currentCharacterIndex < drawingModel.getAllCharacters().size() - 1) {
            currentCharacterIndex++;
            updateCharacterView();
        }
    }

    private void updateCharacterView() {
        WritableImage currentCharacter;
        if (currentCharacterIndex >= 0 && currentCharacterIndex < drawingModel.getAllCharacters().size()) {
            currentCharacter = drawingModel.getCharacter(currentCharacterIndex);
        } else {
            currentCharacter = gameView.getDefaultCharacterImage();  // Get the default character image from GameView
        }
        gameView.setCharacterImage(currentCharacter);
    }

    public void setCurrentCharacterIndex(int index) {
        this.currentCharacterIndex = index;
    }


    public void selectMaze(String mazeName) {
        if (mazeName.equals("maze1")) {
            Maze1 maze1 = new Maze1();
            maze1.generateMazeDesign();
            maze = maze1.getMaze();
        }
        startNewGameArea();
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

    public Node getScene() {
        Node o = null;
        return o;
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
