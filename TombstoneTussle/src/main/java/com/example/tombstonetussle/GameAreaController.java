package com.example.tombstonetussle;

import javafx.scene.input.KeyEvent;

import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import com.example.tombstonetussle.GameAreaView;


public class GameAreaController {
    private char[][] maze;  // Add a field for the maze

    private int size; // Add the size variable

    private GameAreaView gameAreaView;
    private GameAreaModel gameAreaModel;
    private GameController gameController;
    private GameAreaModel playerModel;
    private long lastClickTime = 0;
    private EnemyModel enemyModel;

    private boolean isFollowingBloodTrace = false;

    private Set<Integer> passedBloodStains = new HashSet<>();



    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view;
        this.gameAreaModel = model;
        this.gameController = gameController;
        this.playerModel = model;
        this.maze = maze;  // Initialize the maze field
        this.enemyModel = new EnemyModel(GameAreaView.TILE_SIZE, model.getMaze1());
        this.size = model.getSize(); // Initialize the size variable with the appropriate value


        startEnemyMovement();
        setupKeyListeners();
        setupBackArrowListener();

        //Listener to fast double click
        gameAreaView.setOnMouseClicked(event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime <= 500) {
                handleDoubleClick(event.getX(), event.getY());
            }
            lastClickTime = currentTime;
        });

        // Update the player's position to ensure it's correctly positioned at the start
        gameAreaView.updatePlayerPosition(model.getX(), model.getY());
    }



    private void setupKeyListeners() {
        gameAreaView.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyInput);
    }

    private void handleKeyInput(KeyEvent event) {
        int prevX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int prevY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;
        switch (event.getCode()) {
            case W:
                gameAreaModel.moveUp();
                break;
            case S:
                gameAreaModel.moveDown();
                break;
            case A:
                gameAreaModel.moveLeft();
                break;
            case D:
                gameAreaModel.moveRight();
                break;
            default:
                return; // If it's not one of the movement keys, exit early.
        }

        int currentX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int currentY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;

        // Check if player actually moved
        if (prevX != currentX || prevY != currentY) {
            gameAreaModel.getMaze1().getBloodTrace()[prevY][prevX] = true;
            gameAreaView.updatePlayerPosition(gameAreaModel.getX(), gameAreaModel.getY());
        }
    }

    private void handleDoubleClick(double x, double y) {
        int tileX = (int) x / GameAreaView.TILE_SIZE;
        int tileY = (int) y / GameAreaView.TILE_SIZE;

        // Usa getBloodTrace() per verificare la presenza di una traccia di sangue
        if (gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX]) {
            gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX] = false;
            gameAreaView.removeBloodTrace(tileX, tileY);
        }
    }


    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            gameController.handleBackToMainMenu();
        });
    }

    private void moveEnemyRandomly() {
        Random random = new Random();

        int newX = enemyModel.getX();
        int newY = enemyModel.getY();

        int currentTileX = newX / GameAreaView.TILE_SIZE;
        int currentTileY = newY / GameAreaView.TILE_SIZE;

        List<int[]> neighboringCells = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        // Check neighboring cells for bloodstains
        for (int[] direction : directions) {
            int neighborX = currentTileX + direction[0];
            int neighborY = currentTileY + direction[1];

            if (isValidCell(neighborX, neighborY) && gameAreaModel.getMaze1().getBloodTrace()[neighborY][neighborX]) {
                if (!passedBloodStains.contains(neighborX * 1000 + neighborY)) {
                    neighboringCells.add(new int[]{neighborX, neighborY});
                }
            }
        }

        if (!neighboringCells.isEmpty()) {
            // Find the neighboring cell with bloodstain closest to the enemy
            int closestIndex = findClosestBloodStain(neighboringCells, newX, newY);
            int[] closestCell = neighboringCells.get(closestIndex);

            // Add this bloodstain to the set of passed bloodstains
            passedBloodStains.add(closestCell[0] * 1000 + closestCell[1]);

            // Call the removeBloodTrace method from the GameAreaView
            gameAreaView.removeBloodTrace(closestCell[0], closestCell[1]);

            newX = closestCell[0] * GameAreaView.TILE_SIZE;
            newY = closestCell[1] * GameAreaView.TILE_SIZE;
        } else {
            // If no neighboring cells have bloodstains, move randomly
            int direction = random.nextInt(4); // 0 = up, 1 = down, 2 = left, 3 = right

            switch (direction) {
                case 0:
                    newY -= GameAreaView.TILE_SIZE;
                    break;
                case 1:
                    newY += GameAreaView.TILE_SIZE;
                    break;
                case 2:
                    newX -= GameAreaView.TILE_SIZE;
                    break;
                case 3:
                    newX += GameAreaView.TILE_SIZE;
                    break;
            }
        }

        if (enemyModel.isValidMove(newX, newY)) {
            enemyModel.setX(newX);
            enemyModel.setY(newY);
        }
    }

    private int findClosestBloodStain(List<int[]> cells, int currentX, int currentY) {
        int closestIndex = 0;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < cells.size(); i++) {
            int[] cell = cells.get(i);
            int distance = Math.abs(cell[0] - currentX / GameAreaView.TILE_SIZE) +
                    Math.abs(cell[1] - currentY / GameAreaView.TILE_SIZE);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }



    private boolean isValidCell(int x, int y) {
        int mazeWidth = gameAreaModel.getMaze1().getMaze()[0].length;
        int mazeHeight = gameAreaModel.getMaze1().getMaze().length;
        return x >= 0 && y >= 0 && x < mazeWidth && y < mazeHeight;
    }



    private void startEnemyMovement() {
        final long[] lastUpdateTime = {System.nanoTime()}; // Wrap in an array to make it effectively final
        long updateTimeInterval = 500000000L; // 1 second in nanoseconds

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdateTime[0] >= updateTimeInterval) {
                    moveEnemyRandomly();
                    gameAreaView.updateEnemyPosition(enemyModel.getX(), enemyModel.getY());
                    lastUpdateTime[0] = now; // Update the value
                }
            }
        };
        timer.start();
    }





}
