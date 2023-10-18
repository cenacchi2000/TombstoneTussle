package com.example.tombstonetussle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ResourceBundle;

// Controller for the menu area in the game
public class MenuAreaController implements Initializable {

    // FXML annotations for UI elements
    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane wall;
    @FXML
    private AnchorPane trap;
    @FXML
    private ImageView questionMark;
    @FXML
    private Label wallNumText;
    @FXML
    private Label trapNumText;

    // Number of walls and traps available to the player
    private int wallNum = 8;
    private int trapNum = 3;

    // Initialization method that runs after the FXML file has been loaded
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Load images for use in the menu
        Image keyImg = new Image(getClass().getResourceAsStream("keyCommand.png"));
        Image powerImg = new Image(getClass().getResourceAsStream("powerCommand.png"));
        Image qmImg = new Image(getClass().getResourceAsStream("qm.png"));

        // Set the initial number of walls and traps for the player and their styles
        wallNumText.setText("X"+wallNum);
        trapNumText.setText("X"+trapNum);
        wallNumText.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 25));
        trapNumText.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 25));
    }

    // Called when the wall in the menu bar is dragged
    public void addWall(){
        Image wallImg = new Image(getClass().getResourceAsStream("brickwall.jpg"));

        // Change the cursor to the wall image
        wall.getScene().setCursor(new ImageCursor(wallImg));

        // Start the drag and drop operation
        Dragboard db = wall.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("W");
        db.setContent(cb);

        // Decrease the available number of walls
        this.wallNum --;
        wallNumText.setText("X"+wallNum);

        // If no walls are left, disable the wall option
        if(this.wallNum == 0){
            wall.setDisable(true);
        }
    }

    // Called when the trap in the menu bar is dragged
    public void addTrap(){
        Image trapImg = new Image(getClass().getResourceAsStream("trap.png"));

        // Change the cursor to the trap image
        trap.getScene().setCursor(new ImageCursor(trapImg));

        // Start the drag and drop operation
        Dragboard db = trap.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("T");
        db.setContent(cb);

        // Decrease the available number of traps
        trapNum--;
        trapNumText.setText("X"+trapNum);

        // If no traps are left, disable the trap option
        if(this.trapNum == 0){
            trap.setDisable(true);
        }
    }

    // Resets the available power-ups when a new game is started
    public void resetPowerup(){
        wallNum = 8;
        wallNumText.setText("X"+wallNum);
        trapNum = 3;
        trapNumText.setText("X"+trapNum);
    }

    public void openGuidance(){

    }

    public void closeGuidance(){

    }

    public void wallDragDone(){
        //wall.setCursor(Cursor.DEFAULT);
    }

    public void trapDragDone(){
        //trap.getParent().getScene().setCursor(Cursor.DEFAULT);
    }


}
