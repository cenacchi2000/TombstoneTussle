package com.example.tombstonetussle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameView {

    private BorderPane root; // The main layout
    private Button editButton; // The pencil emoticon button

    private ImageView npcImageView;
    private Button newGameButton; // The button to start a new game
    private Button continueButton; // The button to continue a game
    private GameController gameController; // Reference to the game controller
    private ImageView characterImageView;

    public GameView(GameController controller) {
        this.gameController = controller;

        // Main layout
        root = new BorderPane();
        root.setPadding(new Insets(20, 20, 20, 20));

        // Load NPC image (adjust the path as needed)
        Image npcImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police.png"));

        // Create the NPC ImageView
        npcImageView = new ImageView(npcImage);
        root.getChildren().add(npcImageView); // Add NPC to the scene (initial position may need adjustment)

        // Game title
        Label gameTitle = new Label("Tombstone Tussle");
        gameTitle.setStyle("-fx-font-size: 24px;");
        root.setTop(gameTitle);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);
    }

    public void updateNPCPosition(double x, double y) {
        npcImageView.setX(x);
        npcImageView.setY(y);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getContinueButton(){
        return continueButton;
    }

    public void updateButtonVisibility() {
        newGameButton.setVisible(gameController.isNewGameButtonVisible());
        continueButton.setVisible(gameController.isContinueButtonVisible());
    }

    // Create the main menu layout
    public void setupMainMenu() {
        // Center Box for selections
        VBox centerBox = new VBox(50);
        centerBox.setAlignment(Pos.CENTER);

        // Character and Setting selection
        HBox selectionBox = new HBox(50);
        selectionBox.setAlignment(Pos.CENTER);

        // Character selection
        VBox characterBox = new VBox(10);
        Button leftArrowChar = new Button("<");
        leftArrowChar.setOnAction(event -> gameController.previousCharacter());
        Button rightArrowChar = new Button(">");
        rightArrowChar.setOnAction(event -> gameController.nextCharacter());
        if (editButton == null) {
            editButton = new Button("✎"); // Pencil emoticon button
        }
        characterImageView = new ImageView();
        characterImageView.setFitWidth(100); // Imposta la larghezza desiderata
        characterImageView.setFitHeight(100); // Imposta l'altezza desiderata
        HBox characterControls = new HBox(10, editButton, leftArrowChar, characterImageView, rightArrowChar);
        characterControls.setAlignment(Pos.CENTER);
        characterBox.getChildren().addAll(new Label("Character"), characterControls);

        // Setting selection
        VBox settingBox = new VBox(10);
        Button leftArrowSetting = new Button("<");
        this.setDefaultCharacterImage();
        Button rightArrowSetting = new Button(">");
        Label settingPlaceholder = new Label("M"); // Placeholder for maze selection
        HBox settingControls = new HBox(10, leftArrowSetting, settingPlaceholder, rightArrowSetting);
        settingControls.setAlignment(Pos.CENTER);
        settingBox.getChildren().addAll(new Label("Maze selection"), settingControls);

        // Maze selection logic
        final int[] mazeIndex = {0}; // Index to keep track of the selected maze
        Label[] mazeLabels = { new Label("M1"), new Label("M2"), new Label("M3") }; // Labels for maze selection
        settingPlaceholder.textProperty().bind(mazeLabels[mazeIndex[0]].textProperty()); // Bind the placeholder to the selected maze label

        leftArrowSetting.setOnAction(e -> {
            mazeIndex[0] = (mazeIndex[0] - 1 + mazeLabels.length) % mazeLabels.length; // Decrement the maze index
            settingPlaceholder.textProperty().bind(mazeLabels[mazeIndex[0]].textProperty()); // Update the placeholder
        });

        rightArrowSetting.setOnAction(e -> {
            mazeIndex[0] = (mazeIndex[0] + 1) % mazeLabels.length; // Increment the maze index
            settingPlaceholder.textProperty().bind(mazeLabels[mazeIndex[0]].textProperty()); // Update the placeholder
        });

        selectionBox.getChildren().addAll(characterBox, settingBox);
        centerBox.getChildren().add(selectionBox);
        root.setCenter(centerBox);

        // New Game and Continue buttons
        HBox buttonBox = new HBox(20);
        if (newGameButton == null) {
            newGameButton = new Button("New Game");
        }
        newGameButton.setVisible(gameController.isNewGameButtonVisible());
        if (continueButton == null) {
            continueButton = new Button("Continue");
        }
        continueButton.setVisible(gameController.isContinueButtonVisible());
        buttonBox.getChildren().addAll(newGameButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);
    }

    public void setCharacterImage(WritableImage image) {
        characterImageView.setImage(image);
    }

    public WritableImage getCharacterImage() {
        return (WritableImage) characterImageView.getImage();
    }


    public WritableImage getDefaultCharacterImage() {
        Image defaultImage =  new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/zombieOriginal.png"));
        WritableImage writableDefault = new WritableImage(defaultImage.getPixelReader(), (int) defaultImage.getWidth(), (int) defaultImage.getHeight());
        return writableDefault;
    }
    public void setDefaultCharacterImage() {
        Image defaultCharacter = getDefaultCharacterImage();
        characterImageView.setImage(defaultCharacter);
    }


}
