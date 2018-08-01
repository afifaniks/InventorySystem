package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.DBConnection;
import sample.Dialog;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/23/2018
 * Project: TeslaRentalInventory
 **/
public class Administrator implements Initializable {

    @FXML
    private JFXButton btnLastThirty, btnTrans;

    @FXML
    private JFXButton btnUpdateEmp;

    @FXML
    private JFXButton btnTotalRents;

    @FXML
    private JFXButton btnTotalSell;

    @FXML
    private JFXButton btnAddNew;

    @FXML
    private JFXButton btnRemoveEmployee;

    @FXML
    private JFXButton btEmpList;

    @FXML
    private JFXButton btnTopTen;

    @FXML
    private JFXButton btnMostDue;

    @FXML
    private JFXButton btnAccDelete;

    @FXML
    private JFXButton btnAccUpdate;

    @FXML
    void accUpdate(ActionEvent event) {
        try {
            Parent acc = FXMLLoader.load(getClass().getResource("/fxml/accountlist.fxml"));
            Scene s = new Scene(acc);
            Stage stg = new Stage();
            stg.setTitle("Account Management");
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addNewEmployee(ActionEvent event) {
        try {
            Parent addEmp = FXMLLoader.load(getClass().getResource("/fxml/newemployee.fxml"));
            Scene s = new Scene(addEmp);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteAll(ActionEvent event) {
        Connection con = DBConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("DELETE from accounts");
            PreparedStatement ps2 = con.prepareStatement("DELETE from financialtronrental");
            PreparedStatement ps3 = con.prepareStatement("DELETE from financialtronpurchase");
            PreparedStatement ps6 = con.prepareStatement("DELETE from purchases");
            PreparedStatement ps7 = con.prepareStatement("DELETE from rentals");
            PreparedStatement ps4 = con.prepareStatement("DELETE from item");
            PreparedStatement ps5 = con.prepareStatement("DELETE from customers");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/x-button.png").toString()));

            alert.setHeaderText("Do you really want to delete everything? Once it is done it can not be undone!");
            alert.setContentText("Press OK to confirm, Cancel to go back");

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                ps.executeUpdate();
                ps2.executeUpdate();
                ps3.executeUpdate();
                ps6.executeUpdate();
                ps7.executeUpdate();
                ps4.executeUpdate();
                ps5.executeUpdate();
            }

            new Dialog("Operation Successful!", "The database is reset to initial state.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void empList(ActionEvent event) {
        try {
            Parent listView = FXMLLoader.load(getClass().getResource("/fxml/employeelist.fxml"));
            Scene s = new Scene(listView);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void showTransactions(ActionEvent event) {
        System.out.println(111);
        try {
            Parent trans = FXMLLoader.load(getClass().getResource("/fxml/translist.fxml"));
            Scene s = new Scene(trans);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void totalRents(ActionEvent event) {

        try {
            Parent rentList = FXMLLoader.load(getClass().getResource("/fxml/rentallist.fxml"));
            Scene s = new Scene(rentList);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void totalSell(ActionEvent event) {
        try {
            Parent sellsList = FXMLLoader.load(getClass().getResource("/fxml/selllist.fxml"));
            Scene s = new Scene(sellsList);
            Stage stg = new Stage();
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
