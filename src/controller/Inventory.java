package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;
import sample.DBConnection;
import sample.Dialog;
import sample.Item;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Author: Afif Al Mamun
 * Written on: 09-Jul-18
 * Project: TeslaRentalInventory
 **/
public class Inventory implements Initializable{


    @FXML
    private JFXTextField txtItemName;

    @FXML
    private JFXComboBox<String> txtType;

    @FXML
    private JFXTextField txtRentRate;

    @FXML
    private Label itemID;

    @FXML
    private Circle imgCustomerPhoto;

    @FXML
    private JFXTextField lblStock;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private Label lblPageIndex, lblMode, lblSearchResults;

    @FXML
    private JFXButton btnPrevEntry;

    @FXML
    private JFXButton btnNextEntry;

    @FXML
    private JFXButton btnListAll;

    @FXML
    private JFXButton btnMostSold;

    @FXML
    private JFXButton btnOutOfStock, btnGoBack, btnDelete;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private FontAwesomeIconView btnAddIcon;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private AnchorPane itemPane;

    @FXML
    private AnchorPane itemListPane;

    @FXML
    private TableView<Item> tbl;

    @FXML
    private TableColumn<Item, Integer> columnItemID;

    @FXML
    private TableColumn<Item, String> columnItemName;

    @FXML
    private TableColumn<Item, String> columnItemType;

    @FXML
    private TableColumn<Item, Boolean> columnForRent;

    @FXML
    private TableColumn<Item, Boolean> columnForSale;

    @FXML
    private TableColumn<Item, Double> columnRentalRate;

    @FXML
    private TableColumn<Item, Double> columnPrice;

    @FXML
    private TableColumn<Item, Integer> columnStock;

    @FXML
    private JFXCheckBox chkRent, chkSale;

    @FXML
    private FontAwesomeIconView btnSearchIcon;


