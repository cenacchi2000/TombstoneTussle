
package com.example.tombstonetussle;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameAreaView extends Pane {

    private ImageView playerImageView; // Assuming you have a property to hold the player's ImageView

    private GameAreaModel playerModel; // Define a playerModel field

    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;
    private Rectangle[][] tiles;
    private ImageView enemyImageView;
    private GameAreaModel gameAreaModel;
    private List<ImageView> enemyImageViews = new ArrayList<>();


    public GameAreaView(GameAreaModel model, WritableImage avatar, char[][] selectedMaze, List<EnemyModel> enemyModels) {
        this.playerModel = playerModel; // Set the playerModel through the constructor


        // Set pane's size
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: white;");  // Set a background color

        // Adding a back arrow (emoticon) to the top left
        javafx.scene.control.Label arrowLabel = new javafx.scene.control.Label("⬅️");
        arrowLabel.setFont(new javafx.scene.text.Font(24));
        arrowLabel.setId("backArrow"); // Setting an ID for easier access later
        // Set its position
        arrowLabel.setLayoutY(-40);  // This sets the top margin to 20 pixels


        // Create and position the player
        this.gameAreaModel = model;

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

        //NPC creation
        for (EnemyModel enemyModel : enemyModels) {
            ImageView enemyImageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/tombstonetussle/Police.png")));
            enemyImageView.setFitWidth(TILE_SIZE);
            enemyImageView.setFitHeight(TILE_SIZE);
            enemyImageView.setTranslateX(enemyModel.getX());
            enemyImageView.setTranslateY(enemyModel.getY());
            getChildren().add(enemyImageView);
            enemyImageViews.add(enemyImageView);
            System.out.println("Numero di ImageView dei nemici: " + enemyImageViews.size());
        }

        
        // Create and position the player image view
        playerImageView = new ImageView(avatar);
        playerImageView.setFitWidth(TILE_SIZE);
        playerImageView.setFitHeight(TILE_SIZE);
        playerImageView.setTranslateX(model.getX());
        playerImageView.setTranslateY(model.getY());
        getChildren().add(playerImageView);
        getChildren().addAll(arrowLabel);

    }


    public boolean hasBloodTrace(int x, int y) {
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
        return gameAreaModel.getMaze1().getBloodTrace()[tileY][tileX];
    }

    public GameAreaModel getGameAreaModel() {
        return gameAreaModel;
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
            }
        }


        playerImageView.setTranslateX(playerX);
        playerImageView.setTranslateY(playerY);

        // Update the game area model with the new position
        gameAreaModel.updateLastPosition(playerX, playerY); // Update lastX and lastY
        gameAreaModel.setX((int) playerX);
        gameAreaModel.setY((int) playerY);
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
        if (index < enemyImageViews.size()) {
            enemyImageViews.get(index).setTranslateX(x);
            enemyImageViews.get(index).setTranslateY(y);
        } else {
            // Handle the error condition, e.g., log an error message or throw a custom exception
            System.err.println("Invalid enemy index: " + index);
        }
    }

    public Rectangle[][] getTiles(){
        System.out.println("tiles:"+this.tiles[1][1]);
        return this.tiles;
    }



}
