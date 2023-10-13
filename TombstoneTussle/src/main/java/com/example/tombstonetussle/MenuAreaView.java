package com.example.tombstonetussle;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MenuAreaView extends AnchorPane {
    private AnchorPane anchorPane;
    public MenuAreaView(){
        try {
            FXMLLoader.load(getClass().getResource("menuArea.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //FXMLLoader.load(getClass().getResource("/menuArea.fxml"));
}
