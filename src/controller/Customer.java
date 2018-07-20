package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/12/2018
 * Project: TeslaRentalInventory
 **/
public class Customer implements Initializable {

    @FXML
    private Circle imgCustomerPhoto;

    @FXML
    private JFXToggleButton btnEditMode;

    @FXML
    private Label customerID;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextField txtFName;

    @FXML
    private JFXTextField txtLName;

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

        //Tooltip will be activated on Customer's photo if hovered
        Tooltip tooltip = new Tooltip("Double Click to Change Avatar in 'Edit Mode'");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if (btnEditMode.isSelected() && event.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose Photo");
                fc.showOpenDialog(btnEditMode.getScene().getWindow());
            }
        });
    }

    //This method will toggle edit mode on/off in customer layout

    @FXML
    void btnEditModeToggle(ActionEvent event) {
        if(btnEditMode.isSelected()) {
            phone.setEditable(true);
            txtFName.setEditable(true);
            txtLName.setEditable(true);
            address.setEditable(true);
            email.setEditable(true);
            gender.setEditable(true);
        } else {
            btnEditMode.setStyle("");
            phone.setEditable(false);
            txtFName.setEditable(false);
            txtLName.setEditable(false);
            address.setEditable(false);
            email.setEditable(false);
            gender.setEditable(false);
        }
    }
}
