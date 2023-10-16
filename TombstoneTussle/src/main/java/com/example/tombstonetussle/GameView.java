package com.example.tombstonetussle;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.*;
import javafx.scene.paint.Color;

import java.io.IOException;


public class GameView {

    private BorderPane root; // The main layout
    private Button editButton; // The pencil emoticon button

    private ImageView npcImageView;
    private Button newGameButton; // The button to start a new game
    private GameController gameController; // Reference to the game controller
    private ImageView characterImageView;
    private Title gameTitle = new Title("TOMBSTONE TUSSLE");
    private AnchorPane menu;

    public GameView(GameController controller){
        this.gameController = controller;

        // Main layout
        root = new BorderPane();
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setId("root");

        // Load NPC image (adjust the path as needed)
       // Image npcImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police.png"));

        // Create the NPC ImageView
        //npcImageView = new ImageView(npcImage);
        //root.getChildren().add(npcImageView); // Add NPC to the scene (initial position may need adjustment)

        // Game title
        //Label gameTitle = new Label("Tombstone Tussle");


        //gameTitle.setStyle("-fx-font-size: 24px;");
        root.setTop(gameTitle);

        try {
            this.menu = FXMLLoader.load(getClass().getResource("menuArea.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        root.setRight(menu);
        root.getRight().setVisible(false);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);
    }

    // Style for Game title
    private static class Title extends StackPane{
        public Title(String name) {
//            Rectangle bg = new Rectangle(425, 80);
//            bg.setStroke(Color.WHITE);
//            bg.setStrokeWidth(2);
//            bg.setFill(null);

            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.CENTER);
            getChildren().addAll(text);
        }
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


    public void updateButtonVisibility() {
        newGameButton.setVisible(gameController.isNewGameButtonVisible());
    }
//    public void updateTitleVisibility(){
//        gameTitle.setVisible(false);
//    }

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
        Label character = new Label("Character");
        character.setTextFill(Color.WHITE);
        characterBox.getChildren().addAll(character, characterControls);

        // Setting selection
        VBox settingBox = new VBox(10);

        this.setDefaultCharacterImage();

        HBox settingControls = new HBox(10);
        settingControls.setAlignment(Pos.CENTER);

        settingBox.getChildren().addAll(settingControls);



        selectionBox.getChildren().addAll(characterBox, settingBox);
        centerBox.getChildren().add(selectionBox);
        root.setCenter(centerBox);

        // New Game button
        HBox buttonBox = new HBox(20);
        if (newGameButton == null) {
            newGameButton = new Button("New Game");
        }
        newGameButton.setVisible(gameController.isNewGameButtonVisible());
        buttonBox.getChildren().addAll(newGameButton);
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
    public AnchorPane getMenu(){
        return menu;
    }


}
