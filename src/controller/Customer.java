package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import org.controlsfx.control.textfield.TextFields;
import sample.DBConnection;
import sample.Dialog;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private Label customerID, lblSearchResults;

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
    private FontAwesomeIconView btnSeachIcon;

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
    public static ObservableList<sample.Customer> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> customerNames = new ArrayList<>();
    private sample.Customer onView = null;
    private static boolean searchDone = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextFields.bindAutoCompletion(txtSearch, customerNames); //Auto complete field is set now
        initialView();
    }

    private void initialView() {
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
        } else {
            btnEditMode.setStyle("");
            phone.setEditable(false);
            txtFName.setEditable(false);
            txtLName.setEditable(false);
            address.setEditable(false);
            email.setEditable(false);
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


    @FXML
    void btnSearchAction(ActionEvent event) {
        if (searchDone) {
            searchDone = false;
            lblSearchResults.setVisible(false);
            customersList = tempList; //Reassigning customers List
            recordSize = customersList.size();
            btnSearch.setTooltip(new Tooltip("Search with customers name or id"));
            btnSeachIcon.setGlyphName("SEARCH");
            initialView();
        } else {
            Connection con = DBConnection.getConnection();

            String idSQL = "SELECT * FROM customers WHERE customerID = ?";
            String nameSQL = "SELECT * FROM customers WHERE firstName COLLATE UTF8_GENERAL_CI like ? OR lastName COLLATE UTF8_GENERAL_CI like ?";
            ObservableList<sample.Customer> searchResult = FXCollections.observableArrayList(); //list to hold search result

            try {
                //Checking if input field is a number then searching with ID
                Integer id = Integer.valueOf(txtSearch.getText());
                PreparedStatement preparedStatement = con.prepareStatement(idSQL);
                preparedStatement.setInt(1, id);

                ResultSet customerResultSet = preparedStatement.executeQuery();

                //Getting values from customers result set
                while(customerResultSet.next()) {
                    searchResult.add(new sample.Customer(
                            customerResultSet.getInt(1),
                            customerResultSet.getString(2),
                            customerResultSet.getString(3),
                            customerResultSet.getString(4),
                            customerResultSet.getString(5),
                            customerResultSet.getString(6),
                            customerResultSet.getString(7),
                            customerResultSet.getString(8),
                            customerResultSet.getString(9),
                            customerResultSet.getDate(10)));

                }

                if (searchResult.size() <= 0) {
                    lblSearchResults.setText("No Results Found!");
                    lblSearchResults.setVisible(true);
                } else {
                    tempList = FXCollections.observableArrayList(customersList);
                    searchDone = true;
                    btnSeachIcon.setGlyphName("CLOSE");
                    btnSearch.setTooltip(new Tooltip("Reset Full List"));
                    customersList = searchResult; //Assigning search result to customerList
                    recordSize = searchResult.size();
                    lblSearchResults.setText(Integer.valueOf(recordSize) + " results found!");
                    initialView();

                    con.close();
                }

            } catch(NumberFormatException eN) {

                try {
                    PreparedStatement preparedStatement2 = con.prepareStatement(nameSQL);
                    preparedStatement2.setString(1, "%" + txtSearch.getText() + "%");
                    preparedStatement2.setString(2, "%" + txtSearch.getText() + "%");

                    ResultSet customerResultSet2 = preparedStatement2.executeQuery();

                    //Getting values from customers result set
                    while(customerResultSet2.next()) {
                        searchResult.add(new sample.Customer(
                                customerResultSet2.getInt(1),
                                customerResultSet2.getString(2),
                                customerResultSet2.getString(3),
                                customerResultSet2.getString(4),
                                customerResultSet2.getString(5),
                                customerResultSet2.getString(6),
                                customerResultSet2.getString(7),
                                customerResultSet2.getString(8),
                                customerResultSet2.getString(9),
                                customerResultSet2.getDate(10)));

                    }

                    if (searchResult.size() <= 0) {
                        lblSearchResults.setText("No Results Found!");
                        lblSearchResults.setVisible(true);
                    } else {
                        tempList = FXCollections.observableArrayList(customersList);
                        searchDone = true;
                        btnSeachIcon.setGlyphName("CLOSE");
                        btnSearch.setTooltip(new Tooltip("Reset Full List"));
                        customersList = searchResult; //Assigning search result to customerList
                        recordSize = searchResult.size();
                        lblSearchResults.setText(Integer.valueOf(recordSize) + " results found!");
                        initialView();

                        con.close();
                    }

                } catch (SQLException eS2) {

                    eS2.printStackTrace();
                }

            } catch (SQLException eS) {
                new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + eS.getErrorCode());
            }
        }

    }
}
