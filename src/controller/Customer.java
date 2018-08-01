package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import sample.DBConnection;
import sample.Dialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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
    private Label customerID, lblSearchResults, lblMode;

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
    private JFXButton btnAddNew, btnSave;

    @FXML
    private JFXButton btnPurchases;

    @FXML
    private JFXButton btnRentals, btnDelete;

    @FXML
    private FontAwesomeIconView btnAddIcon;

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
    private static boolean addFlag = false;
    private static String imgPath = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(LogIn.loggerAccessLevel.equals("Admin"))
            btnDelete.setDisable(false);

        TextFields.bindAutoCompletion(txtSearch, customerNames); //Auto complete field is set now
        initialView();
    }

    private void initialView() {
        imgPath = null;
        customerListPane.setVisible(false); //Initially customer list view is set as not visible

        recordIndex = 0; //Resetting record index
        recordSize = customersList.size();

        //Tooltip will be activated on Customer's photo if hovered
        Tooltip tooltip = new Tooltip("Double Click to Change Avatar in 'Edit Mode'");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if (btnEditMode.isSelected() && event.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose Photo");

                File imgFile = fc.showOpenDialog(btnEditMode.getScene().getWindow());

                imgPath = imgFile.toURI().toString();

                if(imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".gif") ||imgPath.contains(".jpeg")) {
                    ImagePattern gg = new ImagePattern(new Image(imgPath));
                    imgCustomerPhoto.setFill(gg);
                } else {
                    new Dialog("File Format Error!", "Please select a valid image file. You can select JPG, JPEG, PNG, GIF");
                }
            }
        });

        //Setting next entry if any on next button action
        btnNextEntry.setOnAction(event -> {
            onView = customersList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            if (recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);
            btnPrevEntry.setDisable(false);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = customersList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            btnNextEntry.setDisable(false);
            if (recordIndex == 0)
                btnPrevEntry.setDisable(true);

        });


        btnNextEntry.setDisable(true); //For purpose ;)

        if (recordSize > 0) {
            onView = customersList.get(recordIndex); //Setting value for current record

            //Setting customer default fields
            recordNavigator();

            //Setting page indexer value
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");

            if (recordSize > 1) {
                btnNextEntry.setDisable(false); //Next entry will be enabled if there is more than one entry
            }
        }

        btnPrevEntry.setDisable(true); //Disabling prevButton Initially
    }

    //This method will toggle edit mode on/off in customer layout

    @FXML
    void btnEditModeToggle(ActionEvent event) {
        if (btnEditMode.isSelected()) {
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

        //Setting Image
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/resource/icons/user.png"));
            imgCustomerPhoto.setFill(img);
        } else {
            try {
                imgPath = onView.getPhoto();
                ImagePattern img = new ImagePattern(new Image(imgPath));
                imgCustomerPhoto.setFill(img);
            } catch (Exception e) {
                //Fallback photo: this will be applied if photo not found or remove in the directory specified directory
                ImagePattern img = new ImagePattern(new Image("/resource/icons/user.png"));
                imgCustomerPhoto.setFill(img);
            }
        }

        if (onView.getGender().equals("Male"))
            radioMale.setSelected(true);
        else
            radioFemale.setSelected(true);

    }

    @FXML
    void showPurchases(ActionEvent event) {
        try {
            CustomerPurchase.customerID = Integer.valueOf(customerID.getText());
            Parent pur = FXMLLoader.load(getClass().getResource("/fxml/customerpurchase.fxml"));
            Scene s = new Scene(pur);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showrentals(ActionEvent event) {
        try {
            CustomersRental.customerID = Integer.valueOf(customerID.getText());
            Parent rent = FXMLLoader.load(getClass().getResource("/fxml/customerrentals.fxml"));
            Scene s = new Scene(rent);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    void btnDelAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/x-button.png").toString()));

        alert.setHeaderText("Do you really want to delete this entry?");
        alert.setContentText("Press OK to confirm, Cancel to go back");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM  customers WHERE customerID = "+Integer.valueOf(customerID.getText()));
                ps.executeUpdate();
                reloadRecords();

                new Dialog("Operation Successful.", "Item is deleted from the database. Restart or refresh to see effective result.");
            } catch (SQLException e) {
                if(e.getErrorCode() == 1451) {
                    new Dialog("Constraint Error", "Customer has accounts & may be transactions. You need to delete them first in Admin Panel if you want to delete this entry");
                }

            }
        }
    }

    private void reloadRecords() {
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement getCustomerList = connection.prepareStatement("SELECT * FROM customers");
            ResultSet customerResultSet = getCustomerList.executeQuery();

            customersList.clear();

            while(customerResultSet.next()) {
                customersList.add(new sample.Customer(
                        customerResultSet.getInt(1),
                        customerResultSet.getString(2),
                        customerResultSet.getString(3),
                        customerResultSet.getString(4),
                        customerResultSet.getString(5),
                        customerResultSet.getString(6),
                        customerResultSet.getString(7),
                        customerResultSet.getString(8),
                        customerResultSet.getString(9),
                        customerResultSet.getDate(10)));;
            }

            initialView();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                while (customerResultSet.next()) {
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
                    lblSearchResults.setText(recordSize + " results found!");
                    lblSearchResults.setVisible(true);
                    initialView();

                    con.close();
                }

            } catch (NumberFormatException eN) {

                try {
                    PreparedStatement preparedStatement2 = con.prepareStatement(nameSQL);
                    preparedStatement2.setString(1, "%" + txtSearch.getText() + "%");
                    preparedStatement2.setString(2, "%" + txtSearch.getText() + "%");

                    ResultSet customerResultSet2 = preparedStatement2.executeQuery();

                    //Getting values from customers result set
                    while (customerResultSet2.next()) {
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
                        lblSearchResults.setText(recordSize + " results found!");
                        lblSearchResults.setVisible(true);
                        initialView();

                        con.close();
                    }

                } catch (SQLException eS2) {

                    new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + eS2.getErrorCode());
                }

            } catch (SQLException eS) {
                new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + eS.getErrorCode());
            }
        }

    }

    @FXML
    void btnAddMode(ActionEvent event) {
        if(addFlag) {
            addFlag = false; //Resetting addFlag value.
            btnAddIcon.setGlyphName("PLUS");

            //Enabling other buttons
            btnPrevEntry.setDisable(false);
            btnNextEntry.setDisable(false);
            btnSearch.setDisable(false);
            btnRentals.setDisable(false);
            btnPurchases.setDisable(false);
            btnLViewAllCustomers.setDisable(false);
            btnEditMode.setSelected(false);

            String defColor = "#263238";

            //Changing Focus Color
            txtFName.setUnFocusColor(Color.web(defColor));
            txtLName.setUnFocusColor(Color.web(defColor));
            address.setUnFocusColor(Color.web(defColor));
            phone.setUnFocusColor(Color.web(defColor));
            email.setUnFocusColor(Color.web(defColor));

            //Setting Label
            lblMode.setText("Navigation Mode");

            reloadRecords();

            btnEditModeToggle(new ActionEvent());

        } else {
            Connection con = DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT max(customerID) FROM customers");
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    customerID.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
                }

                addFlag = true; //Setting flag true to enable exit mode
                btnAddIcon.setGlyphName("UNDO"); //Changing glyph

                //Setting Label
                lblMode.setText("Entry Mode");

                //Disabling other buttons
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnRentals.setDisable(true);
                btnPurchases.setDisable(true);
                btnLViewAllCustomers.setDisable(true);
                btnSearch.setDisable(true);
                btnEditMode.setSelected(true);

                //Cleaning fields
                txtFName.setText("");
                txtLName.setText("");
                address.setText("");
                phone.setText("");
                email.setText("");
                memberSince.setText(LocalDate.now().toString());
                ImagePattern img = new ImagePattern(new Image("/resource/icons/user.png"));
                imgCustomerPhoto.setFill(img);
                imgPath = null;

                btnEditModeToggle(new ActionEvent()); //Changing mode into entry mode.. all fields will be available to edit

            } catch (SQLException e) {
                new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }
    }

    @FXML
    void saveEntry(ActionEvent event) {
        if (addFlag) {
            //addFlag = false;
            boolean entryFlag = true;
            if (txtFName.getText().equals("")) {
                txtFName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(txtLName.getText().equals("")) {
                txtLName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(address.getText().equals("")) {
                address.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(phone.getText().equals("")) {
                phone.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(email.getText().equals("")) {
                email.setUnFocusColor(Color.web("red"));
                entryFlag = false;;
            }

            if(entryFlag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Entry");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/question (2).png").toString()));

                alert.setHeaderText("Do you want to add this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    Connection con = DBConnection.getConnection();
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO customers VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, Integer.valueOf(customerID.getText()));
                        ps.setString(2, txtFName.getText());
                        ps.setString(3, txtLName.getText());
                        ps.setString(4, address.getText());
                        ps.setString(5, phone.getText());
                        ps.setString(6, email.getText());
                        ps.setString(7, "null");
                        ps.setString(8, imgPath);
                        if(radioMale.isSelected()) {
                            ps.setString(9, "Male");
                        } else if(radioFemale.isSelected()) {
                            ps.setString(9, "Female");
                        }

                        ps.setDate(10, Date.valueOf(LocalDate.now()));

                        ps.executeUpdate();

                        new Dialog("Operation Successful!", "New Customer Added!");

                    } catch (SQLException e) {
                        new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                    }
                }
            } else {
                JFXSnackbar snackbar = new JFXSnackbar(customerPane);
                snackbar.show("One or more fields are empty!", 3000);
            }
        } else {
            boolean entryFlag = true;
            if (txtFName.getText().equals("")) {
                txtFName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(txtLName.getText().equals("")) {
                txtLName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(address.getText().equals("")) {
                address.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(phone.getText().equals("")) {
                phone.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(email.getText().equals("")) {
                email.setUnFocusColor(Color.web("red"));
                entryFlag = false;;
            }

            if(entryFlag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to update this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    Connection con = DBConnection.getConnection();
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE customers SET customerID = ?, firstName = ?, lastName = ?, address = ?," +
                                "phone = ?, email = ?, details = ?, photo = ?, gender = ?, memberSince = ? WHERE customerID =" + Integer.valueOf(customerID.getText()));
                        ps.setInt(1, Integer.valueOf(customerID.getText()));
                        ps.setString(2, txtFName.getText());
                        ps.setString(3, txtLName.getText());
                        ps.setString(4, address.getText());
                        ps.setString(5, phone.getText());
                        ps.setString(6, email.getText());
                        ps.setString(7, "null");
                        ps.setString(8, imgPath);
                        if (radioMale.isSelected()) {
                            ps.setString(9, "Male");
                        } else if (radioFemale.isSelected()) {
                            ps.setString(9, "Female");
                        }
                        ps.setDate(10, Date.valueOf(LocalDate.now()));

                        ps.executeUpdate();

                        new Dialog("Operation Successful!", "The record is updated!");
                        reloadRecords();

                    } catch (SQLException e) {
                        e.printStackTrace();
                        new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                    }
                }
            }

        }
    }
}