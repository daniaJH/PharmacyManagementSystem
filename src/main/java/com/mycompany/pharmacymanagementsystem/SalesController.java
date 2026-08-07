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
    private Button btnRemoveItem;

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
    private TableColumn<SaleProduct, String> colRemove;
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

private SalesDAO salesDAO = new SalesDAO();

private ObservableList<SaleProduct> productList =
        FXCollections.observableArrayList();

private ObservableList<InvoiceItem> invoiceList =
        FXCollections.observableArrayList();


@Override
public void initialize(URL url, ResourceBundle rb) {

    lblInvoiceNo.setText(salesDAO.generateInvoiceNumber());

    lblDate.setText(LocalDate.now().toString());

    lblTime.setText(LocalTime.now().withNano(0).toString());

    // لاحقاً سنجلبه من شاشة تسجيل الدخول
    lblEmployeeName.setText("Current Employee");

    rbCash.setSelected(true);

    cmbDiscountType.getItems().addAll(
            "None",
            "Percentage",
            "Amount"
    );

    cmbDiscountType.getSelectionModel().selectFirst();

    initializeProductTable();

    initializeInvoiceTable();

    loadProducts();

    loadCustomers();

    txtDiscount.textProperty().addListener(
            (obs,o,n)->calculateTotals());

    txtAmountPaid.textProperty().addListener(
            (obs,o,n)->calculateTotals());

    cmbDiscountType.valueProperty().addListener(
            (obs,o,n)->calculateTotals());

}
private void loadProducts(){

    productList = salesDAO.loadProducts();

    tblProducts.setItems(productList);

} 
private void loadCustomers(){

    cmbCustomer.setItems(

            salesDAO.loadCustomers()

    );

    cmbCustomer.getSelectionModel().selectFirst();

}
private void initializeProductTable(){

    colProductCode.setCellValueFactory(
            new PropertyValueFactory<>("productCode"));

    colTradeName.setCellValueFactory(
            new PropertyValueFactory<>("tradeName"));

    colUnit.setCellValueFactory(
            new PropertyValueFactory<>("unit"));

    colPrice.setCellValueFactory(
            new PropertyValueFactory<>("price"));

    colStock.setCellValueFactory(
            new PropertyValueFactory<>("stock"));

    addButtonColumn();

}
private void initializeInvoiceTable(){

    colInvoiceCode.setCellValueFactory(
            new PropertyValueFactory<>("code"));

    colInvoiceName.setCellValueFactory(
            new PropertyValueFactory<>("productName"));

    colInvoiceUnit.setCellValueFactory(
            new PropertyValueFactory<>("unit"));

    colInvoicePrice.setCellValueFactory(
            new PropertyValueFactory<>("price"));

    colInvoiceQty.setCellValueFactory(
            new PropertyValueFactory<>("quantity"));

    colInvoiceTotal.setCellValueFactory(
            new PropertyValueFactory<>("total"));

    tblInvoice.setItems(invoiceList);

}
private void addButtonColumn(){

    Callback<TableColumn<SaleProduct,Void>,
            TableCell<SaleProduct,Void>> factory = column->{

        return new TableCell<>(){

            private final Button btn =
                    new Button("Add");

            {

                btn.setOnAction(e->{

                    SaleProduct product =
                            getTableView().getItems().get(getIndex());

                    addProductToInvoice(product);

                });

            }

            @Override
            protected void updateItem(Void item,
                                      boolean empty){

                super.updateItem(item,empty);

                if(empty){

                    setGraphic(null);

                }else{

                    setGraphic(btn);

                }

            }

        };

    };

    colAdd.setCellFactory(factory);

}
private void addProductToInvoice(SaleProduct product){

    for(InvoiceItem item : invoiceList){

        if(item.getCode().equals(product.getProductCode())){

            item.setQuantity(item.getQuantity()+1);

            tblInvoice.refresh();

            calculateTotals();

            return;

        }

    }

    InvoiceItem item = new InvoiceItem(

            product.getProductCode(),
            product.getTradeName(),
            product.getUnit(),
            product.getPrice(),
            1

    );

    invoiceList.add(item);

    calculateTotals();

}
private void calculateTotals(){

    double subtotal = 0;

    for(InvoiceItem item : invoiceList){

        subtotal += item.getTotal();

    }

    double discount = 0;

    try{

        if(!txtDiscount.getText().isBlank()){

            double value =
                    Double.parseDouble(
                            txtDiscount.getText());

            switch(cmbDiscountType.getValue()){

                case "Percentage":

                    discount = subtotal * value /100;

                    break;

                case "Amount":

                    discount = value;

                    break;

            }

        }

    }catch(Exception ex){

        discount=0;

    }

    double tax = (subtotal-discount)*0.16;

    double total =
            subtotal-discount+tax;

    lblSubtotal.setText(String.format("%.2f",subtotal));

    lblTax.setText(String.format("%.2f",tax));

    lblGrandTotal.setText(String.format("%.2f",total));

    calculateChange(total);

}private void calculateChange(double total){

    try{

        double paid =
                Double.parseDouble(
                        txtAmountPaid.getText());

        double change =
                paid-total;

        lblChange.setText(
                String.format("%.2f",
                        Math.max(change,0)));

    }

    catch(Exception ex){

        lblChange.setText("0.00");

    }

}@FXML
private void handleCheckout() {

    if (invoiceList.isEmpty()) {

        showAlert(
                Alert.AlertType.ERROR,
                "Empty Invoice",
                "Please add at least one product."
        );

        return;
    }

    String invoiceNo = lblInvoiceNo.getText();

    // حالياً سنستخدم الموظف الأول
    // لاحقاً سنجلبه من شاشة Login
    String empId = "E001";

    String customerValue = cmbCustomer.getValue();

    String custId = salesDAO.getCustomerId(customerValue);

    double total =
            Double.parseDouble(lblGrandTotal.getText());

    boolean success =
            salesDAO.saveSale(
                    invoiceNo,
                    empId,
                    custId,
                    total,
                    invoiceList);

    if(success){

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Sale completed successfully."
        );

        clearSale();

    }else{

        showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Unable to complete sale."
        );

    }

}
private void clearSale(){

    invoiceList.clear();

    tblInvoice.refresh();

    lblInvoiceNo.setText(
            salesDAO.generateInvoiceNumber());

    txtDiscount.clear();

    txtAmountPaid.clear();

    lblSubtotal.setText("0.00");

    lblTax.setText("0.00");

    lblGrandTotal.setText("0.00");

    lblChange.setText("0.00");

    loadProducts();

}
@FXML
private void handleSearchProduct(){

    String keyword =
            txtSearchProduct.getText().trim();

    if(keyword.isEmpty()){

        loadProducts();

    }else{

        tblProducts.setItems(

                salesDAO.searchProducts(keyword)

        );

    }

}
@FXML
private void handleRemoveItem(){

    InvoiceItem item =
            tblInvoice.getSelectionModel()
                    .getSelectedItem();

    if(item==null)
        return;

    invoiceList.remove(item);

    calculateTotals();

}
@FXML
private void handleClearSale(){

    clearSale();
}

   private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}