package com.example.tombstonetussle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class MenuAreaController {

    @FXML
    private AnchorPane wall;
    @FXML
    private AnchorPane trap;

    public void addWall(){
        Dragboard db = wall.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        //cb.putFiles(wall.)
    }

    public void addTrap(){

    }


}
