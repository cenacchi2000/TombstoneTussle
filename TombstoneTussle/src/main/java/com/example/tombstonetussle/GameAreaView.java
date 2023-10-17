
package com.example.tombstonetussle;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;


public class GameAreaView extends Pane {
    private ImageView playerImageView; // Assuming you have a property to hold the player's ImageView
    private GameAreaModel playerModel; // Define a playerModel field
    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;
    private Rectangle[][] tiles;
    private GameAreaModel gameAreaModel;
    private List<ImageView> enemyImageViews = new ArrayList<>();
    private static final double BULLET_SIZE = 10; // Adjust the size as needed
    private List<ImageView> heartIcons = new ArrayList<>();
    private Label timerLabel = new Label("00:00");
    private List<Rectangle> bulletViews = new ArrayList<>();
    private ImageView shieldImageView;
    private ImageView questionMark = new ImageView();
    private ImageView keyGuidance = new ImageView();
    private ImageView powerGuidance = new ImageView();
    private Rectangle[][] fogTiles;

    public GameAreaView(GameAreaModel model, WritableImage avatar, char[][] selectedMaze, List<EnemyModel> enemyModels) {
        this.playerModel = playerModel; // Set the playerModel through the constructor

        // Set pane's size
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: white;");  // Set a background color

        // Adding a back arrow (emoticon) to the top left
        javafx.scene.control.Label arrowLabel = new javafx.scene.control.Label();
        Image arrow = new Image(getClass().getResourceAsStream("arrowback.png"));
        ImageView view = new ImageView(arrow);
        arrowLabel.setGraphic(view);
        arrowLabel.setId("backArrow"); // Setting an ID for easier access later
        // Set its position
        arrowLabel.setLayoutY(-50);  // This sets the top margin to 20 pixels
        // Create and position the player
        this.gameAreaModel = model;
        // Position and style the timer label
        timerLabel.setLayoutX(1200); // Adjust as needed
        timerLabel.setLayoutY(-60); // Adjust as needed
        //timerLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: white;"); // Adjust styling as needed
        getChildren().add(timerLabel);
        timerLabel.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 45));
        timerLabel.setTextFill(Color.WHITE);
        //Player's life
        initializeHeartIcons();

        // Put the question mark behind the COMMAND
        Image qmImg = new Image(getClass().getResourceAsStream("qm.png"));
        this.questionMark.setImage(qmImg);
        this.questionMark.setFitWidth(50);
        this.questionMark.setFitHeight(50);
        this.questionMark.setLayoutX(1100);
        this.questionMark.setLayoutY(100);
        getChildren().add(this.questionMark);

        // Draw the maze
        char[][] maze = selectedMaze;
        maze = model.getMaze1().getMaze();

        // use an array to contain the tile
        // In order to easily change these rectangles later
        this.tiles = new Rectangle[maze.length][maze[maze.length-1].length];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Rectangle rect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                this.tiles[i][j] = rect;

                //Rectangle rect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                switch (maze[i][j]) {
                    case '#':
                        this.tiles[i][j].setFill(javafx.scene.paint.Color.DARKGREY); // Wall color
                        break;
                    case ' ':
                        this.tiles[i][j].setFill(javafx.scene.paint.Color.LIGHTGRAY); // Path color
                        break;
                    case 'S':
                        this.tiles[i][j].setFill(javafx.scene.paint.Color.GREEN); // Start color
                        break;
                    case 'E':
                        this.tiles[i][j].setFill(javafx.scene.paint.Color.RED); // End color
                        break;
                }
                getChildren().add(this.tiles[i][j]);
            }
        }

        // Initialize the enemyImageViews based on the enemyModels
        for (EnemyModel enemyModel : enemyModels) {
            ImageView enemyImageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police-pistol.png")));
            enemyImageView.setFitWidth(TILE_SIZE);
            enemyImageView.setFitHeight(TILE_SIZE);
            enemyImageView.setTranslateX(enemyModel.getX());
            enemyImageView.setTranslateY(enemyModel.getY());
            getChildren().add(enemyImageView);
            enemyImageViews.add(enemyImageView);
            System.out.println("Numero di ImageView dei nemici: " + enemyImageViews.size());
        }

        this.fogTiles = new Rectangle[maze.length][maze[maze.length-1].length];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Rectangle fogRect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                fogRect.setFill(new Color(0, 0, 0, 0.9));  // semi-transparent gray
                this.fogTiles[i][j] = fogRect;
                getChildren().add(fogRect);
            }
        }

        // Create and position the player image view
        playerImageView = new ImageView(avatar);
        playerImageView.setFitWidth(TILE_SIZE);
        playerImageView.setFitHeight(TILE_SIZE);
        playerImageView.setTranslateX(model.getX());
        playerImageView.setTranslateY(model.getY());
        getChildren().add(playerImageView);
        getChildren().addAll(arrowLabel);

        // Create the shieldImageView
        shieldImageView = new ImageView(new Image(getClass().getResourceAsStream("shield.png")));
        shieldImageView.setFitWidth(TILE_SIZE);
        shieldImageView.setFitHeight(TILE_SIZE);

        // Bind the shieldImageView's position to the playerImageView's position
        shieldImageView.translateXProperty().bind(playerImageView.translateXProperty());
        shieldImageView.translateYProperty().bind(playerImageView.translateYProperty());
        shieldImageView.setVisible(false);

        // Add the shieldImageView to the scene
        getChildren().add(shieldImageView);

        setupGuidance();

    }

    public void showKeyGuidance(boolean show) {
        keyGuidance.setVisible(show);
        System.out.println("carota");
    }


    public void toggleShieldVisibility() {
        shieldImageView.setVisible(!shieldImageView.isVisible());
    }

    public boolean isShieldOn(){
        if(shieldImageView.isVisible()){
            return true;
        }
        else return false;
    }

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

    public void handleEnemyElimination(EnemyModel enemyModel) {
        // Get the enemy's ImageView
        ImageView enemyImageView = getEnemyImageViewForModel(enemyModel);

        if (enemyImageView != null) {
            // Reduce the character's life count
            gameAreaModel.setLives(gameAreaModel.getLives() - 1);

            // Create a FadeTransition to make the enemy fade out over a specified duration
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), enemyImageView);
            fadeOut.setToValue(0); // Fade to fully transparent

            // Define an event handler to be executed when the animation is finished
            fadeOut.setOnFinished(event -> {
                // Perform actions after the animation (e.g., remove from the scene)
                getChildren().remove(enemyImageView);
                enemyImageViews.remove(enemyImageView); // Remove from the list
            });

            // Start the fade-out animation
            fadeOut.play();
        }
    }
    private ImageView getEnemyImageViewForModel(EnemyModel enemyModel) {
        for (ImageView enemyImageView : enemyImageViews) {
            if (enemyImageView.getTranslateX() == enemyModel.getX() && enemyImageView.getTranslateY() == enemyModel.getY()) {
                return enemyImageView;
            }
        }
        return null; // Return null if the ImageView is not found
    }
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
    public void updateEnemyPosition(int x, int y, int index) {
        // Update the position of the enemy ImageView
        if (index >= 0 && index < enemyImageViews.size()) {
            ImageView enemyImageView = enemyImageViews.get(index);
            enemyImageView.setTranslateX(x);
            enemyImageView.setTranslateY(y);
        }
    }
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

    private void initializeHeartIcons() {
        for (int i = 0; i < gameAreaModel.getLives(); i++) {
            ImageView heartIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/heart.png")));
            heartIcon.setFitWidth(30);  // Dimensione desiderata per l'icona
            heartIcon.setFitHeight(30); // Dimensione desiderata per l'icona
            int offsetX = -500;
            heartIcon.setLayoutX(W - (i+1) * 40 + offsetX); // Posizione orizzontale (spostato di 40px per ogni cuore)
            heartIcon.setLayoutY(-45); // Posizione verticale
            getChildren().add(heartIcon);
            heartIcon.toBack();
            heartIcon.setPickOnBounds(false);
            heartIcon.setMouseTransparent(true);
            heartIcons.add(heartIcon);
        }

    }

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

    public void updateTimer(int elapsedSeconds) {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public Rectangle[][] getTiles() {
        return this.tiles;
    }

    public void addBulletView(Bullet bullet) {
        Rectangle bulletView = new Rectangle(bullet.getX(), bullet.getY(), BULLET_SIZE, BULLET_SIZE);
        bulletView.setFill(Color.BLACK);  // Or any color you want for the bullet
        getChildren().add(bulletView);
        bulletViews.add(bulletView);
    }

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

    public void updateBulletPosition(Bullet bullet, double newX, double newY) {
        for (Rectangle bulletView : bulletViews) {
            if (bulletView.getX() != newX && bulletView.getY() != newY) {
                bulletView.setX(newX);
                bulletView.setY(newY);
                break;
            }
        }
    }

    public void setupGuidance(){
        Image keyImg = new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Keycommands.png"));

        keyGuidance = new ImageView(keyImg);

        keyGuidance.setFitHeight(200);
        keyGuidance.setFitWidth(400);

        keyGuidance.setLayoutX(0);
        keyGuidance.setLayoutY(0);

        getChildren().addAll(keyGuidance);
        keyGuidance.setVisible(false);

        System.out.println("cipolla");

    }

    public ImageView getQM(){
        return questionMark;
    }

    public ImageView getKeyGuidance(){
        return keyGuidance;
    }

    public ImageView getPowerGuidance(){
        return powerGuidance;
    }

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
