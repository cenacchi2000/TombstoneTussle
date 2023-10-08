package com.example.tombstonetussle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Maze {
    private char[][] maze;
    private int startRow;
    private int startCol;

    public void generateMazeDesign() {
        int rows = 10; // Number of rows in the maze
        int cols = 10; // Number of columns in the maze

        // Initialize the maze with empty cells
        char[][] maze = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = ' ';
            }
        }

        // Set the start position
        Random random = new Random();
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        maze[startRow][startCol] = 'S';

        // Generate the maze design using a basic algorithm
        generateMaze(maze, startRow, startCol);

        // Set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';

        // Update the maze design of the current instance
        setMaze(maze);
        setStartRow(startRow);
        setStartCol(startCol);
    }

    public void setMaze(char[][] maze) {
        this.maze = maze;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    private void generateMaze(char[][] maze, int row, int col) {
        int rows = maze.length;
        int cols = maze[0].length;

        // Mark the current cell as visited
        maze[row][col] = '#';

        // Define the possible directions to move
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Randomly shuffle the directions
        List<int[]> directionList = Arrays.asList(directions);
        Collections.shuffle(directionList);

        // Explore the neighboring cells
        for (int[] direction : directionList) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            // Check if the neighboring cell is within the maze boundaries
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && maze[newRow][newCol] == ' ') {
                // Mark the wall between the current cell and the neighboring cell as empty
                maze[row + direction[0] / 2][col + direction[1] / 2] = ' ';
                // Recursively generate the maze design for the neighboring cell
                generateMaze(maze, newRow, newCol);
            }
        }
    }
}
