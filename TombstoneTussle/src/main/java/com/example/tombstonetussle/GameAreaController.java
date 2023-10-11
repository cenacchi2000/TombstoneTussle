package com.example.tombstonetussle;

import javafx.scene.input.KeyEvent;

public class GameAreaController {

    private NPCCharacter npcCharacter; // Declare npcCharacter as an instance variable
    
    private GameAreaView gameAreaView;
    private GameAreaModel gameAreaModel;
    private GameController gameController; // Reference to GameController
    private GameAreaModel playerModel;

    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view;
        this.gameAreaModel = model;
        this.gameController = gameController;
        this.playerModel = model; // Initialize playerModel here

        // Initialize npcCharacter here with appropriate values
        int startX = 0;
        int startY = 0;
        npcCharacter = new NPCCharacter(startX, startY);

        setupKeyListeners();
        setupBackArrowListener();
        // Update the player's position to ensure it's correctly positioned at the start
        gameAreaView.updatePlayerPosition(model.getX(), model.getY());
    }

    public void gameLoop() {
        // Example usage of playerModel method
        int playerX = playerModel.getX();
        int playerY = playerModel.getY();

        // ... rest of your gameLoop code ...
    }

    private void renderGameView() {
        // Example usage of playerModel method
        int playerX = playerModel.getX();
        int playerY = playerModel.getY();

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






    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            gameController.handleBackToMainMenu();
        });
    }


}
