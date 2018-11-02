package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.DBConnection;
import main.ItemType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemTypeGUI implements Initializable {
    //TODO: ADD Option TO DELETE AND UPDATE ITEMTYPE
    @FXML
    private TableView<ItemType> tbl;

    @FXML
    private TableColumn<ItemType, Integer> typeID;

    @FXML
    private TableColumn<ItemType, String> typeName;

    @FXML
    private TableColumn<ItemType, Integer> totalItems;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private FontAwesomeIconView btnAddIcon;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private FontAwesomeIconView btnAddIcon1;

    @FXML
    private Label lblType;

    @FXML
    private JFXTextField txtItemType;

    @FXML
    void addItemType(ActionEvent event) {

    }

    @FXML
    void deleteItemType(ActionEvent event) {

    }

    private void setTableData() {
        //Setting cell value factory of the table
        typeID.setCellValueFactory(new PropertyValueFactory<>("itemTypeID"));
        typeName.setCellValueFactory(new PropertyValueFactory<>("itemTypeName"));
        totalItems.setCellValueFactory(new PropertyValueFactory<>("totalItems"));

        //Getting Data From Database
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement stmtItemTypes = con.prepareStatement("SELECT * FROM itemtype");
            ResultSet itemRS = stmtItemTypes.executeQuery();

            ObservableList<ItemType> typeList = FXCollections.observableArrayList();

            while(itemRS.next()) {
                Integer typeID = itemRS.getInt(1); //0 = itemTypeID column index
                String typeName = itemRS.getString(2); //1 = Name obviously
                //Getting total number of items of that type
                PreparedStatement stmtItemCount = con.prepareStatement("SELECT COUNT(*) FROM item WHERE ItemType_itemTypeId ="+typeID);

                ResultSet totalCounterRS = stmtItemCount.executeQuery();
                Integer totalItem = 0;

                while(totalCounterRS.next()) {
                    totalItem = totalCounterRS.getInt(1);
                }

                typeList.add(new ItemType(typeID, typeName, totalItem));

            }

            tbl.setItems(typeList);

            PreparedStatement stmtMaximumID = con.prepareStatement("SELECT MAX(itemTypeId) from itemtype");
            ResultSet getMaxValueID = stmtMaximumID.executeQuery();

            while(getMaxValueID.next()) {
                lblType.setText(Integer.toString(getMaxValueID.getInt(1) + 1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTableData();

    }
}
