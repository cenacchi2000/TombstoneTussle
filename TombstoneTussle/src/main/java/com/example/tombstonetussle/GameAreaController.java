package com.example.tombstonetussle;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;
import java.util.stream.Collectors;

import com.example.tombstonetussle.GameAreaView;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameAreaController {
    private char[][] maze;  // Add a field for the maze
    private int size; // Add the size variable
    private GameAreaView gameAreaView;
    private GameAreaModel gameAreaModel;
    private GameController gameController;
    private GameAreaModel playerModel;
    private StringBuilder keySequence = new StringBuilder();
    private long lastClickTime = 0;
    private EnemyModel enemyModel;
    private List<EnemyModel> enemyModels = new ArrayList<>();

    private Set<Integer> passedBloodStains = new HashSet<>();
    private Timeline timerTimeline;

    private List<Bullet> bullets = new ArrayList<>();
    private final long shootTimeInterval = 2000000000L; // 2 seconds in nanoseconds

    private long lastSpaceBarClickTime = 0;

    private boolean isSpaceBarClicked = false;

    private final Object lock = new Object();

    private static final long SPACE_BAR_CLICK_THRESHOLD = 500; // Define the threshold time in milliseconds


    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view;
        this.gameAreaModel = model;
        this.gameController = gameController;
        this.playerModel = model;
        this.maze = model.getMaze1().getMaze();
        this.size = model.getSize(); // Initialize the size variable with the appropriate value

        startEnemyMovement();
        setupKeyListeners();
        setupBackArrowListener();
        //setupGuidance();

        //Listener to fast double click
        gameAreaView.setOnMouseClicked(event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime <= 500) {
                handleDoubleClick(event.getX(), event.getY());
            }
            lastClickTime = currentTime;

        });

        // MousePress the specific power-up and press key "M"
        // to cancel this power-up(temporary wall or trap)
        gameAreaView.setOnMousePressed(e->{
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
            Rectangle[][] tiles = gameAreaView.getTiles();
            Image floor = new Image(getClass().getResourceAsStream("mug.png")); // Import the path image

            gameAreaView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.M) {
//                    System.out.println("'M' is pressed");
//                    System.out.println("The coordinates:("+prevY+","+prevX+")");
                    if (tiles[prevY][prevX].getFill().getClass().getSimpleName().equals("ImagePattern")){
                        tiles[prevY][prevX].setFill(new ImagePattern(floor)); // Set the tile into path
                        gameAreaModel.getMaze1().changeType(prevY, prevX, ' ');
                    }


                    // Disable the keyPress event everytime the cancelling interaction is done
                    gameAreaView.setOnKeyPressed(null);
                }
            });
        });

        // Listener on OnDragOver
        // Activated when mouse drags something over here
        // Accept the transferred string which indicates the type of the power-up
        gameAreaView.setOnDragOver(e->{
            if(e.getDragboard().hasString()){
                e.acceptTransferModes(TransferMode.ANY);

            }
            e.consume();
        });

        // To set the cursor correspond to the power-up//
        gameAreaView.setOnDragEntered(e->{
            if(e.getDragboard().getString().equals("W")){
                Image wallImg = new Image(getClass().getResourceAsStream("brickwall.jpg"));
                gameAreaView.getScene().setCursor(new ImageCursor(wallImg));
            }
            else if(e.getDragboard().getString().equals("T")){
                Image trapImg = new Image(getClass().getResourceAsStream("trap.png"));
                gameAreaView.getScene().setCursor(new ImageCursor(trapImg));
            }
        });

        gameAreaView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                long currentTime = System.currentTimeMillis();

                synchronized (lock) {
                    if (currentTime - lastSpaceBarClickTime <= SPACE_BAR_CLICK_THRESHOLD) {
                        if (!isSpaceBarClicked) {
                            toggleShield(); // Toggle the shield for each space bar press within the threshold
                            isSpaceBarClicked = true;

                            // Start a thread to handle spam-click checking
                            new Thread(() -> {
                                while (true) {
                                    long newTime = System.currentTimeMillis();
                                    synchronized (lock) {
                                        if (newTime - lastSpaceBarClickTime > SPACE_BAR_CLICK_THRESHOLD) {
                                            isSpaceBarClicked = false;
                                            toggleShield(); // Untoggle the shield
                                            break; // Exit the while loop and end the thread
                                        }
                                    }
                                    try {
                                        Thread.sleep(50); // Sleep for a short duration before checking again
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

        gameAreaView.requestFocus(); // Ensure that the view has focus to capture key events

        // Listener on OnDragDropped
        // Activated when mouse drops the object
        // Then set the wall/trap according to the passed string
        gameAreaView.setOnDragDropped(e->{
            String string = e.getDragboard().getString();
            char type = string.charAt(0);
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
            Rectangle[][] tiles = gameAreaView.getTiles();
            char originalType;

            if((type == 'W') && (maze[prevY][prevX] != 'T')){
                originalType = gameAreaModel.getMaze1().changeType(prevY,prevX,type);
                Image wall = new Image(getClass().getResourceAsStream("brickwall.jpg"));
                tiles[prevY][prevX].setFill(new ImagePattern(wall));

            }
            else if ((type == 'T') && (maze[prevY][prevX] != 'W')) {
                originalType = gameAreaModel.getMaze1().changeType(prevY,prevX,type);
                Image trap = new Image(getClass().getResourceAsStream("trap.png"));
                tiles[prevY][prevX].setFill(new ImagePattern(trap));
            }

            // To avoid putting the power-up on the one exists.
            if(!tiles[prevY][prevX].getFill().getClass().getSimpleName().equals("ImagePattern")){

            }

            // Set the cursor back to default as the dropping completed
            // Also have to the clear the dragboard or else the lines inside the setOnDragEntered
            // will be continuously run and the cursor is always image
            gameAreaView.getScene().setCursor(Cursor.DEFAULT);
            e.getDragboard().clear();
            e.setDropCompleted(true);
        });

        // Set listener on Guidance
        // When the mouse clicked the Question-Mark, the graphical guidance popup
        // Re-click the question mark to close the guidance
        gameAreaView.getQM().setOnMouseClicked(e->{
            ImageView keyGuidance = gameAreaView.getKeyGuidance();
            ImageView powerGuidance = gameAreaView.getPowerGuidance();

            keyGuidance.setVisible(!keyGuidance.isVisible());
            powerGuidance.setVisible(!powerGuidance.isVisible());

        });

        for (int i = 0; i < 4; i++) {
            enemyModels.add(new EnemyModel(GameAreaView.TILE_SIZE, model.getMaze1()));
        }
        System.out.println("Numero di nemici: " + enemyModels.size());

        startTimer();
        // Update the player's position to ensure it's correctly positioned at the start
        gameAreaView.updatePlayerPosition(model.getX(), model.getY());
    }

    private void setupKeyListeners() {
        gameAreaView.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyInput);
    }


    private void handleKeyInput(KeyEvent event) {
        int prevX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int prevY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;

        int playerX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int playerY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;
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
            case F:
                if (keySequence.length() == 0) {
                    keySequence.append("F");
                }
                break;
            case G:
                if (keySequence.toString().equals("F")) {
                    keySequence.append("G");
                } else {
                    keySequence.setLength(0); // Reset if sequence is broken
                }
                break;
            case H:
                if (keySequence.toString().equals("FG")) {
                    keySequence.append("H");
                    // Check if the character and enemy are in the same cell
                    for (EnemyModel enemyModel : enemyModels) {
                        int enemyX = enemyModel.getX() / GameAreaView.TILE_SIZE;
                        int enemyY = enemyModel.getY() / GameAreaView.TILE_SIZE;
                        if (playerX == enemyX && playerY == enemyY) {
                            // Character and enemy are in the same cell, eliminate the enemy
                            handleEnemyElimination(enemyModel, enemyModels.iterator());
                        }
                    }
                    keySequence.setLength(0); // Reset after successful elimination
                } else {
                    keySequence.setLength(0); // Reset if sequence is broken
                }
                break;
            default:
                return; // If it's not one of the valid keys, exit early.
        }

        boolean playerNearEnemy = false;

        for (EnemyModel enemyModel : enemyModels) {
            int enemyX = enemyModel.getX() / GameAreaView.TILE_SIZE;
            int enemyY = enemyModel.getY() / GameAreaView.TILE_SIZE;
            if (Math.abs(playerX - enemyX) <= 1 && Math.abs(playerY - enemyY) <= 1) {
                playerNearEnemy = true;
                break;
            }
        }

        if (playerNearEnemy) {
            gameAreaView.showKeyGuidance(true);
        } else {
            gameAreaView.showKeyGuidance(false);
        }

        int currentX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int currentY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;

        if (prevX != currentX || prevY != currentY) {
            gameAreaModel.getMaze1().getBloodTrace()[prevY][prevX] = true;
            gameAreaView.updatePlayerPosition(gameAreaModel.getX(), gameAreaModel.getY());
        }

        if (gameAreaModel.getLives() <= 0) {
            showFailureMessage();
        }

        if (enemyModels.isEmpty()) {
            stopTimer();
            showWinMessage();
        }

        Iterator<EnemyModel> iterator = enemyModels.iterator();
        while (iterator.hasNext()) {
            EnemyModel enemyModel = iterator.next();
            if (enemyModel.getX() == gameAreaModel.getX() && enemyModel.getY() == gameAreaModel.getY()) {
                handleEnemyElimination(enemyModel, iterator);
            }
        }
    }


    private void showFailureMessage() {
        Alert failureAlert = new Alert(Alert.AlertType.INFORMATION);
        failureAlert.setTitle("Game Over");
        failureAlert.setHeaderText("You've lost!");
        failureAlert.setContentText("You've run out of lives. Better luck next time.");
        ButtonType backToMenuButton = new ButtonType("Back to Menu", ButtonBar.ButtonData.OK_DONE);
        failureAlert.getButtonTypes().setAll(backToMenuButton);

        // Handle the button action to go back to the main menu
        Optional<ButtonType> result = failureAlert.showAndWait();
        if (result.isPresent() && result.get() == backToMenuButton) {
            gameController.handleBackToMainMenu();
        }
    }

    private void showWinMessage() {
        Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
        winAlert.setTitle("Congratulations!");
        winAlert.setHeaderText("You've won!");
        winAlert.setContentText("You've eliminated all the enemies. Great job!");
        ButtonType backToMenuButton = new ButtonType("Back to Menu", ButtonBar.ButtonData.OK_DONE);
        winAlert.getButtonTypes().setAll(backToMenuButton);

        // Handle the button action to go back to the main menu
        Optional<ButtonType> result = winAlert.showAndWait();
        if (result.isPresent() && result.get() == backToMenuButton) {
            gameController.handleBackToMainMenu();
        }
    }
    private void toggleShield() {
        gameAreaView.toggleShieldVisibility();
    }


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

    public void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }


    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            if(gameAreaModel.getLives()<=0){
                showFailureMessage();
            }
            else showConfirmationDialog();
        });
    }

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


    private void moveEnemyRandomly(EnemyModel enemyModel) {
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

    private void handleEnemyElimination(EnemyModel enemyModel, Iterator<EnemyModel> iterator) {
        // Get the enemy's position
        int enemyX = enemyModel.getX();
        int enemyY = enemyModel.getY();

        // Get the player's position
        int playerX = gameAreaModel.getX();
        int playerY = gameAreaModel.getY();

        // Calculate the distance between the player and the enemy
        int distance = Math.abs(playerX - enemyX) + Math.abs(playerY - enemyY);

        // Define a threshold for elimination (e.g., distance less than or equal to 1)
        int eliminationThreshold = 1;

        if (distance <= eliminationThreshold) {
            // The enemy is within the elimination threshold, perform elimination actions

            System.out.println("Enemy eliminated!");
            //gameAreaView.removeEnemyView(enemyModel);
            enemyModel.setZombified(true);
            gameAreaView.updateEnemyImage(enemyModel, "zombie");


        }
    }


    private void startEnemyMovement() {
        final long[] lastUpdateTime = {System.nanoTime()}; // Wrap in an array to make it effectively final
        long updateTimeInterval = 500000000L; // 0.5 seconds in nanoseconds

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdateTime[0] >= updateTimeInterval) {
                    for (int i = 0; i < enemyModels.size(); i++) {
                        moveEnemyRandomly(enemyModels.get(i));
                        gameAreaView.updateEnemyPosition(enemyModels.get(i).getX(), enemyModels.get(i).getY(), i);
                    }
                    moveBullets();  // Move the bullets
                    lastUpdateTime[0] = now; // Update the value
                    // Check if player's life is 0
                    if (gameAreaModel.getLives() <= 0) {
                        gameAreaView.removePlayerView();
                        gameAreaModel.disablePlayer();
                        gameAreaView.updateHeartIcons();
                        stopTimer();
                    }

                }
            }
        };
        timer.start();
        startEnemyShooting();
    }

    private void startEnemyShooting() {
        final long[] lastShootTime = {System.nanoTime()};

        AnimationTimer shootTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastShootTime[0] >= shootTimeInterval) {
                    for (EnemyModel enemy : enemyModels) {
                        if (enemy.canShootAt(gameAreaModel, maze)) {
                            Bullet bullet = createBulletFromEnemyToPlayer(enemy);
                            bullets.add(bullet);
                            gameAreaView.addBulletView(bullet);
                        }
                    }
                    lastShootTime[0] = now; // Update the time
                }
            }
        };
        shootTimer.start();
    }

    private Bullet createBulletFromEnemyToPlayer(EnemyModel enemy) {
        double startX = enemy.getX() + GameAreaView.TILE_SIZE / 2.0;
        double startY = enemy.getY() + GameAreaView.TILE_SIZE / 2.0;

        double directionX = gameAreaModel.getX() - startX;
        double directionY = gameAreaModel.getY() - startY;

        // Normalize the direction
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= magnitude;
        directionY /= magnitude;

        return new Bullet(startX, startY, directionX, directionY, 60);
    }




    private void moveBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            double newX = bullet.getX() + bullet.getDirectionX() * bullet.getSpeed();
            double newY = bullet.getY() + bullet.getDirectionY() * bullet.getSpeed();

            bullet.setX(newX);
            //System.out.println(newX);
            bullet.setY(newY);
            //System.out.println(newX);
            gameAreaView.updateBulletPosition(bullet, newX,newY);


            // Check for collisions or if the bullet is out of bounds
            if (checkBulletCollision(bullet)) {
                gameAreaView.removeBulletView(bullet);
                bulletIterator.remove();
            }

        }
    }

    private boolean checkBulletCollision(Bullet bullet) {
        // Check for collisions with the player, walls, or any other entities.
        // If a collision is detected, return true. Otherwise, return false.
        int bulletTileX = (int) bullet.getX() / GameAreaView.TILE_SIZE;
        int bulletTileY = (int) bullet.getY() / GameAreaView.TILE_SIZE;
        if (bulletTileX < 0 || bulletTileX >= maze[0].length || bulletTileY < 0 || bulletTileY >= maze.length) {
            return false;
        }


        int playerTileX = gameAreaModel.getX() / GameAreaView.TILE_SIZE;
        int playerTileY = gameAreaModel.getY() / GameAreaView.TILE_SIZE;
        // 10 is the bullet size
        if (bulletTileX == playerTileX && bulletTileY == playerTileY) {
            // Handle the player being hit by a bullet here
            if(gameAreaView.isShieldOn()){
                return true;
            }
            else{
                gameAreaModel.setLives(gameAreaModel.getLives()-1);
                gameAreaView.updateHeartIcons();
                return true;
            }
        }

        if (maze[bulletTileY][bulletTileX] == '#') {
            return true;
        }

        if (maze[bulletTileY][bulletTileX] == 'W') {
            return true;
        }

        return false;
    }

    private boolean isBulletOutOfBounds(Bullet bullet) {
        return bullet.getX() < 0 || bullet.getX() >= size * GameAreaView.TILE_SIZE ||
                bullet.getY() < 0 || bullet.getY() >= size * GameAreaView.TILE_SIZE;
    }
}

