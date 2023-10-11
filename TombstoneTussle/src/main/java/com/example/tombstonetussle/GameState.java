
package com.example.tombstonetussle;

public class GameState {
    private Maze1 maze1;

    private Maze selectedMaze;

    public GameState() {
        currentState = State.MENU;
        // Initialize the mazes
        maze1 = new Maze1();
        selectedMaze = maze1; // Default selected maze is Maze1
    }


    // Enumeration of possible game states
    public enum State {
        MENU,        // The main menu screen
        DRAWING,     // Player is designing characters
        PLAYING,     // Active gameplay state
        PAUSED,      // Gameplay temporarily halted
        GAME_OVER,   // Game concluded without a winner
        WIN          // Player wins
    }

    // Variable to track the current state
    private State currentState;
    // Variable to track the previous state
    private State previousState = State.MENU;

    public State getCurrentState() {
        return currentState;
    }

    // Methods to handle state transitions


    public void goToMenu() {
        previousState = currentState;
        currentState = State.MENU;
        executeStateAction();
    }

    public void startDrawing() {
        previousState = currentState;
        currentState = State.DRAWING;
        executeStateAction();
    }

    public void startPlaying() {

        selectedMaze.generateMazeDesign();
        previousState = currentState;
        currentState = State.PLAYING;
        executeStateAction();
    }

    public void pauseGame() {
        previousState = currentState;
        currentState = State.PAUSED;
        executeStateAction();
    }

    public void resumeGame() {
        previousState = currentState;
        currentState = State.PLAYING;
        executeStateAction();
    }

    public void gameOver() {
        previousState = currentState;
        currentState = State.GAME_OVER;
        executeStateAction();
    }

    public void winGame() {
        previousState = currentState;
        currentState = State.WIN;
        executeStateAction();
    }

    public void goToPreviousState() {
        currentState = previousState;
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
