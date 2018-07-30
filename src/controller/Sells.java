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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import sample.DBConnection;
import sample.Purchase;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/
public class Sells implements Initializable{
    @FXML
    private JFXTextField txtCustomerId;

    @FXML
    private Label lblId;

    @FXML
    private JFXTextField txtItemId;

    @FXML
    private JFXDatePicker txtDate;

    @FXML
    private Label lblCost;

    @FXML
    private JFXTextField txtPayAmount, txtQty;

    @FXML
    private JFXButton btnProcced;

    @FXML
    private FontAwesomeIconView btnIcon;

    @FXML
    private TableView<Purchase> tblRecent;


    @FXML
    private TableColumn<Purchase, Integer> purID;

    @FXML
    private TableColumn<Purchase, Integer> cusID;

    @FXML
    private TableColumn<Purchase, Integer> itemID;

    @FXML
    private TableColumn<Purchase, String> date;

    @FXML
    private TableColumn<Purchase, Integer> qty;

    @FXML
    private TableColumn<Purchase, Double> paidAmmount;

    @FXML
    private TableColumn<Purchase, Double> dueAmount;

    @FXML
    private AnchorPane rightPane;

    public static ObservableList<Purchase> purchaseList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
                                                    //The field will be initiated in Initializer class
    public static ArrayList<String> inventoryItem = null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtQty.setText("1");
        txtDate.setValue(LocalDate.now());

        //Getting new connection to db to set new purchaseID
        Connection con = DBConnection.getConnection();

        try{
            PreparedStatement ps = con.prepareStatement("SELECT MAX(purchaseID) FROM purchases");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextFields.bindAutoCompletion(txtCustomerId, customerIDName);
        TextFields.bindAutoCompletion(txtItemId, inventoryItem);

        //Setting up table on load
        purID.setCellValueFactory(new PropertyValueFactory<>("purID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paidAmmount.setCellValueFactory(new PropertyValueFactory<>("paid"));
        dueAmount.setCellValueFactory(new PropertyValueFactory<>("due"));

        tblRecent.setItems(purchaseList);
    }

    @FXML
    void btnProceedAction(ActionEvent event) {
        boolean flag = true;

        if (!txtQty.getText().equals("") && Integer.parseInt(txtQty.getText()) > 0) {

            String customer = txtCustomerId.getText();

            txtItemId.setUnFocusColor(Color.web("#263238"));
            txtCustomerId.setUnFocusColor(Color.web("#263238"));
            txtQty.setUnFocusColor(Color.web("#263238"));

            try {
                txtCustomerId.setText(Integer.valueOf(customer).toString());
            } catch (Exception e) {
                flag = false;
                try {
                    txtCustomerId.setText(customer.substring(0, customer.indexOf('|') - 1));
                    flag = true;
                } catch (Exception en) {
                    flag = false;
                    txtCustomerId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }

            }

            String item = txtItemId.getText();

            try {
                txtItemId.setText(Integer.valueOf(item).toString());
            } catch (Exception e) {
                flag = false;
                try {
                    txtItemId.setText(item.substring(0, item.indexOf('|') - 1));
                    flag = true;
                } catch (Exception en) {
                    flag = false;
                    txtItemId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }
            }

            if(flag) {
                Connection con = DBConnection.getConnection();
                try {
                    PreparedStatement ps = con.prepareStatement("SELECT salePrice FROM item WHERE itemID = " + Integer.valueOf(txtItemId.getText()));
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        lblCost.setText(String.valueOf(Integer.valueOf(txtQty.getText()) * rs.getDouble(1)));
                    }
                } catch (SQLException e) {
                    flag = false;
                    e.printStackTrace();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            flag = false;
            txtQty.setUnFocusColor(Color.web("red"));
            JFXSnackbar snackbar = new JFXSnackbar(rightPane);
            snackbar.show("Quantity field can not be null or 0", 3000);
        }

        if(flag) {
            btnIcon.setGlyphName("CHECK");
            //Loading Transaction Window
            try {
                Parent trPanel = FXMLLoader.load(getClass().getResource("/fxml/transaction.fxml"));
                Scene trScene = new Scene(trPanel);
                Stage trStage = new Stage();
                trStage.setScene(trScene);
                trStage.initModality(Modality.APPLICATION_MODAL);
                trStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
