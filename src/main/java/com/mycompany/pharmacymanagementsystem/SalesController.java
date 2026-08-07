/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;
import java.time.LocalDate;
import java.time.LocalTime;
import com.mycompany.pharmacymanagementsystem.ProductController.Product;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
public class SalesController implements Initializable {

    

    @FXML
    private Button btnAddCustomer;

    @FXML
    private Button btnCheckout;

    @FXML
    private Button btnClearSale;

    

    @FXML
    private Button btnRemoveSale;

    @FXML
    private Button btnSearchProduct;

    @FXML
    private ComboBox<String> cmbCustomer;

    @FXML
    private ComboBox<String> cmbDiscountType;

    @FXML
    private TableColumn<SaleProduct,Void> colAdd;

    @FXML
    private TableColumn<InvoiceItem ,String> colInvoiceCode;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceName;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoicePrice;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceQty;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceTotal;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceUnit;

    @FXML
    private TableColumn<SaleProduct,String> colPrice;

    @FXML
    private TableColumn<SaleProduct,String> colProductCode;
    @FXML
    private TableColumn<InvoiceItem,Void> colRemove;
    @FXML
    private TableColumn<SaleProduct,String> colStock;

    @FXML
    private TableColumn<SaleProduct,String> colTradeName;

    @FXML
    private TableColumn<SaleProduct,String> colUnit;

    @FXML
    private Label lblChange;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblEmployeeName;

    @FXML
    private Label lblGrandTotal;

    @FXML
    private Label lblInvoiceNo;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblTime;

    @FXML
    private ToggleGroup paymentGroup;

    @FXML
    private RadioButton rbCard;

    @FXML
    private RadioButton rbCash;

    @FXML
    private TableView<InvoiceItem> tblInvoice;

    @FXML
    private TableView<SaleProduct> tblProducts;

    @FXML
    private TextField txtAmountPaid;

    @FXML
    private TextField txtDiscount;

