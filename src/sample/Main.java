package sample;

/**
 * Author: Afif Al Mamun
 * Written on: 08-Jul-18
 * Project: TeslaRentalInventory
 **/

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root);

        String css = this.getClass().getResource("/css/login.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Log In Prompt");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
       // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
