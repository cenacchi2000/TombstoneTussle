package com.example.tombstonetussle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyModel {
    private int x;
    private int y;
    private int lastX;
    private int lastY;
    private final int tileSize;
    private Maze1 maze1;
    private List<Bullet> bullets;
    private boolean isFollowingBloodTrace;
    private int lives = 1;


    public EnemyModel(int tileSize, Maze1 maze1) {
        this.tileSize = tileSize;
        this.maze1 = maze1;
        this.isFollowingBloodTrace = false;

        // Initialize the enemy's position in a valid cell
        spawnInValidCell();
        bullets = new ArrayList<>();
    }

    // A simple utility class for a point in the grid
    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

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

    public boolean isValidMove(int newX, int newY) {
        if (newX < 0 || newY < 0 || newX >= maze1.getMaze()[0].length * tileSize || newY >= maze1.getMaze().length * tileSize) {
            return false;
        }
        char cell = maze1.getMaze()[newY / tileSize][newX / tileSize];
        if (cell == '#' || cell == 'W') {
            return false;
        }
        // Check if the cell is a trap
        if (cell == 'T') {
            setLives(0); // Reduce the enemy's life to 0
            return true;
        }
        return true;
    }

    // Getter and setter for lives
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) { this.lives = lives; }
    public boolean canShootAt(GameAreaModel player, char[][] maze) {
        int x1 = this.x / tileSize;
        int y1 = this.y / tileSize;
        int x2 = player.getX() / tileSize;
        int y2 = player.getY() / tileSize;

        // Use Bresenham's line algorithm to determine the cells touched by the line
        List<Point> pointsOnLine = bresenhamLine(x1, y1, x2, y2);

        // Check if there's a wall on any of the cells touched by the line
        for (Point point : pointsOnLine) {
            if (maze[point.y][point.x] == '#' || maze[point.y][point.x] == 'W') {
                return false;
            }
        }
        return true;
    }

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
}

