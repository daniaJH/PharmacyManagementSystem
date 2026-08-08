/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NewPurchaseController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private TableColumn<PurchaseItem, Number> Qty;

    @FXML
    private TextField Qtytxt;

    @FXML
    private Button addbtnpurc;

    @FXML
    private TextField batchno;

    @FXML
    private TextField buyprice;

    @FXML
    private Button clearbtnpur;

    @FXML
    private TableColumn<PurchaseItem, Number> colbuyprice;

    @FXML
    private TableColumn<PurchaseItem, LocalDate> colexpiry;

    @FXML
    private TableColumn<PurchaseItem, String> colnumbatch;

    @FXML
    private TableColumn<PurchaseItem, String> colprocode;

    @FXML
    private TableColumn<PurchaseItem, String> colproname;

    @FXML
    private TableColumn<PurchaseItem, Void> colremove;

    @FXML
    private ComboBox<ProductChoice> combopro;

    @FXML
    private DatePicker datepiker;

    @FXML
    private Button savebtnpur;

    @FXML
    private TableView<PurchaseItem> tblNpur;

    @FXML
    private Label total;

    @FXML
    private Label txtpurdate;

    @FXML
    private Label txtpurnum;


    // =========================================================
    // DAO + LIST
    // =========================================================

    private final PurchaseDAO purchaseDAO =
            new PurchaseDAO();

    private final ObservableList<PurchaseItem> items =
            FXCollections.observableArrayList();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        setupTable();

        loadProducts();

        setupPurchaseNumber();

        setupDate();

        tblNpur.setItems(items);

        total.setText("$0.00");

        addbtnpurc.setOnAction(
                event -> handleAdd()
        );

        clearbtnpur.setOnAction(
                event -> handleClear()
        );

        savebtnpur.setOnAction(
                event -> handleSave()
        );
    }


    // =========================================================
    // PURCHASE NUMBER
    // =========================================================
@FXML
    private void setupPurchaseNumber() {

        txtpurnum.setText(
                purchaseDAO.generatePurchaseNumber()
        );
    }


    // =========================================================
    // DATE
    // =========================================================
@FXML
    private void setupDate() {

        LocalDate today = LocalDate.now();

        txtpurdate.setText(
                today.toString()
        );
    }


    // =========================================================
    // LOAD PRODUCTS
    // =========================================================
@FXML
    private void loadProducts() {

        combopro.getItems().clear();

        ObservableList<ProductChoice> products =
                purchaseDAO.loadProducts();

        combopro.setItems(products);
    }


    // =========================================================
    // TABLE SETUP
    // =========================================================
@FXML
    private void setupTable() {

        colprocode.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getProductCode()
                        )
        );

        colproname.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getProductName()
                        )
        );

        colnumbatch.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue().getBatchNumber()
                        )
        );

        Qty.setCellValueFactory(
                data ->
                        new SimpleIntegerProperty(
                                data.getValue().getQuantity()
                        )
        );

        colbuyprice.setCellValueFactory(
        data ->
                new javafx.beans.property.SimpleObjectProperty<Number>(
                        data.getValue().getBuyPrice()
                )
);

        colexpiry.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleObjectProperty<>(
                                data.getValue().getExpiryDate()
                        )
        );

        colbuyprice.setCellFactory(
                column ->
                        new TableCell<PurchaseItem, Number>() {

                            @Override
                            protected void updateItem(
                                    Number item,
                                    boolean empty) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (empty || item == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            String.format(
                                                    "$%.2f",
                                                    item.doubleValue()
                                            )
                                    );
                                }
                            }
                        }
        );

        addRemoveButton();
    }


    // =========================================================
    // ADD ITEM
    // =========================================================
