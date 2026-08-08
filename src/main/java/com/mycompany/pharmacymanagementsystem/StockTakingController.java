/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.StockTakingDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

public class StockTakingController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private TableColumn<StockItem, Integer> actualquantity;

    @FXML
    private Button btncleartbl;

    @FXML
    private Button btnsavestck;

    @FXML
    private TableColumn<StockItem, String> colprocode;

    @FXML
    private TableColumn<StockItem, String> colproname;

    @FXML
    private ComboBox<ProductChoice> comboproductselect;

    @FXML
    private Label datetoday;

    @FXML
    private TableColumn<StockItem, Integer> differcol;

    @FXML
    private Button searchproduct;

    @FXML
    private Label stocktakingnum;

    @FXML
    private TableColumn<StockItem, Integer> systemquantity;

    @FXML
    private TableView<StockItem> tblstoktaking;

    @FXML
    private Label totalstockTaking;


    // =========================================================
    // DAO + LIST
    // =========================================================

    private final StockTakingDAO stockTakingDAO =
            new StockTakingDAO();

    private final ObservableList<StockItem> items =
            FXCollections.observableArrayList();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        setupTable();

        setupDate();
tblstoktaking.setEditable(true);
        setupStockTakingNumber();

        loadProducts();

        tblstoktaking.setItems(items);

        updateTotal();
    }


    // =========================================================
    // SETUP TABLE
    // =========================================================

    private void setupTable() {

        // -----------------------------------------------------
        // PRODUCT CODE
        // -----------------------------------------------------

        colprocode.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getProductCode()
                        )
        );


        // -----------------------------------------------------
        // PRODUCT NAME
        // -----------------------------------------------------

        colproname.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getProductName()
                        )
        );


        // -----------------------------------------------------
        // SYSTEM QUANTITY
        // -----------------------------------------------------

        systemquantity.setCellValueFactory(
                data ->
                        new SimpleIntegerProperty(
                                data.getValue().getSystemQuantity()
                        ).asObject()
        );


        // -----------------------------------------------------
        // ACTUAL QUANTITY
        // -----------------------------------------------------

        actualquantity.setCellValueFactory(
                data ->
                        new SimpleIntegerProperty(
                                data.getValue().getActualQuantity()
                        ).asObject()
        );


        // -----------------------------------------------------
        // DIFFERENCE
        // -----------------------------------------------------

        differcol.setCellValueFactory(
                data ->
                        new SimpleIntegerProperty(
                                data.getValue().getDifference()
                        ).asObject()
        );


        // -----------------------------------------------------
        // ACTUAL QUANTITY CELL
        // -----------------------------------------------------

       actualquantity.setCellFactory(column ->
        new TableCell<StockItem, Integer>() {

            private final javafx.scene.control.TextField textField =
                    new javafx.scene.control.TextField();

            {
                textField.setOnAction(event -> finishEdit());

                textField.focusedProperty().addListener(
                        (obs, oldValue, newValue) -> {

                            if (!newValue && isEditing()) {
                                finishEdit();
                            }
                        }
                );
            }

            private void finishEdit() {

                if (!isEditing()) {
                    return;
                }

                String text =
                        textField.getText().trim();

                try {

                    int value =
                            Integer.parseInt(text);

                    if (value < 0) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Invalid Quantity",
                                "Actual quantity cannot be negative."
                        );

                        cancelEdit();
                        return;
                    }

                    commitEdit(value);

                } catch (NumberFormatException ex) {

                    showAlert(
                            Alert.AlertType.ERROR,
                            "Invalid Quantity",
                            "Actual quantity must be a whole number."
                    );

                    cancelEdit();
                }
            }

            @Override
            public void startEdit() {

                if (isEmpty()) {
                    return;
                }

                super.startEdit();

                textField.setText(
                        String.valueOf(getItem())
                );

                setText(null);
                setGraphic(textField);

                textField.requestFocus();
                textField.selectAll();
            }

            @Override
            public void cancelEdit() {

                super.cancelEdit();

                setText(
                        getItem() == null
                                ? ""
                                : String.valueOf(getItem())
                );

                setGraphic(null);
            }

            @Override
            public void updateItem(
                    Integer item,
                    boolean empty) {

                super.updateItem(item, empty);

                if (empty) {

                    setText(null);
                    setGraphic(null);

                } else if (isEditing()) {

                    textField.setText(
                            String.valueOf(item)
                    );

                    setText(null);
                    setGraphic(textField);

                } else {

                    setText(
                            String.valueOf(item)
                    );

                    setGraphic(null);
                }
            }

            @Override
            public void commitEdit(Integer newValue) {

                super.commitEdit(newValue);

                StockItem item =
                        getTableView()
                                .getItems()
                                .get(getIndex());

                item.setActualQuantity(newValue);

                getTableView().refresh();

                updateTotal();
            }
        }
);


        // -----------------------------------------------------
        // EDIT COMMIT
        // -----------------------------------------------------

        actualquantity.setOnEditCommit(event -> {

            StockItem item =
                    event.getRowValue();

            Integer value =
                    event.getNewValue();

            if (value == null || value < 0) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Invalid Quantity",
                        "Actual quantity cannot be negative."
                );

                tblstoktaking.refresh();

                return;
            }

            item.setActualQuantity(value);

            tblstoktaking.refresh();

            updateTotal();
        });
    }


    // =========================================================
    // DATE
    // =========================================================

    private void setupDate() {

        datetoday.setText(
                LocalDate.now().toString()
        );
    }


    // =========================================================
    // STOCK TAKING NUMBER
    // =========================================================

    private void setupStockTakingNumber() {

        stocktakingnum.setText(
                stockTakingDAO.generateStockTakingNumber()
        );
    }


    // =========================================================
    // LOAD PRODUCTS
    // =========================================================

    private void loadProducts() {

        comboproductselect.getItems().clear();

        ObservableList<ProductChoice> products =
                stockTakingDAO.loadProducts();

        comboproductselect.setItems(products);
    }


    // =========================================================
    // SEARCH / ADD PRODUCT
    // =========================================================

    @FXML
    private void handlesearchpro(ActionEvent event) {

        ProductChoice product =
                comboproductselect.getValue();


        // -----------------------------------------------------
        // PRODUCT NOT SELECTED
        // -----------------------------------------------------

        if (product == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Product Required",
                    "Please select a product."
            );

            return;
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE
        // -----------------------------------------------------

        for (StockItem item : items) {

            if (item.getProductCode()
                    .equalsIgnoreCase(
                            product.getProductCode()
                    )) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Duplicate Product",
                        "This product has already been added."
                );

                return;
            }
        }


        // -----------------------------------------------------
        // GET SYSTEM STOCK
        // -----------------------------------------------------

        StockItem item =
                stockTakingDAO.getProductStock(
                        product.getProductCode()
                );


        if (item == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Product Error",
                    "Unable to load product stock."
            );

            return;
        }


        // -----------------------------------------------------
        // ADD TO TABLE
        // -----------------------------------------------------

        items.add(item);

        updateTotal();

        comboproductselect
                .getSelectionModel()
                .clearSelection();
    }


    // =========================================================
    // UPDATE TOTAL
    // =========================================================

    private void updateTotal() {

    int total = 0;

    for (StockItem item : items) {

        total += item.getActualQuantity();
    }

    totalstockTaking.setText(
            String.valueOf(total)
    );
}
    // =========================================================
    // CLEAR
    // =========================================================

    @FXML
    private void handleclearbtn(ActionEvent event) {

        if (items.isEmpty()) {
            return;
        }


        items.clear();

        updateTotal();
    }


    // =========================================================
    // SAVE
    // =========================================================

    @FXML
    private void handlesavebtn(ActionEvent event) {

        // -----------------------------------------------------
        // CHECK EMPTY
        // -----------------------------------------------------

        if (items.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Empty Stock Taking",
                    "Please add at least one product."
            );

            return;
        }


        // -----------------------------------------------------
        // GET DATA
        // -----------------------------------------------------

        String countNumber =
                stocktakingnum.getText();

        LocalDate date =
                LocalDate.parse(
                        datetoday.getText()
                );


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        boolean saved =
                stockTakingDAO.saveStockTaking(
                        countNumber,
                        date,
                        items
                );


        if (saved) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Stock Taking Saved",
                    "Stock Taking "
                            + countNumber
                            + " has been saved successfully."
            );


            // Clear after successful save
            items.clear();

            updateTotal();

            setupStockTakingNumber();


        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Error",
                    "Unable to save the stock taking."
            );
        }
    }


    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }


    // =========================================================
    // PRODUCT CHOICE MODEL
    // =========================================================

    public static class ProductChoice {

        private final String productCode;
        private final String productName;


        public ProductChoice(
                String productCode,
                String productName) {

            this.productCode = productCode;
            this.productName = productName;
        }


        public String getProductCode() {

            return productCode;
        }


        public String getProductName() {

            return productName;
        }


        @Override
        public String toString() {

            return productCode
                    + " - "
                    + productName;
        }
    }


    // =========================================================
    // STOCK ITEM MODEL
    // =========================================================

    public static class StockItem {

        private final String productCode;
        private final String productName;

        private final int systemQuantity;

        private int actualQuantity;


        public StockItem(
                String productCode,
                String productName,
                int systemQuantity,
                int actualQuantity) {

            this.productCode = productCode;

            this.productName = productName;

            this.systemQuantity = systemQuantity;

            this.actualQuantity = actualQuantity;
        }


        public String getProductCode() {

            return productCode;
        }


        public String getProductName() {

            return productName;
        }


        public int getSystemQuantity() {

            return systemQuantity;
        }


        public int getActualQuantity() {

            return actualQuantity;
        }


        public void setActualQuantity(
                int actualQuantity) {

            this.actualQuantity =
                    actualQuantity;
        }


        public int getDifference() {

            return actualQuantity
                    - systemQuantity;
        }
    }
}