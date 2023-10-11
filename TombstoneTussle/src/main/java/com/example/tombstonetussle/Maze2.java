package com.example.tombstonetussle;

import java.util.Random;

public class Maze2 extends Maze {
    private char[][] maze;
    private int rows;
    private int cols;
    private Random random;
    private boolean[][] bloodTrace;

    public Maze2(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];
        this.random = new Random();
        initializeMaze();
    }

    private void initializeMaze() {
        bloodTrace = new boolean[rows][cols];
        // Fill the entire maze with open passages
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = ' ';
            }
        }

        // Randomly place walls
        int numWalls = (int) (0.7 * rows * cols); // Adjust the density of walls as needed
        for (int i = 0; i < numWalls; i++) {
            int wallRow = random.nextInt(rows);
            int wallCol = random.nextInt(cols);

            // Ensure that the wall is placed in an empty cell
            if (maze[wallRow][wallCol] == ' ') {
                maze[wallRow][wallCol] = '#'; // Place a wall
            } else {
                // If the cell is not empty, try again
                i--;
            }
        }

        // Ensure the start and end positions are open
        maze[0][1] = 'S'; // Start position
        maze[rows - 1][cols - 2] = 'E'; // End position
    }

    public int[] getEndPointCoordinates() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 'E') {
                    return new int[]{i, j};
                }
            }
        }
        return null; // If 'E' is not found, though it should never happen
    }

    public char[][] getMaze() {
        return maze;
    }

    public boolean[][] getBloodTrace() {
        return bloodTrace;
    }

}

