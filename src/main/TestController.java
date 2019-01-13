package main;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TestController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private JFXTextField txtField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Here goes codes on resources.view initialization
    }
}
