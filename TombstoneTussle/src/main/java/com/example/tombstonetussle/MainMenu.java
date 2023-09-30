package com.example.tombstonetussle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {

    private GameState gameState; // The game state machine
    private BorderPane root; // The main layout

    @Override
    public void start(Stage primaryStage) {
        gameState = new GameState(); // Initialize the state machine

        // Main layout
        root = new BorderPane();
        root.setPadding(new Insets(20, 20, 20, 20));

        // Game title
        Label gameTitle = new Label("Tombstone Tussle");
        gameTitle.setStyle("-fx-font-size: 24px;");
        root.setTop(gameTitle);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);

        setupMainMenu(); // Setup the main menu

        // Set up the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Tombstone Tussle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupMainMenu() {
        // Center Box for selections
        VBox centerBox = new VBox(50);
        centerBox.setAlignment(Pos.CENTER);

        // Character and Setting selection
        HBox selectionBox = new HBox(50);
        selectionBox.setAlignment(Pos.CENTER);

        // Character selection
        VBox characterBox = new VBox(10);
        Button leftArrowChar = new Button("<");
        Button rightArrowChar = new Button(">");
        Button editButton = new Button("✎");
        editButton.setOnAction(event -> switchToDrawingScreen()); // Handle the click on the pencil button
        Label characterPlaceholder = new Label("C"); // Placeholder for character
        HBox characterControls = new HBox(10, editButton, leftArrowChar, characterPlaceholder, rightArrowChar);
        characterControls.setAlignment(Pos.CENTER);
        characterBox.getChildren().addAll(new Label("Character"), characterControls);

        // Setting selection
        VBox settingBox = new VBox(10);
        Button leftArrowSetting = new Button("<");
        Button rightArrowSetting = new Button(">");
        Label settingPlaceholder = new Label("M"); // Placeholder for maze selection
        HBox settingControls = new HBox(10, leftArrowSetting, settingPlaceholder, rightArrowSetting);
        settingControls.setAlignment(Pos.CENTER);
        settingBox.getChildren().addAll(new Label("Maze selection"), settingControls);

        selectionBox.getChildren().addAll(characterBox, settingBox);
        centerBox.getChildren().add(selectionBox);
        root.setCenter(centerBox);

        // New Game and Continue buttons
        HBox buttonBox = new HBox(20);
        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction(event -> startNewGame()); // Handle the click on the "New Game" button
        Button continueButton = new Button("Continue");
        buttonBox.getChildren().addAll(newGameButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);
    }

    private void startNewGame() {
        if (gameState.getCurrentState() == GameState.State.MENU) {
            gameState.startPlaying(); // Update the game state to PLAYING
            NewGameArea newGameArea = new NewGameArea();
            root.setCenter(newGameArea); // Set the NewGameArea to the center of the root

            // Add key listener to move the player
            root.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case W:
                        newGameArea.getPlayer().moveUp();
                        break;
                    case S:
                        newGameArea.getPlayer().moveDown();
                        break;
                    case A:
                        newGameArea.getPlayer().moveLeft();
                        break;
                    case D:
                        newGameArea.getPlayer().moveRight();
                        break;
                }
            });
        }
    }


    private void switchToDrawingScreen() {
        if (gameState.getCurrentState() == GameState.State.MENU) {
            gameState.startDrawing(); // Update the game state
            DrawingArea drawingArea = new DrawingArea();
            root.setCenter(drawingArea);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
