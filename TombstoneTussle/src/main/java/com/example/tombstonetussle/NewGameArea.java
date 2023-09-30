package com.example.tombstonetussle;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class NewGameArea extends Pane {

    // Constants
    public static final int TILE_SIZE = 40;
    private static final int W = 800;
    private static final int H = 800;

    private Player player;

    public NewGameArea() {
        // Set pane's size
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: lightgray;");  // Set a background color

        // Create and position the player
        player = new Player(TILE_SIZE);
        player.setTranslateX(W / 2); // Start in the center
        player.setTranslateY(H / 2);

        getChildren().add(player);
    }

    public Player getPlayer() {
        return player;
    }
}

class Player extends Rectangle {

    private final int tileSize; // Store the tileSize

    public Player(int tileSize) {
        super(tileSize, tileSize);
        this.tileSize = tileSize; // Initialize the tileSize
        setFill(javafx.scene.paint.Color.BLUE); // Give it a color for now
    }

    public void moveUp() {
        setTranslateY(getTranslateY() - tileSize);
    }

    public void moveDown() {
        setTranslateY(getTranslateY() + tileSize);
    }

    public void moveLeft() {
        setTranslateX(getTranslateX() - tileSize);
    }

    public void moveRight() {
        setTranslateX(getTranslateX() + tileSize);
    }
}
