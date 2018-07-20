package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
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
    private TableView<?> tblRecent;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXDatePicker txtReturnDate;

    @FXML
    private JFXButton btnRentalReturned;

    @FXML
    void ctrlRentalReturnedAction(ActionEvent event) {
        lblCategory.setText("Rental Return");
    }


    @FXML
    void ctrlRefreshAction(ActionEvent event) {

    }

    @FXML
    void ctrlAddAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
