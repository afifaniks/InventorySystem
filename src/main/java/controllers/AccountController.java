package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.java.others.Account;
import org.controlsfx.control.textfield.TextFields;
import main.java.others.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/16/2018
 * Project: TeslaRentalInventory
 **/
public class AccountController implements Initializable {
    @FXML
    private JFXTextField txtAccountID;
    @FXML
    private Label lblId, lblS, lblSearchResults;
    @FXML
    private JFXTextField txtCustomerID;
    @FXML
    private Label lblProcessedBy;
    @FXML
    private TableView<Account> tblRecent;
    @FXML
    private TableColumn<Account, String> customer;
    @FXML
    private TableColumn<Account, Integer> accID;
    @FXML
    private TableColumn<Account, String> accName;
    @FXML
    private TableColumn<Account, String> payMethod;
    @FXML
    private JFXTextField txtPayMethod;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private AnchorPane accountPane;
    @FXML
    private FontAwesomeIconView btnSearchIcon;
    public static ObservableList<Account> accountList;
    public static ObservableList<Account> tempList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
    public static ArrayList<String> accountNames = null;
    private static boolean searchDone = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting EmployeeName
        lblProcessedBy.setText(LogInController.loggerUsername);
        TextFields.bindAutoCompletion(txtCustomerID, customerIDName);
        TextFields.bindAutoCompletion(txtSearch, accountNames);

        customer.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        accID.setCellValueFactory(new PropertyValueFactory<>("accID"));
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        payMethod.setCellValueFactory(new PropertyValueFactory<>("paymethod"));

        tblRecent.setItems(accountList);

        initiate();
    }

    private void initiate() {

        //Getting highest account ID to set the next
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT max(acccountID) FROM accounts");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }
        } catch (SQLException e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

        //Resetting fields
        txtAccountID.setText("");
        txtCustomerID.setText("");
        txtPayMethod.setText("");

    }

    @FXML
    void btnSearchAction(ActionEvent event) {
        if (searchDone) {
            searchDone = false;
            lblSearchResults.setVisible(false);
            tblRecent.getItems().clear();
            accountList = tempList; //Reassigning customers List
            tblRecent.setItems(accountList);
            btnSearch.setTooltip(new Tooltip("Search with customers name or id"));
            btnSearchIcon.setGlyphName("SEARCH");
        } else {
            Connection con = DBConnection.getConnection();

            String SQL = "SELECT  customers.firstName, customers.lastName, accounts.acccountID, accounts.accountName, accounts.paymethod " +
                    "FROM accounts, customers WHERE accounts.Customers_customerID = customerID AND accountName COLLATE UTF8_GENERAL_CI like ?";

            ObservableList<Account> searchResult = FXCollections.observableArrayList(); //list to hold search result
            try {
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setString(1, "%" + txtSearch.getText() +"%");
                ResultSet accountResultSet = ps.executeQuery();
                while (accountResultSet.next()) {
                    searchResult.add(new Account(accountResultSet.getInt(3),
                            accountResultSet.getString(1) + " " + accountResultSet.getString(2),
                            accountResultSet.getString(4),
                            accountResultSet.getString(5)));
                    accountNames.add(accountResultSet.getString(4));
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (searchResult.size() <= 0) {
                    lblSearchResults.setText("No Results Found!");
                    lblSearchResults.setVisible(true);
                } else {
                tempList = FXCollections.observableArrayList(accountList);
                searchDone = true;
                btnSearchIcon.setGlyphName("CLOSE");
                btnSearch.setTooltip(new Tooltip("Reset Full List"));
                accountList = searchResult; //Assigning search result to customerList

                lblSearchResults.setText(searchResult.size() + " results found!");
                tblRecent.getItems().clear();
                tblRecent.setItems(accountList);
                lblSearchResults.setVisible(true);
            }
        }

    }

    @FXML
    void reloadAll(ActionEvent event) {
        ObservableList<Account> accountListByUser = FXCollections.observableArrayList();

        Connection connection = DBConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT max(acccountID) FROM accounts");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }

            PreparedStatement getAccountList = connection.prepareStatement("SELECT  customers.firstName, customers.lastName, accounts.acccountID, accounts.accountName, accounts.paymethod " +
                    "FROM accounts, customers WHERE User_username ='"
                    + LogInController.loggerUsername+"' AND Customers_customerID = customerID");

            ResultSet accountResultSet = getAccountList.executeQuery();

            while(accountResultSet.next()) {
                accountListByUser.add(new Account(accountResultSet.getInt(3),
                        accountResultSet.getString(1) + " " + accountResultSet.getString(2),
                        accountResultSet.getString(4),
                        accountResultSet.getString(5)));
            }

            accountList.clear();
            tblRecent.getItems().clear();
            accountList = accountListByUser;
            tblRecent.setItems(accountList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnAddAction(ActionEvent event) {
        boolean flag = true;

        if(txtAccountID.getText().equals("")) {
            flag = false;
            txtAccountID.setUnFocusColor(Color.web("red"));
        }
        if(txtCustomerID.getText().equals("")) {
            flag = false;
            txtCustomerID.setUnFocusColor(Color.web("red"));
        }
        if(txtPayMethod.getText().equals("")) {
            flag = false;
            txtPayMethod.setUnFocusColor(Color.web("red"));
        }

        if(!flag){
            JFXSnackbar snackbar = new JFXSnackbar(accountPane);
            snackbar.show("Fields required!", 3000);
        } else {
            Connection con = DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, Integer.valueOf(lblId.getText()));
                ps.setString(2, txtAccountID.getText());
                ps.setInt(3, Integer.valueOf(txtCustomerID.getText().substring(0, txtCustomerID.getText().indexOf('|') - 1)));
                ps.setString(4, LogInController.loggerUsername);
                ps.setString(5, txtPayMethod.getText());

                ps.executeUpdate();

                String defColor = "#263238";

                txtPayMethod.setUnFocusColor(Color.web(defColor));
                txtCustomerID.setUnFocusColor(Color.web(defColor));
                txtAccountID.setUnFocusColor(Color.web(defColor));

                new PromptDialogController("Operation Successful!", "Account successfully created and stored into database.");

                initiate();
            } catch (SQLException e) {
                new PromptDialogController("Error!", "Invalid Argument.");
            }
        }
    }

}
