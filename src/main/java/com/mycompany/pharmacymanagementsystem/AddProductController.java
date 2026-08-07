package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.Manufacturer;
import com.mycompany.pharmacymanagementsystem.ProductController.Product;
import com.mycompany.pharmacymanagementsystem.dao.AddProductDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AddProductController implements Initializable {

    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private ToggleGroup categoryGroup;
    @FXML private CheckBox chkPrescriptionRequired;
    @FXML private ComboBox<String> cmbProductType;
    @FXML private ComboBox<String> combunit;
    @FXML private RadioButton radCareProduct;
    @FXML private RadioButton radMedicine;
    @FXML private TextField txtActiveIngredients;
    @FXML private TextField txtCode;
    @FXML private ComboBox<Manufacturer> cmbCompanies;
    @FXML private TextField txtDosage;
    @FXML private TextField txtMinStock;
    @FXML private TextField txtScientificName;
    @FXML private TextField txtTName;
    @FXML private TextField txtUsageMethod;

    private Product currentProduct;
    private boolean isEditMode = false;
    private AddProductDAO addProductDAO = new AddProductDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        combunit.getItems().addAll("Box", "Tube", "Bottle");
        cmbCompanies.setItems(addProductDAO.getManufacturers());

        if (!cmbCompanies.getItems().isEmpty()) {
            cmbCompanies.getSelectionModel().selectFirst();
        }
        combunit.getSelectionModel().selectFirst();

        cmbProductType.getItems().addAll("Skin Care", "Hair Care", "Body Care", "Sun Care", "Oral Care");
        cmbProductType.getSelectionModel().selectFirst();
cmbCompanies.setItems(addProductDAO.getManufacturers());

if (!cmbCompanies.getItems().isEmpty()) {
    cmbCompanies.getSelectionModel().selectFirst();
}
        categoryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateFieldsState();
        });

        radMedicine.setSelected(true);
        updateFieldsState();
    }

    public void setProductData(Product product) {
        this.currentProduct = product;
        this.isEditMode = true;

        // 1. تعبئة البيانات الأساسية وقفل الكود
        txtCode.setText(product.getCode());
        txtCode.setDisable(true); // منع تعديل Primary Key

        txtTName.setText(product.getTradeName());
        combunit.setValue(product.getUnit());
        txtMinStock.setText(String.valueOf(product.getMinStockLevel()));

        // 2. تحديد المصنع من قائمة ComboBox
        // ضبط الشركة في الـ ComboBox
if (product.getCompany() != null && !product.getCompany().trim().isEmpty()) {
    for (Manufacturer m : cmbCompanies.getItems()) {
        if (m.getManufCode() != null && m.getManufCode().equalsIgnoreCase(product.getCompany()) ||
            m.getName() != null && m.getName().equalsIgnoreCase(product.getCompany())) {
            
            cmbCompanies.setValue(m);
            break;
        }
    }
}

        // 3. تعبئة حقول الدواء أو منتج العناية
        if (product.getDosage() != null && !product.getDosage().trim().isEmpty()) {
            radMedicine.setSelected(true);
            txtDosage.setText(product.getDosage());
            txtScientificName.setText(product.getScientificName());
            txtActiveIngredients.setText(product.getActiveIngredients());
            chkPrescriptionRequired.setSelected(product.isPrescriptionRequired());
        } else {
            radCareProduct.setSelected(true);
            cmbProductType.setValue(product.getCareType());
            txtUsageMethod.setText(product.getUsageMethod());
        }
        
        updateFieldsState();
    }

    @FXML
    void select(ActionEvent event) {
        if (event.getSource() == combunit) {
            String selectedUnit = combunit.getValue();
            System.out.println("Selected Unit: " + selectedUnit);
        } else if (event.getSource() == cmbProductType) {
            String selectedType = cmbProductType.getValue();
            System.out.println("Selected Product Type: " + selectedType);
        }
    }

    private void updateFieldsState() {
        boolean isMedicine = radMedicine.isSelected();

        txtScientificName.setDisable(!isMedicine);
        txtActiveIngredients.setDisable(!isMedicine);
        txtDosage.setDisable(!isMedicine);
        chkPrescriptionRequired.setDisable(!isMedicine);

        txtScientificName.setOpacity(isMedicine ? 1.0 : 0.4);
        txtActiveIngredients.setOpacity(isMedicine ? 1.0 : 0.4);
        txtDosage.setOpacity(isMedicine ? 1.0 : 0.4);
        chkPrescriptionRequired.setOpacity(isMedicine ? 1.0 : 0.4);

        cmbProductType.setDisable(isMedicine);
        txtUsageMethod.setDisable(isMedicine);

        cmbProductType.setOpacity(isMedicine ? 0.4 : 1.0);
        txtUsageMethod.setOpacity(isMedicine ? 0.4 : 1.0);
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (txtCode.getText().trim().isEmpty() || txtTName.getText().trim().isEmpty() || txtMinStock.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required basic fields.");
            return;
        }

        try {
            String code = txtCode.getText().trim();
            String tName = txtTName.getText().trim();
            String unit = combunit.getValue();
            int minStock = Integer.parseInt(txtMinStock.getText().trim());
            Manufacturer company = cmbCompanies.getValue();



            boolean isMedicine = radMedicine.isSelected();
            String category = isMedicine ? "Medicine" : "CareProduct";

            Product product = new Product();
            product.setCode(code);
            product.setTradeName(tName);
            product.setUnit(unit);
            product.setMinStock(minStock);

            if (company != null) {
                product.setCompany(company.getManufCode());
            }

            if (isMedicine) {
                product.setScientificName(txtScientificName.getText().trim());
                product.setActiveIngredients(txtActiveIngredients.getText().trim());
                product.setDosage(txtDosage.getText().trim());
                product.setPrescriptionRequired(chkPrescriptionRequired.isSelected());
            } else {
                product.setCareType(cmbProductType.getValue());
                product.setUsageMethod(txtUsageMethod.getText().trim());
            }

            boolean success;
            if (isEditMode) {
                success = addProductDAO.updateProduct(product, category);
            } else {
                success = addProductDAO.addProduct(product, category);
            }

            if (success) {
                String successMsg = isEditMode ? "Product updated successfully!" : "Product saved successfully!";
                showAlert(Alert.AlertType.INFORMATION, "Success", successMsg);
                closeWindow();
            } else {
                String errorMsg = isEditMode ? "Failed to update product." : "Failed to save product. Check if product code already exists.";
                showAlert(Alert.AlertType.ERROR, "Database Error", errorMsg);
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Minimum stock must be a valid number.");
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}