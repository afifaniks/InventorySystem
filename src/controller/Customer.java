package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/12/2018
 * Project: TeslaRentalInventory
 **/
public class Customer implements Initializable {

    private static boolean editToggle = false;

    @FXML
    private Circle imgCustomerPhoto;

    @FXML
    private JFXButton btnEditMode;

    @FXML
    private JFXTextField customerID;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTextField address;

    @FXML
    private JFXTextField email;

    @FXML
    private JFXTextField gender;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImagePattern img = new ImagePattern(new Image("/resource/icons/10407479_1396350623998299_689954862227931112_n.jpg"));
        imgCustomerPhoto.setFill(img);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if(editToggle) {
                System.out.println("Yes");
            }
        });

    }

    @FXML
    void btnEditModeToggle(ActionEvent event) {
        if(editToggle) {
            editToggle = false;
            btnEditMode.setStyle("");
            customerID.setEditable(false);
            phone.setEditable(false);
            firstName.setEditable(false);
            lastName.setEditable(false);
            address.setEditable(false);
            email.setEditable(false);
            gender.setEditable(false);


        } else {
            editToggle = true;
            btnEditMode.setStyle("-fx-background-color: green");

            customerID.setEditable(true);
            phone.setEditable(true);
            firstName.setEditable(true);
            lastName.setEditable(true);
            address.setEditable(true);
            email.setEditable(true);
            gender.setEditable(true);
        }
    }
}
