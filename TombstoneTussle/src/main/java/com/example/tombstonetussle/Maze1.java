package com.example.tombstonetussle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Maze1 extends Maze {
    private char[][] maze;
    private boolean[][] hasPassed; // To track if the character has passed through a cell
    private boolean[][] hasTrap;   // To track if a cell contains a trap
    private int startRow;
    private int startCol;

    public Maze1() {
        this.maze = null;
        this.hasPassed = null;
        this.hasTrap = null;
        this.startRow = -1;
        this.startCol = -1;
    }

    public char[][] getMaze() {
        return maze;
    }

    public void setMaze(char[][] maze) {
        this.maze = maze;
    }

    public boolean[][] getHasPassed() {
        return hasPassed;
    }

    public boolean[][] getHasTrap() {
        return hasTrap;
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
        int rows = 19; // Number of rows in the maze
        int cols = 38; // Number of columns in the maze

        // Initialize the maze with walls and additional information arrays
        maze = new char[rows][cols];
        hasPassed = new boolean[rows][cols]; // Initialize the hasPassed array
        hasTrap = new boolean[rows][cols];   // Initialize the hasTrap array

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
                hasPassed[i][j] = false; // Set all cells as not passed through initially
                hasTrap[i][j] = false;  // Set all cells as not containing a trap initially
            }
        }

        // Set the start position
        Random random = new Random();
        startRow = random.nextInt(rows);
        startCol = random.nextInt(cols);
        maze[startRow][startCol] = 'S';

        // Create a stack for backtracking
        Stack<Integer> stack = new Stack<>();
        int currentRow = startRow;
        int currentCol = startCol;

        // Depth-first search algorithm for maze generation
        while (true) {
            maze[currentRow][currentCol] = ' '; // Mark the cell as a path
            hasPassed[currentRow][currentCol] = true; // Mark that the character has passed through this cell

            // Randomly set some cells to contain traps (adjust probability as needed)
            if (random.nextDouble() < 0.1) { // Change 0.1 to your desired trap probability
                hasTrap[currentRow][currentCol] = true;
                maze[currentRow][currentCol] = 'D'; // Set this cell as 'D' (trap)
            }

            // Check neighboring cells
            List<int[]> neighbors = getRandomNeighbors(currentRow, currentCol, rows, cols, random);
            if (!neighbors.isEmpty()) {
                int[] neighbor = neighbors.get(0);
                int newRow = neighbor[0];
                int newCol = neighbor[1];

                // Push the current cell onto the stack
                stack.push(currentRow);
                stack.push(currentCol);

                // Move to the next cell
                currentRow = newRow;
                currentCol = newCol;
            } else if (!stack.isEmpty()) {
                // Backtrack
                currentCol = stack.pop();
                currentRow = stack.pop();
            } else {
                break; // Maze generation complete
            }
        }

        // Set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';

    }

    private List<int[]> getRandomNeighbors(int row, int col, int rows, int cols, Random random) {
        int[][] neighbors = {{row - 2, col}, {row + 2, col}, {row, col - 2}, {row, col + 2}};
        List<int[]> validNeighbors = new ArrayList<>();

        for (int[] neighbor : neighbors) {
            int newRow = neighbor[0];
            int newCol = neighbor[1];
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && maze[newRow][newCol] == '#') {
                // Valid neighbor found
                if (!hasTrap[newRow][newCol]) {
                    maze[(row + newRow) / 2][(col + newCol) / 2] = ' '; // Remove the wall between cells
                    validNeighbors.add(new int[]{newRow, newCol});
                }
            }
        }

        // Shuffle the valid neighbors
        Collections.shuffle(validNeighbors, random);

        return validNeighbors;
    }

}

