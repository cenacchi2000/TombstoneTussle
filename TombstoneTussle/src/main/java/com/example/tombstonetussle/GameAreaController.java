package com.example.tombstonetussle;

import javafx.animation.*;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
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
    private long lastClickTime = 0;
    private EnemyModel enemyModel;
    private List<EnemyModel> enemyModels = new ArrayList<>();

    private boolean isFollowingBloodTrace = false;

    private Set<Integer> passedBloodStains = new HashSet<>();



    public GameAreaController(GameAreaView view, GameAreaModel model, GameController gameController) {
        this.gameAreaView = view;
        this.gameAreaModel = model;
        this.gameController = gameController;
        this.playerModel = model;
        this.maze = maze;  // Initialize the maze field
        this.size = model.getSize(); // Initialize the size variable with the appropriate value


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

        // MousePress the specific power-up and press key "M"
        // to cancel this power-up(temporary wall or trap)
        gameAreaView.setOnMousePressed(e->{
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
            Rectangle[][] tiles = gameAreaView.getTiles();

            gameAreaView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.M) {
//                    System.out.println("'M' is pressed");
//                    System.out.println("The coordinates:("+prevY+","+prevX+")");
                    if (tiles[prevY][prevX].getFill().getClass().getSimpleName().equals("ImagePattern")){
                        tiles[prevY][prevX].setFill(Color.LIGHTGRAY);
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

        // To set the cursor correspond to the power-up
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
            // Set the cursor back to default as the dropping completed
            // Also have to the clear the dragboard or else the lines inside the setOnDragEntered
            // will be continuously run and the cursor is always image
            gameAreaView.getScene().setCursor(Cursor.DEFAULT);
            e.getDragboard().clear();
            e.setDropCompleted(true);
        });

        // Handle the swiping interaction to cancel the power up
        gameAreaView.setOnSwipeRight(e->{
            Rectangle[][] tiles = gameAreaView.getTiles();
            int prevX = (int)e.getX() / GameAreaView.TILE_SIZE;
            int prevY = (int)e.getY() / GameAreaView.TILE_SIZE;
//            if(maze[prevY][prevX] == 'W' || maze[prevY][prevX] == 'T'){
//                maze[prevY][prevX] = ' ';
//            }
            //if(tiles[prevY][prevX].getFill())
            System.out.println(tiles[prevY][prevX].getFill());
        });


        for (int i = 0; i < 4; i++) {
            enemyModels.add(new EnemyModel(GameAreaView.TILE_SIZE, model.getMaze1()));
        }
        System.out.println("Numero di nemici: " + enemyModels.size());



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

        // Check if player's life is 0
        if (gameAreaModel.getLives() <= 0) {
            gameAreaView.removePlayerView();
            gameAreaModel.disablePlayer();
            gameAreaView.updateHeartIcons();
        }

        // Check for collision with enemies
        Iterator<EnemyModel> iterator = enemyModels.iterator();
        while (iterator.hasNext()) {
            EnemyModel enemyModel = iterator.next();
            if (enemyModel.getX() == gameAreaModel.getX() && enemyModel.getY() == gameAreaModel.getY()) {
                handleEnemyElimination(enemyModel, iterator);
            }
        }
    }





    // Create a method to animate cursor movement
    private void animateCursorAroundCharacter(double centerX, double centerY) {
        Cursor originalCursor = gameAreaView.getCursor();
        ImageCursor imageCursor = new ImageCursor(new Image(getClass().getResourceAsStream("cursor_image.png")));

        int radius = 50; // Set the desired radius of the circular movement
        int durationMillis = 2000; // Set the desired duration of the animation

        // Create a Timeline for cursor animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(gameAreaView.cursorProperty(), originalCursor)),
                new KeyFrame(Duration.millis(durationMillis), event -> gameAreaView.setCursor(originalCursor))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        // Animate cursor movement along a circular path
        timeline.setOnFinished(event -> gameAreaView.setCursor(originalCursor));
        gameAreaView.setCursor(imageCursor);

        // Start the animation
        timeline.play();
    }

    private void handleDoubleClick(double x, double y) {
        int tileX = (int) x / GameAreaView.TILE_SIZE;
        int tileY = (int) y / GameAreaView.TILE_SIZE;

        try {
            if (gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX]) {
                gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX] = false;
                gameAreaView.removeBloodTrace(tileX, tileY);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Usa getBloodTrace() per verificare la presenza di una traccia di sangue

    }


    private void setupBackArrowListener() {
        gameAreaView.lookup("#backArrow").setOnMouseClicked(event -> {
            gameController.handleBackToMainMenu();
        });
    }

    private void moveEnemyRandomly(EnemyModel enemyModel) {
        Random random = new Random();

        int newX = enemyModel.getX();
        int newY = enemyModel.getY();

        int currentTileX = newX / GameAreaView.TILE_SIZE;
        int currentTileY = newY / GameAreaView.TILE_SIZE;

        List<int[]> neighboringCells = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        // If the enemy's life is 0, remove it and return
        if (enemyModel.getLives() <= 0) {
            enemyModels.remove(enemyModel);
            gameAreaView.removeEnemyView(enemyModel);
            return;
        }

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
            // You can add your elimination logic here, such as reducing player health or ending the game.
            // For example:
            System.out.println("Enemy eliminated!");

            // Remove the enemy model from the list of enemyModels
            iterator.remove();

            // You can also perform other actions here based on your game's logic.
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
                    lastUpdateTime[0] = now; // Update the value
                }
            }
        };
        timer.start();
    }






}
