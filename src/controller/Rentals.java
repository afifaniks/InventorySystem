package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import sample.DBConnection;
import sample.Dialog;
import sample.Purchase;
import sample.Rent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/16/2018
 * Project: TeslaRentalInventory
 **/
public class Rentals implements Initializable {

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private Label lblCategory;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXTextField txtCustomerId;

    @FXML
    private Label lblId;

    @FXML
    private JFXTextField txtItemId;

    @FXML
    private JFXDatePicker txtRentalDate;

    @FXML
    private Label lblCost;

    @FXML
    private JFXTextField txtPayAmount;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private JFXButton btnProcced;

    @FXML
    private JFXDatePicker txtReturnDate;

    @FXML
    private JFXButton btnRentalReturned;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private TableView<Rent> tblRecent;

    @FXML
    private TableColumn<Rent, Integer> rentID;

    @FXML
    private TableColumn<Rent, Integer> cusID;

    @FXML
    private TableColumn<Rent, Integer> itemID;

    @FXML
    private TableColumn<Rent, String> rentalDate;

    @FXML
    private TableColumn<Rent, String> returnDate;

    @FXML
    private TableColumn<Rent, Double> paid;

    @FXML
    private TableColumn<Rent, Double> due;

    @FXML
    private FontAwesomeIconView btnIcon;

    private static boolean startTransaction = false;
    public static ObservableList<Rent> rentalList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
    //The field will be initiated in Initializer class
    public static ArrayList<String> inventoryItem = null;
    public static ArrayList<Integer> customerID = null;
    public static ArrayList<Integer> itemIDForRent = null;

    @FXML
    void ctrlRentalReturnedAction(ActionEvent event) {
        lblCategory.setText("Rental Return");
    }


    @FXML
    void ctrlRefreshAction(ActionEvent event) {
        txtRentalDate.setValue(LocalDate.now());

    }

    @FXML
    void btnProceedAction(ActionEvent event) {
        if (startTransaction) {
            startTransaction = false; //Resetting Transaction Value
            btnIcon.setGlyphName("QUESTION");
            Tooltip tooltip = new Tooltip("Verify Input");
            btnProcced.setTooltip(tooltip);

            //Loading Transaction Window
            //Setting Values in Transaction Window
            Transaction.cusID = Integer.valueOf(txtCustomerId.getText());
            Transaction.rentalId = lblId.getText();
            Transaction.itemID = Integer.valueOf(txtItemId.getText());
            Transaction.rentalReturnDate = txtReturnDate.getValue();
            Transaction.rentOrSale = false;

            Double paid = 0.0;

            //Checking if input is valid
            try {
                if(txtPayAmount.getText().equals(""))
                    paid = 0.0;
                else
                    paid = Double.valueOf(txtPayAmount.getText());

                Transaction.payAmount = paid;
                Transaction.due = Double.valueOf(lblCost.getText()) - paid;

                //Checking for accounnts
                Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) from accounts, customers where customerID = Customers_customerID AND Customers_customerID = "+Integer.valueOf(txtCustomerId.getText()));
                ResultSet rs = preparedStatement.executeQuery();

                int chk = 0;

                while(rs.next()) {
                    chk = rs.getInt(1);
                }
                if(chk <= 0) {
                    new Dialog("No Account!", "Customer has no account. Please create an account first then try again.");
                } else {
                    Parent trPanel = FXMLLoader.load(getClass().getResource("/fxml/transaction.fxml"));
                    Scene trScene = new Scene(trPanel);
                    Stage trStage = new Stage();
                    trStage.setScene(trScene);
                    trStage.setResizable(false);
                    trStage.show();
                }

                connection.close();

            } catch (Exception e) {
                txtPayAmount.setUnFocusColor(Color.web("red"));
                JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                snackbar.show("Invalid Input Format", 3000);
            }
        } else {
            boolean flag = true;

            String customer = txtCustomerId.getText();
            String item = txtItemId.getText();

            try {
                txtCustomerId.setText(Integer.valueOf(customer).toString());
                //Searching if the ID is valid
                if (customerID.indexOf(Integer.valueOf(txtCustomerId.getText())) == -1) {
                    flag = false;
                    txtCustomerId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Customer ID", 3000);
                }
            } catch (Exception e) {
                flag = false;
                try {
                    txtCustomerId.setText(customer.substring(0, customer.indexOf('|') - 1));
                    flag = true;
                    //Searching if the ID is valid
                    if (customerID.indexOf(Integer.valueOf(txtCustomerId.getText())) == -1) {
                        flag = false;
                        txtCustomerId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Invalid Customer ID", 3000);
                    }
                } catch (Exception ex) {
                    flag = false;
                    txtCustomerId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }
            }

            try {
                txtItemId.setText(Integer.valueOf(item).toString());
                if (itemIDForRent.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
                    flag = false;
                    txtItemId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Item ID is not listed", 3000);
                }
            } catch (Exception e) {
                flag = false;
                try {
                    txtItemId.setText(item.substring(0, item.indexOf('|') - 1));
                    flag = true;
                    if (itemIDForRent.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
                        flag = false;
                        txtItemId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Item ID is not listed", 3000);
                    }
                } catch (Exception ex) {
                    flag = false;
                    txtItemId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }
            }

            Long days = null;

            if(txtReturnDate.getValue() == null) {
                flag = false;
                JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                snackbar.show("Return Date Required", 3000);
            } else {
                //Calculating Item Rental in days
                days = ChronoUnit.DAYS.between(txtRentalDate.getValue(), txtReturnDate.getValue()); //Calculates total days for the item being rented

                if(days < 0) {
                    flag = false;
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Return Date can not be earlier than Rental Date!", 3000);

                }
            }

            if (flag) {
                //Getting rental rate from database for the item
                Connection connection = DBConnection.getConnection();
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT rentRate FROM item WHERE itemID = "+Integer.valueOf(txtItemId.getText()));
                    ResultSet resultSet = ps.executeQuery();

                    Long cost = null;

                    while(resultSet.next()) {
                        cost = resultSet.getInt(1) * days;
                    }

                    lblCost.setText(cost.toString());
                    btnIcon.setGlyphName("CHECK");
                    startTransaction = true;
                    Tooltip tooltip = new Tooltip("Proceed to Transaction");
                    btnProcced.setTooltip(tooltip);

                } catch (SQLException e) {
                    new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                }

            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextFields.bindAutoCompletion(txtCustomerId, customerIDName);
        TextFields.bindAutoCompletion(txtItemId, inventoryItem);

        Tooltip tooltip = new Tooltip("Verify Input");
        btnProcced.setTooltip(tooltip);

        txtRentalDate.setValue(LocalDate.now()); //Initializing with current date

        //Getting new connection to db to set new rentalID
        Connection con = DBConnection.getConnection();

        try{
            PreparedStatement ps = con.prepareStatement("SELECT MAX(rentalID) FROM rentals");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Setting up table columns
        rentID.setCellValueFactory(new PropertyValueFactory<>("rentID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        rentalDate.setCellValueFactory(new PropertyValueFactory<>("rentDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        paid.setCellValueFactory(new PropertyValueFactory<>("payAmoun"));
        due.setCellValueFactory(new PropertyValueFactory<>("amountDue"));

        tblRecent.setItems(rentalList);
    }

}
