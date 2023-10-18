package com.example.tombstonetussle;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

// Represents the view for the menu area in the game, extending the AnchorPane component
public class MenuAreaView extends AnchorPane {

    private AnchorPane anchorPane; // AnchorPane to hold the loaded FXML content

    // Constructor for the MenuAreaView class
    public MenuAreaView(){
        try {
            // Attempt to load the associated FXML layout for the menu area
            FXMLLoader.load(getClass().getResource("menuArea.fxml"));
        } catch (IOException e) {
            // If there's an error in loading the FXML file, throw a runtime exception
            throw new RuntimeException(e);
        }
    }
    //FXMLLoader.load(getClass().getResource("/menuArea.fxml"));
}
