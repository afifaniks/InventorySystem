package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import sample.DBConnection;
import sample.Item;

import java.io.IOException;
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
    private JFXButton btnOutOfStock, btnGoBack;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private AnchorPane itemPane;

    @FXML
    private AnchorPane itemListPane;

    @FXML
    private TableView<Item> tbl;

    @FXML
    private TableColumn<Item, Integer> columnItemID;

    @FXML
    private TableColumn<Item, String> columnItemName;

    @FXML
    private TableColumn<Item, String> columnItemType;

    @FXML
    private TableColumn<Item, Boolean> columnForRent;

    @FXML
    private TableColumn<Item, Boolean> columnForSale;

    @FXML
    private TableColumn<Item, Double> columnRentalRate;

    @FXML
    private TableColumn<Item, Double> columnPrice;

    @FXML
    private TableColumn<Item, Integer> columnStock;

    @FXML
    private JFXCheckBox chkRent, chkSale;

    private static int recordIndex = 0;
    private static int recordSize = 0;
    private sample.Item onView = null;

    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from Initializer Class

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemListPane.setVisible(false);
        recordIndex = 0; //Resetting index value
        recordSize = itemList.size();


        //Setting Fields
        btnNextEntry.setOnAction(event -> {
            onView = itemList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            if (recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);
            btnPrevEntry.setDisable(false);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = itemList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            btnNextEntry.setDisable(false);
            if (recordIndex == 0)
                btnPrevEntry.setDisable(true);

        });

        btnNextEntry.setDisable(true); //For purpose ;)

        if (recordSize > 0) {
            onView = itemList.get(recordIndex); //Setting value for current record

            //Setting customer default fields
            recordNavigator();

            //Setting page indexer value
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");

            if (recordSize > 1) {
                btnNextEntry.setDisable(false); //Next entry will be enabled if there is more than one entry
            }
        }

        btnPrevEntry.setDisable(true); //Disabling prevButton Initially


    }

    //This method will set navigate between Items
    private void recordNavigator() {
        lblStock.setStyle("-fx-text-fill: #263238"); //Resetting stock color

        chkRent.setSelected(false);
        chkSale.setSelected(false);

        itemID.setText(Integer.toString(onView.getId()));
        txtItemName.setText(onView.getName());
        txtType.setText(onView.getItemType());
        if(onView.isRent()) {
            chkRent.setSelected(true);
            txtRentRate.setText(Double.toString(onView.getRentRate()) + " $");
        }
        if(onView.isSale()) {
            chkSale.setSelected(true);
            txtPrice.setText(Double.toString(onView.getSalePrice()) + " $");
        }
        lblStock.setText(Integer.toString(onView.getStock()));

        if(onView.getStock() <= 5) //Setting stock color red if it's very limited
            lblStock.setStyle("-fx-text-fill: red");

    }

    @FXML
    void listAllItems(ActionEvent event) {
        itemPane.setVisible(false); //Setting default item pane not visible
        itemListPane.setVisible(true); //Setting item list visible

        columnItemID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnItemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        columnForRent.setCellValueFactory(new PropertyValueFactory<>("rent"));
        columnForSale.setCellValueFactory(new PropertyValueFactory<>("sale"));
        columnRentalRate.setCellValueFactory(new PropertyValueFactory<>("rentRate"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

        tbl.setItems(itemList);

        btnGoBack.setOnAction(e -> {
            itemListPane.setVisible(false);  //Setting item list pane visible
            itemPane.setVisible(true); //Setting item pane visible
        });
    }

}
