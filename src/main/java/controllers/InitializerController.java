package main.java.controllers;

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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.java.others.*;
import main.java.others.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Author: Afif Al Mamun
 * Written on: 7/24/2018
 * Project: TeslaRentalInventory
 **/
public class InitializerController implements Initializable {

    private static final int THREAD_SLEEP_INTERVAL = 50;
    @FXML
    private JFXProgressBar progressIndicator;
    @FXML
    private Label taskName;
    public String sessionUser = LogInController.loggerUsername; //This firld will hold userName whos currently using the system
                                                //The field is initiated from LogInController Class
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoadRecords initializerTask = new LoadRecords();
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
            loadApplication();
        });
    }

    private void loadApplication() {
        //Creating a new stage for main application
        Parent root = null;
        Stage base = new Stage();

        try {
            root = FXMLLoader.load(getClass().getResource("/main/resources/view/base.fxml"));
            Scene scene = new Scene(root);
            String css = this.getClass().getResource("/main/resources/css/base.css").toExternalForm();
            scene.getStylesheets().add(css);
            base.setTitle("Inventory System");
            base.getIcons().add(new Image("/main/resources/icons/Logo.png"));
            base.setScene(scene);
            base.setMaximized(true);
            base.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class LoadRecords extends Task {
        @Override
        protected Object call() throws Exception {
            Connection connection = DBConnection.getConnection();

            //Creating OLs to save values from result set
            ObservableList<Customer> customersList = FXCollections.observableArrayList();
            ObservableList<Item> itemList = FXCollections.observableArrayList();
            ObservableList<String> itemTypeName = FXCollections.observableArrayList();

            PreparedStatement getCustomerList = connection.prepareStatement("SELECT * FROM customers");
            PreparedStatement getItemList = connection.prepareStatement("SELECT *" +
                    "FROM item, itemtype WHERE item.ItemType_itemTypeId = itemtype.itemTypeId ORDER BY itemID");
            PreparedStatement getSellsList = connection.prepareStatement("SELECT * FROM purchases WHERE User_username ='"
                    +sessionUser+"'" + " ORDER BY(purchaseID) DESC");
            PreparedStatement getRentalList = connection.prepareStatement("SELECT * FROM rentals WHERE User_username ='"
                    +sessionUser+"'" + " ORDER BY(rentalID) DESC");
            PreparedStatement getAccountList = connection.prepareStatement("SELECT  customers.firstName, customers.lastName, accounts.acccountID, accounts.accountName, accounts.paymethod " +
                    "FROM accounts, customers WHERE User_username ='"
                    +sessionUser+"' AND Customers_customerID = customerID");
            PreparedStatement getItemType = connection.prepareStatement("SELECT * FROM itemtype");
            PreparedStatement getOutOfStock = connection.prepareStatement("SELECT * FROM item, itemtype WHERE itemTypeId = ItemType_itemTypeId AND stock ="+0);

            //DashboardController stmts
            PreparedStatement getRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0");
            PreparedStatement getPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0");
            PreparedStatement getTodaysSell = connection.prepareStatement("SELECT COUNT(*), SUM(payAmount) FROM purchases WHERE purchaseDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRent = connection.prepareStatement("SELECT COUNT(*), SUM(paid) FROM rentals WHERE rentalDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysRentalDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM rentals WHERE amountDue <> 0 AND rentalDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            PreparedStatement getTodaysPurchaseDue = connection.prepareStatement("SELECT COUNT(*), SUM(amountDue) FROM purchases WHERE amountDue <> 0 AND purchaseDate = '"+ Date.valueOf(LocalDate.now()) + "'");

            ResultSet itemResultSet = getItemList.executeQuery();
            ResultSet customerResultSet = getCustomerList.executeQuery();
            ResultSet sellsList = getSellsList.executeQuery();
            ResultSet rentList = getRentalList.executeQuery();
            ResultSet accountResultSet = getAccountList.executeQuery();
            ResultSet itemType = getItemType.executeQuery();
            ResultSet stockOut = getOutOfStock.executeQuery();

            //DashboardController rs
            ResultSet rentalDue = getRentalDue.executeQuery();
            ResultSet purchaseDue = getPurchaseDue.executeQuery();
            ResultSet todaysSell = getTodaysSell.executeQuery();
            ResultSet todaysRent = getTodaysRent.executeQuery();
            ResultSet todaysRentDue = getTodaysRentalDue.executeQuery();
            ResultSet todysPurchaseDue = getTodaysPurchaseDue.executeQuery();

            //Updating task message
            this.updateMessage("Loading Customers...");
            Thread.sleep(THREAD_SLEEP_INTERVAL);

            ArrayList<String> customerIDNameHolder = new ArrayList<>(); //Will store ID and Name from ResultSet
            ArrayList<String> itemIDNameForSale = new ArrayList<>(); //Will hold item id name for sell
            ArrayList<String> customerName = new ArrayList<>();
            ArrayList<Integer> itemIDForSale = new ArrayList<>();
            ArrayList<String> itemIDNameForRentals = new ArrayList<>(); //Will hold item id name for rent
            ArrayList<Integer> itemIDForRent = new ArrayList<>();
            ArrayList<String> itemNames = new ArrayList<>();
            ArrayList<Integer> customerID = new ArrayList<>();
            TreeMap<String, Integer> itemTypeTree = new TreeMap<>();

            // Getting values from customers result set
            while(customerResultSet.next()) {
                customerIDNameHolder.add(customerResultSet.getInt(1) + " | "
                        + customerResultSet.getString(2) + "  "
                        + customerResultSet.getString(3));

                customerName.add(customerResultSet.getString(2)); //Adding first Name
                customerName.add(customerResultSet.getString(3)); //Adding last name

                Customer newCustomer = new Customer(
                        customerResultSet.getInt("customerID"),
                        customerResultSet.getString("firstName"),
                        customerResultSet.getString("lastName"),
                        customerResultSet.getString("address"),
                        customerResultSet.getString("phone"),
                        customerResultSet.getString("email"),
                        customerResultSet.getString("photo"),
                        customerResultSet.getString("gender"),
                        customerResultSet.getDate("memberSince"));

                customersList.add(newCustomer);

                customerID.add(customerResultSet.getInt(1));
            }

            //Setting fields in Customers List
            CustomerController.customersList = customersList;
            CustomerController.customerNames = customerName;

            //Setting Id and Name to SellsController, RentalsController, Accounts
            SellsController.customerIDName = customerIDNameHolder;
            SellsController.customerID = customerID;
            RentalsController.customerIDName = customerIDNameHolder;
            RentalsController.customerID = customerID;
            AccountController.customerIDName = customerIDNameHolder;

            Thread.sleep(THREAD_SLEEP_INTERVAL);
            //Updating Task status
            this.updateMessage("Loading Items...");

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

                itemNames.add(itemResultSet.getString("itemName"));

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                {
                    item.setRent(true);
                    itemIDNameForRentals.add(itemResultSet.getInt("itemID") + " | " +
                            itemResultSet.getString("itemName"));
                    itemIDForRent.add(itemResultSet.getInt("itemID"));
                }
                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                    itemIDNameForSale.add(itemResultSet.getInt("itemID") + " | " + itemResultSet.getString("itemName"));
                    itemIDForSale.add(itemResultSet.getInt("itemID"));
                    item.setSale(true);
                }

                itemList.add(item);

            }

            //Setting Observable Lists to the static field of InventoryController
            InventoryController.itemList = itemList;
            InventoryController.itemNames = itemNames;
            SellsController.inventoryItem = itemIDNameForSale; //Setting item id and name for sale & RentalsController
            SellsController.itemIDForSale = itemIDForSale;
            RentalsController.inventoryItem = itemIDNameForRentals;
            RentalsController.itemIDForRent = itemIDForRent;

            Thread.sleep(THREAD_SLEEP_INTERVAL);

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
            SellsController.purchaseList = sellsListByUser;

            Thread.sleep(THREAD_SLEEP_INTERVAL);

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
            RentalsController.rentalList = rentsListByUser;

            Thread.sleep(THREAD_SLEEP_INTERVAL);

            //Updating task status
            this.updateMessage("Loading Accounts...");

            ObservableList<Account> accountListByUser = FXCollections.observableArrayList();
            ArrayList<String> accountNames = new ArrayList<>();

            while(accountResultSet.next()) {
                accountListByUser.add(new Account(accountResultSet.getInt(3),
                        accountResultSet.getString(1) + " " + accountResultSet.getString(2),
                        accountResultSet.getString(4),
                        accountResultSet.getString(5)));
                accountNames.add(accountResultSet.getString(4));
            }

            //Setting Accounts on AccountController Class
            AccountController.accountList = accountListByUser;
            AccountController.accountNames = accountNames;

            Thread.sleep(THREAD_SLEEP_INTERVAL);

            //Updating Task Message
            //DashboardController contents
            this.updateMessage("Loading Dashboard Contents...");

            Double totalDueAmount = 0.0;
            Integer totalDueCtr = 0;

            while (rentalDue.next()) {
                totalDueCtr += rentalDue.getInt(1);
                totalDueAmount += rentalDue.getDouble(2);
            }

            while (purchaseDue.next()) {
                totalDueCtr += purchaseDue.getInt(1);
                totalDueAmount += purchaseDue.getDouble(2);
            }

            Double todaySell = 0.0;
            Double todayRent = 0.0;
            Integer rentCount = 0;
            Integer sellCount = 0;

            while (todaysSell.next()) {
                sellCount += todaysSell.getInt(1);
                todaySell += todaysSell.getDouble(2);
            }

            while (todaysRent.next()) {
                rentCount += todaysRent.getInt(1);
                todayRent += todaysRent.getDouble(2);
            }

            Double todaysDueAmount = 0.0;
            Integer dueCtr = 0;

            while (todaysRentDue.next()) {
                dueCtr += todaysRentDue.getInt(1);
                todaysDueAmount += todaysRentDue.getDouble(2);
            }

            while (todysPurchaseDue.next()) {
                dueCtr += todysPurchaseDue.getInt(1);
                todaysDueAmount += todysPurchaseDue.getDouble(2);
            }

            Integer stockOutCtr = 0;

            while (stockOut.next()) {
                stockOutCtr += 1;
            }

            //Setting on DashboardController
            DashboardController.totalDueCtr = totalDueCtr;
            DashboardController.totalDueAmount = totalDueAmount;
            DashboardController.todaySellCtr = sellCount;
            DashboardController.todaysTotalSell = todaySell;
            DashboardController.todaysRentalCtr = rentCount;
            DashboardController.todayTotalRental = todayRent;
            DashboardController.todaysTotalDue = todaysDueAmount;
            DashboardController.stockOut = stockOutCtr;

            Thread.sleep(THREAD_SLEEP_INTERVAL);
            this.updateMessage("Loading Item Types....");

            while (itemType.next()) {
                itemTypeTree.put(itemType.getString(2), itemType.getInt(1));
                itemTypeName.add(itemType.getString(2));
            }

            InventoryController.itemType = itemTypeTree;
            InventoryController.itemTypeNames = itemTypeName;

            //Updating Status of the Task
            this.updateMessage("Loading Finished!");
            Thread.sleep(THREAD_SLEEP_INTERVAL);

            return null;
        }
    }
}
