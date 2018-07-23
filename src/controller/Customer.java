package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
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
    private Label customerID, memberSince;

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
    private JFXComboBox gender;

    @FXML
    private JFXButton btnPrevEntry;

    @FXML
    private JFXButton btnNextEntry;

    @FXML
    private Label lblPageIndex;

    private static int recordIndex = 0;
    private static int recordSize = 0;

    public static ObservableList<sample.Customer> customersList = FXCollections.observableArrayList(); //This field will auto set from Initializer Class
    private sample.Customer onView = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        recordIndex = 0; //Resetting record index
        recordSize = customersList.size();
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

        //Setting gender button
        gender.setItems(FXCollections.observableArrayList("Male", "Female"));

        onView = customersList.get(recordIndex); //Setting value for current record

        //Setting customer default fields
        recordNavigator();

        //Setting page indexer value
        lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize +" results.");

        //Setting next entry if any on next button action
        btnNextEntry.setOnAction(event -> {
            onView = customersList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize +" results.");
            if(recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = customersList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize +" results.");

            if(recordIndex == 0)
                btnPrevEntry.setDisable(true);

        });

        btnPrevEntry.setDisable(true);
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

    //This method will navigate between customer records

    private void recordNavigator() {
        customerID.setText(String.valueOf(onView.getId()));
        txtFName.setText(onView.getFirstName());
        txtLName.setText(onView.getLastName());
        address.setText(onView.getAddress());
        email.setText(onView.getEmail());
        phone.setText(onView.getPhone());
        memberSince.setText(onView.getDate().toString());
        gender.setValue(onView.getGender());

        //Re-initiating navigator button property
        btnPrevEntry.setDisable(false);
        btnNextEntry.setDisable(false);
 }
}
