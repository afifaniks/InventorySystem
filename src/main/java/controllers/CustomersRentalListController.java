package main.java.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.others.DBConnection;
import main.java.others.Rent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class CustomersRentalListController implements Initializable {
    @FXML
    private Label lblSellCount;
    @FXML
    private Label lblDue;
    @FXML
    private Label lblAmount;
    @FXML
    private Label today;
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
    private TableColumn<Rent, String> empName;
    @FXML
    private TableColumn<Rent, Double> paid;
    @FXML
    private TableColumn<Rent, Double> due;
    public static int customerID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        today.setText(LocalDate.now().toString());

        Connection con = DBConnection.getConnection();
        //Setting up table columns
        rentID.setCellValueFactory(new PropertyValueFactory<>("rentID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        rentalDate.setCellValueFactory(new PropertyValueFactory<>("rentDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        paid.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        due.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        empName.setCellValueFactory(new PropertyValueFactory<>("user"));

        try {
            PreparedStatement getRentalList = con.prepareStatement("SELECT * FROM rentals WHERE Customers_customerID ="+customerID);
            ResultSet rentList = getRentalList.executeQuery();

            ObservableList<Rent> rentsListByUser = FXCollections.observableArrayList();

            Integer ctr = 0;
            Double due = 0.0;
            Double total = 0.0;

            while (rentList.next()) {
                rentsListByUser.add(new Rent(rentList.getInt("rentalID"),
                        rentList.getInt("Item_itemID"),
                        rentList.getInt("Customers_customerID"),
                        rentList.getString("rentalDate"),
                        rentList.getString("returnDate"),
                        rentList.getDouble("paid"),
                        rentList.getDouble("amountDue"),
                        rentList.getString("User_username")));
                ctr++;
                due += rentList.getDouble("amountDue");
                total += rentList.getDouble("paid");
            }

            lblDue.setText(due.toString() + " $");
            lblAmount.setText(total.toString() + " $");
            lblSellCount.setText(ctr.toString());

            tblRecent.setItems(rentsListByUser);

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
