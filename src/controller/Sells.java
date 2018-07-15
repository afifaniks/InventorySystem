package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private JFXDatePicker txtRentalDate;

    @FXML
    private Label lblCost;

    @FXML
    private JFXTextField txtPayAmount;

    @FXML
    private TableView<?> tblRecent;

    @FXML
    private JFXDatePicker txtReturnDate;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] ps = {"10 XXX", "20 AVSYTS", "30 SHJGD", "hjfsdhjg"};

        //TextFields.bindAutoCompletion(txt, ps);
    }
}
