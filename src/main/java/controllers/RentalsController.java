package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import main.java.others.DBConnection;
import main.java.others.Rent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/16/2018
 * Project: TeslaRentalInventory
 **/

public class RentalsController implements Initializable {
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private Label lblCategory;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXTextField txtCustomerId;
    @FXML
    private Label lblId, lblVerify;
    @FXML
    private JFXTextField txtItemId;
    @FXML
    private JFXDatePicker txtRentalDate;
    @FXML
    private Label lblCost;
    @FXML
    private JFXTextField txtPayAmount;
    @FXML
    private JFXButton btnRefresh;
    @FXML
    private JFXButton btnProcced;
    @FXML
    private JFXDatePicker txtReturnDate;
    @FXML
    private JFXButton btnRentalReturned;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private TableView<Rent> tblRecent;
    @FXML
    private TableColumn<Rent, Integer> rentID;
    @FXML
    private TableColumn<Rent, Integer> cusID;
    @FXML
    private TableColumn<Rent, Integer> itemID;
    @FXML
    private TableColumn<Rent, String> rentalDate;
    @FXML
    private TableColumn<Rent, String> returnDate;
    @FXML
    private TableColumn<Rent, Double> paid;
    @FXML
    private TableColumn<Rent, Double> due;
    @FXML
    private FontAwesomeIconView btnIcon;
    @FXML
    private JFXButton btnBarchart;
    @FXML
    private FontAwesomeIconView btnChartIcon;
    @FXML
    private LineChart<String, Integer> lineChart;
    @FXML
    private CategoryAxis dateAxis;
    @FXML
    private NumberAxis amountAxis;
    private static boolean toggleTable = true; //This field will be used to transition b/w Table and Chart
    private static boolean startTransaction = false;
    public static ObservableList<Rent> rentalList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
    //The field will be initiated in InitializerController class
    public static ArrayList<String> inventoryItem = null;
    public static ArrayList<Integer> customerID = null;
    public static ArrayList<Integer> itemIDForRent = null;

    @FXML
    void ctrlRefreshAction(ActionEvent event) {
        txtRentalDate.setValue(LocalDate.now());

    }

    @FXML
    void btnProceedAction(ActionEvent event) {
        if (startTransaction) {
            startTransaction = false; //Resetting TransactionController Value
            btnIcon.setGlyphName("QUESTION");
            Tooltip tooltip = new Tooltip("Verify Input");
            lblVerify.setText("Verify Input");
            btnProcced.setTooltip(tooltip);

            //Loading TransactionController Window
            //Setting Values in TransactionController Window
            TransactionController.cusID = Integer.valueOf(txtCustomerId.getText());
            TransactionController.rentalId = lblId.getText();
            TransactionController.itemID = Integer.valueOf(txtItemId.getText());
            TransactionController.rentalReturnDate = txtReturnDate.getValue();
            TransactionController.rentOrSale = false;

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

                if(stock == 0) {
                    new PromptDialogController("Insufficient Stock!", "This item is not currently available for renting.");
                    return;
                }

                TransactionController.stock = stock; //because its rental

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

            String customer = txtCustomerId.getText();
            String item = txtItemId.getText();

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
                } catch (Exception ex) {
                    flag = false;
                    txtCustomerId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }
            }

            try {
                txtItemId.setText(Integer.valueOf(item).toString());
                if (itemIDForRent.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
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
                    if (itemIDForRent.indexOf(Integer.valueOf(txtItemId.getText())) == -1) {
                        flag = false;
                        txtItemId.setUnFocusColor(Color.web("red"));
                        JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                        snackbar.show("Item ID is not listed", 3000);
                    }
                } catch (Exception ex) {
                    flag = false;
                    txtItemId.setUnFocusColor(Color.web("red"));
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Invalid Input", 3000);
                }
            }

            Long days = null;

            if(txtReturnDate.getValue() == null) {
                flag = false;
                JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                snackbar.show("Return Date Required", 3000);
            } else {
                //Calculating Item Rental in days
                days = ChronoUnit.DAYS.between(txtRentalDate.getValue(), txtReturnDate.getValue()); //Calculates total days for the item being rented

                if(days < 0) {
                    flag = false;
                    JFXSnackbar snackbar = new JFXSnackbar(rightPane);
                    snackbar.show("Return Date can not be earlier than Rental Date!", 3000);

                }
            }

            if (flag) {
                //Getting rental rate from database for the item
                Connection connection = DBConnection.getConnection();
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT rentRate FROM item WHERE itemID = "+Integer.valueOf(txtItemId.getText()));
                    ResultSet resultSet = ps.executeQuery();

                    Long cost = null;

                    while(resultSet.next()) {
                        cost = resultSet.getInt(1) * days;
                    }

                    lblCost.setText(cost.toString());
                    btnIcon.setGlyphName("CHECK");
                    startTransaction = true;
                    Tooltip tooltip = new Tooltip("Proceed to Transaction");
                    lblVerify.setText("Finalize");
                    btnProcced.setTooltip(tooltip);

                } catch (SQLException e) {
                    new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                }
            }
        }
    }

    @FXML
    void loadAgain(ActionEvent event) {

        Connection con = DBConnection.getConnection();

        PreparedStatement getSellsList = null;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT MAX(rentalID) FROM rentals");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }

            PreparedStatement getRentalList = con.prepareStatement("SELECT * FROM rentals WHERE User_username ='"
                    + LogInController.loggerUsername+"'" + " ORDER BY(rentalID) DESC");
            PreparedStatement getTodaysRent = con.prepareStatement("SELECT COUNT(*), SUM(paid) FROM rentals WHERE rentalDate = '"+ Date.valueOf(LocalDate.now()) + "'");
            ResultSet rentList = getRentalList.executeQuery();
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

            //Resetting Fields
            tblRecent.getItems().clear();
            rentalList = rentsListByUser;
            tblRecent.setItems(rentalList);
            txtPayAmount.setText("");
            txtReturnDate.setValue(null);
            txtCustomerId.setText("");
            txtItemId.setText("");
            lblCost.setText("??");
            lblVerify.setText("Verify Input");
            btnIcon.setGlyphName("QUESTION");

            // Resetting Line Chart if toggleTable is false
            if (!toggleTable) {
                generateLineChart();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateLineChart() {
        lineChart.getData().clear();
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT rentalDate, sum(paid) FROM rentals WHERE User_username ='"+ LogInController.loggerUsername+"' GROUP BY rentalDate ORDER BY UNIX_TIMESTAMP(rentalDate) DESC LIMIT 15");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextFields.bindAutoCompletion(txtCustomerId, customerIDName);
        TextFields.bindAutoCompletion(txtItemId, inventoryItem);
        toggleTable = true;

        Tooltip tooltip = new Tooltip("Verify Input");
        btnProcced.setTooltip(tooltip);

        txtRentalDate.setValue(LocalDate.now()); //Initializing with current date

        //Getting new connection to db to set new rentalID
        Connection con = DBConnection.getConnection();

        try{
            PreparedStatement ps = con.prepareStatement("SELECT MAX(rentalID) FROM rentals");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                lblId.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Setting up table columns
        rentID.setCellValueFactory(new PropertyValueFactory<>("rentID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        rentalDate.setCellValueFactory(new PropertyValueFactory<>("rentDate"));
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        paid.setCellValueFactory(new PropertyValueFactory<>("payAmount"));
        due.setCellValueFactory(new PropertyValueFactory<>("amountDue"));

        tblRecent.setItems(rentalList);
    }
}
