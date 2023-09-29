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

    //IMPORTANT: I HAVE INITIALIZED THE STATE MACHINE BUT IT IS NOT LINKED, PLEASE DESIGN THE GAME LOGIC ACCORDINGLY
    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 20, 20, 20));

        // Game title
        Label gameTitle = new Label("Tombstone Tussle");
        gameTitle.setStyle("-fx-font-size: 24px;");
        root.setTop(gameTitle);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);

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
        Button continueButton = new Button("Continue");
        buttonBox.getChildren().addAll(newGameButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);

        // Set up the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Tombstone Tussle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
