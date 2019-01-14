package main.java.controllers;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import main.java.others.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class NewEmployeeController implements Initializable {
    @FXML
    private JFXTextField txtUser;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXComboBox<String> cboAccessLevel;
    @FXML
    private JFXPasswordField txtPass;
    @FXML
    private AnchorPane empPane;
    @FXML
    private JFXPasswordField txtPassConf;
    @FXML
    private JFXButton addEmployee;
    @FXML
    void saveEmployee(ActionEvent event) {
        boolean flag = true;

        if(txtUser.getText().equals("") || txtEmail.getText().equals("") || txtPass.equals("") || txtPass.equals("")) {
            flag = false;
            JFXSnackbar s = new JFXSnackbar(empPane);
            s.setStyle("-fx-background-color: red");
            s.show("Fields can not be empty!", 3000);
        } else if(!txtPass.getText().equals(txtPassConf.getText())) {
            flag = false;
            JFXSnackbar s = new JFXSnackbar(empPane);
            s.setStyle("-fx-background-color: red");
            s.show("Passwords did not match", 3000);
        }

        if (flag) {
            Connection con = DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO user VALUES(?, ?, ?, ?)");
                ps.setString(1, txtUser.getText());
                ps.setString(2, txtPass.getText());
                ps.setString(3, txtEmail.getText());
                ps.setString(4, cboAccessLevel.getValue());

                ps.executeUpdate();

                txtPass.getScene().getWindow().hide();
                new PromptDialogController("Operation Successful", "New Employee added! You can now log in with the given credentials.");
            } catch (SQLException e) {
                if(e.getErrorCode() == 1062) {
                    new PromptDialogController("Operation failed", "This username is already taken. Try another!");
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> accessLevel = FXCollections.observableArrayList();
        accessLevel.addAll("Admin", "Employee");
        cboAccessLevel.setItems(accessLevel);
        cboAccessLevel.setValue("Employee");
    }
}