    private static int recordIndex = 0;
    private static int recordSize = 0;
    private sample.Item onView = null;
    private static boolean addFlag = false;
    private static boolean searchDone = false;
    private static String imgPath = null;
    public static TreeMap<String, Integer> itemType = new TreeMap<>();

    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from Initializer Class
    public static ObservableList<Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(LogIn.loggerAccessLevel.equals("Admin")) {
            btnDelete.setDisable(false);
        }
        txtType.setItems(itemTypeNames);
        TextFields.bindAutoCompletion(txtSearch, itemNames);
        initialView();
    }
    private void reloadRecords(){
        itemList.clear();
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement getItemList = connection.prepareStatement("SELECT *" +
                    "FROM item, itemtype WHERE item.ItemType_itemTypeId = itemtype.itemTypeId ORDER BY itemID");
            ResultSet itemResultSet = getItemList.executeQuery();

            while(itemResultSet.next()) {
                Item item = new Item(itemResultSet.getInt("itemID"),
                        itemResultSet.getString("itemName"),
                        itemResultSet.getInt("stock"),
                        false,
                        false,
                        itemResultSet.getDouble("salePrice"),
                        itemResultSet.getDouble("rentRate"),
                        itemResultSet.getString("photo"),
                        itemResultSet.getString("typeName"));

                itemNames.add(itemResultSet.getString("itemName"));

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                {
                    item.setRent(true);
                }
                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                    item.setSale(true);
                }

                itemList.add(item);

            }

            initialView();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialView() {
        itemListPane.setVisible(false);
        recordIndex = 0; //Resetting index value
        recordSize = itemList.size();

        //Tooltip will be activated on Customer's photo if hovered
        Tooltip tooltip = new Tooltip("Double Click to Change Avatar in 'Edit Mode'");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose Photo");

                File imgFile = fc.showOpenDialog(imgCustomerPhoto.getScene().getWindow());

                imgPath = imgFile.toURI().toString();

                if(imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".gif") ||imgPath.contains(".jpeg")) {
                    ImagePattern gg = new ImagePattern(new Image(imgPath));
                    imgCustomerPhoto.setFill(gg);
                } else {
                    new Dialog("File Format Error!", "Please select a valid image file. You can select JPG, JPEG, PNG, GIF");
                }
            }
        });

        //Setting Fields
        btnNextEntry.setOnAction(event -> {
            onView = itemList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            if (recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);
            btnPrevEntry.setDisable(false);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = itemList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            btnNextEntry.setDisable(false);
            if (recordIndex == 0)
                btnPrevEntry.setDisable(true);

        });

        btnNextEntry.setDisable(true); //For purpose ;)

        if (recordSize > 0) {
            onView = itemList.get(recordIndex); //Setting value for current record

            //Setting customer default fields
            recordNavigator();

            //Setting page indexer value
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");

            if (recordSize > 1) {
                btnNextEntry.setDisable(false); //Next entry will be enabled if there is more than one entry
            }
        }

        btnPrevEntry.setDisable(true); //Disabling prevButton Initially


    }

    //This method will set navigate between Items
    private void recordNavigator() {
        lblStock.setStyle("-fx-text-fill: #263238"); //Resetting stock color

        chkRent.setSelected(false);
        chkSale.setSelected(false);
        txtRentRate.setText("0.0");
        txtPrice.setText("0.0");

        itemID.setText(Integer.toString(onView.getId()));
        txtItemName.setText(onView.getName());
        txtType.setValue(onView.getItemType().toString());
        if(onView.isRent()) {
            chkRent.setSelected(true);
            txtRentRate.setText(Double.toString(onView.getRentRate()));
        }
        if(onView.isSale()) {
            chkSale.setSelected(true);
            txtPrice.setText(Double.toString(onView.getSalePrice()));
        }
        lblStock.setText(Integer.toString(onView.getStock()));

        if(onView.getStock() <= 5) //Setting stock color red if it's very limited
            lblStock.setStyle("-fx-text-fill: red");

        //Setting Image
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/resource/icons/trolley.png"));
            imgCustomerPhoto.setFill(img);
        } else {
            try {
                imgPath = onView.getPhoto();

                File tmpPath = new File(imgPath.replace("file:", ""));

                if(tmpPath.exists()) {
                    ImagePattern img = new ImagePattern(new Image(imgPath));
                    imgCustomerPhoto.setFill(img);
                } else {
                    imgPath = null;
                    ImagePattern img = new ImagePattern(new Image("/resource/icons/trolley.png"));
                    imgCustomerPhoto.setFill(img);
                }

            } catch (Exception e) {
                //Fallback photo in case image not found
                ImagePattern img = new ImagePattern(new Image("/resource/icons/trolley.png"));
                imgCustomerPhoto.setFill(img);
            }
        }

    }

    @FXML
    void listAllItems(ActionEvent event) {
        btnGoBack.setOnAction(e -> {
            itemListPane.setVisible(false);  //Setting item list pane visible
            itemPane.setVisible(true); //Setting item pane visible
        });
        tbl.setItems(itemList);
        listView();
    }

    @FXML
    void btnDelAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/x-button.png").toString()));

        alert.setHeaderText("Do you really want to delete this entry?");
        alert.setContentText("Press OK to confirm, Cancel to go back");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM  item WHERE itemID = "+Integer.valueOf(itemID.getText()));
                ps.executeUpdate();

                new Dialog("Operation Successful.", "Item is deleted from the database. Restart or refresh to see effective result.");
                reloadRecords();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void outOfStockList(ActionEvent event) {

        btnGoBack.setOnAction(e -> {
            itemListPane.setVisible(false);  //Setting item list pane visible
            itemPane.setVisible(true); //Setting item pane visible
        });

        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM item, itemtype WHERE itemTypeId = ItemType_itemTypeId AND stock ="+0);
            ResultSet itemResultSet = ps.executeQuery();

            ObservableList<Item> outOfStk = FXCollections.observableArrayList();

            while(itemResultSet.next()) {
                Item item = new Item(itemResultSet.getInt("itemID"),
                        itemResultSet.getString("itemName"),
                        itemResultSet.getInt("stock"),
                        false,
                        false,
                        itemResultSet.getDouble("salePrice"),
                        itemResultSet.getDouble("rentRate"),
                        itemResultSet.getString("photo"),
                        itemResultSet.getString("typeName"));

                itemNames.add(itemResultSet.getString("itemName"));

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                {
                    item.setRent(true);
                }
                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                }

                btnGoBack.setOnAction(e -> {
                    itemListPane.setVisible(false);  //Setting item list pane visible
                    itemPane.setVisible(true); //Setting item pane visible
                });

                outOfStk.add(item);

            }

            tbl.setItems(outOfStk);

            listView();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void listView() {
        itemPane.setVisible(false); //Setting default item pane not visible
        itemListPane.setVisible(true); //Setting item list visible

        columnItemID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnItemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        columnForRent.setCellValueFactory(new PropertyValueFactory<>("rent"));
        columnForSale.setCellValueFactory(new PropertyValueFactory<>("sale"));
        columnRentalRate.setCellValueFactory(new PropertyValueFactory<>("rentRate"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

    }

    @FXML
    void btnAddMode(ActionEvent event) {
        if(addFlag) {
            addFlag = false; //Resetting addFlag value.
            btnAddIcon.setGlyphName("PLUS");

            //Enabling other buttons
            btnPrevEntry.setDisable(false);
            btnNextEntry.setDisable(false);
            btnListAll.setDisable(false);
            btnSearch.setDisable(false);
            btnMostSold.setDisable(false);
            btnOutOfStock.setDisable(false);

            String defColor = "#263238";

            //Changing Focus Color
            txtItemName.setUnFocusColor(Color.web(defColor));
            txtPrice.setUnFocusColor(Color.web(defColor));
            txtRentRate.setUnFocusColor(Color.web(defColor));
            txtType.setUnFocusColor(Color.web(defColor));
            txtSearch.setUnFocusColor(Color.web(defColor));

            //Setting Label
            lblMode.setText("Navigation Mode");

            reloadRecords();


        } else {
            Connection con = DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT max(itemID) FROM item");
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    itemID.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
                }

                addFlag = true; //Setting flag true to enable exit mode
                btnAddIcon.setGlyphName("UNDO"); //Changing glyph

                //Setting Label
                lblMode.setText("Entry Mode");

                ImagePattern img = new ImagePattern(new Image("/resource/icons/trolley.png"));
                imgCustomerPhoto.setFill(img);

                //Disabling other buttons
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnOutOfStock.setDisable(true);
                btnMostSold.setDisable(true);
                btnListAll.setDisable(true);
                btnSearch.setDisable(true);



                //Cleaning fields
                txtItemName.setText("");
                txtType.setValue("");
                txtRentRate.setText("");
                txtPrice.setText("");
                imgPath = null;
                lblStock.setText("");
            } catch (SQLException e) {
                new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }
    }

    @FXML
    void btnSearchAction(ActionEvent event) {
        if (searchDone) {
            searchDone = false;
            lblSearchResults.setVisible(false);
            itemList = tempList; //Reassigning customers List
            recordSize = itemList.size();
            btnSearch.setTooltip(new Tooltip("Search with customers name or id"));
            btnSearchIcon.setGlyphName("SEARCH");
            initialView();
        } else {
            Connection con = DBConnection.getConnection();

            String idSQL = "SELECT * FROM item, itemtype WHERE itemID = ? AND itemTypeId = ItemType_itemTypeId";
            String nameSQL = "SELECT * FROM item, itemtype WHERE itemName COLLATE UTF8_GENERAL_CI like ? AND itemTypeId = ItemType_itemTypeId";

            ObservableList<Item> searchResult = FXCollections.observableArrayList(); //list to hold search result

            try {
                //Checking if input field is a number then searching with ID
                Integer id = Integer.valueOf(txtSearch.getText());
                PreparedStatement preparedStatement = con.prepareStatement(idSQL);
                preparedStatement.setInt(1, id);

                ResultSet itemResultSet = preparedStatement.executeQuery();

                //Getting values from customers result set
                while (itemResultSet.next()) {
                    Item item = new Item(itemResultSet.getInt("itemID"),
                            itemResultSet.getString("itemName"),
                            itemResultSet.getInt("stock"),
                            false,
                            false,
                            itemResultSet.getDouble("salePrice"),
                            itemResultSet.getDouble("rentRate"),
                            itemResultSet.getString("photo"),
                            itemResultSet.getString("typeName"));

                    if(itemResultSet.getString("rentalOrSale").contains("Rental")) {
                        item.setRent(true);
                    }
                    if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                        item.setSale(true);
                    }

                    searchResult.add(item);

                }

                if (searchResult.size() <= 0) {
                    lblSearchResults.setText("No Results Found!");
                    lblSearchResults.setVisible(true);
                } else {
                    tempList = FXCollections.observableArrayList(itemList);
                    searchDone = true;
                    btnSearchIcon.setGlyphName("CLOSE");
                    btnSearch.setTooltip(new Tooltip("Reset Full List"));
                    itemList = searchResult; //Assigning search result to customerList
                    recordSize = searchResult.size();
                    lblSearchResults.setText(recordSize + " results found!");
                    lblSearchResults.setVisible(true);
                    initialView();

                    con.close();
                }

            } catch (NumberFormatException eN) {
                try {
                    PreparedStatement preparedStatement2 = con.prepareStatement(nameSQL);
                    preparedStatement2.setString(1, "%" + txtSearch.getText() + "%");

                    ResultSet itemResultSet = preparedStatement2.executeQuery();

                    //Getting values from customers result set
                    while (itemResultSet.next()) {
                        Item item = new Item(itemResultSet.getInt("itemID"),
                                itemResultSet.getString("itemName"),
                                itemResultSet.getInt("stock"),
                                false,
                                false,
                                itemResultSet.getDouble("salePrice"),
                                itemResultSet.getDouble("rentRate"),
                                itemResultSet.getString("photo"),
                                itemResultSet.getString("typeName"));

                        if(itemResultSet.getString("rentalOrSale").contains("Rental")) {
                            item.setRent(true);
                        }
                        if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                            item.setSale(true);
                        }
                        searchResult.add(item);
                    }

                    if (searchResult.size() <= 0) {
                        lblSearchResults.setText("No Results Found!");
                        lblSearchResults.setVisible(true);
                    } else {
                        tempList = FXCollections.observableArrayList(itemList);
                        searchDone = true;
                        btnSearchIcon.setGlyphName("CLOSE");
                        btnSearch.setTooltip(new Tooltip("Reset Full List"));
                        itemList = searchResult; //Assigning search result to customerList
                        recordSize = searchResult.size();
                        lblSearchResults.setText(recordSize + " results found!");
                        lblSearchResults.setVisible(true);
                        initialView();

                        con.close();
                    }

                } catch (SQLException eS2) {

                    eS2.printStackTrace();
                    new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + eS2.getErrorCode());
                }

            } catch (SQLException eS) {
                eS.printStackTrace();
                new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + eS.getErrorCode());
            }
        }

    }

    @FXML
    void saveEntry(ActionEvent event) {
        if (addFlag) {
            boolean entryFlag = true;
            if (txtItemName.getText().equals("")) {
                txtItemName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(chkSale.isSelected() && txtPrice.getText().equals("")) {
                txtPrice.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(!chkRent.isSelected() && !chkSale.isSelected()) {
                entryFlag = false;
            }

            if(chkRent.isSelected() && txtRentRate.getText().equals("")) {
                txtRentRate.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(txtType.getValue().equals("")) {
                txtType.setUnFocusColor(Color.web("red"));
                entryFlag = false;;
            }

            if(entryFlag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Entry");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to add this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    Connection con = DBConnection.getConnection();

                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO item VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");

                        ps.setInt(1, Integer.valueOf(itemID.getText()));
                        ps.setString(2, txtItemName.getText());
                        ps.setString(3, "null");
                        ps.setInt(4, Integer.valueOf(lblStock.getText()));

                        if(chkRent.isSelected() && chkSale.isSelected())
                            ps.setString(5, "Rental,Sale");
                        else if(chkSale.isSelected())
                            ps.setString(5, "Sale");
                        else if(chkRent.isSelected())
                            ps.setString(5, "Rental");

                        Double salePrice = 0.0;

                        if(!txtPrice.getText().equals("")) {
                            salePrice = Double.valueOf(txtPrice.getText());
                        }

                        Double rentPrice = 0.0;

                        if(!txtRentRate.getText().equals("")) {
                            rentPrice = Double.valueOf(txtRentRate.getText());
                        }

                        ps.setDouble(6, salePrice);
                        ps.setDouble(7, rentPrice);
                        ps.setString(8, imgPath);
                        ps.setInt(9, itemType.get(txtType.getValue()));

                        ps.executeUpdate();

                        new Dialog("Operation Successful!", "New Item Added!");


                    } catch (SQLException e) {
                        new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                    }
                }
            } else {
                JFXSnackbar snackbar = new JFXSnackbar(itemPane);
                snackbar.show("One or more fields are empty!", 3000);
            }
        } else {
            boolean entryFlag = true;
            if (txtItemName.getText().equals("")) {
                txtItemName.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(chkSale.isSelected() && txtPrice.getText().equals("")) {
                txtPrice.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(!chkRent.isSelected() && !chkSale.isSelected()) {
                entryFlag = false;
            }

            if(chkRent.isSelected() && txtRentRate.getText().equals("")) {
                txtRentRate.setUnFocusColor(Color.web("red"));
                entryFlag = false;
            }

            if(txtType.getValue().equals("")) {
                txtType.setUnFocusColor(Color.web("red"));
                entryFlag = false;;
            }


            if(entryFlag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/resource/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to update this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    Connection con = DBConnection.getConnection();
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE item SET itemID = ?, itemName = ?, description = ?," +
                                "stock = ?, rentalOrSale = ?, salePrice = ?, rentRate = ?, photo = ?, ItemType_itemTypeId = ? WHERE itemID = "+Integer.valueOf(itemID.getText()));
                        ps.setInt(1, Integer.valueOf(itemID.getText()));
                        ps.setString(2, txtItemName.getText());
                        ps.setString(3, "null");
                        ps.setInt(4, Integer.valueOf(lblStock.getText()));

                        if(chkRent.isSelected() && chkSale.isSelected())
                            ps.setString(5, "Rental,Sale");
                        else if(chkSale.isSelected())
                            ps.setString(5, "Sale");
                        else if(chkRent.isSelected())
                            ps.setString(5, "Rental");

                        Double salePrice = 0.0;

                        if(!txtPrice.getText().equals("")) {
                            salePrice = Double.valueOf(txtPrice.getText());
                        }

                        Double rentPrice = 0.0;

                        if(!txtRentRate.getText().equals("")) {
                            rentPrice = Double.valueOf(txtRentRate.getText());
                        }

                        ps.setDouble(6, salePrice);
                        ps.setDouble(7, rentPrice);
                        ps.setString(8, imgPath);
                        ps.setInt(9, itemType.get(txtType.getValue()));

                        ps.executeUpdate();

                        new Dialog("Operation Successful!", "Entry updated!");

                        reloadRecords();

                    } catch (SQLException e) {
                        e.printStackTrace();
                        new Dialog("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
                    }
                }
            }

        }
    }

}
