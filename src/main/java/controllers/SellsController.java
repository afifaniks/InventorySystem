package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import main.java.others.DBConnection;
import main.java.others.Purchase;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/

public class SellsController implements Initializable{
    @FXML
    private JFXTextField txtCustomerId;
    @FXML
    private Label lblId;
    @FXML
    private JFXTextField txtItemId;
    @FXML
    private JFXDatePicker txtDate;
    @FXML
    private Label lblCost, lblVerify;
    @FXML
    private JFXTextField txtPayAmount, txtQty;
    @FXML
    private JFXButton btnProcced;
    @FXML
    private FontAwesomeIconView btnIcon;
    @FXML
    private TableView<Purchase> tblRecent;
    @FXML
    private TableColumn<Purchase, Integer> purID;
    @FXML
    private TableColumn<Purchase, Integer> cusID;
    @FXML
    private TableColumn<Purchase, Integer> itemID;
    @FXML
    private TableColumn<Purchase, String> date;
    @FXML
    private TableColumn<Purchase, Integer> qty;
    @FXML
    private TableColumn<Purchase, Double> paidAmmount;
    @FXML
    private TableColumn<Purchase, Double> dueAmount;
    @FXML
    private LineChart<String, Integer> lineChart;
    @FXML
    private FontAwesomeIconView btnChartIcon;
    @FXML
    private CategoryAxis dateAxis;
    @FXML
    private NumberAxis amountAxis;
    @FXML
    private JFXButton btnBarchart;
    /**
     * This field will be used to transition b/w Table and Chart
     * toggleTable = true; Table View is being showed
     * toggleTable = false; LineChart is being showed
     */
    private static boolean toggleTable = true;

