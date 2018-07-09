package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mysql.cj.protocol.Resultset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class LogIn {
    private static final String URL = "jdbc:mysql://localhost:3306/tri";

    @FXML
    private JFXButton btnLogIn;

    @FXML
    private AnchorPane topPane;

    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    void ctrlLogInCheck(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
        Connection con = DriverManager.getConnection(URL, "root","3267");

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        String sql = "SELECT * FROM accessInfo WHERE username = '" + username + "' AND pass = '" + password +"'";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {

            Stage logIn = (Stage) btnLogIn.getScene().getWindow();
            logIn.close();

            Stage base = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/base.fxml"));
            Scene scene = new Scene(root);
            String css = this.getClass().getResource("/css/base.css").toExternalForm();
            scene.getStylesheets().add(css);
            base.setTitle("Tesla Rental Inventory");
            base.setScene(scene);
            base.show();
        } else
            System.out.println(0);



    }
}
