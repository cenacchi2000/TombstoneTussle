package com.example.tombstonetussle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze3 extends Maze {
    private char[][] maze;
    private int startRow;
    private int startCol;

    public Maze3() {
        this.maze = null;
        this.startRow = -1;
        this.startCol = -1;
    }

    public char[][] getMaze() {
        return maze;
    }

    public void setMaze(char[][] maze) {
        this.maze = maze;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public void generateMazeDesign() {
        int rows = 20; // Number of rows in the maze (adjust as needed)
        int cols = 20; // Number of columns in the maze (adjust as needed)

        // Initialize the maze with walls
        maze = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
            }
        }

        // Set the start position
        Random random = new Random();
        startRow = random.nextInt(rows);
        startCol = random.nextInt(cols);
        maze[startRow][startCol] = 'S';

        // Generate the maze using a modified Prim's algorithm
        generateMazePrim(rows, cols, random);

        // Set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';
    }

    private void generateMazePrim(int rows, int cols, Random random) {
        List<int[]> walls = new ArrayList<>();
        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        int initialRow = startRow;
        int initialCol = startCol;

        maze[initialRow][initialCol] = ' ';

        // Add walls around the initial cell
        for (int[] dir : directions) {
            int newRow = initialRow + dir[0];
            int newCol = initialCol + dir[1];
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                walls.add(new int[]{newRow, newCol});
            }
        }

        while (!walls.isEmpty()) {
            int randomIndex = random.nextInt(walls.size());
            int[] wall = walls.get(randomIndex);
            int wallRow = wall[0];
            int wallCol = wall[1];

            // Find the neighboring cells
            int corridorRow = wallRow + (wallRow - initialRow) / 2;
            int corridorCol = wallCol + (wallCol - initialCol) / 2;
            int adjacentRow = wallRow + (wallRow - corridorRow);
            int adjacentCol = wallCol + (wallCol - corridorCol);

            if (corridorRow >= 0 && corridorRow < rows && corridorCol >= 0 && corridorCol < cols &&
                    maze[corridorRow][corridorCol] == ' ') {
                maze[wallRow][wallCol] = ' ';
                maze[adjacentRow][adjacentCol] = ' ';

                // Add new walls
                for (int[] dir : directions) {
                    int newRow = wallRow + dir[0];
                    int newCol = wallCol + dir[1];
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
                            maze[newRow][newCol] == '#') {
                        walls.add(new int[]{newRow, newCol});
                    }
                }
            }

            walls.remove(randomIndex);
        }
    }
}
