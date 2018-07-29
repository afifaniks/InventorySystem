package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sample.*;
import sample.Account;
import sample.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/24/2018
 * Project: TeslaRentalInventory
 **/
public class Initializer implements Initializable {

    @FXML
    private JFXProgressBar progressIndicator;

    @FXML
    private Label taskName;

    public String sessionUser = LogIn.loggerUsername; //This firld will hold userName whos currently using the system
                                                //The field is initiated from LogIn Class

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        NewTask initializerTask = new NewTask();
        progressIndicator.progressProperty().unbind();
       // progressIndicator.progressProperty().bind(t.progressProperty());
        taskName.textProperty().unbind();
        taskName.textProperty().bind(initializerTask.messageProperty());

        new Thread(initializerTask).start();

        //Loading Main Application upon initializer task's succession
        initializerTask.setOnSucceeded(e -> {
            //Closing Current Stage
            Stage currentSatge = (Stage) taskName.getScene().getWindow();
            currentSatge.close();

            //Creating a new stage for main application
            Parent root = null;
            Stage base = new Stage();

            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/base.fxml"));
                Scene scene = new Scene(root);
                String css = this.getClass().getResource("/css/base.css").toExternalForm();
                scene.getStylesheets().add(css);
                base.setTitle("Tesla Rental Inventory");
                base.setScene(scene);
                base.setMaximized(true);
                base.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    class NewTask extends Task {

        @Override
        protected Object call() throws Exception {
            Connection connection = DBConnection.getConnection();

            //Creating OLs to save values from result set
            ObservableList<Customer> customersList = FXCollections.observableArrayList();
            ObservableList<Item> itemList = FXCollections.observableArrayList();

            PreparedStatement getCustomerList = connection.prepareStatement("SELECT * FROM customers");
            PreparedStatement getItemList = connection.prepareStatement("SELECT *" +
                    "FROM item, itemtype WHERE item.ItemType_itemTypeId = itemtype.itemTypeId");
            PreparedStatement getSellsList = connection.prepareStatement("SELECT * FROM purchases WHERE User_username ='"
                    +sessionUser+"'");
            PreparedStatement getRentalList = connection.prepareStatement("SELECT * FROM rentals WHERE User_username ='"
                    +sessionUser+"'");
            PreparedStatement getAccountList = connection.prepareStatement("SELECT  customers.firstName, customers.lastName, accounts.acccountID, accounts.accountName, accounts.paymethod " +
                    "FROM accounts, customers WHERE User_username ='"
                    +sessionUser+"' AND Customers_customerID = customerID");

            ResultSet itemResultSet = getItemList.executeQuery();
            ResultSet customerResultSet = getCustomerList.executeQuery();
            ResultSet sellsList = getSellsList.executeQuery();
            ResultSet rentList = getRentalList.executeQuery();
            ResultSet accountResultSet = getAccountList.executeQuery();

            //Updating task message
            this.updateMessage("Loading Customers...");
            Thread.sleep(200);

            ArrayList<String> customerIDNameHolder = new ArrayList<>(); //Will store ID and Name from ResultSet
            //Getting values from customers result set
            while(customerResultSet.next()) {
                customerIDNameHolder.add(customerResultSet.getInt(1) + " | "
                        + customerResultSet.getString(2) + "  "
                        + customerResultSet.getString(3));

                customersList.add(new sample.Customer(
                        customerResultSet.getInt(1),
                        customerResultSet.getString(2),
                        customerResultSet.getString(3),
                        customerResultSet.getString(4),
                        customerResultSet.getString(5),
                        customerResultSet.getString(6),
                        customerResultSet.getString(7),
                        customerResultSet.getString(8),
                        customerResultSet.getString(9),
                        customerResultSet.getDate(10)));
            }

            //Setting value to Customers List
            controller.Customer.customersList = customersList;

            //Setting Id and Name to Sells, Rentals, Accounts
            Sells.customerIDName = customerIDNameHolder;
            Rentals.customerIDName = customerIDNameHolder;
            controller.Account.customerIDName = customerIDNameHolder;

            Thread.sleep(500);
            //Updating Task status
            this.updateMessage("Loading Items...");

            ArrayList<String> itemIDNameForSale = new ArrayList<>(); //Will hold item id name for sell
            ArrayList<String> itemIDNameForRentals = new ArrayList<>(); //Will hold item id name for rent

            while(itemResultSet.next()) {
                Item item = new Item(itemResultSet.getInt("itemID"),
                        itemResultSet.getString("itemName"),
                        itemResultSet.getInt("stock"),
                        false,
                        false,
                        itemResultSet.getDouble("salePrice"),
                        itemResultSet.getDouble("rentRate"),
                        itemResultSet.getString("photo"),
                        itemResultSet.getString("typeName"));

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                {
                    item.setRent(true);
                    itemIDNameForRentals.add(itemResultSet.getInt("itemID") + " | " +
                            itemResultSet.getString("itemName"));
                }
                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                    itemIDNameForSale.add(itemResultSet.getInt("itemID") + " | " + itemResultSet.getString("itemName"));
                    item.setSale(true);
                }

                itemList.add(item);

            }

            //Setting OL to the static field of Inventory
            Inventory.itemList = itemList;

            Sells.inventoryItem = itemIDNameForSale; //Setting item id and name for sale & Rentals
            Rentals.inventoryItem = itemIDNameForRentals;

            Thread.sleep(200);

            //Updating task status
            this.updateMessage("Loading Sells...");
            ObservableList<Purchase> sellsListByUser = FXCollections.observableArrayList();

            while(sellsList.next()) {
                sellsListByUser.add(new Purchase(sellsList.getInt("purchaseID"),
                        sellsList.getInt("Customers_customerID"),
                        sellsList.getInt("Item_itemID"),
                        sellsList.getString("purchaseDate"),
                        sellsList.getInt("purchaseQuantity"),
                        sellsList.getDouble("payAmount"),
                        sellsList.getDouble("amountDue")));

            }

            //Setting Purchases on Sell Class
            Sells.purchaseList = sellsListByUser;

            Thread.sleep(200);

            //Updating Task Status
            this.updateMessage("Loading Rentals...");
            ObservableList<Rent> rentsListByUser = FXCollections.observableArrayList();

            while (rentList.next()) {
                rentsListByUser.add(new Rent(rentList.getInt("rentalID"),
                        rentList.getInt("Item_itemID"),
                        rentList.getInt("Customers_customerID"),
                        rentList.getString("rentalDate"),
                        rentList.getString("returnDate"),
                        rentList.getDouble("paid"),
                        rentList.getDouble("amountDue")));
            }

            //Setting Rents on Rental Class
            Rentals.rentalList = rentsListByUser;

            Thread.sleep(200);

            //Updating task status
            this.updateMessage("Loading Accounts...");

            ObservableList<Account> accountListByUser = FXCollections.observableArrayList();

            while(accountResultSet.next()) {
                accountListByUser.add(new Account(accountResultSet.getInt(3),
                        accountResultSet.getString(1) + " " + accountResultSet.getString(2),
                        accountResultSet.getString(4),
                        accountResultSet.getString(5)));
            }

            //Setting Accounts on Account Class
            controller.Account.accountList = accountListByUser;


            //Updating Status of the Task
            this.updateMessage("Loading Finished!");
            Thread.sleep(200);

            return null;
        }
    }

}
