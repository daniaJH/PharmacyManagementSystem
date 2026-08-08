/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;
import com.mycompany.pharmacymanagementsystem.dao.EmployeeDAO;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SalesController implements Initializable {

    @FXML
    private Button btnAddCustomer;

    @FXML
    private ComboBox<Employee> compforowner;
    @FXML
    private Button btnCheckout;

    @FXML
    private Button btnClearSale;

    @FXML
    private Button btnRemoveSale;

    @FXML
    private Button btnSearchProduct;

    @FXML
    private Button saleshistorybtn;

    @FXML
    private ComboBox<String> cmbCustomer;

    @FXML
    private ComboBox<String> cmbDiscountType;

    @FXML
    private TableColumn<SaleProduct, Void> colAdd;

    @FXML
    private TableColumn<SaleProduct, String> colProductCode;

    @FXML
    private TableColumn<SaleProduct, String> colTradeName;

    @FXML
    private TableColumn<SaleProduct, String> colUnit;

    @FXML
    private TableColumn<SaleProduct, Double> colPrice;

    @FXML
    private TableColumn<SaleProduct, Integer> colStock;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceCode;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceName;

    @FXML
    private TableColumn<InvoiceItem, String> colInvoiceUnit;

    @FXML
    private TableColumn<InvoiceItem, Double> colInvoicePrice;

    @FXML
    private TableColumn<InvoiceItem, Integer> colInvoiceQty;

    @FXML
    private TableColumn<InvoiceItem, Double> colInvoiceTotal;

    @FXML
    private TableColumn<InvoiceItem, Void> colRemove;

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
private double currentGrandTotal = 0.0;
    @FXML
    private TextField txtSearchProduct;

    private final SalesDAO salesDAO =
            new SalesDAO();
private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ObservableList<SaleProduct> productList =
            FXCollections.observableArrayList();

    private final ObservableList<InvoiceItem> invoiceList =
            FXCollections.observableArrayList();

    // =========================================================
    // INITIALIZE
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        lblInvoiceNo.setText(
                salesDAO.generateInvoiceNumber()
        );

        lblDate.setText(
                LocalDate.now().toString()
        );

        lblTime.setText(
                LocalTime.now()
                        .withNano(0)
                        .toString()
        );

      if (UserSession.isOwner()) {

    compforowner.setVisible(true);
    compforowner.setManaged(true);

    loadEmployeesForOwner();

} else {
lblEmployeeName.setText("Select Employee");
    compforowner.setVisible(false);
    compforowner.setManaged(false);

    lblEmployeeName.setText(
        UserSession.getFirstName()
    );
}

        // Customers
        cmbCustomer.setItems(
                salesDAO.loadCustomers()
        );

        cmbCustomer.getSelectionModel()
                .selectFirst();

        // Discount
        cmbDiscountType.getItems().addAll(
                "None",
                "Percentage",
                "Amount"
        );

        cmbDiscountType.getSelectionModel()
                .selectFirst();

        // Tables
        initializeProductTable();
        initializeInvoiceTable();

        // Products
        productList.setAll(
                salesDAO.loadProducts()
        );

        tblProducts.setItems(productList);

        // Invoice
        tblInvoice.setItems(invoiceList);

        // Initial values
        lblSubtotal.setText("0.00");
        lblTax.setText("0.00");
        lblGrandTotal.setText("0.00");
        lblChange.setText("0.00");

        rbCash.setSelected(true);

        // Listeners
        txtAmountPaid.textProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> calculateTotals()
                );

        txtDiscount.textProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> calculateTotals()
                );

        cmbDiscountType.valueProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> calculateTotals()
                );
    }
