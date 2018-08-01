package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Account;
import sample.DBConnection;
import sample.Dialog;
import sample.Employee;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class AccountList implements Initializable {

    @FXML
    private JFXTextField txtAccID;

    @FXML
    private JFXTextField txtCusID;

    @FXML
    private TableView<sample.Account> tbl;

    @FXML
    private TableColumn<sample.Account, Integer> accID;

    @FXML
    private TableColumn<sample.Account, String> accName;

    @FXML
    private TableColumn<sample.Account, Integer> cusID;

    @FXML
    private TableColumn<sample.Account, String> issued;

    @FXML
    private TableColumn<sample.Account, String> payType;

    @FXML
    private JFXButton btnAddNew;

    @FXML
    private FontAwesomeIconView btnAddIcon;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private FontAwesomeIconView btnAddIcon1;

    @FXML
    private JFXTextField txtAccName;

    @FXML
    private JFXTextField txtPayMethod;

    @FXML
    private JFXTextField txtEmpName;

    @FXML
    void deleteAcc(ActionEvent event) {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM accounts WHERE acccountID = "+Integer.valueOf(txtAccID.getText()));
            ps.executeUpdate();

            txtPayMethod.getScene().getWindow().hide();
            new Dialog("Operation Successful", "Record deleted");

            con.close();
        } catch (SQLException e) {
            new Dialog("Operation Failed", "Invalid Arguments!");
        }
    }

    @FXML
    void updateAcc(ActionEvent event) {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET acccountID = ?, accountName = ?, accountDetails = ?, Customers_customerID = ?, User_username = ?, payMethod = ? WHERE acccountID = "+Integer.valueOf(txtAccID.getText()));
            ps.setInt(1, Integer.valueOf(txtAccID.getText()));
            ps.setString(2, txtAccName.getText());
            ps.setString(3, "None");
            ps.setInt(4, Integer.valueOf(txtCusID.getText()));
            ps.setString(5, txtEmpName.getText());
            ps.setString(6, txtPayMethod.getText());

            ps.executeUpdate();

            txtPayMethod.getScene().getWindow().hide();
            new Dialog("Operation Successful", "Record updated");
            con.close();
        } catch (SQLException e) {
            new Dialog("Operation Failed", "Invalid Arguments!");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbl.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2)
            {
                loadContents();
            }
        });

        Connection connection = DBConnection.getConnection();
        ObservableList<sample.Account> list = FXCollections.observableArrayList();

        accID.setCellValueFactory(new PropertyValueFactory<>("accID"));
        accName.setCellValueFactory(new PropertyValueFactory<>("accName"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        payType.setCellValueFactory(new PropertyValueFactory<>("paymethod"));
        issued.setCellValueFactory(new PropertyValueFactory<>("user"));

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new sample.Account(rs.getInt(1), rs.getString(2), rs.getInt(4), rs.getString(5), rs.getString(6) ));
            }

            tbl.setItems(list);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadContents() {
        sample.Account a = tbl.getSelectionModel().getSelectedItem();

        txtAccID.setText(Integer.valueOf(a.getAccID()).toString());
        txtAccName.setText(a.getAccName());
        txtCusID.setText(Integer.valueOf(a.getCusID()).toString());
        txtEmpName.setText(a.getUser());
        txtPayMethod.setText(a.getPaymethod());
    }
}
