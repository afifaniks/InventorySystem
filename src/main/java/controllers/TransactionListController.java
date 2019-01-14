package main.java.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.others.DBConnection;
import main.java.others.Transact;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class TransactionListController implements Initializable {
    @FXML
    private Label today;
    @FXML
    private TableView<Transact> tblP;
    @FXML
    private TableColumn<Transact, Integer> trIDP;
    @FXML
    private TableColumn<Transact, String> trDatePurch;
    @FXML
    private TableColumn<Transact, Integer> trAccP;
    @FXML
    private TableColumn<Transact, Integer> purchID;
    @FXML
    private TableColumn<Transact, String> trIssueP;
    @FXML
    private TableView<Transact> tblR;
    @FXML
    private TableColumn<Transact, Integer> trIDR;
    @FXML
    private TableColumn<Transact, String> trDateR;
    @FXML
    private TableColumn<Transact, Integer> trAccR;
    @FXML
    private TableColumn<Transact, Integer> rentalID;
    @FXML
    private TableColumn<Transact, String> trIssuedR;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        today.setText(LocalDate.now().toString());

        ObservableList<Transact> purchase = FXCollections.observableArrayList();
        ObservableList<Transact> rent = FXCollections.observableArrayList();

        trIDP.setCellValueFactory(new PropertyValueFactory<>("trID"));
        trAccP.setCellValueFactory(new PropertyValueFactory<>("accID"));
        trDatePurch.setCellValueFactory(new PropertyValueFactory<>("date"));
        purchID.setCellValueFactory(new PropertyValueFactory<>("purchaseOrRentID"));
        trIssueP.setCellValueFactory(new PropertyValueFactory<>("issuedBy"));
        trIDR.setCellValueFactory(new PropertyValueFactory<>("trID"));
        trAccR.setCellValueFactory(new PropertyValueFactory<>("accID"));
        trDateR.setCellValueFactory(new PropertyValueFactory<>("date"));
        rentalID.setCellValueFactory(new PropertyValueFactory<>("purchaseOrRentID"));
        trIssuedR.setCellValueFactory(new PropertyValueFactory<>("issuedBy"));

        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM financialtronpurchase");
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM financialtronrental");

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            while(rs.next()) {
                purchase.add(new Transact(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(5), rs.getString(6)));
            }

            while(rs2.next()) {
                rent.add(new Transact(rs2.getInt(1), rs2.getString(2), rs2.getInt(3), rs2.getInt(5), rs2.getString(6)));
            }

            tblP.setItems(purchase);
            tblR.setItems(rent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
