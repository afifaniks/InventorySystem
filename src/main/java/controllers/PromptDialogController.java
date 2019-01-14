package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class PromptDialogController {
    /**
     * Constructor will pop up a new stage which will contain
     * type of error/notification(header) and its details.
     * @param header : Prompt headline
     * @param error : Description message of prompt
     */
    public PromptDialogController(String header, String error) {

        Stage stg = new Stage();
        stg.setAlwaysOnTop(true);

        //Modality is so that this window must be interacted before others
        stg.initModality(Modality.APPLICATION_MODAL);
        stg.setResizable(false);

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/main/resources/view/dialog.fxml"));
            Scene s = new Scene(root);

            //Getting useful nodes from FXML to set error report
            Label lblHeader = (Label) root.lookup("#lblHeader");
            JFXTextArea txtError = (JFXTextArea) root.lookup("#txtError");
            JFXButton btnClose = (JFXButton) root.lookup("#btnClose");

            lblHeader.setText(header);
            txtError.setText(error);

            //Setting close button event
            btnClose.setOnAction(event -> {
                stg.hide();
            });

            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
