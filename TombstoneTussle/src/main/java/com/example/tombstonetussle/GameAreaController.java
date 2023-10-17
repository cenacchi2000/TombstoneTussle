package com.example.tombstonetussle;

// JavaFX imports
import javafx.animation.*;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// Java utility imports
import java.util.*;


public class GameAreaController {
    // Game configuration and state variables
    private char[][] maze; // The representation of the game maze
    private int size; // The size of the game area
    private GameAreaView gameAreaView; // The view representation of the game area
    private GameAreaModel gameAreaModel; // The model representation of the game area
    private GameController gameController; // The main game controller
    private GameAreaModel playerModel; // The model representation of the player
    private MenuAreaController menuAreaController = new MenuAreaController(); // Controller for the menu area
    private long lastClickTime = 0; // Time of the last click event
    private EnemyModel enemyModel; // The enemy in the game
    private List<EnemyModel> enemyModels = new ArrayList<>(); // List of all enemy models
    private boolean isFollowingBloodTrace = false; // Boolean to track if the player is following a blood trace
    private Set<Integer> passedBloodStains = new HashSet<>(); // Set of blood stains that the player has passed
    private Timeline timerTimeline; // Timer for game events
    private List<Bullet> bullets = new ArrayList<>(); // List of bullets in the game
    private final long shootTimeInterval = 2000000000L; // 2 seconds in nanoseconds
    private long lastSpaceBarClickTime = 0; // Time when the space bar was last clicked
    private boolean isSpaceBarClicked = false; // Boolean to track if the space bar has been clicked
    private boolean isShieldToggled = false; // Boolean to track if the shield is toggled on/off
    private final Object lock = new Object(); // Lock object for synchronization
    private static final long SPACE_BAR_CLICK_THRESHOLD = 500; // Define the threshold time in milliseconds for space bar click

    // Constructor for the game area controller
    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view; // Set the game area view
        this.gameAreaModel = model; // Set the game area model
        this.gameController = gameController; // Set the game controller
        this.playerModel = model; // Set the player model
        this.maze = model.getMaze1().getMaze(); // Get the maze from the model
        this.size = model.getSize(); // Get the size of the game area

        startEnemyMovement(); // Start the movement of the enemies
        setupKeyListeners(); // Set up the key listeners for user input
        setupBackArrowListener(); // Set up the listener for the back arrow