private void loadEmployeesForOwner() {

    ObservableList<Employee> employees =
            employeeDAO.getAllEmployees();

    compforowner.setItems(employees);

    compforowner.setCellFactory(list -> new javafx.scene.control.ListCell<>() {

        @Override
        protected void updateItem(Employee employee, boolean empty) {

            super.updateItem(employee, empty);

            if (empty || employee == null) {
                setText(null);
            } else {
                setText(
                    employee.getEmpId()
                    + " - "
                    + employee.getFirstName()
                    + " "
                    + employee.getLastName()
                );
            }
        }
    });

    compforowner.setButtonCell(
        new javafx.scene.control.ListCell<>() {

            @Override
            protected void updateItem(Employee employee, boolean empty) {

                super.updateItem(employee, empty);

                if (empty || employee == null) {
                    setText(null);
                } else {
                    setText(
                        employee.getEmpId()
                        + " - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
                    );
                }
            }
        }
    );

    if (!employees.isEmpty()) {

        compforowner.getSelectionModel().selectFirst();

        updateEmployeeLabel(
            compforowner.getValue()
        );
    }

    compforowner.valueProperty().addListener(
        (obs, oldValue, newValue) ->
            updateEmployeeLabel(newValue)
    );
}
private void updateEmployeeLabel(Employee employee) {

    if (employee == null) {

        lblEmployeeName.setText("");

        return;
    }

    lblEmployeeName.setText(
        employee.getFirstName()
        + " "
        + employee.getLastName()
    );
}
    // =========================================================
    // PRODUCT TABLE
    // =========================================================

    private void initializeProductTable() {

        colProductCode.setCellValueFactory(
                new PropertyValueFactory<>("productCode")
        );

        colTradeName.setCellValueFactory(
                new PropertyValueFactory<>("tradeName")
        );

        colUnit.setCellValueFactory(
                new PropertyValueFactory<>("unit")
        );

        colPrice.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );

        addButtonToProductTable();
    }

    // =========================================================
    // ADD BUTTON INSIDE PRODUCT TABLE
    // =========================================================

    private void addButtonToProductTable() {

        Callback<TableColumn<SaleProduct, Void>,
                TableCell<SaleProduct, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn =
                            new Button("Add");

                    {
                        btn.setOnAction(event -> {

                            SaleProduct product =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            addProductToInvoice(product);
                        });
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
                            setGraphic(btn);
                        }
                    }
                };

        colAdd.setCellFactory(cellFactory);
    }

    // =========================================================
    // INVOICE TABLE
    // =========================================================

    private void initializeInvoiceTable() {

        colInvoiceCode.setCellValueFactory(
                new PropertyValueFactory<>("code")
        );

        colInvoiceName.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        colInvoiceUnit.setCellValueFactory(
                new PropertyValueFactory<>("unit")
        );

        colInvoicePrice.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );

        colInvoiceQty.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        colInvoiceTotal.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        addRemoveButtonToInvoiceTable();

        tblInvoice.setItems(invoiceList);
    }

    // =========================================================
    // REMOVE BUTTON INSIDE INVOICE TABLE
    // =========================================================

    private void addRemoveButtonToInvoiceTable() {

        Callback<TableColumn<InvoiceItem, Void>,
                TableCell<InvoiceItem, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn =
                            new Button("Remove");

                    {
                        btn.setOnAction(event -> {

                            InvoiceItem item =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            invoiceList.remove(item);

                            calculateTotals();
                        });
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
                            setGraphic(btn);
                        }
                    }
                };

        colRemove.setCellFactory(cellFactory);
    }

    // =========================================================
    // SEARCH PRODUCT
    // =========================================================

    @FXML
    private void handleSearchProduct(ActionEvent event) {

        String keyword =
                txtSearchProduct.getText()
                        .trim();

        if (keyword.isEmpty()) {

            productList.setAll(
                    salesDAO.loadProducts()
            );

        } else {

            productList.setAll(
                    salesDAO.searchProducts(keyword)
            );
        }
    }

    // =========================================================
    // ADD PRODUCT TO INVOICE
    // =========================================================

    private void addProductToInvoice(
            SaleProduct product) {

        for (InvoiceItem item : invoiceList) {

            if (item.getCode()
                    .equals(product.getProductCode())) {

                if (item.getQuantity()
                        >= product.getStock()) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "Stock",
                            "No more stock available."
                    );

                    return;
                }

                item.setQuantity(
                        item.getQuantity() + 1
                );

                tblInvoice.refresh();

                calculateTotals();

                return;
            }
        }

        InvoiceItem item =
                new InvoiceItem(
                        product.getProductCode(),
                        product.getTradeName(),
                        product.getUnit(),
                        product.getPrice(),
                        1
                );

        invoiceList.add(item);

        calculateTotals();
    }

    // =========================================================
    // TOTALS
    // =========================================================

    private void calculateTotals() {

        double subtotal = 0;

        for (InvoiceItem item : invoiceList) {

            subtotal += item.getTotal();
        }

        double discount = 0;

        try {

            if (!txtDiscount.getText()
                    .trim()
                    .isEmpty()) {

                double value =
                        Double.parseDouble(
                                txtDiscount.getText()
                        );

                if ("Percentage".equals(
                        cmbDiscountType.getValue())) {

                    discount =
                            subtotal * value / 100;

                } else if ("Amount".equals(
                        cmbDiscountType.getValue())) {

                    discount = value;
                }
            }

        } catch (NumberFormatException e) {

            discount = 0;
        }

        if (discount > subtotal) {
            discount = subtotal;
        }

        double tax =
                (subtotal - discount) * 0.10;

        double grand =
                subtotal - discount + tax;

        lblSubtotal.setText(
                String.format("%.2f", subtotal)
        );

        lblTax.setText(
                String.format("%.2f", tax)
        );

        lblGrandTotal.setText(
                String.format("%.2f", grand)
        );
currentGrandTotal = grand;
        calculateChange();
        
    }

    // =========================================================
    // CHANGE
    // =========================================================

    private void calculateChange() {

    String paidText = txtAmountPaid.getText().trim();

    // لا يوجد مبلغ مدفوع
    if (paidText.isEmpty()) {
        lblChange.setText("0.00");
        return;
    }

    try {

        double paid = Double.parseDouble(paidText);

        // استخدم القيمة الرقمية المحفوظة
        // بدل قراءة Label كنص
        double change = paid - currentGrandTotal;

        if (change < 0) {
            lblChange.setText("0.00");
        } else {
            lblChange.setText(
                    String.format("%.2f", change)
            );
        }

    } catch (NumberFormatException e) {

        lblChange.setText("0.00");
    }
}

    // =========================================================
    // ADD CUSTOMER
    // =========================================================

    @FXML
    private void handleAddCustomer(
            ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/CustomerForm.fxml"
                            )
                    );

            Parent root = loader.load();

            CustomerFormController controller =
                    loader.getController();

            controller.setCustomer(null);

            Stage stage = new Stage();

            stage.setTitle("Add Customer");

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.showAndWait();

            cmbCustomer.setItems(
                    salesDAO.loadCustomers()
            );

            String id =
                    controller.getAddedCustomerId();

            if (id != null) {

                for (String customer :
                        cmbCustomer.getItems()) {

                    if (customer.startsWith(
                            id + " - ")) {

                        cmbCustomer
                                .getSelectionModel()
                                .select(customer);

                        break;
                    }
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Cannot open Customer Form."
            );
        }
    }

    // =========================================================
    // CLEAR SALE
    // =========================================================

    @FXML
    private void handleClearSale(
            ActionEvent event) {

        invoiceList.clear();

        txtAmountPaid.clear();

        txtDiscount.clear();
currentGrandTotal = 0.0;
        cmbCustomer
                .getSelectionModel()
                .selectFirst();

        cmbDiscountType
                .getSelectionModel()
                .selectFirst();

        lblSubtotal.setText("0.00");
        lblTax.setText("0.00");
        lblGrandTotal.setText("0.00");
        lblChange.setText("0.00");

        lblInvoiceNo.setText(
                salesDAO.generateInvoiceNumber()
        );
    }

    // =========================================================
    // COMPLETE SALE
    // =========================================================

    @FXML
    private void handleCompleteSale(
            ActionEvent event) {

        if (invoiceList.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Empty Invoice",
                    "Please add products first."
            );

            return;
        }

        double grandTotal =
                getGrandTotal();

        if (grandTotal <= 0) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Invalid total."
            );

            return;
        }

        double paid;

        try {

            paid =
                    Double.parseDouble(
                            txtAmountPaid.getText()
                    );

        } catch (NumberFormatException e) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment",
                    "Enter amount paid."
            );

            return;
        }

        if (paid < grandTotal) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Payment",
                    "Amount paid is not enough."
            );

            return;
        }

        String customerID =
                salesDAO.getCustomerId(
                        cmbCustomer.getValue()
                );
String employeeId;

if (UserSession.isEmployee()) {

    employeeId = UserSession.getUserId();

} else {

    Employee selectedEmployee =
            compforowner.getValue();

    if (selectedEmployee == null) {

        showAlert(
                Alert.AlertType.WARNING,
                "Employee",
                "Please select the employee responsible for this sale."
        );

        return;
    }

    employeeId = selectedEmployee.getEmpId();
}
        boolean saved =
        salesDAO.saveSale(
                lblInvoiceNo.getText(),
                employeeId,
                customerID,
                grandTotal,
                invoiceList
        );

        if (saved) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Sale completed successfully."
            );

            productList.setAll(
                    salesDAO.loadProducts()
            );

            handleClearSale(null);

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database",
                    "Sale failed."
            );
        }
    }

    // =========================================================
    // OPEN SALES HISTORY
    // =========================================================

    @FXML
    private void handlesaleshistorybtn(
            ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/salesHistory.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Sales History");

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.showAndWait();

        } catch (Exception ex) {

            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Unable to open Sales History."
            );
        }
    }

    // =========================================================
    // GRAND TOTAL
    // =========================================================

   private double getGrandTotal() {
    return currentGrandTotal;
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
}