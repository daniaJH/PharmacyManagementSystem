package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.ProductDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class ProductController implements Initializable {

    // =========================================================
    // BUTTONS
    // =========================================================

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnClearSearch;

    @FXML
    private Button btnClearTable;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;


    // =========================================================
    // TABLE
    // =========================================================

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, String> productCodeColumn;

    @FXML
    private TableColumn<Product, String> tradeNameColumn;

    @FXML
    private TableColumn<Product, String> unitColumn;

    @FXML
    private TableColumn<Product, Integer> minStockColumn;

    @FXML
    private TableColumn<Product, String> manufacturerColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, String> dosageColumn;

    @FXML
    private TableColumn<Product, String> scientificNameColumn;

    @FXML
    private TableColumn<Product, String> productTypeColumn;

    @FXML
    private TableColumn<Product, String> usageMethodColumn;

    @FXML
    private TableColumn<Product, String> activeIngredientsColumn;

    @FXML
    private TableColumn<Product, Boolean> prescriptionRequiredColumn;


    // =========================================================
    // RADIO BUTTONS
    // =========================================================

    @FXML
    private ToggleGroup producttype;

    @FXML
    private RadioButton rbAll;

    @FXML
    private RadioButton rbCare;

    @FXML
    private RadioButton rbMedicine;


    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    private TextField searchField;


    // =========================================================
    // DAO
    // =========================================================

    private final ProductDAO productDAO = new ProductDAO();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTableColumns();

        rbAll.setSelected(true);

        updateColumnsVisibility();

        loadDataFromDatabase();

        rbAll.setOnAction(e -> handleFilter());
        rbMedicine.setOnAction(e -> handleFilter());
        rbCare.setOnAction(e -> handleFilter());
    }


    // =========================================================
    // TABLE COLUMNS
    // =========================================================

    private void setupTableColumns() {

        // Product Code
        productCodeColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCode()
                )
        );


        // Trade Name
        tradeNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getTradeName()
                )
        );


        // Unit
        unitColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getUnit()
                )
        );


        // Minimum Stock
        minStockColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getMinStock()
                ).asObject()
        );


        // Manufacturer
        manufacturerColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCompany()
                )
        );


        // Price
        if (priceColumn != null) {

            priceColumn.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleObjectProperty<>(
                            data.getValue().getPrice()
                    )
            );
        }


        // =====================================================
        // MEDICINE COLUMNS
        // =====================================================

        if (dosageColumn != null) {

            dosageColumn.setCellValueFactory(
                    data -> new SimpleStringProperty(
                            data.getValue().getDosage()
                    )
            );
        }


        if (scientificNameColumn != null) {

            scientificNameColumn.setCellValueFactory(
                    data -> new SimpleStringProperty(
                            data.getValue().getScientificName()
                    )
            );
        }


        if (activeIngredientsColumn != null) {

            activeIngredientsColumn.setCellValueFactory(
                    data -> new SimpleStringProperty(
                            data.getValue().getActiveIngredients()
                    )
            );
        }


        if (prescriptionRequiredColumn != null) {

            prescriptionRequiredColumn.setCellValueFactory(
                    data -> new SimpleBooleanProperty(
                            data.getValue().isPrescriptionRequired()
                    ).asObject()
            );
        }


        // =====================================================
        // CARE PRODUCT COLUMNS
        // =====================================================

        if (productTypeColumn != null) {

            productTypeColumn.setCellValueFactory(
                    data -> new SimpleStringProperty(
                            data.getValue().getCareType()
                    )
            );
        }


        if (usageMethodColumn != null) {

            usageMethodColumn.setCellValueFactory(
                    data -> new SimpleStringProperty(
                            data.getValue().getUsageMethod()
                    )
            );
        }
    }


    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String content) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================
