package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.io.IOException;

/**
 * Author: Afif Al Mamun
 * Written on: 08-Jul-18
 * Project: TeslaRentalInventory
 **/
public class Base {

    @FXML
    private AnchorPane paneRight;

    @FXML
    private JFXButton btnCustomers;

    @FXML
    private JFXButton btnInventoryItem;

    @FXML
    private JFXButton btnAccounts;

    @FXML
    private JFXButton btnRentals;

    @FXML
    private JFXButton btnSells;

    private AnchorPane paneInventory = null;
    private JFXButton temp = null;
    private JFXButton recover = null;
    private final static String INVENTORY_URL = "/fxml/inventory.fxml";

    @FXML
    private void ctrlRightPane(String URL) throws IOException {
        try {
            paneInventory = FXMLLoader.load(getClass().getResource(URL));

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

    @FXML
    void btnNavigators(ActionEvent event) {
        borderSelector(event);

        JFXButton btn = (JFXButton)event.getSource();

        if(btn.getText().equals(btnInventoryItem.getText())) {
            try {
                ctrlRightPane(INVENTORY_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //This method will create a border around selected navigator
    //from left navigation pane and will remove it if another
    //navition button is clicked.

    private void borderSelector(ActionEvent event) {
        JFXButton btn = (JFXButton)event.getSource();

        if(temp == null) {
            temp = btn;
        } else {
            temp.setStyle("");
            temp = btn;
        }

        btn.setStyle("-fx-background-color: #455A64");
    }

}
