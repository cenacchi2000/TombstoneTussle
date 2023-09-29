package com.example.tombstonetussle;

public class GameState {

    // Enumeration of possible game states
    public enum State {
        MENU,        // The main menu screen
        DRAWING,     // Players are designing characters
        PLAYING,     // Active gameplay state
        PAUSED,      // Gameplay temporarily halted
        GAME_OVER,   // Game concluded without a winner
        WIN          // Player or team wins
    }

    // Variable to track the current state
    private State currentState;

    // Constructor: sets the initial game state to MENU
    public GameState() {
        currentState = State.MENU;
    }

    public State getCurrentState() {
        return currentState;
    }

    // Methods to handle state transitions

    public void goToMenu() {
        currentState = State.MENU;
        executeStateAction();
    }

    public void startDrawing() {
        currentState = State.DRAWING;
        executeStateAction();
    }

    public void startPlaying() {
        currentState = State.PLAYING;
        executeStateAction();
    }

    public void pauseGame() {
        currentState = State.PAUSED;
        executeStateAction();
    }

    public void resumeGame() {
        currentState = State.PLAYING;
        executeStateAction();
    }

    public void gameOver() {
        currentState = State.GAME_OVER;
        executeStateAction();
    }

    public void winGame() {
        currentState = State.WIN;
        executeStateAction();
    }

    private void executeStateAction() {
        System.out.println("State changed to: " + currentState);
        switch (currentState) {
            case MENU:
                // Any logic for transitioning to the MENU state
                break;
            case DRAWING:
                // Logic for DRAWING state, e.g., allow player to design characters
                break;
            case PLAYING:
                break;
            case PAUSED:
                break;
            case GAME_OVER:
                break;
            case WIN:
                break;
        }
    }


}
