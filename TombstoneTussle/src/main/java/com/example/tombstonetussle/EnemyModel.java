// Package declaration
package com.example.tombstonetussle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Class that represents the model for an enemy in the game
public class EnemyModel {
    // Instance variables for enemy's properties
    private int x;  // Enemy's horizontal position
    private int y;  // Enemy's vertical position
    private int lastX;  // Enemy's previous horizontal position (not used in this code)
    private int lastY;  // Enemy's previous vertical position (not used in this code)
    private final int tileSize;  // Size of a tile in the maze
    private Maze1 maze1;  // The maze the enemy is in
    private List<Bullet> bullets;  // Bullets shot by the enemy (not used in this code)
    private boolean isFollowingBloodTrace;  // Whether the enemy is following a blood trace (not used in this code)
    private int lives = 1;  // Number of lives the enemy has
    private boolean isZombified = false;  // Whether the enemy has been zombified

    // Constructor for initializing the enemy
    public EnemyModel(int tileSize, Maze1 maze1) {
        this.tileSize = tileSize;
        this.maze1 = maze1;
        this.isFollowingBloodTrace = false;

        // Spawn the enemy at a valid position
        spawnInValidCell();
        bullets = new ArrayList<>();
    }

    // A simple utility class to represent a point in the grid
    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Method to spawn the enemy at a random valid cell in the maze
    private void spawnInValidCell() {
        int mazeWidth = maze1.getMaze()[0].length;
        int mazeHeight = maze1.getMaze().length;

        Random random = new Random();

        // Continue generating random positions until a valid cell is found
        do {
            x = random.nextInt(mazeWidth) * tileSize;
            y = random.nextInt(mazeHeight) * tileSize;
        } while (!isValidMove(x, y));
    }

    // Getter methods for enemy properties
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

    // Method to check if the given position is a valid move for the enemy
    public boolean isValidMove(int newX, int newY) {
        // Check if position is outside the maze bounds
        if (newX < 0 || newY < 0 || newX >= maze1.getMaze()[0].length * tileSize || newY >= maze1.getMaze().length * tileSize) {
            return false;
        }
        char cell = maze1.getMaze()[newY / tileSize][newX / tileSize];
        // Check if the cell is a wall or water
        if (cell == '#' || cell == 'W') {
            return false;
        }
        // Check if the cell is a trap
        if (cell == 'T') {
            setLives(0); // Kill the enemy if it steps on a trap
            return true;
        }
        return true;
    }

    // Getter and setter for the enemy's lives
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) { this.lives = lives; }

    // Method to check if the enemy can shoot at the given player position, based on the given maze
    public boolean canShootAt(GameAreaModel player, char[][] maze) {
        // If the enemy is zombified, it can't shoot
        if (isZombified) {
            return false;
        }
        // Calculate the tiles that the enemy and player are in
        int x1 = this.x / tileSize;
        int y1 = this.y / tileSize;
        int x2 = player.getX() / tileSize;
        int y2 = player.getY() / tileSize;

        // Use Bresenham's line algorithm to determine if the line of sight between enemy and player is blocked by a wall
        List<Point> pointsOnLine = bresenhamLine(x1, y1, x2, y2);
        for (Point point : pointsOnLine) {
            if (maze[point.y][point.x] == '#' || maze[point.y][point.x] == 'W') {
                return false;
            }
        }
        return true;
    }

    // Bresenham's line algorithm to get all the points on a line between two given points
    private List<Point> bresenhamLine(int x1, int y1, int x2, int y2) {
        List<Point> line = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while (true) {
            line.add(new Point(x1, y1));

            if (x1 == x2 && y1 == y2) break;

            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

        return line;
    }

    // Getter and setter for the enemy's zombified status
    public boolean isZombified() {
        return isZombified;
    }

    public void setZombified(boolean isZombified) {
        this.isZombified = isZombified;
    }
}
