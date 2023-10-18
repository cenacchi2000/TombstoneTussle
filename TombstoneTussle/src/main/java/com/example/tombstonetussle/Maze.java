package com.example.tombstonetussle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Class representing a Maze with methods to generate and manage its design
public class Maze {

    private char[][] maze; // 2D array representing the maze grid
    private int startRow;  // Row index of the starting position
    private int startCol;  // Column index of the starting position
    private int npcRow;    // Row index of the NPC position
    private int npcCol;    // Column index of the NPC position

    // Method to generate the maze design
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

        // Randomly set the start position
        Random random = new Random();
        int startRow = random.nextInt(rows);
        int startCol = random.nextInt(cols);
        maze[startRow][startCol] = 'S';  // 'S' denotes the start position

        // Randomly set the NPC position
        int npcRow = random.nextInt(rows);
        int npcCol = random.nextInt(cols);
        maze[npcRow][npcCol] = 'N';  // 'N' denotes the NPC position

        // Generate the maze design using a basic algorithm
        generateMaze(maze, startRow, startCol);

        // Randomly set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';  // 'E' denotes the end position

        // Update the maze design of the current instance
        setMaze(maze);
        setStartRow(startRow);
        setStartCol(startCol);
        setNpcRow(npcRow);
        setNpcCol(npcCol);
    }

    // Setters for the maze and its properties
    public void setMaze(char[][] maze) {
        this.maze = maze;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public void setNpcRow(int npcRow) {
        this.npcRow = npcRow;
    }

    public void setNpcCol(int npcCol) {
        this.npcCol = npcCol;
    }

    private void generateMaze(char[][] maze, int row, int col) {
        int rows = maze.length;
        int cols = maze[0].length;

        // Mark the current cell as visited
        maze[row][col] = '#';

        // Define the possible directions to move
        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        // Randomly shuffle the directions
        List<int[]> directionList = Arrays.asList(directions);
        Collections.shuffle(directionList);

        // Explore the neighboring cells
        for (int[] direction : directionList) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            // Check if the neighboring cell is within the maze boundaries
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                if (maze[newRow][newCol] == ' ') {
                    // Mark the wall between the current cell and the neighboring cell as empty
                    maze[row + direction[0] / 2][col + direction[1] / 2] = ' ';
                    // Recursively generate the maze design for the neighboring cell
                    generateMaze(maze, newRow, newCol);
                }
            }
        }
    }

}
