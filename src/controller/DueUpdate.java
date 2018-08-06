package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sample.DBConnection;
import sample.Dialog;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/7/2018
 * Project: TeslaRentalInventory
 **/
public class DueUpdate implements Initializable {


    @FXML
    private AnchorPane rightPane;

    @FXML
    private JFXTextField txtCustomerId;

    @FXML
    private JFXTextField txtItemId;

    @FXML
    private Label lblCategory;

    @FXML
    private JFXDatePicker txtSellDate;

    @FXML
    private JFXTextField txtPaid;

    @FXML
    private JFXButton btnProcced;

    @FXML
    private FontAwesomeIconView btnIcon;

    @FXML
    private Label lblVerify;

    @FXML
    private JFXTextField txtDue;

    @FXML
    private JFXTextField txtNewPay;

    @FXML
    private JFXTextField txtSellId;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private FontAwesomeIconView btnSearchIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void btnSearchAction(ActionEvent event) {
        Integer sId = Integer.valueOf(txtSellId.getText());

        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM purchases WHERE purchaseID ="+sId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                txtSellId.setText(Integer.valueOf(rs.getInt("purchaseID")).toString());
                txtCustomerId.setText(rs.getString("Customers_customerID"));
                txtItemId.setText(Integer.valueOf(rs.getInt("Item_itemID")).toString());
                txtSellDate.setValue(Date.valueOf(rs.getString("purchaseDate")).toLocalDate());
                txtPaid.setText(Double.valueOf(rs.getDouble("payAmount")).toString());
                txtDue.setText(Double.valueOf(rs.getDouble("amountDue")).toString());

            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateSellDue(ActionEvent event) {

        Double newAmount = Double.valueOf(txtNewPay.getText());
        Double newDue = Double.valueOf(txtDue.getText()) - newAmount;
        Double totalPaid = Double.valueOf(txtPaid.getText()) + newAmount;
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE purchases SET payAmount = "+totalPaid+", amountDue = "+newDue+"WHERE purchaseID = "+Integer.valueOf(txtSellId.getText()));
            ps.executeUpdate();

            new Dialog("Operation Successful!", "The due is updated successfully and recorded into database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
