package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.java.others.DBConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class LogInController implements Initializable{
    @FXML
    private JFXButton btnLogIn;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private Label lblWarnUsername;
    @FXML
    private Label lblWarnPassword;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXTextField txtPasswordShown;
    @FXML
    private JFXCheckBox chkPasswordMask;
    @FXML
    private JFXCheckBox chkSaveCredentials;
    private static final String DIALOG_URL = "/main/resources/view/dialog.fxml";
    private static final String RED = "-fx-text-fill: red";
    public static String loggerUsername = "";
    public static String loggerAccessLevel = "";

    @FXML
    void ctrlLogInCheck(ActionEvent event)  {
        userLogger();
    }

    @FXML
    void onEnterKey(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            userLogger();
        }
    }

    @FXML
    public void chkPasswordMaskAction()
    {
        if (chkPasswordMask.isSelected())
        {
            txtPasswordShown.setText(txtPassword.getText());
            txtPasswordShown.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordShown.getText());
            txtPassword.setVisible(true);
            txtPasswordShown.setVisible(false);
        }
    }

    /**
     * This method will set the previous saved username
     * password if any. In addition this method is responsible
     * for password visibility toggling
     */
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        Connection connection = DBConnection.getConnection();

        try {
            PreparedStatement getCredents = connection.prepareStatement("SELECT * FROM usercredents");
            ResultSet resultSet = getCredents.executeQuery();

            if(resultSet.next()) {
                txtUsername.setText(resultSet.getString(1)); //Getting Saved Username
                txtPassword.setText(resultSet.getString(2)); //Getting Saved Password
            }
            txtPasswordShown.setVisible(false);

            txtUsername.setOnMouseClicked(event -> {
                lblWarnUsername.setVisible(false);
            });

            txtPasswordShown.setOnMouseClicked(event -> {
                lblWarnPassword.setVisible(false);
            });

            txtPassword.setOnMouseClicked(event -> {
                lblWarnPassword.setVisible(false);
            });

           connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    private void userLogger() {
        //Taking input from the username & password fields
        String username = txtUsername.getText();
        String password;

        //Getting input from the field in which
        //user inputted password.
	    //Note: We have two password field.
	    //One for visible password and another for hidden.
        if(chkPasswordMask.isSelected())
            password = txtPasswordShown.getText();
        else
            password = txtPassword.getText();

        //Checking if any fields were blank
        //If not then we're attempting to connect to DB

        if (username.equals("")) {
            lblWarnUsername.setVisible(true);
        } else if(password.equals("")) {
            lblWarnPassword.setVisible(true);
        } else {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    //Setting user credentials for further processing
                    loggerUsername = rs.getString("username");
                    loggerAccessLevel = rs.getString("accessLevel");

                    //Checking for Save Credential CheckBox
                    //Upon true value saving new credents in DataBase
                    if(chkSaveCredentials.isSelected()) {
                        PreparedStatement delPrevCredents = con.prepareStatement("DELETE FROM usercredents");
                        delPrevCredents.executeUpdate();

                        PreparedStatement saveCredents = con.prepareStatement("INSERT INTO usercredents VALUES ('"+username+"',"+"'"+password+"')");
                        saveCredents.executeUpdate();
                    }

                    Stage logIn = (Stage) btnLogIn.getScene().getWindow(); //Getting current window

                    Stage base = new Stage();
                    Parent root = null;

                    //Moving to InitializerController Class to load all required main.resources
                    try {
                        root = FXMLLoader.load(getClass().getResource("/main/resources/view/initializer.fxml"));
                        Scene s = new Scene(root);
                        logIn.setScene(s);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    new PromptDialogController("Authentication Error!", "Either username or password did not match!");
                }

                con.close();
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
                e.printStackTrace();

            }
        }
    }

}
