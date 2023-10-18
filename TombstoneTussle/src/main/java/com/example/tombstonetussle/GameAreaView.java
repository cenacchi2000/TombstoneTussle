package com.example.tombstonetussle;

// Import necessary JavaFX classes and utilities
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

// Class that represents the visual game area in JavaFX
public class GameAreaView extends Pane {

    // Variables for the game's components
    private ImageView playerImageView;
    private GameAreaModel playerModel;
    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;
    private Rectangle[][] tiles;
    private GameAreaModel gameAreaModel;
    private List<ImageView> enemyImageViews = new ArrayList<>();
    private static final double BULLET_SIZE = 10;
    private List<ImageView> heartIcons = new ArrayList<>();
    private Label timerLabel = new Label("00:00");
    private List<Rectangle> bulletViews = new ArrayList<>();
    private ImageView shieldImageView;
    private ImageView questionMark = new ImageView();
    private ImageView keyGuidance = new ImageView();
    private ImageView powerGuidance = new ImageView();
    private Rectangle[][] fogTiles;

    // Constructor for the GameAreaView class
    public GameAreaView(GameAreaModel model, WritableImage avatar, char[][] selectedMaze, List<EnemyModel> enemyModels) {
        this.playerModel = playerModel; // Initialize player model

        // Configure pane size and style
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: white;");

        // Create a back arrow label with an image
        Label arrowLabel = new Label();
        Image arrow = new Image(getClass().getResourceAsStream("arrowback.png"));
        ImageView view = new ImageView(arrow);
        arrowLabel.setGraphic(view);
        arrowLabel.setId("backArrow");
        arrowLabel.setLayoutY(-50);

        // Initialize game model and set timer label position and style
        this.gameAreaModel = model;
        timerLabel.setLayoutX(1200);
        timerLabel.setLayoutY(-60);
        getChildren().add(timerLabel);
        timerLabel.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 45));
        timerLabel.setTextFill(Color.WHITE);

        // Initialize the player's life icons
        initializeHeartIcons();

        // Add a question mark image behind the COMMAND
        Image qmImg = new Image(getClass().getResourceAsStream("qm.png"));
        this.questionMark.setImage(qmImg);
        this.questionMark.setFitWidth(50);
        this.questionMark.setFitHeight(50);
        this.questionMark.setLayoutX(1100);
        this.questionMark.setLayoutY(100);
        getChildren().add(this.questionMark);

        // Load and display the maze
        char[][] maze = model.getMaze1().getMaze();
        this.tiles = new Rectangle[maze.length][maze[maze.length-1].length];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Rectangle rect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                this.tiles[i][j] = rect;
                Image greyWall = new Image(getClass().getResourceAsStream("darkwall.jpg"));
                Image floor = new Image(getClass().getResourceAsStream("mug.png"));
                switch (maze[i][j]) {
                    case '#':
                        this.tiles[i][j].setFill(new ImagePattern(greyWall)); // Wall image
                        break;
                    case ' ':
                        this.tiles[i][j].setFill(new ImagePattern(floor)); // Path image
                        break;
                    case 'S':
                        this.tiles[i][j].setFill(Color.GREEN); // Start color
                        break;
                    case 'E':
                        this.tiles[i][j].setFill(Color.RED); // End color
                        break;
                }
                getChildren().add(this.tiles[i][j]);
            }
        }

        // Initialize enemy views
        for (EnemyModel enemyModel : enemyModels) {
            ImageView enemyImageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police-pistol.png")));
            enemyImageView.setFitWidth(TILE_SIZE);
            enemyImageView.setFitHeight(TILE_SIZE);
            enemyImageView.setTranslateX(enemyModel.getX());
            enemyImageView.setTranslateY(enemyModel.getY());
            getChildren().add(enemyImageView);
            enemyImageViews.add(enemyImageView);
        }

        // Initialize fog tiles that obscure the player's view
        this.fogTiles = new Rectangle[maze.length][maze[maze.length-1].length];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Rectangle fogRect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                fogRect.setFill(new Color(0, 0, 0, 0.9));  // semi-transparent gray
                this.fogTiles[i][j] = fogRect;
                getChildren().add(fogRect);
            }
        }

        // Create and position the player's image view
        playerImageView = new ImageView(avatar);
        playerImageView.setFitWidth(TILE_SIZE);
        playerImageView.setFitHeight(TILE_SIZE);
        playerImageView.setTranslateX(model.getX());
        playerImageView.setTranslateY(model.getY());
        getChildren().add(playerImageView);
        getChildren().addAll(arrowLabel);

        // Create the shield image view and bind its position to the player's
        shieldImageView = new ImageView(new Image(getClass().getResourceAsStream("shield.png")));
        shieldImageView.setFitWidth(TILE_SIZE);
        shieldImageView.setFitHeight(TILE_SIZE);
        shieldImageView.translateXProperty().bind(playerImageView.translateXProperty());
        shieldImageView.translateYProperty().bind(playerImageView.translateYProperty());
        shieldImageView.setVisible(false);
        getChildren().add(shieldImageView);

        // Initialize game guidance imagery
        setupGuidance();
    }

    /**
     * Shows or hides the key guidance image on the game area.
     *
     * @param show If true, shows the key guidance; otherwise, hides it.
     */
    public void showKeyGuidance(boolean show) {
        keyGuidance.setVisible(show);
    }

    /**
     * Toggles the visibility of the player's shield image.
     */
    public void toggleShieldVisibility() {
        shieldImageView.setVisible(!shieldImageView.isVisible());
    }

    /**
     * Checks if the player's shield is currently visible (active).
     *
     * @return True if the shield is active, false otherwise.
     */
    public boolean isShieldOn(){
        if(shieldImageView.isVisible()){
            return true;
        }
        else return false;
    }

    /**
     * Updates the player's position on the game area.
     *
     * @param x The new X-coordinate of the player.
     * @param y The new Y-coordinate of the player.
     */
    public void updatePlayerPosition(int x, int y) {
        double playerX = gameAreaModel.getX();
        double playerY = gameAreaModel.getY();
        int currentTileX = x / TILE_SIZE;
        int currentTileY = y / TILE_SIZE;

        // Get the previous position using the methods you've provided
        int prevTileX = (int) gameAreaModel.getLastX() / TILE_SIZE;
        int prevTileY = (int) gameAreaModel.getLastY() / TILE_SIZE;

        // Check if a blood trace already exists before adding
        if (!getChildren().stream().anyMatch(node ->
                node instanceof ImageView &&
                        ((ImageView) node).getX() == prevTileX * TILE_SIZE &&
                        ((ImageView) node).getY() == prevTileY * TILE_SIZE)) {

            if (gameAreaModel.getMaze1().getBloodTrace()[prevTileY][prevTileX]) {
                ImageView bloodTraceView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/bloodTrace.png")));
                bloodTraceView.setId("bloodTrace");
                bloodTraceView.setFitWidth(TILE_SIZE);
                bloodTraceView.setFitHeight(TILE_SIZE);
                bloodTraceView.setX(prevTileX * TILE_SIZE);
                bloodTraceView.setY(prevTileY * TILE_SIZE);
                getChildren().add(bloodTraceView);
                playerImageView.toFront();
                shieldImageView.toFront();
            }
        }

        playerImageView.setTranslateX(playerX);
        playerImageView.setTranslateY(playerY);

        // Update the game area model with the new position
        gameAreaModel.updateLastPosition(playerX, playerY); // Update lastX and lastY
        gameAreaModel.setX((int) playerX);
        gameAreaModel.setY((int) playerY);
        clearFogAroundPlayer(x, y);
    }

    /**
     * Retrieves the image view corresponding to a given enemy model.
     *
     * @param enemyModel The enemy model to get the image view for.
     * @return The corresponding ImageView of the enemy, or null if not found.
     */
    private ImageView getEnemyImageViewForModel(EnemyModel enemyModel) {
        for (ImageView enemyImageView : enemyImageViews) {
            if (enemyImageView.getTranslateX() == enemyModel.getX() && enemyImageView.getTranslateY() == enemyModel.getY()) {
                return enemyImageView;
            }
        }
        return null; // Return null if the ImageView is not found
    }

    /**
     * Removes the blood trace image at the specified tile position.
     *
     * @param tileX The X-coordinate of the tile.
     * @param tileY The Y-coordinate of the tile.
     */
    public void removeBloodTrace(int tileX, int tileY) {
        ImageView bloodTraceToRemove = null;
        for (Node node : getChildren()) {
            if (node instanceof ImageView &&
                    ((ImageView) node).getX() == tileX * TILE_SIZE &&
                    ((ImageView) node).getY() == tileY * TILE_SIZE &&
                    "bloodTrace".equals(node.getId())) {
                bloodTraceToRemove = (ImageView) node;
                break;
            }
        }

        if (bloodTraceToRemove != null) {
            getChildren().remove(bloodTraceToRemove);
        }
    }

    /**
     * Updates the position of a specific enemy on the game area.
     *
     * @param x The new X-coordinate of the enemy.
     * @param y The new Y-coordinate of the enemy.
     * @param index The index of the enemy in the list of enemy image views.
     */
    public void updateEnemyPosition(int x, int y, int index) {
        // Update the position of the enemy ImageView
        if (index >= 0 && index < enemyImageViews.size()) {
            ImageView enemyImageView = enemyImageViews.get(index);
            enemyImageView.setTranslateX(x);
            enemyImageView.setTranslateY(y);
        }
    }

    /**
     * Removes the image view of a specified enemy from the game area.
     *
     * @param enemyModel The enemy model to remove the view for.
     */
    public void removeEnemyView(EnemyModel enemyModel) {
        // Get the enemy's ImageView
        ImageView enemyView = getEnemyImageViewForModel(enemyModel);
        if (enemyView != null) {
            // Create a FadeTransition to make the enemy fade out over a specified duration
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), enemyView);
            fadeOut.setToValue(0); // Fade to fully transparent

            // Define an event handler to be executed when the animation is finished
            fadeOut.setOnFinished(event -> {
                // Remove the enemy ImageView from the scene and from the list of enemyImageViews
                getChildren().remove(enemyView);
                enemyImageViews.remove(enemyView);
            });

            // Start the fade-out animation
            fadeOut.play();
        }
    }

    /**
     * Removes the player's image view from the game area.
     */
    public void removePlayerView() {
        // Create a FadeTransition to make the player fade out over a specified duration
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), playerImageView);
        fadeOut.setToValue(0); // Fade to fully transparent

        // Define an event handler to be executed when the animation is finished
        fadeOut.setOnFinished(event -> {
            // Remove the player ImageView from the scene
            getChildren().remove(playerImageView);
        });

        // Start the fade-out animation
        fadeOut.play();
    }

    /**
     * Initializes the heart icons on the game area based on the player's lives.
     */
    private void initializeHeartIcons() {
        for (int i = 0; i < gameAreaModel.getLives(); i++) {
            ImageView heartIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/heart.png")));
            heartIcon.setFitWidth(30);
            heartIcon.setFitHeight(30);
            int offsetX = -500;
            heartIcon.setLayoutX(W - (i+1) * 40 + offsetX);
            heartIcon.setLayoutY(-45);
            getChildren().add(heartIcon);
            heartIcon.toBack();
            heartIcon.setPickOnBounds(false);
            heartIcon.setMouseTransparent(true);
            heartIcons.add(heartIcon);
        }

    }

    /**
     * Updates the enemy's image view based on a specified type.
     *
     * @param enemyModel The enemy model to update the view for.
     * @param newType The new type of the enemy (e.g., "police", "zombie").
     */
    public void updateEnemyImage(EnemyModel enemyModel, String newType) {
        ImageView enemyImageView = getEnemyImageViewForModel(enemyModel);
        if (enemyImageView != null) {
            Image newImage;
            switch (newType) {
                case "police":
                    newImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police-pistol.png"));
                    break;
                case "zombie":
                    newImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/zombieTransformed.png"));
                    break;

                default:
                    newImage = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police-pistol.png")); // un'immagine di default
                    break;
            }
            enemyImageView.setImage(newImage);
        }
    }

    /**
     * Updates the visibility of heart icons based on the player's current lives.
     */
    public void updateHeartIcons() {
        int currentLives = gameAreaModel.getLives();
        for (int i = 0; i < heartIcons.size(); i++) {
            if (i < currentLives) {
                heartIcons.get(i).setVisible(true);
            } else {
                heartIcons.get(i).setVisible(false);
            }
        }
    }

    /**
     * Updates the game timer displayed on the game area.
     *
     * @param elapsedSeconds The total elapsed seconds since the game started.
     */
    public void updateTimer(int elapsedSeconds) {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * Retrieves the tiles of the game area.
     *
     * @return A 2D array of Rectangle representing the tiles.
     */
    public Rectangle[][] getTiles() {
        return this.tiles;
    }

    /**
     * Adds a bullet view to the game area.
     *
     * @param bullet The bullet to add the view for.
     */
    public void addBulletView(Bullet bullet) {
        Rectangle bulletView = new Rectangle(bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE);
        bulletView.setFill(Color.BLACK);  // Or any color you want for the bullet
        getChildren().add(bulletView);
        bulletViews.add(bulletView);
    }

    /**
     * Removes a bullet view from the game area.
     *
     * @param bullet The bullet to remove the view for.
     */
    public void removeBulletView(Bullet bullet) {
        Rectangle bulletViewToRemove = null;
        for (Rectangle bulletView : bulletViews) {
            if (bulletView.getX() == bullet.getX() && bulletView.getY() == bullet.getY()) {
                bulletViewToRemove = bulletView;
                break;
            }
        }

        if (bulletViewToRemove != null) {
            getChildren().remove(bulletViewToRemove);
            bulletViews.remove(bulletViewToRemove);
        }
    }

    /**
     * Updates the position of a bullet view on the game area.
     *
     * @param bullet The bullet to update the position for.
     * @param newX The new X-coordinate of the bullet.
     * @param newY The new Y-coordinate of the bullet.
     */
    public void updateBulletPosition(Bullet bullet, double newX, double newY) {
        for (Rectangle bulletView : bulletViews) {
            if (bulletView.getX() != newX && bulletView.getY() != newY) {
                bulletView.setX(newX);
                bulletView.setY(newY);
                break;
            }
        }
    }

    /**
     * Sets up the key guidance image on the game area.
     */
    public void setupGuidance(){
        Image keyImg = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Keycommands.png"));

        keyGuidance = new ImageView(keyImg);

        keyGuidance.setFitHeight(200);
        keyGuidance.setFitWidth(400);

        keyGuidance.setLayoutX(0);
        keyGuidance.setLayoutY(0);

        getChildren().addAll(keyGuidance);
        keyGuidance.setVisible(false);
    }

    /**
     * Retrieves the question mark image view.
     *
     * @return The ImageView representing the question mark.
     */
    public ImageView getQM(){
        return questionMark;
    }

    /**
     * Retrieves the key guidance image view.
     *
     * @return The ImageView representing the key guidance.
     */
    public ImageView getKeyGuidance(){
        return keyGuidance;
    }

    /**
     * Retrieves the power guidance image view.
     *
     * @return The ImageView representing the power guidance.
     */
    public ImageView getPowerGuidance(){
        return powerGuidance;
    }

    /**
     * Clears the fog around the player's current position.
     *
     * @param playerX The X-coordinate of the player.
     * @param playerY The Y-coordinate of the player.
     */
    public void clearFogAroundPlayer(int playerX, int playerY) {
        int radius = 2;
        int tileX = playerX / TILE_SIZE;
        int tileY = playerY / TILE_SIZE;

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (tileY + i >= 0 && tileY + i < fogTiles.length &&
                        tileX + j >= 0 && tileX + j < fogTiles[tileY + i].length) {
                    getChildren().remove(fogTiles[tileY + i][tileX + j]);
                }
            }
        }
    }
}
