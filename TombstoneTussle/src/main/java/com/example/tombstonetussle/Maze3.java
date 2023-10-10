package com.example.tombstonetussle;
import java.util.Random;

import java.util.Random;

public class Maze3 extends Maze {
    private char[][] maze;
    private int rows;
    private int cols;

    public Maze3(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];
        initializeMaze();
    }



    public char[][] getMaze() {
        return maze;
    }

    private void initializeMaze() {
        Random random = new Random();

        // Initialize the maze with walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
            }
        }

        // Start from a random position
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);

        // Initialize the current position
        int currentRow = startRow;
        int currentCol = startCol;

        // Set the start and end positions
        maze[startRow][startCol] = 'S';

        // Perform a random walk to create paths
        int maxSteps = rows * cols / 5; // Adjust the number of steps as needed
        int steps = 0;

        while (steps < maxSteps) {
            // Randomly choose a direction (up, down, left, right)
            int direction = random.nextInt(4);

            // Move in the chosen direction
            switch (direction) {
                case 0: // Up
                    if (currentRow > 1) {
                        currentRow -= 2;
                        maze[currentRow][currentCol] = ' ';
                        maze[currentRow + 1][currentCol] = ' ';
                        steps++;
                    }
                    break;
                case 1: // Down
                    if (currentRow < rows - 2) {
                        currentRow += 2;
                        maze[currentRow][currentCol] = ' ';
                        maze[currentRow - 1][currentCol] = ' ';
                        steps++;
                    }
                    break;
                case 2: // Left
                    if (currentCol > 1) {
                        currentCol -= 2;
                        maze[currentRow][currentCol] = ' ';
                        maze[currentRow][currentCol + 1] = ' ';
                        steps++;
                    }
                    break;
                case 3: // Right
                    if (currentCol < cols - 2) {
                        currentCol += 2;
                        maze[currentRow][currentCol] = ' ';
                        maze[currentRow][currentCol - 1] = ' ';
                        steps++;
                    }
                    break;
            }
        }

        // Set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';
    }
}