    private void generateLineChart() {
        lineChart.getData().clear();
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT purchaseDate, sum(payAmount) FROM purchases WHERE User_username='"+ LogInController.loggerUsername+"' GROUP BY purchaseDate ORDER BY UNIX_TIMESTAMP(purchaseDate) DESC LIMIT 15");
            ResultSet rs = ps.executeQuery();

            XYChart.Series chartData = new XYChart.Series<>();

            while(rs.next()) {
                chartData.getData().add(new XYChart.Data(rs.getString(1), rs.getInt(2)));
            }

            lineChart.getData().addAll(chartData);

        } catch (SQLException e) {
            e.printStackTrace();
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    @FXML
    void btnBarchartAction(ActionEvent event) {
        if(toggleTable) { //If toggleTable is true that means table view is currently in view
            toggleTable = false; //Changing toggleTable value
            btnChartIcon.setGlyphName("TABLE");
            tblRecent.setVisible(false);
            lineChart.setVisible(true);

            generateLineChart();

        } else {
            toggleTable = true;
            btnChartIcon.setGlyphName("LINE_CHART");
            lineChart.setVisible(false);
            lineChart.getData().clear();
            tblRecent.setVisible(true);
        }
    }

    @FXML
    private AnchorPane rightPane;

    /**
     * startTransaction variable will be used to verify inputs - That is
     * if all the inputs are OK then start transaction, otherwise not
     */
    private static boolean startTransaction = false;

    public static ObservableList<Purchase> purchaseList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
    //The field will be initiated in InitializerController class
    public static ArrayList<String> inventoryItem = null;
    public static ArrayList<Integer> customerID = null;
    public static ArrayList<Integer> itemIDForSale = null;

    @FXML
    void loadAgain(ActionEvent event) {

        Connection con = DBConnection.getConnection();

        PreparedStatement getSellsList = null;
        try {
            getSellsList = con.prepareStatement("SELECT * FROM purchases WHERE User_username ='"
                    + LogInController.loggerUsername+ "'" + " ORDER BY(purchaseID) DESC");

            PreparedStatement ps = con.prepareStatement("SELECT MAX(purchaseID) FROM purchases");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }

            ObservableList<Purchase> sellsListByUser = FXCollections.observableArrayList();

            ResultSet sellsList = getSellsList.executeQuery();

            while(sellsList.next()) {
                sellsListByUser.add(new Purchase(sellsList.getInt("purchaseID"),
                        sellsList.getInt("Customers_customerID"),
                        sellsList.getInt("Item_itemID"),
                        sellsList.getString("purchaseDate"),
                        sellsList.getInt("purchaseQuantity"),
                        sellsList.getDouble("payAmount"),
                        sellsList.getDouble("amountDue")));

            }

            // Resetting Fields
            purchaseList = sellsListByUser;
            tblRecent.getItems().clear();
            tblRecent.setItems(purchaseList);
            txtPayAmount.setText("");
            txtQty.setText("1");
            txtCustomerId.setText("");
            txtItemId.setText("");
            txtDate.setValue(LocalDate.now());
            lblVerify.setText("Verify Input");
            lblCost.setText("??");
            btnIcon.setGlyphName("QUESTION");

            // Resetting lineChart if toggleTable is false
            if(!toggleTable) {
                generateLineChart();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleTable = true;
        startTransaction = false;
        txtQty.setText("1");
        txtDate.setValue(LocalDate.now());

        Tooltip tooltip = new Tooltip("Verify Input");
        btnProcced.setTooltip(tooltip);

        // Getting new connection to db to set new purchaseID
        Connection con = DBConnection.getConnection();

        try{
            PreparedStatement ps = con.prepareStatement("SELECT MAX(purchaseID) FROM purchases");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextFields.bindAutoCompletion(txtCustomerId, customerIDName);
        TextFields.bindAutoCompletion(txtItemId, inventoryItem);

        // Setting up table on load
        purID.setCellValueFactory(new PropertyValueFactory<>("purID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paidAmmount.setCellValueFactory(new PropertyValueFactory<>("paid"));
        dueAmount.setCellValueFactory(new PropertyValueFactory<>("due"));

        tblRecent.setItems(purchaseList);
    }

    @FXML
    void btnProceedAction(ActionEvent event) {
        if (startTransaction) {
            startTransaction = false; //Resetting TransactionController Value
            btnIcon.setGlyphName("QUESTION");
            lblVerify.setText("Verify Input");
            Tooltip tooltip = new Tooltip("Verify Input");
            btnProcced.setTooltip(tooltip);
            //Loading TransactionController Window
            //Setting Values in TransactionController Window
            TransactionController.cusID = Integer.valueOf(txtCustomerId.getText());
            TransactionController.purchaseId = lblId.getText();
            TransactionController.purchaseQty = Integer.valueOf(txtQty.getText());
            TransactionController.itemID = Integer.valueOf(txtItemId.getText());
            TransactionController.rentOrSale = true;

            Double paid = 0.0;

            //Checking if input is valid
            try {
                if(txtPayAmount.getText().equals(""))
                    paid = 0.0;
                else
                    paid = Double.valueOf(txtPayAmount.getText());

                TransactionController.payAmount = paid;
                TransactionController.due = Double.valueOf(lblCost.getText()) - paid;


                Connection connection = DBConnection.getConnection();

                //Checking for stock availability
                PreparedStatement checkItemStock = connection.prepareStatement("SELECT stock FROM item WHERE itemID = "+Integer.valueOf(txtItemId.getText()));
                ResultSet itemStock = checkItemStock.executeQuery();

                Integer stock = 0;

                while(itemStock.next()) {
                    stock = itemStock.getInt(1);
                }

                if((stock - Integer.valueOf(txtQty.getText())) < 0) {
                    new PromptDialogController("Insuffecient Stock!", "Stock is insuffecient to validate this purchase.\nRequired: " +
                            Integer.valueOf(txtQty.getText()) +"\nIn Stock:" + stock);
                    return;
                }

                TransactionController.stock = stock;

                //Checking for accounnts
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) from accounts, customers where customerID = Customers_customerID AND Customers_customerID = "+Integer.valueOf(txtCustomerId.getText()));
                ResultSet rs = preparedStatement.executeQuery();

                int chk = 0;

                while(rs.next()) {
                    chk = rs.getInt(1);
                }
                if(chk <= 0) {
                    new PromptDialogController("No Account!", "Customer has no account. Please create an account first then try again.");
                } else {
                    Parent trPanel = FXMLLoader.load(getClass().getResource("/main/resources/view/transaction.fxml"));
                    Scene trScene = new Scene(trPanel);
                    Stage trStage = new Stage();
                    trStage.setScene(trScene);
                    trStage.setResizable(false);
                    trStage.show();
                }

                connection.close();

            } catch (Exception e) {
                txtPayAmount.setUnFocusColor(Color.web("red"));
                JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                snackbar.show("Invalid Input Format", 3000);
            }

        } else {
            boolean flag = true;

            if (!txtQty.getText().equals("") && Integer.parseInt(txtQty.getText()) > 0) {

                String customer = txtCustomerId.getText();

                txtItemId.setUnFocusColor(Color.web("#263238"));
                txtCustomerId.setUnFocusColor(Color.web("#263238"));
                txtQty.setUnFocusColor(Color.web("#263238"));
                txtPayAmount.setUnFocusColor(Color.web("#263238"));

                try {
                    txtCustomerId.setText(Integer.valueOf(customer).toString());
                    //Searching if the ID is valid
                    if (customerID.indexOf(Integer.valueOf(txtCustomerId.getText())) == -1) {
                        flag = false;
                        txtCustomerId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Invalid Customer ID", 3000);
                    }
                } catch (Exception e) {
                    flag = false;
                    try {
                        txtCustomerId.setText(customer.substring(0, customer.indexOf('|') - 1));
                        flag = true;
                        //Searching if the ID is valid
                        if (customerID.indexOf(Integer.valueOf(txtCustomerId.getText())) == -1) {
                            flag = false;
                            txtCustomerId.setUnFocusColor(Color.web("red"));
                            JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                            snackbar.show("Invalid Customer ID", 3000);
                        }
                    } catch (Exception en) {
                        flag = false;
                        txtCustomerId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Invalid Input", 3000);
                    }

                }

                String item = txtItemId.getText();

                try {
                    txtItemId.setText(Integer.valueOf(item).toString());
                    if (itemIDForSale.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
                        flag = false;
                        txtItemId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Item ID is not listed", 3000);
                    }
                } catch (Exception e) {
                    flag = false;
                    try {
                        txtItemId.setText(item.substring(0, item.indexOf('|') - 1));
                        flag = true;
                        if (itemIDForSale.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
                            flag = false;
                            txtItemId.setUnFocusColor(Color.web("red"));
                            JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                            snackbar.show("Item ID is not listed", 3000);
                        }
                    } catch (Exception en) {
                        flag = false;
                        txtItemId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Invalid Input", 3000);
                    }
                }

                if(flag) {
                    Connection con = DBConnection.getConnection();
                    try {
                        PreparedStatement ps = con.prepareStatement("SELECT salePrice FROM item WHERE itemID = " + Integer.valueOf(txtItemId.getText()));
                        ResultSet rs = ps.executeQuery();

                        while (rs.next()) {
                            lblCost.setText(String.valueOf(Integer.valueOf(txtQty.getText()) * rs.getDouble(1)));
                        }
                    } catch (SQLException e) {
                        flag = false;
                        e.printStackTrace();
                    }
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                flag = false;
                txtQty.setUnFocusColor(Color.web("red"));
                JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                snackbar.show("Quantity field can not be null or 0", 3000);
            }

            if(flag) {
                btnIcon.setGlyphName("CHECK");
                lblVerify.setText("Finalize");
                startTransaction = true;
                Tooltip tooltip = new Tooltip("Proceed to Transaction");
                btnProcced.setTooltip(tooltip);
            }
        }

    }

}
