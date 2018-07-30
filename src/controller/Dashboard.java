package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/
public class Dashboard implements Initializable{
    @FXML
    private Label lblTodaysRentalCtr;

    @FXML
    private Label lblTodaysRentalAmount;

    @FXML
    private Label lblTotalDueCtr;

    @FXML
    private Label lblTotalDueAmount;

    public static Integer todaysRentalCtr = 0;
    public static Integer totalDueCtr = 0;
    public static Double todayTotalRental = 0.0;
    public static Double totalDueAmount = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTotalDueAmount.setText(totalDueAmount.toString() + " $");
        lblTotalDueCtr.setText(totalDueCtr.toString());
    }
}
