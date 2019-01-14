package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import main.java.others.DBConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Author: Afif Al Mamun
 * Written on: 7/30/2018
 * Project: TeslaRentalInventory
 **/
public class TransactionController implements Initializable {
    @FXML
    private Label lblTrID;
    @FXML
    private Label lblCategory;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXButton btnProcced;
    @FXML
    private AnchorPane trPane;
    @FXML
    private FontAwesomeIconView btnIcon;
    @FXML
    private JFXDatePicker txtTrDate;
    @FXML
    private Label lblPurID;
    @FXML
    private JFXComboBox<String> cboChooseAccount;
    @FXML
    private JFXComboBox<String> cboTrType;
    @FXML
    private Label lblCusID, lblPurchaseOrRent;
    @FXML
    private Label lblAmounPaid;
    @FXML
    private Label lblDue;
    public static String cusName = null; //These static fields will be initiated from Sells or Purchases
    public static String purchaseId = null;
    public static String rentalId = null;
    public static Integer purchaseQty = null;
    public static Integer itemID = null;
    public static Integer cusID = null;
    public static Double payAmount = null;
    public static LocalDate rentalReturnDate = null;
    /**
     * rentOrSale is a field to distinguish between
     * a transaction type.
     * Field value will be set from RentalsController or SellsController class before starting transaction
     * True: If the transaction is for selling an item
     * False: If the transaction is for renting an item
     */
    public static boolean rentOrSale = false;
    public static Double due = null;
    private static TreeMap<String, Integer> trType = new TreeMap<>();
    public static Integer stock = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCancel.setOnAction(e -> {
            hideWindow();
        });

