package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Rent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/16/2018
 * Project: TeslaRentalInventory
 **/
public class Account implements Initializable {

    @FXML
    private JFXTextField txtAccountID;

    @FXML
    private Label lblId;

    @FXML
    private JFXTextField txtCustomerID;

    @FXML
    private Label lblProcessedBy;

    @FXML
    private TableView<sample.Account> tblRecent;

    @FXML
    private TableColumn<sample.Account, String> customer;

    @FXML
    private TableColumn<sample.Account, Integer> accID;

    @FXML
    private TableColumn<sample.Account, String> accName;

    @FXML
    private TableColumn<sample.Account, String> payMethod;

    @FXML
    private JFXTextField txtPayMethod;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXButton btnSearch;

    public static ObservableList<sample.Account> accountList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting EmployeeName
        lblProcessedBy.setText(LogIn.loggerUsername);

//        int accID;
//        String cusName;
//        String accName;
//        String paymethod;

        customer.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        accID.setCellValueFactory(new PropertyValueFactory<>("accID"));
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        payMethod.setCellValueFactory(new PropertyValueFactory<>("paymethod"));

        tblRecent.setItems(accountList);


    }
}
