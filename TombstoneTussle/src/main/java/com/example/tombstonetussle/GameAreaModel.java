
package com.example.tombstonetussle;

public class GameAreaModel {
    private int x;
    private int y;
    private final int tileSize;

    public GameAreaModel(int tileSize, int W, int H) {
        this.tileSize = tileSize;
        //Setup initial positioning
        this.x = W / 2 - tileSize / 2;
        this.y = H / 2 - tileSize / 2;
    }

    // Getter and Setter for x and y
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveUp() {
        y -= tileSize;
    }

    public void moveDown() {
        y += tileSize;
    }

    public void moveLeft() {
        x -= tileSize;
    }

    public void moveRight() {
        x += tileSize;
    }
}