        // Listener for double click events on the game area view
        gameAreaView.setOnMouseClicked(event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime <= 500) {
                handleDoubleClick(event.getX(), event.getY());
            }
            lastClickTime = currentTime;
        });

        // Listener for mouse pressed events on the game area view
        gameAreaView.setOnMousePressed(e->{
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
            Rectangle[][] tiles = gameAreaView.getTiles();

            // Listener for key pressed events when the "M" key is pressed
            gameAreaView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.M) {
                    if (tiles[prevY][prevX].getFill().getClass().getSimpleName().equals("ImagePattern")){
                        tiles[prevY][prevX].setFill(Color.LIGHTGRAY);
                        gameAreaModel.getMaze1().changeType(prevY, prevX, ' ');
                    }
                    // Disable the keyPress event everytime the cancelling interaction is done
                    gameAreaView.setOnKeyPressed(null);
                }
            });
        });

        // Listener for drag over events on the game area view
        gameAreaView.setOnDragOver(e->{
            if(e.getDragboard().hasString()){
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });

        // Listener for drag entered events on the game area view
        gameAreaView.setOnDragEntered(e->{
            if(e.getDragboard().getString().equals("W")){
                Image wallImg = new Image(getClass().getResourceAsStream("wall.jpg"));
                gameAreaView.getScene().setCursor(new ImageCursor(wallImg));
            }
            else if(e.getDragboard().getString().equals("T")){
                Image trapImg = new Image(getClass().getResourceAsStream("trap.png"));
                gameAreaView.getScene().setCursor(new ImageCursor(trapImg));
            }
        });

        // Listener for key pressed events on the game area view
        gameAreaView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                long currentTime = System.currentTimeMillis();
                synchronized (lock) {
                    if (currentTime - lastSpaceBarClickTime <= SPACE_BAR_CLICK_THRESHOLD) {
                        if (!isSpaceBarClicked) {
                            toggleShield(); // Toggle the shield on/off
                            isSpaceBarClicked = true;
                            // Start a new thread to handle the shield toggling
                            new Thread(() -> {
                                while (true) {
                                    long newTime = System.currentTimeMillis();
                                    synchronized (lock) {
                                        if (newTime - lastSpaceBarClickTime > SPACE_BAR_CLICK_THRESHOLD) {
                                            isSpaceBarClicked = false;
                                            toggleShield();
                                            break;
                                        }
                                    }
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    lastSpaceBarClickTime = currentTime;
                }
            }
        });

        // Request focus for the game area view to capture key events
        gameAreaView.requestFocus();

        // Listener for drag dropped events on the game area view
        gameAreaView.setOnDragDropped(e->{
            String string = e.getDragboard().getString();
            char type = string.charAt(0);
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
            Rectangle[][] tiles = gameAreaView.getTiles();
            char originalType;

            // Check if a power-up can be placed in the current tile
            if(!tiles[prevY][prevX].getFill().getClass().getSimpleName().equals("ImagePattern")){
                if(type == 'W'){
                    originalType = gameAreaModel.getMaze1().changeType(prevY,prevX,type);
                    Image wall = new Image(getClass().getResourceAsStream("wall.jpg"));
                    tiles[prevY][prevX].setFill(new ImagePattern(wall));
                }
                else if (type == 'T') {
                    originalType = gameAreaModel.getMaze1().changeType(prevY,prevX,type);
                    Image trap = new Image(getClass().getResourceAsStream("trap.png"));
                    tiles[prevY][prevX].setFill(new ImagePattern(trap));
                }
            }

            // Reset the cursor and clear the dragboard
            gameAreaView.getScene().setCursor(Cursor.DEFAULT);
            e.getDragboard().clear();
            e.setDropCompleted(true);
        });

        // Listener for click events on the question mark icon
        gameAreaView.getQM().setOnMouseClicked(e->{
            ImageView keyGuidance = gameAreaView.getKeyGuidance();
            ImageView powerGuidance = gameAreaView.getPowerGuidance();
            keyGuidance.setVisible(!keyGuidance.isVisible());
            powerGuidance.setVisible(!powerGuidance.isVisible());
        });

        // Initialize the enemies in the game
        for (int i = 0; i < 4; i++) {
            enemyModels.add(new EnemyModel(GameAreaView.TILE_SIZE, model.getMaze1()));
        }

        // Start the game timer
        startTimer();
        // Update the player's position at the start of the game
        gameAreaView.updatePlayerPosition(model.getX(), model.getY());
    }

    // Set up the key listeners for user input
    private void setupKeyListeners() {
        gameAreaView.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyInput);
    }

    // Handle key input events from the user
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
                return; // If the key is not a movement key, exit the method
        }

        int currentX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int currentY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;

        // Check if the player actually moved
        if (prevX != currentX || prevY != currentY) {
            gameAreaModel.getMaze1().getBloodTrace()[prevY][prevX] = true;
            gameAreaView.updatePlayerPosition(gameAreaModel.getX(), gameAreaModel.getY());
        }

        // Check if the player is out of lives
        if(gameAreaModel.getLives()<=0) {
            showFailureMessage();
        }

        // Check if all the enemies have been eliminated
        if (enemyModels.isEmpty()) {
            stopTimer();
            showWinMessage();
        }

        // Check for collisions with the enemies
        Iterator<EnemyModel> iterator = enemyModels.iterator();
        while (iterator.hasNext()) {
            EnemyModel enemyModel = iterator.next();
            if (enemyModel.getX() == gameAreaModel.getX() && enemyModel.getY() == gameAreaModel.getY()) {
                handleEnemyElimination(enemyModel, iterator);
            }
        }
    }

    // Show a failure message when the player is out of lives
    private void showFailureMessage() {
        Alert failureAlert = new Alert(Alert.AlertType.INFORMATION);
        failureAlert.setTitle("Game Over");
        failureAlert.setHeaderText("You've lost!");
        failureAlert.setContentText("You've run out of lives. Better luck next time.");
        ButtonType backToMenuButton = new ButtonType("Back to Menu", ButtonBar.ButtonData.OK_DONE);
        failureAlert.getButtonTypes().setAll(backToMenuButton);

        // Handle the action to go back to the main menu
        Optional<ButtonType> result = failureAlert.showAndWait();
        if (result.isPresent() && result.get() == backToMenuButton) {
            gameController.handleBackToMainMenu();
        }
    }

    // Show a win message when all the enemies have been eliminated
    private void showWinMessage() {
        Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
        winAlert.setTitle("Congratulations!");
        winAlert.setHeaderText("You've won!");
        winAlert.setContentText("You've eliminated all the enemies. Great job!");
        ButtonType backToMenuButton = new ButtonType("Back to Menu", ButtonBar.ButtonData.OK_DONE);
        winAlert.getButtonTypes().setAll(backToMenuButton);

        // Handle the action to go back to the main menu
        Optional<ButtonType> result = winAlert.showAndWait();
        if (result.isPresent() && result.get() == backToMenuButton) {
            gameController.handleBackToMainMenu();
        }
    }

    // Toggle the visibility of the shield
    private void toggleShield() {
        gameAreaView.toggleShieldVisibility();
    }

    // Handle double click events on the game area view
    private void handleDoubleClick(double x, double y) {
        int tileX = (int) x / GameAreaView.TILE_SIZE;
        int tileY = (int) y / GameAreaView.TILE_SIZE;

        if (tileX >= 0 && tileX < gameAreaModel.getMaze1().getMaze()[0].length &&
                tileY >= 0 && tileY < gameAreaModel.getMaze1().getMaze().length) {
            if (gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX]) {
                gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX] = false;
                gameAreaView.removeBloodTrace(tileX, tileY);
            }
        }
    }

    // Start the game timer
    private void startTimer() {
        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    gameAreaModel.incrementElapsedTime();
                    gameAreaView.updateTimer(gameAreaModel.getElapsedTime());
                })
        );
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    // Stop the game timer
    public void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }

    // Set up the listener for the back arrow
    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            if(gameAreaModel.getLives()<=0){
                showFailureMessage();
            }
            else showConfirmationDialog();
        });
    }

    // Show a confirmation dialog when the player wants to go back to the main menu
    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Go back to the main menu?");
        alert.setContentText("All progress will be lost. Are you sure you want to continue?");

        ButtonType buttonConfirm = new ButtonType("Confirm");
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonConfirm, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonConfirm) {
            gameController.handleBackToMainMenu();
        }
    }

    // Move the enemy in a random direction
    private void moveEnemyRandomly(EnemyModel enemyModel) {
        Random random = new Random();

        int newX = enemyModel.getX();
        int newY = enemyModel.getY();

        int currentTileX = newX / GameAreaView.TILE_SIZE;
        int currentTileY = newY / GameAreaView.TILE_SIZE;

        List<int[]> neighboringCells = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Directions: Up, Down, Left, Right

        // Check the neighboring cells for bloodstains
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
            // If there are neighboring cells with bloodstains, find the closest one
            int closestIndex = findClosestBloodStain(neighboringCells, newX, newY);
            int[] closestCell = neighboringCells.get(closestIndex);

            passedBloodStains.add(closestCell[0] * 1000 + closestCell[1]); // Add this cell to the set of passed bloodstains
            gameAreaView.removeBloodTrace(closestCell[0], closestCell[1]); // Remove the bloodstain from the game area view

            newX = closestCell[0] * GameAreaView.TILE_SIZE;
            newY = closestCell[1] * GameAreaView.TILE_SIZE;
        } else {
            // If there are no neighboring cells with bloodstains, move in a random direction
            int direction = random.nextInt(4); // Choose a random direction: 0 = up, 1 = down, 2 = left, 3 = right

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

    // Find the closest bloodstain to a given position
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

    // Check if a given cell is valid (i.e., within the bounds of the maze)
    private boolean isValidCell(int x, int y) {
        int mazeWidth = gameAreaModel.getMaze1().getMaze()[0].length;
        int mazeHeight = gameAreaModel.getMaze1().getMaze().length;

        return x >= 0 && y >= 0 && x < mazeWidth && y < mazeHeight;
    }

    /**
     * Handle the elimination of an enemy based on its proximity to the player.
     *
     * @param enemyModel The enemy model to be checked for elimination.
     * @param iterator The iterator for the collection of enemy models.
     */
    private void handleEnemyElimination(EnemyModel enemyModel, Iterator<EnemyModel> iterator) {
        // Get the enemy's X and Y position
        int enemyX = enemyModel.getX();
        int enemyY = enemyModel.getY();

        // Get the player's X and Y position from the game area model
        int playerX = gameAreaModel.getX();
        int playerY = gameAreaModel.getY();

        // Calculate the Manhattan distance between the player and the enemy
        int distance = Math.abs(playerX - enemyX) + Math.abs(playerY - enemyY);

        // Set a threshold for elimination. If the distance is less than or equal to this value, the enemy is eliminated
        int eliminationThreshold = 1;

        if (distance <= eliminationThreshold) {
            // If the enemy is within the elimination threshold, perform necessary actions
            System.out.println("Enemy eliminated!");
            gameAreaView.removeEnemyView(enemyModel);

            // Remove the enemy model from the collection
            iterator.remove();
        }
    }

    /**
     * Start the movement of enemies and periodically update their positions.
     */
    private void startEnemyMovement() {
        // Store the last update time. It's wrapped in an array to make it effectively final and mutable inside the lambda
        final long[] lastUpdateTime = {System.nanoTime()};
        long updateTimeInterval = 500000000L; // Time interval for updates (0.5 seconds in nanoseconds)

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // If the time since the last update exceeds the interval, update enemy positions
                if (now - lastUpdateTime[0] >= updateTimeInterval) {
                    for (int i = 0; i < enemyModels.size(); i++) {
                        moveEnemyRandomly(enemyModels.get(i));
                        gameAreaView.updateEnemyPosition(enemyModels.get(i).getX(), enemyModels.get(i).getY(), i);
                    }

                    moveBullets();  // Move all bullets in the game

                    // Update the last update time
                    lastUpdateTime[0] = now;

                    // Check player's life count and handle game logic accordingly
                    if (gameAreaModel.getLives() <= 0) {
                        gameAreaView.removePlayerView();
                        gameAreaModel.disablePlayer();
                        gameAreaView.updateHeartIcons();
                        stopTimer();
                    }
                }
            }
        };
        timer.start(); // Start the animation timer
        startEnemyShooting(); // Begin enemy shooting sequence
    }

    /**
     * Start the enemy shooting mechanism.
     */
    private void startEnemyShooting() {
        // Store the last shoot time. It's wrapped in an array to make it effectively final and mutable inside the lambda
        final long[] lastShootTime = {System.nanoTime()};

        AnimationTimer shootTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // If the time since the last shot exceeds the interval, enemies shoot
                if (now - lastShootTime[0] >= shootTimeInterval) {
                    for (EnemyModel enemy : enemyModels) {
                        if (enemy.canShootAt(gameAreaModel, maze)) {
                            Bullet bullet = createBulletFromEnemyToPlayer(enemy);
                            bullets.add(bullet);
                            gameAreaView.addBulletView(bullet);
                        }
                    }
                    // Update the last shoot time
                    lastShootTime[0] = now;
                }
            }
        };
        shootTimer.start(); // Start the shooting timer
    }


    //Create a bullet that moves from an enemy's position towards the player.
    private Bullet createBulletFromEnemyToPlayer(EnemyModel enemy) {
        // Calculate the bullet's starting position (center of the tile)
        double startX = enemy.getX() + GameAreaView.TILE_SIZE / 2.0;
        double startY = enemy.getY() + GameAreaView.TILE_SIZE / 2.0;

        // Calculate the direction vector from the enemy to the player
        double directionX = gameAreaModel.getX() - startX;
        double directionY = gameAreaModel.getY() - startY;

        // Normalize the direction vector to unit length
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= magnitude;
        directionY /= magnitude;

        // Return a new Bullet object with the calculated parameters
        return new Bullet(startX, startY, directionX, directionY, 60);
    }

    //Move all bullets in the game and check for collisions or out-of-bounds conditions.
    private void moveBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            // Update bullet's position based on its direction and speed
            double newX = bullet.getX() + bullet.getDirectionX() * bullet.getSpeed();
            double newY = bullet.getY() + bullet.getDirectionY() * bullet.getSpeed();

            bullet.setX(newX);
            bullet.setY(newY);
            gameAreaView.updateBulletPosition(bullet, newX,newY);

            // Check if bullet collides with any entity or if it goes out of bounds
            if (checkBulletCollision(bullet)) {
                gameAreaView.removeBulletView(bullet);
                bulletIterator.remove();
            }
        }
    }


    //Check if a bullet collides with any game entity.
    private boolean checkBulletCollision(Bullet bullet) {
        // Convert bullet's coordinates to tile coordinates
        int bulletTileX = (int) bullet.getX() / GameAreaView.TILE_SIZE;
        int bulletTileY = (int) bullet.getY() / GameAreaView.TILE_SIZE;

        // Check if bullet is out of maze bounds
        if (bulletTileX < 0 || bulletTileX >= maze[0].length || bulletTileY < 0 || bulletTileY >= maze.length) {
            return false;
        }

        // Convert player's coordinates to tile coordinates
        int playerTileX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int playerTileY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;

        // Check if bullet collides with the player
        if (bulletTileX == playerTileX && bulletTileY == playerTileY) {
            if (gameAreaView.isShieldOn()) {
                return true; // Bullet is blocked by the shield
            } else {
                // Decrease player's lives and update the heart icons in the game view
                gameAreaModel.setLives(gameAreaModel.getLives() - 1);
                gameAreaView.updateHeartIcons();
                return true;
            }
        }

        // Check if bullet collides with a wall or another obstacle in the maze
        if (maze[bulletTileY][bulletTileX] == '#' || maze[bulletTileY][bulletTileX] == 'W') {
            return true;
        }

        return false; // No collision detected
    }
}

