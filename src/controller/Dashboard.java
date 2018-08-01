package controller;

import com.jfoenix.controls.JFXButton;
import com.sun.deploy.resources.Deployment_pt_BR;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/
public class Dashboard implements Initializable{

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
    private JFXButton btnTodaysDue;

    @FXML
    private Label lblTodaysDueCtr;

    @FXML
    private Label lblTodaysDueAmount;

    @FXML
    private JFXButton btnTotalAmountDue;

    @FXML
    private Label lblTotalDueCtr;

    @FXML
    private Label lblTotalDueAmount, lblOutOfStock;

    @FXML
    private JFXButton btnTotalRentalDue;

    @FXML
    private Label lblTotalRentalCtr;

    public static Integer todaysRentalCtr = 0;
    public static Integer totalDueCtr = 0;
    public static Integer todaySellCtr = 0;
    public static Integer todaysDueCtr = 0;
    public static Double todaysTotalDue = 0.0;
    public static Double todaysTotalSell = 0.0;
    public static Double todayTotalRental = 0.0;
    public static Double totalDueAmount = 0.0;
    public static Integer stockOut = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting total due amount
        lblTotalDueAmount.setText(totalDueAmount.toString() + " $");
        lblTotalDueCtr.setText(totalDueCtr.toString());

        //Setting todays sell amount
        lblTodaySellCtr.setText(todaySellCtr.toString());
        lblTodaysSellAmount.setText(todaysTotalSell.toString() + " $");

        //Setting todays rent amount
        lblTodaysRentalAmount.setText(todayTotalRental.toString() + " $");
        lblTodaysRentalCtr.setText(todaysRentalCtr.toString());

        //Setting todays due
        lblTodaysDueAmount.setText(todaysTotalDue.toString() + " $");
        lblTodaysDueCtr.setText(todaysDueCtr.toString());

        //Setting out of stock
        lblOutOfStock.setText(stockOut.toString());



    }

    @FXML
    void showRent(ActionEvent event) {
        try {
            RentalList.todayFlag = true;
            Parent rentList = FXMLLoader.load(getClass().getResource("/fxml/rentallist.fxml"));
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
            SellList.todayFlag = true;
            Parent sellsList = FXMLLoader.load(getClass().getResource("/fxml/selllist.fxml"));
            Scene s = new Scene(sellsList);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showStockOut(ActionEvent event) {

    }
}
