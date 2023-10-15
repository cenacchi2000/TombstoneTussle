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

    public EnemyModel(int tileSize, Maze1 maze1) {
        this.tileSize = tileSize;
        this.maze1 = maze1;
        this.isFollowingBloodTrace = false;

        // Initialize the enemy's position in a valid cell
        spawnInValidCell();
        bullets = new ArrayList<>();
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
        return true;
    }
}
