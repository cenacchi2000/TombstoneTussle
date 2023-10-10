
package com.example.tombstonetussle;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class GameAreaView extends Pane {

    private ImageView playerImageView; // Assuming you have a property to hold the player's ImageView
    private Pane gamePane;              // Assuming you have a Pane to render the game
    private GameAreaModel playerModel; // Define a playerModel field

    public static final int TILE_SIZE = 40;
    public static final int W = 800;
    public static final int H = 800;

    private GameAreaModel gameAreaModel;
    private GameAreaModel npcCharacter;

    public GameAreaView(GameAreaModel model, WritableImage avatar) {
        this.playerModel = playerModel; // Set the playerModel through the constructor

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
        playerImageView.setTranslateX(model.getX());
        playerImageView.setTranslateY(model.getY());
        getChildren().add(playerImageView);
    }

    public GameAreaModel getGameAreaModel() {
        return gameAreaModel;
    }

    public void updatePlayerPosition(int x, int y) {
        double playerX = gameAreaModel.getX();
        double playerY = gameAreaModel.getY();
/*
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
*/
        playerImageView.setTranslateX(playerX);
        playerImageView.setTranslateY(playerY);

        // Update the game area model with the new position
        gameAreaModel.updateLastPosition(playerX, playerY); // Update lastX and lastY
        gameAreaModel.setX((int) playerX);
        gameAreaModel.setY((int) playerY);
    }




    public void updateNPCPosition(int x, int y) {
        // Assuming you have an ImageView for the NPC character in your GameAreaView
        ImageView npcImageView = getNPCImageView(); // Replace with your actual method to get the NPC ImageView

        // Update the position of the NPC ImageView
        npcImageView.setTranslateX(x);
        npcImageView.setTranslateY(y);
    }

    private ImageView getNPCImageView() {
        // Get the x and y coordinates of the NPC character from your NPCCharacter object
        int npcX = npcCharacter.getX();
        int npcY = npcCharacter.getY();

        // Create an ImageView for the NPC character and set its properties
        ImageView npcImageView = new ImageView(); // You need to initialize it with an image


        Image npcImage = new Image("/com/example/tombstonetussle/police.jpg"); // Replace 'npc_image.png' with your image path
        npcImageView.setImage(npcImage);

        // Set the position of the NPC ImageView based on the NPCCharacter's coordinates
        npcImageView.setX(npcX * GameAreaView.TILE_SIZE); // Adjust for tile size
        npcImageView.setY(npcY * GameAreaView.TILE_SIZE); // Adjust for tile size

        // Set the width and height of the NPC ImageView (adjust as needed)
        npcImageView.setFitWidth(GameAreaView.TILE_SIZE);
        npcImageView.setFitHeight(GameAreaView.TILE_SIZE);

        // Set any other properties for the ImageView, such as scaling or rotation

        return npcImageView;
    }

    public void redraw() {
        // Assuming gamePane is your JavaFX Pane where you render the game
        // Clear the existing content on the gamePane
        gamePane.getChildren().clear();

        // Draw or update the player character's image
        playerImageView.setX(gameAreaModel.getX());
        playerImageView.setY(gameAreaModel.getY());

        // Get the NPC ImageView from the getNPCImageView method
        ImageView npcImageView = getNPCImageView();

        // Add the player and NPC images to the gamePane
        gamePane.getChildren().addAll(playerImageView, npcImageView);
    }

}
