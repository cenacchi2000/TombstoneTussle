
package com.example.tombstonetussle;

import javafx.scene.shape.Rectangle;

public class GameAreaModel extends Rectangle {

    private final int tileSize; // Store the tileSize

    public GameAreaModel(int tileSize) {
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