@FXML
void handleDeleteAction(ActionEvent event) {

    Product selectedProduct =
            productTable
                    .getSelectionModel()
                    .getSelectedItem();


    // =====================================================
    // NO SELECTION
    // =====================================================

    if (selectedProduct == null) {

        showAlert(
                Alert.AlertType.WARNING,
                "No Selection",
                "Please select a product from the table first."
        );

        return;
    }


    // =====================================================
    // CONFIRMATION
    // =====================================================

    Alert confirmation =
            new Alert(
                    Alert.AlertType.CONFIRMATION
            );


    confirmation.setTitle(
            "Delete Product"
    );


    confirmation.setHeaderText(
            "Are you sure you want to delete this product?"
    );


    confirmation.setContentText(
            "Product Code: "
            + selectedProduct.getCode()
            + "\nTrade Name: "
            + selectedProduct.getTradeName()
            + "\n\n"
            + "The product will be removed from the active "
            + "products list, but its historical records "
            + "will be preserved."
    );


    // =====================================================
    // USER CANCELLED
    // =====================================================

    if (confirmation.showAndWait()
            .orElse(ButtonType.CANCEL)
            != ButtonType.OK) {

        return;
    }


    // =====================================================
    // DEACTIVATE PRODUCT
    // =====================================================

    boolean success =
            productDAO.deleteProduct(
                    selectedProduct.getCode()
            );


    // =====================================================
    // SUCCESS
    // =====================================================

    if (success) {

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Product deleted successfully.\n\n"
                + "The product was removed from the active "
                + "products list.\n"
                + "All historical records were preserved."
        );


        loadDataFromDatabase();


        productTable
                .getSelectionModel()
                .clearSelection();


    } else {


        // =================================================
        // STOCK EXISTS OR PRODUCT ALREADY INACTIVE
        // =================================================

        showAlert(
                Alert.AlertType.WARNING,
                "Cannot Delete Product",
                "This product cannot be deleted because "
                + "it still has stock greater than 0.\n\n"
                + "Please make sure the total quantity of "
                + "all batches is 0."
        );
    }
}

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @FXML
    void handleUpdateAction(ActionEvent event) {

        Product selectedProduct =
                productTable.getSelectionModel()
                        .getSelectedItem();


        if (selectedProduct == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Selection",
                    "Please select a product from the table first."
            );

            return;
        }


        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/AddProductDialog.fxml"
                            )
                    );


            Parent root = loader.load();


            AddProductController controller =
                    loader.getController();


            controller.setProductData(selectedProduct);


            Stage stage = new Stage();

            stage.setTitle(
                    "Edit Product - "
                    + selectedProduct.getCode()
            );

            stage.setScene(new Scene(root));


            stage.setOnHiding(
                    e -> loadDataFromDatabase()
            );


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not open product edit window."
            );
        }
    }


    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    void handleSearchAction(ActionEvent event) {

        loadDataFromDatabase();
    }


    // =========================================================
    // CLEAR SEARCH
    // =========================================================

    @FXML
    void handleClearSearchAction(ActionEvent event) {

        searchField.clear();

        rbAll.setSelected(true);

        updateColumnsVisibility();

        loadDataFromDatabase();
    }


    // =========================================================
    // CLEAR TABLE
    // =========================================================

    @FXML
    void handleClearTableAction(ActionEvent event) {

        productTable.getItems().clear();

        productTable.getSelectionModel()
                .clearSelection();
    }


    // =========================================================
    // OPEN ADD PRODUCT WINDOW
    // =========================================================

    @FXML
    void handleOpenAddProductWindow(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/AddProductDialog.fxml"
                            )
                    );


            Parent root = loader.load();


            Stage stage = new Stage();

            stage.setTitle("Add New Product");

            stage.setScene(new Scene(root));


            stage.setOnHiding(
                    e -> loadDataFromDatabase()
            );


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not open Add Product window."
            );
        }
    }


    // =========================================================
    // FILTER
    // =========================================================

    private void handleFilter() {

        updateColumnsVisibility();

        loadDataFromDatabase();
    }


    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadDataFromDatabase() {

        String category =
                getSelectedCategory();


        String searchText =
                searchField.getText() == null
                ? ""
                : searchField.getText().trim();


        ObservableList<Product> list =
                productDAO.getFilteredProducts(
                        category,
                        searchText
                );


        productTable.setItems(list);
    }


    // =========================================================
    // GET SELECTED CATEGORY
    // =========================================================

    private String getSelectedCategory() {

        if (rbMedicine.isSelected()) {
            return "Medicine";
        }


        if (rbCare.isSelected()) {
            return "Care Product";
        }


        return "All";
    }


    // =========================================================
    // UPDATE COLUMN VISIBILITY
    // =========================================================

    private void updateColumnsVisibility() {

        boolean isMedicine =
                rbMedicine.isSelected();

        boolean isCare =
                rbCare.isSelected();

        boolean isAll =
                rbAll.isSelected();


        // =====================================================
        // MEDICINE
        // =====================================================

        if (dosageColumn != null) {

            dosageColumn.setVisible(
                    isMedicine || isAll
            );
        }


        if (scientificNameColumn != null) {

            scientificNameColumn.setVisible(
                    isMedicine || isAll
            );
        }


        // =====================================================
        // CARE PRODUCT
        // =====================================================

        if (productTypeColumn != null) {

            productTypeColumn.setVisible(
                    isCare || isAll
            );
        }


        if (usageMethodColumn != null) {

            usageMethodColumn.setVisible(
                    isCare || isAll
            );
        }


        // =====================================================
        // MEDICINE ONLY
        // =====================================================

        if (activeIngredientsColumn != null) {

            activeIngredientsColumn.setVisible(
                    isMedicine
            );
        }


        if (prescriptionRequiredColumn != null) {

            prescriptionRequiredColumn.setVisible(
                    isMedicine
            );
        }
    }
}