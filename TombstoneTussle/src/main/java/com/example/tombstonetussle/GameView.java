package com.example.tombstonetussle;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
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

// Class representing the game view
public class GameView {

    // Main layout container for the game view
    private BorderPane root;
    // Button for editing character design
    private Button editButton;
    // Image view to display NPCs
    private ImageView npcImageView;
    // Button to start a new game
    private Button newGameButton;
    // Reference to the game controller
    private GameController gameController;
    // Controller for the menu area
    private MenuAreaController menuAreaController;
    // Image view to display the player's character
    private ImageView characterImageView;
    // Game title displayed at the top
    private Title gameTitle = new Title("TOMBSTONE TUSSLE");
    // Menu container
    private AnchorPane menu;

    // Constructor to initialize the game view
    public GameView(GameController controller){
        this.gameController = controller;

        // Initialize the main layout
        root = new BorderPane();
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setId("root");

        // Load and position the NPC image (path might need adjustment)
        // Commented out for now
        // Image npcImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police.png"));
        // npcImageView = new ImageView(npcImage);
        // root.getChildren().add(npcImageView);

        // Set the game title to the top of the layout
        root.setTop(gameTitle);

        // Load the menu area from an external FXML file
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuArea.fxml"));
            this.menu = fxmlLoader.load();
            this.menuAreaController = fxmlLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        root.setRight(menu);
        root.getRight().setVisible(false);
        BorderPane.setAlignment(gameTitle, Pos.CENTER);
    }

    // Inner class to style and display the game title
    private static class Title extends StackPane{
        public Title(String name) {
            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 60));

            // Set text effects
            Blend blend = new Blend();
            blend.setMode(BlendMode.MULTIPLY);

            DropShadow ds = new DropShadow();
            ds.setColor(Color.rgb(254, 235, 66, 0.3));
            ds.setOffsetX(5);
            ds.setOffsetY(5);
            ds.setRadius(5);
            ds.setSpread(0.2);

            blend.setBottomInput(ds);

            DropShadow ds1 = new DropShadow();
            ds1.setColor(Color.web("#f13a00"));
            ds1.setRadius(20);
            ds1.setSpread(0.2);

            Blend blend2 = new Blend();
            blend2.setMode(BlendMode.MULTIPLY);

            InnerShadow is = new InnerShadow();
            is.setColor(Color.web("#feeb42"));
            is.setRadius(9);
            is.setChoke(0.8);
            blend2.setBottomInput(is);

            InnerShadow is1 = new InnerShadow();
            is1.setColor(Color.web("#f13a00"));
            is1.setRadius(5);
            is1.setChoke(0.4);
            blend2.setTopInput(is1);

            Blend blend1 = new Blend();
            blend1.setMode(BlendMode.MULTIPLY);
            blend1.setBottomInput(ds1);
            blend1.setTopInput(blend2);

            blend.setTopInput(blend1);

            text.setEffect(blend);

            setAlignment(Pos.CENTER);
            getChildren().addAll(text);
        }
    }

    // Update NPC's position on the screen
    public void updateNPCPosition(double x, double y) {
        npcImageView.setX(x);
        npcImageView.setY(y);
    }

    // Get the main layout
    public BorderPane getRoot() {
        return root;
    }

    // Getters for various UI components
    public Button getEditButton() {
        return editButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    // Update the visibility of the "New Game" button based on game state
    public void updateButtonVisibility() {
        newGameButton.setVisible(gameController.isNewGameButtonVisible());
    }

    // Create the main menu layout
    public void setupMainMenu() {
        // Center Box for selections
        VBox centerBox = new VBox(100);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setTranslateX(100);

        // Character and Setting selection
        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER);

        // Character selection
        VBox characterBox = new VBox(100);

        Button leftArrowChar = new Button("<");
        leftArrowChar.setStyle("-fx-background-color:rgba(246,115,34,0.65),linear-gradient(#efb686, #cb6003); -fx-text-fill: white;-fx-font-size: 20");
        leftArrowChar.setOnAction(event -> gameController.previousCharacter());

        Button rightArrowChar = new Button(">");
        rightArrowChar.setStyle("-fx-background-color:rgba(246,115,34,0.65),linear-gradient(#efb686, #cb6003); -fx-text-fill: white;-fx-font-size: 20;");
        rightArrowChar.setOnAction(event -> gameController.nextCharacter());

        if (editButton == null) {
            editButton = new Button("✎"); // Pencil emoticon button
            editButton.setStyle("-fx-background-color:rgba(246,115,34,0.65),linear-gradient(#efb686, #cb6003); -fx-text-fill: white;-fx-font-size: 20");
        }
        characterImageView = new ImageView();
        characterImageView.setFitWidth(200); // Imposta la larghezza desiderata
        characterImageView.setFitHeight(200); // Imposta l'altezza desiderata
        HBox characterControls = new HBox(10, editButton, leftArrowChar, characterImageView, rightArrowChar);
        characterControls.setAlignment(Pos.CENTER);
        Label character = new Label("Create your own character:");
        character.setFont(Font.font("Impact",30));
        DropShadow shadow = new DropShadow();
        character.setEffect(shadow);
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
        HBox buttonBox = new HBox(40);
        if (newGameButton == null) {
            newGameButton = new Button("New Game");
        }
//        newGameButton.setStyle("-fx-background-color: \n" +
//                "        #b9d1ff,\n" +
//                "        linear-gradient(#dde9f1 0%, #c8d4e1 20%, #396ba6 100%),\n" +
//                "        linear-gradient(#a4d6ff, #63a4ff),\n" +
//                "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\n" +
//                "    -fx-background-radius: 5,4,3,5;\n" +
//                "    -fx-background-insets: 0,1,2,0;\n" +
//                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n" +
//                "    -fx-text-fill: linear-gradient(white, #faf9f9);\n" +
//                "    -fx-padding: 10 20 10 20;");
        newGameButton.setStyle("-fx-background-color:rgba(246,115,34,0.65),linear-gradient(#efb686, #cb6003); -fx-text-fill: white;");
        newGameButton.setFont(Font.font("Impact",30));
        newGameButton.setTranslateY(-100);
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
    // Get the menu container
    public AnchorPane getMenu(){
        return menu;
    }

    // Reset the menu, e.g., after a game is over
    public void resetMenu(){
        menuAreaController.resetPowerup();
    }
}
