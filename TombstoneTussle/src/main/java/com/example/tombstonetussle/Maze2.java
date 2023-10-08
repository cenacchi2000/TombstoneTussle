package com.example.tombstonetussle;

import java.util.Random;
import java.util.Stack;

public class Maze2 extends Maze {
    private char[][] maze;
    private int startRow;
    private int startCol;

    public Maze2() {
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
        int rows = 10; // Number of rows in the maze
        int cols = 10; // Number of columns in the maze

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

        // Create a stack for backtracking
        Stack<Integer> stack = new Stack<>();
        int currentRow = startRow;
        int currentCol = startCol;

        // Recursive Backtracking algorithm for maze generation
        generateMazeRecursive(currentRow, currentCol, rows, cols, stack);

        // Set the end position
        int endRow = random.nextInt(rows);
        int endCol = random.nextInt(cols);
        maze[endRow][endCol] = 'E';
    }

    private void generateMazeRecursive(int row, int col, int rows, int cols, Stack<Integer> stack) {
        maze[row][col] = ' '; // Mark the cell as a path

        // Create a list of neighboring cells in random order
        int[][] neighbors = {{row - 2, col}, {row + 2, col}, {row, col - 2}, {row, col + 2}};
        shuffleArray(neighbors);

        for (int[] neighbor : neighbors) {
            int newRow = neighbor[0];
            int newCol = neighbor[1];
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && maze[newRow][newCol] == '#') {
                // Valid neighbor found
                int wallRow = (row + newRow) / 2;
                int wallCol = (col + newCol) / 2;
                maze[wallRow][wallCol] = ' '; // Remove the wall between cells

                // Push the current cell onto the stack
                stack.push(row);
                stack.push(col);

                // Move to the next cell
                generateMazeRecursive(newRow, newCol, rows, cols, stack);
            }
        }

        if (!stack.isEmpty()) {
            // Backtrack
            col = stack.pop();
            row = stack.pop();
            generateMazeRecursive(row, col, rows, cols, stack);
        }
    }

    private void shuffleArray(int[][] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
