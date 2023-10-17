package com.example.tombstonetussle;

public class GameAreaModel {
    // Player's current x-coordinate
    private int x;

    // Player's current y-coordinate
    private int y;

    // Size of a tile in the game grid
    private final int tileSize;

    // Instance of the maze
    private Maze1 maze1;

    // Previous x-coordinate of the player
    private double lastX;

    // Previous y-coordinate of the player
    private double lastY;

    // Size of the game grid
    private int size;

    // Number of lives the player has
    private int lives = 5;

    // Time (in seconds) since the game started
    private int elapsedTime = 0;

    // Flag to check if the player is dead
    private boolean isDead = false;

    // Singleton instance of the game state
    GameState gameState = GameState.getInstance();

    // Getter for lastX
    public double getLastX() {
        return lastX;
    }

    // Getter for lastY
    public double getLastY() {
        return lastY;
    }

    // Method to update the lastX and lastY values
    public void updateLastPosition(double x, double y) {
        this.lastX = x;
        this.lastY = y;
    }

    // Constructor for the GameAreaModel
    public GameAreaModel(int tileSize, int W, int H) {
        this.tileSize = tileSize;

        // Initialize the maze and generate its design
        this.maze1 = new Maze1();
        this.maze1.generateMazeDesign();

        // Set player's starting position based on the maze's end point
        int[] endPoint = this.maze1.getEndPointCoordinates();
        if (endPoint != null) {
            this.x = endPoint[1] * tileSize; // Column is the x-coordinate
            this.y = endPoint[0] * tileSize; // Row is the y-coordinate
        }
    }

    // Getter and Setter for x
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    // Getter and Setter for y
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Getter and Setter for size
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    // Method to move player up
    public void moveUp() {
        if (isValidMove(x, y - tileSize)) {
            y -= tileSize;
        }
    }

    // Method to move player down
    public void moveDown() {
        if (isValidMove(x, y + tileSize)) {
            y += tileSize;
        }
    }

    // Method to move player left
    public void moveLeft() {
        if (isValidMove(x - tileSize, y)) {
            x -= tileSize;
        }
    }

    // Method to move player right
    public void moveRight() {
        if (isValidMove(x + tileSize, y)) {
            x += tileSize;
        }
    }

    // Checks if a move to a new position is valid
    private boolean isValidMove(int newX, int newY) {
        // Check if the new position is outside the maze boundaries or if the player is dead
        if (newX < 0 || newY < 0 || newX >= maze1.getMaze()[0].length * tileSize || newY >= maze1.getMaze().length * tileSize || isDead) {
            return false;
        }

        // Check if the new position is a wall
        char cell = maze1.getMaze()[newY / tileSize][newX / tileSize];
        if (cell == '#' || cell == 'W') {
            return false;
        }

        // Check if the cell is a trap
        if (cell == 'T') {
            setLives(0); // Reduce the player's life to 0
            return true;
        }

        // If none of the above conditions are met, the move is valid
        return true;
    }

    // Getter and setter for lives
    public int getLives() {
        return lives;
    }

    // Set the number of lives and check if game is over
    public void setLives(int lives) {
        this.lives = lives;
        if (this.lives <= 0) {
            gameState.gameOver(); // End the game if lives are 0 or less
        }
    }

    // Disable the player (mark as dead)
    public void disablePlayer() {
        isDead = true;
    }

    // Getter for elapsed time
    public int getElapsedTime() {
        return elapsedTime;
    }

    // Increment the elapsed time by one second
    public void incrementElapsedTime() {
        this.elapsedTime++;
    }

    // Getter for the maze instance
    public Maze1 getMaze1() {
        return maze1;
    }
}
