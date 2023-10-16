package com.example.tombstonetussle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuAreaController implements Initializable {

    @FXML
    private AnchorPane wall;
    @FXML
    private AnchorPane trap;
    @FXML
    private ImageView keyCommand;
    @FXML
    private ImageView powerCommand;
    private int wallNum = 5;
    private int trapNum = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image keyimg = new Image(getClass().getResourceAsStream("keyCommand.png"));
        keyCommand.setImage(keyimg);

    }

    // Listener on OnDragDetected
    // Activated when the wall on the menu bar is dragged
    public void addWall(){

        Image wallImg = new Image(getClass().getResourceAsStream("wall.jpg"));
        wall.getScene().setCursor(new ImageCursor(wallImg));
        Dragboard db = wall.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("W");
        this.wallNum --;
        db.setContent(cb);
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
        //trap.getScene().setCursor(new ImageCursor(trapImg));
        //trap.setCursor(new ImageCursor(trapImg));

    }

    public void wallDragDone(){
        //wall.setCursor(Cursor.DEFAULT);
    }

    public void trapDragDone(){
        //trap.getParent().getScene().setCursor(Cursor.DEFAULT);
    }



}
