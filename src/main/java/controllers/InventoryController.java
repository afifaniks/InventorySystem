package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import main.java.others.DBConnection;
import main.java.others.Item;

import java.io.File;
import java.net.URL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Author: Afif Al Mamun
 * Written on: 09-Jul-18
 * Project: TeslaRentalInventory
 **/
public class InventoryController implements Initializable{
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
    private JFXTextField txtStock;
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
    private Item onView = null;
    /**
     * addFlag will differentiate b/w Adding a new entry
     * and updating an existing entry.
     * True: New Record Entry Mode
     * False: Updating an Existing Entry
     */
    private static boolean addFlag = false;
    private static boolean searchDone = false;
    private static String imgPath = null;
    public static TreeMap<String, Integer> itemType = new TreeMap<>();
    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(LogInController.loggerAccessLevel.equals("Admin")) {
            btnDelete.setDisable(false);
        }
        txtType.setItems(itemTypeNames);
        TextFields.bindAutoCompletion(txtSearch, itemNames);
        setView();
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

            setView();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setView() {
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

                if(imgFile != null) { //This block will be only executed if there is any file chosen
                    imgPath = imgFile.toURI().toString();

                    if(imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".gif") ||imgPath.contains(".jpeg")) {
                        ImagePattern gg = new ImagePattern(new Image(imgPath));
                        imgCustomerPhoto.setFill(gg);
                    } else {
                        new PromptDialogController("File Format Error!", "Please select a valid image file. You can select JPG, JPEG, PNG, GIF");
                    }
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
        txtStock.setStyle("-fx-text-fill: #263238"); //Resetting stock color

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
        txtStock.setText(Integer.toString(onView.getStock()));

        if(onView.getStock() <= 5) //Setting stock color red if it's very limited
            txtStock.setStyle("-fx-text-fill: red");

        //Setting Image
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/main/resources/icons/trolley.png"));
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
                    ImagePattern img = new ImagePattern(new Image("/main/resources/icons/trolley.png"));
                    imgCustomerPhoto.setFill(img);
                }

            } catch (Exception e) {
                //Fallback photo in case image not found
                ImagePattern img = new ImagePattern(new Image("/main/resources/icons/trolley.png"));
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
        alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/x-button.png").toString()));

        alert.setHeaderText("Do you really want to delete this entry?");
        alert.setContentText("Press OK to confirm, Cancel to go back");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM  item WHERE itemID = "+Integer.valueOf(itemID.getText()));
                ps.executeUpdate();

                new PromptDialogController("Operation Successful.", "Item is deleted from the database. Restart or refresh to see effective result.");
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
            btnOutOfStock.setDisable(false);
            btnDelete.setDisable(false);

            String defColor = "#263238";

            //Changing Focus Color
            txtItemName.setUnFocusColor(Color.web(defColor));
            txtPrice.setUnFocusColor(Color.web(defColor));
            txtRentRate.setUnFocusColor(Color.web(defColor));
            txtType.setUnFocusColor(Color.web(defColor));
            txtSearch.setUnFocusColor(Color.web(defColor));
            txtStock.setUnFocusColor(Color.web(defColor));

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

                ImagePattern img = new ImagePattern(new Image("/main/resources/icons/trolley.png"));
                imgCustomerPhoto.setFill(img);

                //Disabling other buttons
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnOutOfStock.setDisable(true);
                btnListAll.setDisable(true);
                btnSearch.setDisable(true);
                btnDelete.setDisable(true);

                //Cleaning fields
                txtItemName.setText("");
                txtType.setValue("");
                txtRentRate.setText("");
                txtPrice.setText("");
                imgPath = null;
                txtStock.setText("");
            } catch (SQLException e) {
                new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }
    }

    /**
     * This method search into database with the id of a item and returns the result
     * @param id: ID of the item
     * @return: The search result of the query
     */
    private ObservableList<Item> searchWithID(Integer id) {
        Connection con = DBConnection.getConnection();

        String idSQL = "SELECT * FROM item, itemtype WHERE itemID = ? AND itemTypeId = ItemType_itemTypeId";

        ObservableList<Item> searchResult = FXCollections.observableArrayList(); //list to hold search result

        try {
            PreparedStatement preparedStatement = con.prepareStatement(idSQL);
            preparedStatement.setInt(1, id);

            ResultSet itemResultSet = preparedStatement.executeQuery();

            //Getting values from Items result set
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

                if (itemResultSet.getString("rentalOrSale").contains("Rental")) {
                    item.setRent(true);
                }
                if (itemResultSet.getString("rentalOrSale").contains("Sale")) {
                    item.setSale(true);
                }

                searchResult.add(item);
            }

            con.close();

        } catch (SQLException e) {
            new PromptDialogController("SQL Error!",
                    "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

        return searchResult;
    }

    /**
     * This method search into the customer database with a string and
     * returns any records that has a match with the search string
     * @param name: Name of the item
     * @return: List of the result
     */

    private ObservableList<Item> searchWithName(String name) {
        Connection con = DBConnection.getConnection();

        String nameSQL = "SELECT * FROM item, itemtype WHERE itemName COLLATE UTF8_GENERAL_CI like ? AND itemTypeId = ItemType_itemTypeId";

        ObservableList<Item> searchResult = FXCollections.observableArrayList(); //list to hold search result

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

            con.close();

        } catch (SQLException e) {
            new PromptDialogController("SQL Error!",
                    "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }

        return searchResult;
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
            setView();
        } else {
            ObservableList<Item> searchResult = FXCollections.observableArrayList(); //list to hold search result
            try {
                // Checking if input field is a number then searching with ID
                // If parsing of Integer fails then we shall try to search
                // with name instead
                Integer id = Integer.valueOf(txtSearch.getText());
                searchResult = searchWithID(id);
            } catch (NumberFormatException e) {
                String name = txtSearch.getText();
                searchResult = searchWithName(name);
            } finally {
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
                    setView();
                }
            }

        }
    }

    private boolean checkFields() {
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

        if(txtStock.getText().equals("")) {
            txtStock.setUnFocusColor(Color.web("red"));
            entryFlag = false;;
        }

        return entryFlag;
    }

    private void addRecordToDatabase() {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO item VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

            ps.setInt(1, Integer.valueOf(itemID.getText()));
            ps.setString(2, txtItemName.getText());
            ps.setInt(3, Integer.valueOf(txtStock.getText()));

            if(chkRent.isSelected() && chkSale.isSelected())
                ps.setString(4, "Rental,Sale");
            else if(chkSale.isSelected())
                ps.setString(4, "Sale");
            else if(chkRent.isSelected())
                ps.setString(4, "Rental");

            Double salePrice = 0.0;

            if(!txtPrice.getText().equals("")) {
                salePrice = Double.valueOf(txtPrice.getText());
            }

            Double rentPrice = 0.0;

            if(!txtRentRate.getText().equals("")) {
                rentPrice = Double.valueOf(txtRentRate.getText());
            }

            ps.setDouble(5, salePrice);
            ps.setDouble(6, rentPrice);
            ps.setString(7, imgPath);
            ps.setInt(8, itemType.get(txtType.getValue()));

            ps.executeUpdate();

            new PromptDialogController("Operation Successful!", "New Item Added!");


        } catch (SQLException e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    private void updateRecord () {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE item SET itemID = ?, itemName = ?," +
                    "stock = ?, rentalOrSale = ?, salePrice = ?, rentRate = ?, photo = ?, ItemType_itemTypeId = ? WHERE itemID = "+Integer.valueOf(itemID.getText()));
            ps.setInt(1, Integer.valueOf(itemID.getText()));
            ps.setString(2, txtItemName.getText());
            ps.setInt(3, Integer.valueOf(txtStock.getText()));

            if(chkRent.isSelected() && chkSale.isSelected())
                ps.setString(4, "Rental,Sale");
            else if(chkSale.isSelected())
                ps.setString(4, "Sale");
            else if(chkRent.isSelected())
                ps.setString(4, "Rental");

            Double salePrice = 0.0;

            if(!txtPrice.getText().equals("")) {
                salePrice = Double.valueOf(txtPrice.getText());
            }

            Double rentPrice = 0.0;

            if(!txtRentRate.getText().equals("")) {
                rentPrice = Double.valueOf(txtRentRate.getText());
            }

            ps.setDouble(5, salePrice);
            ps.setDouble(6, rentPrice);
            ps.setString(7, imgPath);
            ps.setInt(8, itemType.get(txtType.getValue()));

            ps.executeUpdate();

            new PromptDialogController("Operation Successful!", "Entry updated!");

            reloadRecords();

        } catch (SQLException e) {
            e.printStackTrace();
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    @FXML
    void btnSaveAction(ActionEvent event) {
        if (addFlag) {
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Entry");
                alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to add this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    addRecordToDatabase();
                }
            } else {
                JFXSnackbar snackbar = new JFXSnackbar(itemPane);
                snackbar.show("One or more fields are empty!", 3000);
            }
        } else {
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to update this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    updateRecord();
                }
            }

        }
    }

}
