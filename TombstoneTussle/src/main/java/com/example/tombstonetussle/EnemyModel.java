package com.example.tombstonetussle;

public class EnemyModel {
    private int x;
    private int y;
    private int lastX;
    private int lastY;
    private final int tileSize;
    private Maze1 maze1;
    private boolean isFollowingBloodTrace;

    public EnemyModel(int tileSize, Maze1 maze1) {
        this.tileSize = tileSize;
        this.maze1 = maze1;
        this.x = 4;
        this.y = 4;
        this.lastX = 0;
        this.lastY = 0;
        this.isFollowingBloodTrace = false;
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
        if (maze1.getMaze()[newY / tileSize][newX / tileSize] == '#') {
            return false;
        }
        return true;
    }
}
