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

public class MenuAreaController implements Initializable {

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
    private int wallNum = 8;
    private int trapNum = 8;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Import the images
        Image keyImg = new Image(getClass().getResourceAsStream("keyCommand.png"));
        Image powerImg = new Image(getClass().getResourceAsStream("powerCommand.png"));
        Image qmImg = new Image(getClass().getResourceAsStream("qm.png"));

        wallNumText.setText("X"+wallNum);
        trapNumText.setText("X"+trapNum);
        wallNumText.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 25));
        trapNumText.setFont(Font.font("Impact", FontWeight.SEMI_BOLD, 25));

    }

    // Listener on OnDragDetected
    // Activated when the wall on the menu bar is dragged
    public void addWall(){

        Image wallImg = new Image(getClass().getResourceAsStream("brickwall.jpg"));
        wall.getScene().setCursor(new ImageCursor(wallImg));
        Dragboard db = wall.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("W");
        db.setContent(cb);

        this.wallNum --;
        wallNumText.setText("X"+wallNum);
        if(this.wallNum == 0){
            wall.setDisable(true);
        }

        //cb.putFiles(wall.)
    }

    // Activated when the trap on the menu bar is dragged
    public void addTrap(){

        Image trapImg = new Image(getClass().getResourceAsStream("trap.png"));
        trap.getScene().setCursor(new ImageCursor(trapImg));
        Dragboard db = trap.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("T");
        db.setContent(cb);
        trapNum--;
        trapNumText.setText("X"+trapNum);
        if(this.trapNum == 0){
            trap.setDisable(true);
        }
        //trap.getScene().setCursor(new ImageCursor(trapImg));
        //trap.setCursor(new ImageCursor(trapImg));

    }

    // Reset the power-up when opening a new game
    public void resetPowerup(){
        wallNum = 8;
        wallNumText.setText("X"+wallNum);
        trapNum = 8;
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
