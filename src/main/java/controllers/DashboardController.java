package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.java.others.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/

public class DashboardController implements Initializable{
    @FXML
    private JFXButton btnTodaySell;
    @FXML
    private Label lblTodaySellCtr;
    @FXML
    private Label lblTodaysSellAmount;
    @FXML
    private JFXButton btnTodayRental;
    @FXML
    private Label lblTodaysRentalCtr;
    @FXML
    private Label lblTodaysRentalAmount;
    @FXML
    private JFXButton loadAgain;
    @FXML
    private Label lblOutOfStock;
    @FXML
    private Label lblTotalDueAmount;
    @FXML
    private Label lblTodaysDueAmount;
    public static Integer todaysRentalCtr = 0;
    public static Integer totalDueCtr = 0;
    public static Integer todaySellCtr = 0;
    public static Double todaysTotalDue = 0.0;
    public static Double todaysTotalSell = 0.0;
    public static Double todayTotalRental = 0.0;
    public static Double totalDueAmount = 0.0;
    public static Integer stockOut = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFields();
    }

    private void setFields() {
        //Setting total due amount
        lblTotalDueAmount.setText(totalDueAmount.toString() + " $");

        //Setting todays sell amount
        lblTodaySellCtr.setText(todaySellCtr.toString());
        lblTodaysSellAmount.setText(todaysTotalSell.toString() + " $");

        //Setting todays rent amount
        lblTodaysRentalAmount.setText(todayTotalRental.toString() + " $");
        lblTodaysRentalCtr.setText(todaysRentalCtr.toString());

        //Setting todays due
        lblTodaysDueAmount.setText(todaysTotalDue.toString() + " $");

        //Setting out of stock
        lblOutOfStock.setText(stockOut.toString());
    }

    @FXML
    void loadAgain(ActionEvent event) {
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement getRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0");
            PreparedStatement getPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0");
            PreparedStatement getTodaysSell = connection.prepareStatement("SELECT COUNT(*), SUM(payAmount) FROM purchases WHERE purchaseDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRent = connection.prepareStatement("SELECT COUNT(*), SUM(paid) FROM rentals WHERE rentalDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0 AND rentalDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0 AND purchaseDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getOutOfStock = connection.prepareStatement("SELECT * FROM item, itemtype WHERE itemTypeId = ItemType_itemTypeId AND stock ="+0);

            ResultSet rentalDue = getRentalDue.executeQuery();
            ResultSet purchaseDue = getPurchaseDue.executeQuery();
            ResultSet todaysSell = getTodaysSell.executeQuery();
            ResultSet todaysRent = getTodaysRent.executeQuery();
            ResultSet todaysRentDue = getTodaysRentalDue.executeQuery();
            ResultSet todysPurchaseDue = getTodaysPurchaseDue.executeQuery();
            ResultSet stockOutRs = getOutOfStock.executeQuery();

            Double tDAmount = 0.0; //Total Due Amount

            while (rentalDue.next()) {
                tDAmount += rentalDue.getDouble(2);
            }

            while (purchaseDue.next()) {
                tDAmount += purchaseDue.getDouble(2);
            }

            Double tSell = 0.0; //Today's sell
            Double tRent = 0.0; //Today's rent
            Integer rCount = 0;
            Integer sCount = 0;

            while (todaysSell.next()) {
                sCount += todaysSell.getInt(1);
                tSell += todaysSell.getDouble(2);
            }

            while (todaysRent.next()) {
                rCount += todaysRent.getInt(1);
                tRent += todaysRent.getDouble(2);
            }

            Double todayDAmount = 0.0;
            Integer dCtr = 0;

            while (todaysRentDue.next()) {
                dCtr += todaysRentDue.getInt(1);
                todayDAmount += todaysRentDue.getDouble(2);
            }

            while (todysPurchaseDue.next()) {
                dCtr += todysPurchaseDue.getInt(1);
                todayDAmount += todysPurchaseDue.getDouble(2);
            }

            Integer sOCtr = 0;

            while (stockOutRs.next()) {
                sOCtr += 1;
            }

            totalDueAmount = tDAmount;
            todaySellCtr = sCount;
            todaysTotalSell = tSell;
            todaysRentalCtr = rCount;
            todayTotalRental = tRent;
            todaysTotalDue = todayDAmount;
            stockOut = sOCtr;

            //Setting values on the fields
            setFields();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showRent(ActionEvent event) {
        try {
            RentalListController.todayFlag = true;
            Parent rentList = FXMLLoader.load(getClass().getResource("/main/resources/view/rentallist.fxml"));
            Scene s = new Scene(rentList);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showSell(ActionEvent event) {
        try {
            SellListController.todayFlag = true;
            Parent sellsList = FXMLLoader.load(getClass().getResource("/main/resources/view/selllist.fxml"));
            Scene s = new Scene(sellsList);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
