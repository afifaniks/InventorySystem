package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import sample.DBConnection;
import sample.Item;

import java.net.URL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 09-Jul-18
 * Project: TeslaRentalInventory
 **/
public class Inventory implements Initializable{


    @FXML
    private JFXTextField txtItemName;

    @FXML
    private JFXTextField txtType;

    @FXML
    private JFXTextField txtRentRate;

    @FXML
    private Label itemID;

    @FXML
    private Circle imgCustomerPhoto;

    @FXML
    private Label lblStock;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private Label lblPageIndex;

    @FXML
    private JFXButton btnPrevEntry;

    @FXML
    private JFXButton btnNextEntry;

    @FXML
    private JFXButton btnUpdateStock;

    @FXML
    private JFXButton btnListAll;

    @FXML
    private JFXButton btnMostSold;

    @FXML
    private JFXButton btnOutOfStock;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXCheckBox chkRent, chkSale;

    private static int recordIndex = 0;
    private static int itemSize = 0;

    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from Initializer Class

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        recordIndex = 0; //Resetting index value
        itemSize = itemList.size();


        if(itemSize > 0) {
            Item itemRecord = itemList.get(recordIndex);

            //Setting Fields
            itemID.setText(Integer.toString(itemRecord.getId()));
            txtItemName.setText(itemRecord.getName());
            txtPrice.setText("Null");
            txtRentRate.setText("Null");
            txtType.setText(itemRecord.getItemType());
            lblStock.setText(Integer.toString(itemRecord.getStock()));
            if (itemRecord.isRent()) {
                txtRentRate.setText(Double.toString(itemRecord.getRentRate()) + " $");
                chkRent.setSelected(true);
            }
            if (itemRecord.isSale()) {
                txtPrice.setText(Double.toString(itemRecord.getSalePrice()) + " $");
                chkSale.setSelected(true);
            }
        }


    }

}
