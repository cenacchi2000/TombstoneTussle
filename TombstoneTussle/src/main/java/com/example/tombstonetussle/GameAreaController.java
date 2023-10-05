
package com.example.tombstonetussle;

import javafx.scene.input.KeyEvent;

public class GameAreaController {

    private GameAreaView gameAreaView;
    private GameAreaModel gameAreaModel;

    public GameAreaController(GameAreaView view, GameAreaModel model) {
        this.gameAreaView = view;
        this.gameAreaModel = model;

        setupKeyListeners();
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
    }
}
