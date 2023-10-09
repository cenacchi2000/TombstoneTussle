
package com.example.tombstonetussle;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class GameAreaView extends Pane {

    // Constants
    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;

    private GameAreaModel gameAreaModel;
    private ImageView playerImageView;

    public GameAreaView(GameAreaModel model, WritableImage avatar) {
        // Set pane's size
        setPrefSize(W, H);
        this.setStyle("-fx-background-color: lightgreen;");  // Set a background color

        // Adding a back arrow (emoticon) to the top left
        javafx.scene.control.Label arrowLabel = new javafx.scene.control.Label("⬅️");
        arrowLabel.setFont(new javafx.scene.text.Font(24));
        arrowLabel.setId("backArrow"); // Setting an ID for easier access later
        getChildren().addAll(arrowLabel);

        // Create and position the player
        this.gameAreaModel = model;

        // Draw the maze
        char[][] maze = model.getMaze1().getMaze();
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Rectangle rect = new Rectangle(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                switch (maze[i][j]) {
                    case '#':
                        rect.setFill(javafx.scene.paint.Color.DARKGREY); // Wall color
                        break;
                    case ' ':
                        rect.setFill(javafx.scene.paint.Color.LIGHTGRAY); // Path color
                        break;
                    case 'S':
                        rect.setFill(javafx.scene.paint.Color.GREEN); // Start color
                        break;
                    case 'E':
                        rect.setFill(javafx.scene.paint.Color.RED); // End color
                        break;
                }
                getChildren().add(rect);
            }
        }

        // Create and position the player image view
        playerImageView = new ImageView(avatar);
        playerImageView.setFitWidth(TILE_SIZE);
        playerImageView.setFitHeight(TILE_SIZE);
        getChildren().add(playerImageView);
    }

    public GameAreaModel getGameAreaModel() {
        return gameAreaModel;
    }

    public void updatePlayerPosition() {
        double playerX = gameAreaModel.getX();
        double playerY = gameAreaModel.getY();

        // Check for collisions with walls
        for (Node node : getChildren()) {
            if (node instanceof Rectangle) {
                Rectangle wall = (Rectangle) node;
                if (wall.getFill() == javafx.scene.paint.Color.DARKGREY && playerImageView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                    // Collision with a wall, adjust the player's position
                    playerX = gameAreaModel.getLastX(); // Restore the previous X position
                    playerY = gameAreaModel.getLastY(); // Restore the previous Y position
                    break;
                }
            }
        }

        // Clamp player position to within the maze bounds
        if (playerX < 0) {
            playerX = 0;
        } else if (playerX + TILE_SIZE > W) {
            playerX = W - TILE_SIZE;
        }

        if (playerY < 0) {
            playerY = 0;
        } else if (playerY + TILE_SIZE > H) {
            playerY = H - TILE_SIZE;
        }

        playerImageView.setTranslateX(playerX);
        playerImageView.setTranslateY(playerY);

        // Update the game area model with the new position
        gameAreaModel.updateLastPosition(playerX, playerY); // Update lastX and lastY
        gameAreaModel.setX((int) playerX);
        gameAreaModel.setY((int) playerY);
    }

}
