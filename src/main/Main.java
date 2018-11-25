package main;

/**
 * Author: Afif Al Mamun
 * Written on: 08-Jul-18
 * Project: TeslaRentalInventory
 **/

import controller.PromptDialogGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);

            String css = this.getClass().getResource("/css/login.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Log In Prompt");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("/resource/icons/Accounts_main.png"));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            new PromptDialogGUI("Error!", "Error Occured. Failed to initialize system. Either database server is not online or database dropped.");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