    @FXML
    private TextField txtSearchProduct;

private final SalesDAO salesDAO = new SalesDAO();

private final ObservableList<SaleProduct> productList =
        FXCollections.observableArrayList();

private final ObservableList<InvoiceItem> invoiceList =
        FXCollections.observableArrayList();


@Override

public void initialize(URL url, ResourceBundle rb) {

    // بيانات الفاتورة
    lblInvoiceNo.setText(salesDAO.generateInvoiceNumber());
    lblDate.setText(LocalDate.now().toString());
    lblTime.setText(LocalTime.now().withNano(0).toString());

    // اسم الموظف الحالي (عدليه لاحقاً عند نظام تسجيل الدخول)
    lblEmployeeName.setText("Current Employee");

    // تحميل العملاء
    cmbCustomer.setItems(salesDAO.loadCustomers());
    cmbCustomer.getSelectionModel().selectFirst();

    // أنواع الخصم
    cmbDiscountType.getItems().addAll(
            "None",
            "Percentage",
            "Amount"
    );
    cmbDiscountType.getSelectionModel().selectFirst();

    // ربط الأعمدة
    initializeProductTable();
    initializeInvoiceTable();

    // تحميل المنتجات
    productList.setAll(salesDAO.loadProducts());
    tblProducts.setItems(productList);

    // جدول الفاتورة
    tblInvoice.setItems(invoiceList);

    // زر الإضافة
    addButtonToTable();
addRemoveButtonToInvoice();
    // القيم الابتدائية
    lblSubtotal.setText("0.00");
    lblTax.setText("0.00");
    lblGrandTotal.setText("0.00");
    lblChange.setText("0.00");

    rbCash.setSelected(true);

    // إعادة الحساب عند أي تغيير
    txtAmountPaid.textProperty().addListener((obs, oldVal, newVal) -> calculateTotals());

    txtDiscount.textProperty().addListener((obs, oldVal, newVal) -> calculateTotals());

    cmbDiscountType.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotals());
}
@FXML
private void handleSearchProduct() {

    String keyword = txtSearchProduct.getText().trim();

    if(keyword.isEmpty()){

        productList.setAll(salesDAO.loadProducts());

    }else{

        productList.setAll(
                salesDAO.searchProducts(keyword)
        );

    }

}private void initializeProductTable() {

    colProductCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
    colTradeName.setCellValueFactory(new PropertyValueFactory<>("tradeName"));
    colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
    colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

}
private void initializeInvoiceTable() {

    colInvoiceCode.setCellValueFactory(new PropertyValueFactory<>("code"));
    colInvoiceName.setCellValueFactory(new PropertyValueFactory<>("productName"));
    colInvoiceUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
    colInvoicePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    colInvoiceQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    colInvoiceTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

}
private void addButtonToTable() {

    Callback<TableColumn<SaleProduct, Void>, TableCell<SaleProduct, Void>> cellFactory = param -> {

        return new TableCell<>() {

            private final Button btn = new Button("Add");

            {
                btn.setOnAction(event -> {

                    SaleProduct product =
                            getTableView().getItems().get(getIndex());

                    addProductToInvoice(product);

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }

            }

        };

    };

    colAdd.setCellFactory(cellFactory);

}
private void addProductToInvoice(SaleProduct product) {

    for (InvoiceItem item : invoiceList) {

        if (item.getCode().equals(product.getProductCode())) {

            item.setQuantity(item.getQuantity() + 1);

            tblInvoice.refresh();

            calculateTotals();

            return;
        }

    }

    invoiceList.add(

            new InvoiceItem(

                    product.getProductCode(),
                    product.getTradeName(),
                    product.getUnit(),
                    product.getPrice(),
                    1

            )

    );

    calculateTotals();

}
private void calculateTotals() {

    double subtotal = 0;

    for (InvoiceItem item : invoiceList) {

        subtotal += item.getTotal();

    }

    double discount = 0;

    try {

        if (!txtDiscount.getText().isEmpty()) {

            double value =
                    Double.parseDouble(txtDiscount.getText());

            if (cmbDiscountType.getValue().equals("Percentage")) {

                discount = subtotal * value / 100;

            } else if (cmbDiscountType.getValue().equals("Amount")) {

                discount = value;

            }

        }

    } catch (Exception e) {

        discount = 0;

    }

    double tax = (subtotal - discount) * 0.10;

    double grand = subtotal - discount + tax;

    lblSubtotal.setText(String.format("%.2f", subtotal));
    lblTax.setText(String.format("%.2f", tax));
    lblGrandTotal.setText(String.format("%.2f", grand));

    calculateChange();

}
private void calculateChange() {

    try {

        double paid =
                Double.parseDouble(txtAmountPaid.getText());

        double total =
                Double.parseDouble(lblGrandTotal.getText());

        lblChange.setText(
                String.format("%.2f", paid - total));

    } catch (Exception e) {

        lblChange.setText("0.00");

    }

}
private void addRemoveButtonToInvoice() {

    Callback<TableColumn<InvoiceItem, Void>, TableCell<InvoiceItem, Void>> cellFactory =
            param -> new TableCell<>() {

        private final Button btn = new Button("Remove");

        {
            btn.setOnAction(e -> {

                InvoiceItem item =
                        getTableView().getItems().get(getIndex());

                invoiceList.remove(item);

                calculateTotals();

            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {

            super.updateItem(item, empty);

            if (empty)
                setGraphic(null);
            else
                setGraphic(btn);

        }
    };

    colRemove.setCellFactory(cellFactory);

}


    @FXML
    void handleAddCustomer(ActionEvent event) {

    }

    @FXML
    void handleAddProduct(ActionEvent event) {

    }

    @FXML
void handleClearSale(ActionEvent event) {

    invoiceList.clear();

    txtAmountPaid.clear();

    txtDiscount.clear();

    cmbCustomer.getSelectionModel().selectFirst();

    cmbDiscountType.getSelectionModel().selectFirst();

    lblSubtotal.setText("0.00");
    lblTax.setText("0.00");
    lblGrandTotal.setText("0.00");
    lblChange.setText("0.00");

    lblInvoiceNo.setText(
            salesDAO.generateInvoiceNumber()
    );

}

   @FXML
void handleCompleteSale(ActionEvent event) {

    if (invoiceList.isEmpty()) {

        showAlert(
                Alert.AlertType.WARNING,
                "Empty Invoice",
                "Please add products first."
        );

        return;
    }

    double grandTotal;

    try {

        grandTotal =
                Double.parseDouble(lblGrandTotal.getText());

    } catch (Exception e) {

        showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Invalid total."
        );

        return;
    }

    try {

        double paid =
                Double.parseDouble(txtAmountPaid.getText());

        if (paid < grandTotal) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment",
                    "Amount paid is not enough."
            );

            return;

        }

    } catch (Exception e) {

        showAlert(
                Alert.AlertType.WARNING,
                "Payment",
                "Enter amount paid."
        );

        return;

    }

    String customerID =
            salesDAO.getCustomerId(
                    cmbCustomer.getValue()
            );

    boolean saved =
            salesDAO.saveSale(

                    lblInvoiceNo.getText(),

                    "EMP001",

                    customerID,

                    grandTotal,

                    invoiceList

            );

    if(saved){

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Sale completed successfully."
        );

        productList.setAll(
                salesDAO.loadProducts()
        );

        handleClearSale(null);

    }else{

        showAlert(
                Alert.AlertType.ERROR,
                "Database",
                "Sale failed."
        );

    }

}

    @FXML
    void handleRemoveItem(ActionEvent event) {

    }

    @FXML
    void handleRemoveSale(ActionEvent event) {

    }

    
   private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}