package com.example.tombstonetussle;

public class EnemyModel {
    private int x;
    private int y;
    private final int tileSize;
    private Maze1 maze1;

    public EnemyModel(int tileSize, Maze1 maze1) {
        this.tileSize = tileSize;
        this.maze1 = maze1;

        // Posizione iniziale del nemico. Puoi cambiare queste coordinate come preferisci.
        this.x = 0;
        this.y = 0;
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
