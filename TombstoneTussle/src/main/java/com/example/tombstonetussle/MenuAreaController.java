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

    // Listener on OnDragDetected
    // Activated when the wall on the menu bar is dragged
    public void addWall(){

        Dragboard db = wall.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("W");
        db.setContent(cb);
        //cb.putFiles(wall.)
    }

    // Activated when the trap on the menu bar is dragged
    public void addTrap(){

        Dragboard db = trap.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString("T");
        db.setContent(cb);

    }


}
