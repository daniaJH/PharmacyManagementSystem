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

    @FXML private Button btnAddProduct;
    @FXML private Button btnClear;
    @FXML private Button btnSearch;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> productCodeColumn;
    @FXML private TableColumn<Product, String> tradeNameColumn;
    @FXML private TableColumn<Product, String> unitColumn;
    @FXML private TableColumn<Product, Integer> minStockColumn;
    @FXML private TableColumn<Product, String> manufacturerColumn;
    @FXML private TableColumn<Product, String> dosageColumn;
    @FXML private TableColumn<Product, String> scientificNameColumn;
    @FXML private TableColumn<Product, String> productTypeColumn;
    @FXML private TableColumn<Product, String> usageMethodColumn;
@FXML private TableColumn<Product, String> activeIngredientsColumn;
@FXML private TableColumn<Product, Boolean> prescriptionRequiredColumn;
    @FXML private ToggleGroup producttype;
    @FXML private RadioButton rbAll;
    @FXML private RadioButton rbCare;
    @FXML private RadioButton rbMedicine;

    @FXML private TextField searchField;
    @FXML
private Button btnUpdate;

@FXML
void handleUpdateAction(ActionEvent event) {
    // 1. جلب المنتج المحدد من الجدول
    Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

    // 2. إذا لم يحدد المستخدم أي منتج، نُظهر تنبيه
    if (selectedProduct == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText(null);
        alert.setContentText("Please select a product from the table first.");
        alert.showAndWait();
        return;
    }

    // 3. فتح نافذة التعديل وتمرير البيانات إليها
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddProductDialog.fxml"));
        Parent root = loader.load();

        // إرسال المنتج المحدد إلى الكنترولر الخاص بنافذة الإضافة/التعديل
        AddProductController controller = loader.getController();
        controller.setProductData(selectedProduct); // هذه الدالة سنضيفها في AddProductController

        Stage stage = new Stage();
        stage.setTitle("Edit Product - " + selectedProduct.getCode());
        stage.setScene(new Scene(root));

        // إعادة تحديث الجدول تلقائياً بعد إغلاق النافذة
        stage.setOnHiding(e -> loadDataFromDatabase());

        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private ProductDAO productDAO = new ProductDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Mapping table columns to model fields
        productCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCode()));
        tradeNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().gettName()));
        unitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnit()));
        minStockColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinStockLevel()).asObject());
        manufacturerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCompanies()));
        dosageColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDosage()));
        scientificNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getsName()));
        productTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        usageMethodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsageMethod()));
// ربط الأعمدة الجديدة بالـ Properties الخاصة بالمنتج
// ربط عمود المواد الفعالة
if (activeIngredientsColumn != null) {
    activeIngredientsColumn.setCellValueFactory(data -> 
        new SimpleStringProperty(
            data.getValue().getActiveIngredients() != null ? data.getValue().getActiveIngredients() : ""
        )
    );
}

// ربط عمود الوصفة الطبية وتحويل الـ Boolean إلى (Yes / No)
if (prescriptionRequiredColumn != null) {
    prescriptionRequiredColumn.setCellValueFactory(data -> 
        new SimpleBooleanProperty(data.getValue().isPrescriptionRequired())
    );
}
        // 2. Initial state and load data
        updateColumnsVisibility();
        loadDataFromDatabase();

        // 3. Listeners for radio buttons selection change
        rbAll.setOnAction(e -> handleFilter());
        rbMedicine.setOnAction(e -> handleFilter());
        rbCare.setOnAction(e -> handleFilter());
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
    @FXML
