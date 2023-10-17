
package com.example.tombstonetussle;

public class GameState {
    private Maze1 maze1;

    private Maze selectedMaze;

    // Variabile statica per tenere traccia dell'istanza Singleton
    private static GameState instance;

    // Rendi il costruttore privato in modo che non possa essere istanziato direttamente
    private GameState() {
        currentState = State.MENU;
        // Inizializza i labirinti
        maze1 = new Maze1();
        selectedMaze = maze1; // Il labirinto selezionato di default è Maze1
    }

    // Metodo statico per ottenere l'istanza Singleton
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
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
    private State currentState = State.MENU;

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

        selectedMaze.generateMazeDesign();
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
