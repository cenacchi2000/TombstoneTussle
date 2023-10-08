
package com.example.tombstonetussle;

import javafx.scene.layout.Pane;

public class GameAreaView extends Pane {

    // Constants
    public static final int TILE_SIZE = 40;
    private static final int W = 800;
    private static final int H = 800;

    private GameAreaModel gameAreaModel;



    public GameAreaView(GameAreaModel model) {

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
        gameAreaModel.setTranslateX(W / 2); // Start in the center
        gameAreaModel.setTranslateY(H / 2);

        getChildren().add(gameAreaModel);

    }

    public GameAreaModel getGameAreaModel() {
        return gameAreaModel;
    }
}
