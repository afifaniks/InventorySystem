package controller;

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
import java.io.IOException;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class LogIn implements Initializable{
    private static final String URL = "jdbc:mysql://localhost:3306/tri?autoReconnect=yes&useSSL=no";
    private static final String DIALOG_URL = "/fxml/dialog.fxml";
    private static final String RED = "-fx-text-fill: red";
    public static String loggerUsername = "";
    public static String loggerAccessLevel = "";

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

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
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

        txtUsername.setText("kamla");
        txtPassword.setText("0000");
    }

    private void userLogger() {
        //Taking input from the username & password fields
        String username = txtUsername.getText();
        String password;

        //Checking from the actual selected field
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
                Connection con = DriverManager.getConnection(URL, "root", "3267");

                String sql = "SELECT * FROM accessInfo WHERE username = '" + username + "' AND pass = '" + password + "'";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    //Setting user credentials for further processing
                    loggerUsername = rs.getString("username");
                    loggerAccessLevel = rs.getString("accessLevel");

                    Stage logIn = (Stage) btnLogIn.getScene().getWindow();
                    logIn.close();

                    Stage base = new Stage();
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/base.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene scene = new Scene(root);
                    String css = this.getClass().getResource("/css/base.css").toExternalForm();
                    scene.getStylesheets().add(css);
                    base.setTitle("Tesla Rental Inventory");
                    base.setScene(scene);
                    base.show();
                }
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
                if (e.getErrorCode() == 0) { //Error Code 0: database server offline
                    new sample.Dialog("Error!", "Database server is offline!");
                }
            }
        }
    }

}
