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
import javafx.stage.Stage;
import main.DBConnection;
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
public class AdministratorGUI implements Initializable {

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

    //This method will take Window Title and Scene location as arguments
    //and will show a new window

    void loadWindow(String title, String URL) {
        try {
            Parent acc = FXMLLoader.load(getClass().getResource(URL));
            Scene s = new Scene(acc);
            Stage stg = new Stage();
            stg.setTitle(title);
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void accUpdate(ActionEvent event) {
        loadWindow("Account Management", "/fxml/accountmanager.fxml");
    }

    @FXML
    void itemTypeManage(ActionEvent event) {
        loadWindow("Item Type Management", "/fxml/itemtypemanager.fxml");
    }

    @FXML
    void addNewEmployee(ActionEvent event) {
        loadWindow("Add Employee", "/fxml/newemployee.fxml");
    }

    @FXML
    void deleteAll(ActionEvent event) {
        Connection con = DBConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("DELETE from accounts");
            PreparedStatement ps2 = con.prepareStatement("DELETE from financialtronrental");
            PreparedStatement ps3 = con.prepareStatement("DELETE from financialtronpurchase");
            PreparedStatement px = con.prepareStatement("DELETE FROM trtypecode");
            PreparedStatement ps6 = con.prepareStatement("DELETE from purchases");
            PreparedStatement ps7 = con.prepareStatement("DELETE from rentals");
            PreparedStatement ps4 = con.prepareStatement("DELETE from item");
            PreparedStatement py = con.prepareStatement("DELETE from itemtypes");
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
                //px.executeUpdate();
                ps6.executeUpdate();
                ps7.executeUpdate();
                ps4.executeUpdate();
                //py.executeUpdate();
                ps5.executeUpdate();

                new PromptDialogGUI("Operation Successful!", "The database is reset to initial state.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void empList(ActionEvent event) {
        loadWindow("Employee Management", "/fxml/employeelist.fxml");
    }


    @FXML
    void showTransactions(ActionEvent event) {
        loadWindow("Transaction List", "/fxml/translist.fxml");
    }

    @FXML
    void totalRents(ActionEvent event) {
        loadWindow("Rental List", "/fxml/rentallist.fxml");
    }

    @FXML
    void totalSell(ActionEvent event) {
        loadWindow("Sell List", "/fxml/selllist.fxml");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