void handleClearTableAction(ActionEvent event) {
    productTable.getItems().clear(); // تفريغ الصفوف المعروضة في الجدول
    productTable.getSelectionModel().clearSelection(); // إلغاء التحديد
}
@FXML
void handleDeleteAction(ActionEvent event) {
    // 1. التاكد من أن المستخدم حدد منتجاً من الجدول
    Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

    if (selectedProduct == null) {
        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product from the table to delete.");
        return;
    }

    // 2. إظهار رسالة تأكيد قبل الحذف
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Product");
    alert.setHeaderText("Are you sure you want to delete this product?");
    alert.setContentText("Product Code: " + selectedProduct.getCode() + "\nTrade Name: " + selectedProduct.getTradeName());

    // 3. التحقق من رد المستخدم
    if (alert.showAndWait().get() == ButtonType.OK) {
        boolean success = productDAO.deleteProduct(selectedProduct.getCode());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");
            loadDataFromDatabase();// إعادة تحميل بيانات الجدول لتحديث القائمة
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product from database.");
        }
    }
}
    @FXML
    void handleSearchAction(ActionEvent event) {
        loadDataFromDatabase();
    }

    @FXML
void handleClearSearchAction(ActionEvent event) {
    searchField.clear();
    rbAll.setSelected(true);
    updateColumnsVisibility();
    loadDataFromDatabase();
}

    @FXML
    void handleOpenAddProductWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddProductDialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Add New Product");
            stage.setScene(new Scene(root));
            
            // Reload database data automatically when the add window is closed
            stage.setOnHiding(e -> loadDataFromDatabase());
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFilter() {
        updateColumnsVisibility();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        String category = getSelectedCategory();
        String searchText = searchField.getText().trim();

        ObservableList<Product> list = productDAO.getFilteredProducts(category, searchText);
        productTable.setItems(list);
    }

    private String getSelectedCategory() {
        if (rbMedicine.isSelected()) return "Medicine";
        if (rbCare.isSelected()) return "Care Product";
        return "All";
    }

    private void updateColumnsVisibility() {
    boolean isMedicine = rbMedicine.isSelected();
    boolean isCare = rbCare.isSelected();
    boolean isAll = rbAll.isSelected();

    // الأعمدة الأساسية التي تظهر في All وفي تخصصها
    dosageColumn.setVisible(isMedicine || isAll);
    scientificNameColumn.setVisible(isMedicine || isAll);
    productTypeColumn.setVisible(isCare || isAll);
    usageMethodColumn.setVisible(isCare || isAll);

    // الأعمدة الإضافية للأدوية: تظهر فقط وفقط عند اختيار Medicine
    if (activeIngredientsColumn != null) {
        activeIngredientsColumn.setVisible(isMedicine);
    }
    if (prescriptionRequiredColumn != null) {
        prescriptionRequiredColumn.setVisible(isMedicine);
    }
}

    // Model Class Model
    public static class Product {
        private String code;
        private String tName;
        private String unit;
        private int minStockLevel;
        private String companies;
        private String dosage;
        private String sName;
        private String type;
        private String usageMethod;
        private String activeIngredients;
        private boolean prescriptionRequired;

        public Product() {}

        public Product(String code, String tName, String unit, int minStockLevel, String companies, String dosage, String sName, String type, String usageMethod) {
            this.code = code;
            this.tName = tName;
            this.unit = unit;
            this.minStockLevel = minStockLevel;
            this.companies = companies;
            this.dosage = dosage;
            this.sName = sName;
            this.type = type;
            this.usageMethod = usageMethod;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String gettName() { return tName; }
        public String getTradeName() { return tName; }
        public void setTradeName(String tName) { this.tName = tName; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public int getMinStockLevel() { return minStockLevel; }
        public int getMinStock() { return minStockLevel; }
        public void setMinStock(int minStockLevel) { this.minStockLevel = minStockLevel; }

        public String getCompanies() { return companies; }
        public String getCompany() { return companies; }
        public void setCompany(String companies) { this.companies = companies; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getsName() { return sName; }
        public String getScientificName() { return sName; }
        public void setScientificName(String sName) { this.sName = sName; }

        public String getType() { return type; }
        public String getCareType() { return type; }
        public void setCareType(String type) { this.type = type; }

        public String getUsageMethod() { return usageMethod; }
        public void setUsageMethod(String usageMethod) { this.usageMethod = usageMethod; }

        public String getActiveIngredients() { return activeIngredients; }
        public void setActiveIngredients(String activeIngredients) { this.activeIngredients = activeIngredients; }

        public boolean isPrescriptionRequired() { return prescriptionRequired; }
        public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }
    }
}
