package com.example.tombstonetussle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// Class that extends the base Maze class to implement specific Maze1 behavior
public class Maze1 extends Maze {
    private char[][] maze;        // 2D char array representing the maze
    private boolean[][] hasTrap;  // 2D boolean array to track if a cell contains a trap
    private int startRow;         // Starting row of the maze
    private int startCol;         // Starting column of the maze
    private boolean[][] bloodTrace; // 2D boolean array to track the blood trace

    // Default constructor initializes maze to null and start positions to -1
    public Maze1() {
        this.maze = null;
        this.hasTrap = null;
        this.startRow = -1;
        this.startCol = -1;
    }

    // Getter for maze
    public char[][] getMaze() {
        return maze;
    }

    // Setter for maze
    public void setMaze(char[][] maze) {
        this.maze = maze;
    }

    // Getter for hasTrap
    public boolean[][] getHasTrap() {
        return hasTrap;
    }

    // Getter for startRow
    public int getStartRow() {
        return startRow;
    }

    // Setter for startRow
    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    // Getter for startCol
    public int getStartCol() {
        return startCol;
    }

    // Setter for startCol
    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    // Method to generate the design of the maze
    public void generateMazeDesign() {
        int rows = 17; // Number of rows in the maze
        int cols = 26; // Number of columns in the maze

        // Initialize the maze with walls and additional information arrays
        maze = new char[rows][cols];
        hasTrap = new boolean[rows][cols];
        bloodTrace = new boolean[rows][cols];

        // Populate maze with walls and initialize trap tracking
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
                hasTrap[i][j] = false;
            }
        }

        // Randomly set the start position
        Random random = new Random();
        startRow = random.nextInt(rows);
        startCol = random.nextInt(cols);
        maze[startRow][startCol] = 'S';

        // Stack for backtracking during maze generation
        Stack<Integer> stack = new Stack<>();
        int currentRow = startRow;
        int currentCol = startCol;

        // Use depth-first search for maze generation
        while (true) {
            maze[currentRow][currentCol] = ' '; // Mark current cell as path

            // Occasionally backtrack to create more labyrinthine structure
            if (random.nextDouble() < 0.2) {
                if (!stack.isEmpty()) {
                    currentCol = stack.pop();
                    currentRow = stack.pop();
                }
            }

            // Check valid neighboring cells
            List<int[]> neighbors = getRandomNeighbors(currentRow, currentCol, rows, cols, random);
            if (!neighbors.isEmpty()) {
                int[] neighbor = neighbors.get(random.nextInt(neighbors.size()));
                int newRow = neighbor[0];
                int newCol = neighbor[1];

                // Store current cell for potential backtracking
                stack.push(currentRow);
                stack.push(currentCol);

                // Move to next cell
                currentRow = newRow;
                currentCol = newCol;
            } else if (!stack.isEmpty()) {
                // If no valid neighbors and stack is not empty, backtrack
                currentCol = stack.pop();
                currentRow = stack.pop();
            } else {
                // If starting position is not reachable, regenerate the maze
                if (maze[startRow][startCol] != ' ') {
                    generateMazeDesign();
                } else {
                    break; // Maze generation is complete
                }
            }
        }

        // Randomly set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';
    }

    // Method to get valid random neighbors for current cell
    private List<int[]> getRandomNeighbors(int row, int col, int rows, int cols, Random random) {
        int[][] neighbors = {{row - 2, col}, {row + 2, col}, {row, col - 2}, {row, col + 2}};
        List<int[]> validNeighbors = new ArrayList<>();

        for (int[] neighbor : neighbors) {
            int newRow = neighbor[0];
            int newCol = neighbor[1];
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && maze[newRow][newCol] == '#') {
                if (!hasTrap[newRow][newCol]) {
                    maze[(row + newRow) / 2][(col + newCol) / 2] = ' ';
                    validNeighbors.add(new int[]{newRow, newCol});
                }
            }
        }

        Collections.shuffle(validNeighbors, random); // Shuffle to get random order

        return validNeighbors;
    }

    // Method to get coordinates of the end point in the maze
    public int[] getEndPointCoordinates() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 'E') {
                    return new int[]{i, j};
                }
            }
        }
        return null; // Should not reach here if 'E' is set properly
    }

    // Getter for bloodTrace
    public boolean[][] getBloodTrace() {
        return bloodTrace;
    }

    // Method to change the type of a tile in the maze
    public char changeType(int row, int column, char type){
        char originalType = this.maze[row][column];
        this.maze[row][column] = type;
        return originalType;
    }
}
