package com.example.tombstonetussle;

import com.example.tombstonetussle.Maze;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Maze1 extends Maze {
    private char[][] maze;
    private boolean[][] hasTrap;   // To track if a cell contains a trap
    private int startRow;
    private int startCol;
    private boolean[][] bloodTrace;

    public Maze1() {
        this.maze = null;
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
        int rows = 17; // Number of rows in the maze
        int cols = 26; // Number of columns in the maze

        // Initialize the maze with walls and additional information arrays
        maze = new char[rows][cols];
        hasTrap = new boolean[rows][cols];   // Initialize the hasTrap array
        bloodTrace = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
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

            // Create labyrinthine structures by allowing backtracking
            if (random.nextDouble() < 0.2) { // 40% chance of backtracking
                if (!stack.isEmpty()) {
                    currentCol = stack.pop();
                    currentRow = stack.pop();
                }
            }

            // Randomly set some cells to contain traps (adjust probability as needed)
            if (random.nextDouble() < 0.1) { // Change 0.1 to your desired trap probability
                hasTrap[currentRow][currentCol] = true;
                maze[currentRow][currentCol] = 'D'; // Set this cell as 'D' (trap)
            }

            // Check neighboring cells
            List<int[]> neighbors = getRandomNeighbors(currentRow, currentCol, rows, cols, random);
            if (!neighbors.isEmpty()) {
                int[] neighbor = neighbors.get(random.nextInt(neighbors.size()));
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
                // Check if the starting position is reachable
                if (maze[startRow][startCol] != ' ') {
                    // The character cannot move from the spawn position; regenerate the maze
                    generateMazeDesign();
                } else {
                    break; // Maze generation complete
                }
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

    public boolean[][] getBloodTrace() {
        return bloodTrace;
    }

    // Set the char inside the maze array to change the type of the tile
    public char changeType(int row, int column, char type){
        System.out.println("And my coordinates are:("+column+","+row+")");
        char originalType = this.maze[row][column];
        this.maze[row][column] = type;
        System.out.println("The tile is changed from:" + originalType + "to:" + type);
        return originalType;

    }



}