        loadTransactionWindowContents();
    }

    private void loadTransactionWindowContents() {
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT firstName, lastName FROM customers WHERE customerID =" + cusID);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                lblCusID.setText(rs.getString(1) + " " + rs.getString(2));
            }

            if (rentOrSale) { //Checking Last Tr Id from purchase
                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT max(trID) FROM financialtronpurchase");
                ResultSet rs1 = preparedStatement1.executeQuery();
                lblPurID.setText(purchaseId);

                while (rs1.next()) {
                    lblTrID.setText(Integer.valueOf(rs1.getInt(1) + 1).toString());
                }
            } else {
                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT max(trID) FROM financialtronrental");
                ResultSet rs1 = preparedStatement1.executeQuery();
                lblPurID.setText(rentalId);
                lblPurchaseOrRent.setText("Rental ID:");

                while (rs1.next()) { //Checking Last Tr Id from rent
                    lblTrID.setText(Integer.valueOf(rs1.getInt(1) + 1).toString());
                }
            }

            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM accounts WHERE Customers_customerID = "+cusID);

            ObservableList<String> accountList = FXCollections.observableArrayList();

            ResultSet rs2 = preparedStatement2.executeQuery();

            while(rs2.next()) {
                accountList.add(rs2.getString("accountName"));
            }

            cboChooseAccount.setItems(accountList); //Setting Accounts List
            cboChooseAccount.setValue(accountList.get(0));

            PreparedStatement preparedStatement3 = connection.prepareStatement("SELECT * FROM trtypecode");
            ResultSet rs3 = preparedStatement3.executeQuery();

            ObservableList<String> trTypeList = FXCollections.observableArrayList();

            while(rs3.next()) {
                trTypeList.add(rs3.getString(2));
                trType.put(rs3.getString(2), rs3.getInt(1));
            }

            cboTrType.setItems(trTypeList); //Setting TransactionController Types
            cboTrType.setValue(trTypeList.get(0));

            connection.close();

        } catch (SQLException e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

        lblAmounPaid.setText(payAmount.toString());
        lblDue.setText(due.toString());
        txtTrDate.setValue(LocalDate.now());
    }

    @FXML
    void btnProceedAction(ActionEvent event) {
        if (rentOrSale) {
            finalizePurchase(); // Calling method to finalize the purchase
        } else {
            finalizeRental(); // Calling method to finalize rent
        }
    }

    private void finalizePurchase() {
        Connection con = DBConnection.getConnection();

        //Updating Purchase
        try {
            PreparedStatement purchaseStmt = con.prepareStatement("INSERT INTO purchases VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            purchaseStmt.setInt(1, Integer.valueOf(purchaseId));
            purchaseStmt.setDate(2, Date.valueOf(LocalDate.now()));
            purchaseStmt.setInt(3, purchaseQty);
            purchaseStmt.setDouble(4, due);
            purchaseStmt.setString(5, LogInController.loggerUsername);
            purchaseStmt.setInt(6, itemID);
            purchaseStmt.setInt(7, cusID);
            purchaseStmt.setDouble(8, payAmount);

            purchaseStmt.executeUpdate();

            //Getting Account ID
            PreparedStatement acc = con.prepareStatement("SELECT acccountID FROM accounts WHERE accountName = '"+cboChooseAccount.getValue()+"'");
            ResultSet getAccID = acc.executeQuery();

            Integer accID = null;

            while(getAccID.next()) {accID = getAccID.getInt(1);}

            //Updating TransactionController Table
            PreparedStatement transactionStmt = con.prepareStatement("INSERT INTO financialtronpurchase VALUES(?, ?, ?, ?, ?, ?)");
            transactionStmt.setInt(1, Integer.valueOf(lblTrID.getText()));
            transactionStmt.setDate(2, Date.valueOf(LocalDate.now()));
            transactionStmt.setInt(3, accID);
            transactionStmt.setInt(4, trType.get(cboTrType.getValue()));
            transactionStmt.setInt(5, Integer.valueOf(purchaseId));
            transactionStmt.setString(6, LogInController.loggerUsername);

            transactionStmt.executeUpdate();

            hideWindow(); //Closing TransactionController Window

            new PromptDialogController("Transaction Complete!", "Purchase & Transaction are successful! " +
                    "You can close this dialog and start over.");

            // Updating stock of the item
            PreparedStatement updateStock = con.prepareStatement("UPDATE item SET stock = ? WHERE itemID = ?");
            updateStock.setInt(1, stock - purchaseQty); // NewStock = PreviousStock - PurchasedQty
            updateStock.setInt(2, itemID);
            updateStock.executeUpdate();

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    private void finalizeRental() {
        Connection con = DBConnection.getConnection();

        //Updating Purchase
        try {
            PreparedStatement purchaseStmt = con.prepareStatement("INSERT INTO rentals VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            purchaseStmt.setInt(1, Integer.valueOf(rentalId));
            purchaseStmt.setDate(2, Date.valueOf(LocalDate.now()));
            purchaseStmt.setDate(3, Date.valueOf(rentalReturnDate));
            purchaseStmt.setDouble(4, payAmount);
            purchaseStmt.setDouble(5, due);
            purchaseStmt.setString(6, LogInController.loggerUsername);
            purchaseStmt.setInt(7, itemID);
            purchaseStmt.setDouble(8, cusID);

            purchaseStmt.executeUpdate();

            //Getting Account ID
            PreparedStatement acc = con.prepareStatement("SELECT acccountID FROM accounts WHERE accountName = '"+cboChooseAccount.getValue()+"'");
            ResultSet getAccID = acc.executeQuery();

            Integer accID = null;

            while(getAccID.next()) {accID = getAccID.getInt(1);}

            //Updating TransactionController Table
            PreparedStatement transactionStmt = con.prepareStatement("INSERT INTO financialtronrental VALUES(?, ?, ?, ?, ?, ?)");
            transactionStmt.setInt(1, Integer.valueOf(lblTrID.getText()));
            transactionStmt.setDate(2, Date.valueOf(LocalDate.now()));
            transactionStmt.setInt(3, accID);
            transactionStmt.setInt(4, Integer.valueOf(rentalId));
            transactionStmt.setInt(5, trType.get(cboTrType.getValue()));
            transactionStmt.setString(6, LogInController.loggerUsername);

            transactionStmt.executeUpdate();

            hideWindow(); //Closing TransactionController Window

            new PromptDialogController("Transaction Complete!", "Rent & Transaction are successful! " +
                    "You can close this dialog and start over.");

            // Updating stock of the item
            PreparedStatement updateStock = con.prepareStatement("UPDATE item SET stock = ? WHERE itemID = ?");
            updateStock.setInt(1, stock - 1); // NewStock = PrevStock - 1 ; As we considered only one item will be rented once
            updateStock.setInt(2, itemID);
            updateStock.executeUpdate();

            con.close();
        } catch (SQLException e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    private void hideWindow() {
        trPane.getScene().getWindow().hide(); //Hiding window on action
    }
}
