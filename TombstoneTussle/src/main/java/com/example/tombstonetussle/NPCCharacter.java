package com.example.tombstonetussle;


import javafx.scene.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NPCCharacter {
    private int x;
    private int y;
    private char[][] maze;
    private List<Node> path;

    public NPCCharacter(char[][] maze) {
        this.maze = maze;
        // Find the center of the maze
        int centerX = maze.length / 2;
        int centerY = maze[0].length / 2;
        this.x = centerX;
        this.y = centerY;
    }

    public NPCCharacter(int startX, int startY) {
    }

    // Helper method to get a list of available movement directions
    private List<int[]> getAvailableDirections() {
        List<int[]> directions = new ArrayList<>();
        int[][] possibleDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        for (int[] dir : possibleDirections) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < maze.length && newY >= 0 && newY < maze[0].length && maze[newX][newY] != '#') {
                directions.add(dir);
            }
        }
        return directions;
    }

    // Method to make the NPC character move randomly
    public void moveRandomly() {
        List<int[]> availableDirections = getAvailableDirections();
        if (!availableDirections.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableDirections.size());
            int[] randomDirection = availableDirections.get(randomIndex);
            x += randomDirection[0];
            y += randomDirection[1];
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void updatePosition(GameAreaModel playerModel, char[][] maze) {
        int playerX = playerModel.getX();
        int playerY = playerModel.getY();
        int npcX = getX();
        int npcY = getY();

        int distanceToPlayer = Math.abs(playerX - npcX) + Math.abs(playerY - npcY);

        if (distanceToPlayer <= 5) {
            // If the player is within a certain range (e.g., 5), follow the player
            List<Node> newPath = calculatePathToPlayer(playerModel, maze);
            setPath(newPath);
        } else {
            // If the player is far away, move randomly
            moveRandomly();
        }
    }

    private List<Node> calculatePathToPlayer(GameAreaModel playerModel, char[][] maze) {
        List<Node> o = null;
        List<Node> o1 = o;
        return o1;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }

    public List<Node> getPath() {
        return path;
    }
}


