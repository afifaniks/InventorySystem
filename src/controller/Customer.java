package controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane cusTomerPane;

    @FXML
    private AnchorPane customerPane;

    @FXML
    private JFXTextField txtFName;

    @FXML
    private JFXTextField txtLName;

    @FXML
    private JFXTextField address;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextField email;

    @FXML
    private Label memberSince;

    @FXML
    private JFXToggleButton btnEditMode;

    @FXML
    private JFXButton btnPrevEntry;

    @FXML
    private JFXButton btnNextEntry;

    @FXML
    private Label customerID;

    @FXML
    private Label lblPageIndex;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private Circle imgCustomerPhoto;

    @FXML
    private JFXRadioButton radioMale;

    @FXML
    private ToggleGroup gender;

    @FXML
    private JFXRadioButton radioFemale;

    @FXML
    private AnchorPane customerListPane;

    @FXML
    private JFXButton btnLViewAllCustomers, btnGoBack;

    @FXML
    private TableView<sample.Customer> tbl;

    @FXML
    private TableColumn<sample.Customer, Integer> columnID;

    @FXML
    private TableColumn<sample.Customer, String> columnFirstName;

    @FXML
    private TableColumn<sample.Customer, String> columnLastName;

    @FXML
    private TableColumn<sample.Customer, String> columnGender;

    @FXML
    private TableColumn<sample.Customer, String> columnAddress;

    @FXML
    private TableColumn<sample.Customer, String> columnPhone;

    @FXML
    private TableColumn<sample.Customer, String> columnEmail;



    private static int recordIndex = 0;
    private static int recordSize = 0;

    public static ObservableList<sample.Customer> customersList = FXCollections.observableArrayList(); //This field will auto set from Initializer Class
    private sample.Customer onView = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerListPane.setVisible(false); //Initially customer list view is set as not visible

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

        //Setting next entry if any on next button action
        btnNextEntry.setOnAction(event -> {
            onView = customersList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize +" results.");
            if(recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);
            btnPrevEntry.setDisable(false);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = customersList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize +" results.");
            btnNextEntry.setDisable(false);
            if(recordIndex == 0)
                btnPrevEntry.setDisable(true);

        });


        btnNextEntry.setDisable(true); //For purpose ;)

        if (recordSize > 0) {
            onView = customersList.get(recordIndex); //Setting value for current record

            //Setting customer default fields
            recordNavigator();

            //Setting page indexer value
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");

            if(recordSize > 1) {
                btnNextEntry.setDisable(false); //Next entry will be enabled if there is more than one entry
            }
        }

        btnPrevEntry.setDisable(true); //Disabling prevButton Initially

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
            radioFemale.setDisable(false);
            radioMale.setDisable(false);
        } else {
            btnEditMode.setStyle("");
            phone.setEditable(false);
            txtFName.setEditable(false);
            txtLName.setEditable(false);
            address.setEditable(false);
            email.setEditable(false);
            radioFemale.setDisable(true);
            radioMale.setDisable(true);
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

        if(onView.getGender().equals("Male"))
            radioMale.setSelected(true);
        else
            radioFemale.setSelected(true);

    }

    @FXML
    private void listAllCustomers(ActionEvent event) {
        //cusTomerPane.setVisible(false); //Hiding default customer view
        customerListPane.setVisible(true); //Showing total list
        customerPane.setVisible(false);

        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        tbl.setItems(customersList);

        btnGoBack.setOnAction(e -> {
            customerListPane.setVisible(false);
            customerPane.setVisible(true);
        });

    }
}
