
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
