package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
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
import sample.DBConnection;
import sample.Dialog;
import sample.Item;
import sample.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            ResultSet itemResultSet = getItemList.executeQuery();
            ResultSet customerResultSet = getCustomerList.executeQuery();

            //Updating task message
            this.updateMessage("Loading Customers...");
            Thread.sleep(200);

            //Getting values from customers result set
            while(customerResultSet.next()) {
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

            Thread.sleep(500);
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

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                    item.setRent(true);
                if(itemResultSet.getString("rentalOrSale").contains("Sale"))
                    item.setSale(true);

                itemList.add(item);

            }

            //Setting OL to the static field of Inventory
            Inventory.itemList = itemList;

            Thread.sleep(500);

            //Updating Status of the Task
            this.updateMessage("Loading Finished!");
            Thread.sleep(400);

            return null;
        }
    }

}
