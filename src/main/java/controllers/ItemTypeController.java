package main.java.controllers;

import com.jfoenix.controls.JFXButton;
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
import main.java.others.DBConnection;
import main.java.others.ItemType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemTypeController implements Initializable {
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
    private JFXButton btnRefresh;
    private boolean updateMode = false; //This is variable will be used to differentiate
                                        //between update mode and new entry mode
                                            //false = new entry; true = update
    @FXML
    void reload(ActionEvent event) {
        //Setting Values to null and normal
        lblType.setText("");
        txtItemType.setText("");
        btnAddIcon.setGlyphName("PLUS");
        updateMode = false;

        setTableData();
    }

    private void loadContents() {
        ItemType selectedItem = tbl.getSelectionModel().getSelectedItem();

        //Setting Values on field
        lblType.setText(selectedItem.getItemTypeID().toString());
        txtItemType.setText(selectedItem.getItemTypeName());
    }

    @FXML
    void addOrUpdateItemType(ActionEvent event) {
        Connection con = DBConnection.getConnection();

       if (updateMode) {
           try {
               PreparedStatement stmtInsert = con.prepareStatement("UPDATE itemtype SET typeName = ? WHERE itemTypeId = ?");

               stmtInsert.setString(1, txtItemType.getText());
               stmtInsert.setInt(2, Integer.parseInt(lblType.getText()));

               stmtInsert.executeUpdate();
               con.close();

               new PromptDialogController("Success!","Updating Entry Successful");
           } catch (SQLException e) {
               new PromptDialogController("Error","Error Code: " + e.getErrorCode());
           }
       } else {
           try {
               PreparedStatement stmtInsert = con.prepareStatement("INSERT INTO itemtype VALUES(?, ?)");

               stmtInsert.setInt(1, Integer.parseInt(lblType.getText()));
               stmtInsert.setString(2, txtItemType.getText());

               stmtInsert.executeUpdate();
               con.close();

               new PromptDialogController("Success!","New Entry Added");
           } catch (SQLException e) {
               new PromptDialogController("Error","Error Code: " + e.getErrorCode());
           }
       }
       reload(null);
    }

    @FXML
    void deleteItemType(ActionEvent event) {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement stmtInsert = con.prepareStatement("DELETE FROM itemtype WHERE itemTypeId = ?");

            stmtInsert.setInt(1, Integer.parseInt(lblType.getText()));

            stmtInsert.executeUpdate();
            con.close();

            new PromptDialogController("Success!","Deleting Entry Successful");
        } catch (SQLException e) {
            if(e.getErrorCode() == 1451) //Has foreign key constraints
                new PromptDialogController("Error","Error Code: " + e.getErrorCode() +"\n" +
                        "Can't delete type. The type has one or more item entries listed.");
            else
                new PromptDialogController("Error","Error Code: " + e.getErrorCode());
        }
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
                Integer typeID = itemRS.getInt(1); //1 = itemTypeID column index
                String typeName = itemRS.getString(2); //2 = itemTypeName column index
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

            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbl.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2)
            {
                updateMode = true;
                btnAddIcon.setGlyphName("SAVE");
                loadContents();
            }
        });
        setTableData();
    }
}
