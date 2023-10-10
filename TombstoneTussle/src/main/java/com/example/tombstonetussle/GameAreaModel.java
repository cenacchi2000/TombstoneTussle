package com.example.tombstonetussle;

public class GameAreaModel {
    private int x;
    private int y;
    private final int tileSize;
    private Maze1 maze1;

    private double lastX;
    private double lastY;

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    // Method to update the lastX and lastY values
    public void updateLastPosition(double x, double y) {
        this.lastX = x;
        this.lastY = y;
    }

    public GameAreaModel(int tileSize, int W, int H) {
        this.tileSize = tileSize;
        this.maze1 = new Maze1();
        this.maze1.generateMazeDesign();

        int[] endPoint = this.maze1.getEndPointCoordinates();
        if (endPoint != null) {
            this.x = endPoint[1] * tileSize; // Remember that the column is the x-coordinate
            this.y = endPoint[0] * tileSize; // And the row is the y-coordinate
        }
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
        if (isValidMove(x, y - tileSize)) {
            y -= tileSize;
        }
    }

    public void moveDown() {
        if (isValidMove(x, y + tileSize)) {
            y += tileSize;
        }
    }

    public void moveLeft() {
        if (isValidMove(x - tileSize, y)) {
            x -= tileSize;
        }
    }

    public void moveRight() {
        if (isValidMove(x + tileSize, y)) {
            x += tileSize;
        }
    }

    private boolean isValidMove(int newX, int newY) {
        // Check if the new position is outside the maze boundaries
        if (newX < 0 || newY < 0 || newX >= maze1.getMaze()[0].length * tileSize || newY >= maze1.getMaze().length * tileSize) {
            return false;
        }

        // Check if the new position is a wall
        if (maze1.getMaze()[newY / tileSize][newX / tileSize] == '#') {
            return false;
        }

        return true;
    }

    public Maze1 getMaze1() {
        return maze1;
    }
}
