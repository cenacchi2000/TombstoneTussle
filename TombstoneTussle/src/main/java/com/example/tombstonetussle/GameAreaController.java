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
        
        // Initialize npcCharacter here with appropriate values
        int startX = 0;
        int startY = 0;
        npcCharacter = new NPCCharacter(startX, startY);

        setupKeyListeners();
        setupBackArrowListener();
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
        }
        gameAreaView.updatePlayerPosition(playerModel.getX(), playerModel.getY());
    }


    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            gameController.handleBackToMainMenu();
        });
    }
}
