package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class Dialog {

    //Constructor will pop up a new stage which will contain
    //type of error(header) and details(error).
    public Dialog(String header, String error) {

        Stage stg = new Stage();
        stg.setResizable(false);

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dialog.fxml"));
            Scene s = new Scene(root);

            //Getting useful nodes from FXML to set error report

            Label lblHeader = (Label) root.lookup("#lblHeader");
            JFXTextArea txtError = (JFXTextArea) root.lookup("#txtError");
            JFXButton btnClose = (JFXButton) root.lookup("#btnClose");

            lblHeader.setText(header);
            txtError.setText(error);

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
