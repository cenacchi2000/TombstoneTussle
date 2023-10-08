
package com.example.tombstonetussle;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class GameAreaView extends Pane {

    // Constants
    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;

    private GameAreaModel gameAreaModel;

    private ImageView playerImageView;



    public GameAreaView(GameAreaModel model, WritableImage avatar) {

        // Set pane's size
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: lightgray;");  // Set a background color

        // Adding a back arrow (emoticon) to the top left
        javafx.scene.control.Label arrowLabel = new javafx.scene.control.Label("⬅️");
        arrowLabel.setFont(new javafx.scene.text.Font(24));
        arrowLabel.setId("backArrow"); // Setting an ID for easier access later
        getChildren().addAll(arrowLabel);


        // Create and position the player
        this.gameAreaModel = model;
        playerImageView = new ImageView(avatar);
        playerImageView.setFitWidth(TILE_SIZE);
        playerImageView.setFitHeight(TILE_SIZE * (avatar.getHeight() / avatar.getWidth()));
        playerImageView.setTranslateX(model.getX());
        playerImageView.setTranslateY(model.getY());
        getChildren().add(playerImageView);

    }

    public GameAreaModel getGameAreaModel() {
        return gameAreaModel;
    }

    public void updatePlayerPosition() {
        playerImageView.setTranslateX(gameAreaModel.getX());
        playerImageView.setTranslateY(gameAreaModel.getY());
    }
}
