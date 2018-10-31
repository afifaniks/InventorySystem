package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 08-Jul-18
 * Project: TeslaRentalInventory
 **/
public class BaseGUI implements Initializable{

    @FXML
    private AnchorPane paneRight;

    @FXML
    private AnchorPane paneLeft;

    @FXML
    private JFXButton btnDashboard;

    @FXML
    private Label lblAccessLevel;

    @FXML
    private Label lblUsername;

    @FXML
    private JFXButton btnCustomers;

    @FXML
    private JFXButton btnInventoryItem;

    @FXML
    private JFXButton btnAccounts;

    @FXML
    private JFXButton btnDueUpdate;

    @FXML
    private JFXButton btnAdmin;

    @FXML
    private JFXButton btn;

    @FXML
    private Label lblClock;

    @FXML
    private JFXButton btnRentals;

    @FXML
    private JFXButton btnSells;

    private static String username = "";
    private static String accessLevel = "";

    private AnchorPane newRightPane = null;
    private JFXButton temp = null;
    private JFXButton recover = null;
    private static boolean anchorFlag = false;
    private final static String LOGIN_URL = "/fxml/login.fxml";
    private final static String INVENTORY_URL = "/fxml/inventory.fxml";
    private final static String CUSTOMER_URL = "/fxml/customer.fxml";
    private final static String DASHBOARD_URL = "/fxml/dashboard.fxml";
    private final static String SELLS_URL = "/fxml/sells.fxml";
    private final static String RENTALS_URL = "/fxml/rentals.fxml";
    private final static String ACCOUNTS_URL = "/fxml/accounts.fxml";
    private final static String ADMIN_URL = "/fxml/administrator.fxml";
    private final static String DUE_UPDATE = "/fxml/dueupdate.fxml";

    //This method will help to set appropriate right pane
    //respective to the left pane selection and will make it responsive if
    //any window resize occurs

    @FXML
    private void ctrlRightPane(String URL) throws IOException {
        try {
            paneRight.getChildren().clear(); //Removing previous nodes
            newRightPane = FXMLLoader.load(getClass().getResource(URL));

            newRightPane.setPrefHeight(paneRight.getHeight());
            newRightPane.setPrefWidth(paneRight.getWidth());


            paneRight.getChildren().add(newRightPane);

            //Listener to monitor any window size change
            paneRight.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
              autoResizePane();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method will resize right pane size
    //relative to it's parent whenever window is resized

    private void autoResizePane() {
        newRightPane.setPrefWidth(paneRight.getWidth());
        newRightPane.setPrefHeight(paneRight.getHeight());
    }

    //The method here is universal method for all the navigators from left
    //pane which will identify the selection by user and
    //set the respective right pane FXML

    @FXML
    void btnNavigators(ActionEvent event) {
        borderSelector(event); //Marking selected navigator button

        JFXButton btn = (JFXButton)event.getSource();

        if(btn.getText().equals(btnInventoryItem.getText())) {
            try {
                ctrlRightPane(INVENTORY_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnCustomers.getText())) {
            try {
                ctrlRightPane(CUSTOMER_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnDashboard.getText())) {
            try {
                ctrlRightPane(DASHBOARD_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnSells.getText())) {
            try {
                ctrlRightPane(SELLS_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnRentals.getText())) {
            try {
                ctrlRightPane(RENTALS_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnAccounts.getText())) {
            try {
                ctrlRightPane(ACCOUNTS_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnAdmin.getText())) {
            try {
                ctrlRightPane(ADMIN_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(btn.getText().equals(btnDueUpdate.getText())) {
            try {
                ctrlRightPane(DUE_UPDATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //This method will mark selected navigator
    //from left navigation pane and will remove it if another
    //navition button is clicked.

    private void borderSelector(ActionEvent event) {
        JFXButton btn = (JFXButton)event.getSource();

        if(temp == null) {
            temp = btn; //Saving current button properties
        } else {
            temp.setStyle(""); //Resetting previous selected button properties
            temp = btn; //Saving current button properties
        }

        btn.setStyle("-fx-background-color: #455A64");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username = LogInGUI.loggerUsername;
        accessLevel = LogInGUI.loggerAccessLevel;

        lblUsername.setText(username.toUpperCase());
        lblAccessLevel.setText(accessLevel);

        //Controling access by checking access level of user
        if(accessLevel.equals("Employee")) {
            btnAdmin.setDisable(true);

        }

        //Setting Clock within a new Thread
        Runnable clock = new Runnable() {
            @Override
            public void run() {
                runClock();
            }
        };

        Thread newClock = new Thread(clock); //Creating new thread
        newClock.setDaemon(true); //Thread will automatically close on applications closing
        newClock.start(); //Starting Thread

        //Setting DashboardGUI on RightPane
        try {
            ctrlRightPane(DASHBOARD_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method will set the clock running
    private void runClock() {
        while (true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    lblClock.setText(LocalTime.now().format(
                            DateTimeFormatter.ofPattern("hh:mm:ss a")));

                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Upon logging out this method will set log in prompt on
    //screen by closing main application
    @FXML
    private void logOut() {
        Stage current = (Stage)lblUsername.getScene().getWindow();
        current.close();

        try {
            Parent root = FXMLLoader.load(getClass().getResource(LOGIN_URL));
            root.getStylesheets().add("/css/login.css");
            Scene scene = new Scene(root);
            Stage logInPrompt = new Stage();
            logInPrompt.setScene(scene);
            logInPrompt.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