@FXML
    private void handleAdd() {

        ProductChoice product =
                combopro.getValue();

        String batch =
                batchno.getText().trim();

        String priceText =
                buyprice.getText().trim();

        String qtyText =
                Qtytxt.getText().trim();

        LocalDate expiry =
                datepiker.getValue();


        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (product == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Product",
                    "Please select a product."
            );

            return;
        }


        if (batch.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Batch",
                    "Please enter the batch number."
            );

            return;
        }


        if (priceText.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Price",
                    "Please enter the purchase price."
            );

            return;
        }


        if (qtyText.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Quantity",
                    "Please enter the quantity."
            );

            return;
        }


        if (expiry == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Expiry",
                    "Please select the expiry date."
            );

            return;
        }


        // -----------------------------------------------------
        // PARSE PRICE
        // -----------------------------------------------------

        double price;

        try {

            price = Double.parseDouble(priceText);

        } catch (NumberFormatException ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Price",
                    "Purchase price must be a valid number."
            );

            return;
        }


        if (price < 0) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Price",
                    "Purchase price cannot be negative."
            );

            return;
        }


        // -----------------------------------------------------
        // PARSE QUANTITY
        // -----------------------------------------------------

        int quantity;

        try {

            quantity = Integer.parseInt(qtyText);

        } catch (NumberFormatException ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Quantity",
                    "Quantity must be a whole number."
            );

            return;
        }


        if (quantity <= 0) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Quantity",
                    "Quantity must be greater than zero."
            );

            return;
        }


        // -----------------------------------------------------
        // CHECK DUPLICATE BATCH
        // -----------------------------------------------------

        for (PurchaseItem item : items) {

            if (item.getBatchNumber()
                    .equalsIgnoreCase(batch)) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Duplicate Batch",
                        "This batch is already added to the purchase."
                );

                return;
            }
        }


        // -----------------------------------------------------
        // ADD TO TABLE
        // -----------------------------------------------------

        items.add(
                new PurchaseItem(
                        product.getProductCode(),
                        product.getProductName(),
                        batch,
                        quantity,
                        price,
                        expiry
                )
        );


        updateTotal();

        clearItemFields();
    }


    // =========================================================
    // REMOVE BUTTON
    // =========================================================
@FXML
    private void addRemoveButton() {

        colremove.setCellFactory(
                column ->
                        new TableCell<PurchaseItem, Void>() {

                            private final Button button =
                                    new Button("Remove");

                            {
                                button.setOnAction(
                                        event -> {

                                            PurchaseItem item =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            items.remove(item);

                                            updateTotal();
                                        }
                                );
                            }

                            @Override
                            protected void updateItem(
                                    Void item,
                                    boolean empty) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (empty) {

                                    setGraphic(null);

                                } else {

                                    setGraphic(button);
                                }
                            }
                        }
        );
    }


    // =========================================================
    // TOTAL
    // =========================================================

    private void updateTotal() {

        double sum = 0;

        for (PurchaseItem item : items) {

            sum += item.getTotal();
        }

        total.setText(
                String.format(
                        "$%.2f",
                        sum
                )
        );
    }


    // =========================================================
    // CLEAR ITEM FIELDS
    // =========================================================

    private void clearItemFields() {

        combopro.getSelectionModel().clearSelection();

        batchno.clear();

        buyprice.clear();

        Qtytxt.clear();

        datepiker.setValue(null);
    }


    // =========================================================
    // CLEAR PURCHASE
    // =========================================================
@FXML
    private void handleClear() {

        items.clear();

        clearItemFields();

        updateTotal();
    }


    // =========================================================
    // SAVE PURCHASE
    // =========================================================
@FXML
    private void handleSave() {

        if (items.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Empty Purchase",
                    "Please add at least one product."
            );

            return;
        }


        String purchaseNumber =
                txtpurnum.getText();

        LocalDate date =
                LocalDate.parse(
                        txtpurdate.getText()
                );

        double purchaseTotal = 0;

        for (PurchaseItem item : items) {

            purchaseTotal += item.getTotal();
        }


        boolean saved =
                purchaseDAO.savePurchase(
                        purchaseNumber,
                        date,
                        purchaseTotal,
                        items
                );


        if (saved) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Purchase Saved",
                    "Purchase "
                            + purchaseNumber
                            + " has been saved successfully."
            );


            closeWindow();

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Error",
                    "Unable to save the purchase."
            );
        }
    }


    // =========================================================
    // CLOSE WINDOW
    // =========================================================
@FXML
    private void closeWindow() {

        Stage stage =
                (Stage) savebtnpur.getScene()
                        .getWindow();

        stage.close();
    }


    // =========================================================
    // ALERT
    // =========================================================
@FXML
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
    // PRODUCT MODEL
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
    // PURCHASE ITEM MODEL
    // =========================================================

    public static class PurchaseItem {

        private final String productCode;
        private final String productName;
        private final String batchNumber;
        private final int quantity;
        private final double buyPrice;
        private final LocalDate expiryDate;

        public PurchaseItem(
                String productCode,
                String productName,
                String batchNumber,
                int quantity,
                double buyPrice,
                LocalDate expiryDate) {

            this.productCode = productCode;
            this.productName = productName;
            this.batchNumber = batchNumber;
            this.quantity = quantity;
            this.buyPrice = buyPrice;
            this.expiryDate = expiryDate;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductName() {
            return productName;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getBuyPrice() {
            return buyPrice;
        }

        public LocalDate getExpiryDate() {
            return expiryDate;
        }

        public double getTotal() {

            return quantity * buyPrice;
        }
    }
}