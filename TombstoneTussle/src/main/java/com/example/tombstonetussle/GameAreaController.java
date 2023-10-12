package com.example.tombstonetussle;

import javafx.scene.input.KeyEvent;

import java.util.Random;
import javafx.animation.AnimationTimer;


public class GameAreaController {
    private char[][] maze;  // Add a field for the maze
    private GameAreaView gameAreaView;
    private GameAreaModel gameAreaModel;
    private GameController gameController;
    private GameAreaModel playerModel;
    private long lastClickTime = 0;
    private EnemyModel enemyModel;


    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view;
        this.gameAreaModel = model;
        this.gameController = gameController;
        this.playerModel = model;
        this.maze = maze;  // Initialize the maze field
        this.enemyModel = new EnemyModel(GameAreaView.TILE_SIZE, model.getMaze1());


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
        int direction = random.nextInt(4); // 0 = up, 1 = down, 2 = left, 3 = right

        int newX = enemyModel.getX();
        int newY = enemyModel.getY();

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

        if (enemyModel.isValidMove(newX, newY)) {
            enemyModel.setX(newX);
            enemyModel.setY(newY);
        }
    }

    private void startEnemyMovement() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveEnemyRandomly();
                gameAreaView.updateEnemyPosition(enemyModel.getX(), enemyModel.getY()); // Aggiorna la posizione nella vista
            }
        };
        timer.start();
    }



}
