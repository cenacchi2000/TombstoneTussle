package com.example.tombstonetussle;

// Class that represents the game state
public class GameState {
    // Maze related variables
    private Maze1 maze1;
    private Maze selectedMaze;

    // Static variable to track the Singleton instance of GameState
    private static GameState instance;

    // Private constructor to ensure that the GameState object cannot be instantiated directly
    private GameState() {
        currentState = State.MENU;
        // Initialize mazes
        maze1 = new Maze1();
        selectedMaze = maze1; // Set the default selected maze to Maze1
    }

    // Static method to obtain the Singleton instance of GameState
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // Enumeration representing the possible states of the game
    public enum State {
        MENU,        // Representing the main menu screen
        DRAWING,     // State when the player is designing characters
        PLAYING,     // State for active gameplay
        PAUSED,      // State when gameplay is temporarily halted
        GAME_OVER,   // State when the game concludes without a winner
        WIN          // State when the player wins the game
    }

    // Variable to keep track of the current state of the game
    private State currentState = State.MENU;

    // Method to get the current state of the game
    public State getCurrentState() {
        return currentState;
    }

    // State transition methods

    // Transition to the MENU state
    public void goToMenu() {
        currentState = State.MENU;
        executeStateAction();
    }

    // Transition to the DRAWING state
    public void startDrawing() {
        currentState = State.DRAWING;
        executeStateAction();
    }

    // Transition to the PLAYING state and generate the maze design
    public void startPlaying() {
        selectedMaze.generateMazeDesign();
        currentState = State.PLAYING;
        executeStateAction();
    }

    // Transition to the PAUSED state
    public void pauseGame() {
        currentState = State.PAUSED;
        executeStateAction();
    }


    // Transition to the GAME_OVER state
    public void gameOver() {
        currentState = State.GAME_OVER;
        executeStateAction();
    }

    // Transition to the WIN state when the player wins the game
    public void winGame() {
        currentState = State.WIN;
        executeStateAction();
    }

    // Execute logic/actions based on the current game state
    private void executeStateAction() {
        System.out.println("State changed to: " + currentState);
        switch (currentState) {
            case MENU:
                // Logic/actions for transitioning to the MENU state
                break;
            case DRAWING:
                // Logic/actions for the DRAWING state, e.g., allowing player to design characters
                break;
            case PLAYING:
                // Logic/actions for the PLAYING state
                break;
            case PAUSED:
                // Logic/actions for the PAUSED state
                break;
            case GAME_OVER:
                // Logic/actions for the GAME_OVER state
                break;
            case WIN:
                // Logic/actions for the WIN state
                break;
        }
    }
}
