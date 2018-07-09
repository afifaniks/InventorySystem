package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Author: Afif Al Mamun
 * Written on: 08-Jul-18
 * Project: TeslaRentalInventory
 **/
public class Base {

    @FXML
    private AnchorPane paneRight;

    private AnchorPane paneInventory = null;

    @FXML
    private void test() throws IOException {
        try {
            paneInventory = FXMLLoader.load(getClass().getResource("/fxml/inventory.fxml"));

            paneInventory.setPrefHeight(paneRight.getHeight());
            paneInventory.setPrefWidth(paneRight.getWidth());

            paneRight.getChildren().add(paneInventory);

            //Listener to monitor any window size change
            paneRight.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
              autoResizePane();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method will resize right pane size
    //relative to it's parent whenever window is resized

    private void autoResizePane() {
        paneInventory.setPrefWidth(paneRight.getWidth());
        paneInventory.setPrefHeight(paneRight.getHeight());
    }

}
