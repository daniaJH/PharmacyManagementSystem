/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerController {

    @FXML
    private TableColumn<Customer, String> Fnamecust;

    @FXML
    private TableColumn<Customer, String> Lnamecust;

    @FXML
    private TableColumn<Customer, String> MNcolcust;

    @FXML
    private TableColumn<Customer, String> Mnamecust;

    @FXML
    private TableColumn<Customer, String> PhcolCust;

    @FXML
    private TableColumn<Customer, String> TnameCust;

    @FXML
    private TableColumn<Customer, String> allcolcust;

    @FXML
    private Button btnaddcust;

    @FXML
    private Button btnclearcust;

    @FXML
    private Button btnclearsearch;

    @FXML
    private Button btndeletecust;

    @FXML
    private Button btnsearchcust;

    @FXML
    private Button btnupdatecust;

    @FXML
    private TableColumn<Customer, String> colidcust;

    @FXML
    private TableView<Customer> tblcust;

    @FXML
    private TextField txtsearchcust;


    private final ObservableList<Customer> customerList =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        setupTableColumns();

        loadCustomers();
    }


    private void setupTableColumns() {

        colidcust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getCustId()
                        )
        );

        Fnamecust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getFirstName()
                        )
        );

        Mnamecust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getMiddleName()
                        )
        );

        TnameCust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getThirdName()
                        )
        );

        Lnamecust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getLastName()
                        )
        );

        PhcolCust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getPhone()
                        )
        );

        MNcolcust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getMedicalNotes()
                        )
        );

        allcolcust.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getAllergies()
                        )
        );
    }


    private void loadCustomers() {

        customerList.clear();

        String sql = """
                SELECT cust_id,
                       first_name,
                       middle_name,
                       third_name,
                       last_name,
                       phone,
                       medicalnotes,
                       allergies
                FROM customer
                ORDER BY cust_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Customer customer = new Customer(
                        rs.getString("cust_id"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("third_name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getString("medicalnotes"),
                        rs.getString("allergies")
                );

                customerList.add(customer);
            }

            tblcust.setItems(customerList);

        } catch (Exception e) {
            showError("Database Error",
                    "Could not load customers.",
                    e.getMessage());
        }
    }


   

@FXML
void handleupdatecust(ActionEvent event) {

    // 1. جلب العميل المحدد من الجدول
    Customer selectedCustomer =
            tblcust.getSelectionModel().getSelectedItem();

    // 2. التأكد من اختيار عميل
    if (selectedCustomer == null) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("No Selection");
        alert.setHeaderText(null);
        alert.setContentText(
                "Please select a customer from the table first."
        );

        alert.showAndWait();

        return;
    }

    // 3. فتح نافذة التعديل
    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/fxml/CustomerForm.fxml"
                )
        );

        Parent root = loader.load();

        // 4. الحصول على Controller الخاص بالنافذة
        CustomerFormController controller =
                loader.getController();

        // 5. إرسال العميل المحدد إلى نافذة التعديل
        controller.setCustomerData(selectedCustomer);

        Stage stage = new Stage();

        stage.setTitle(
                "Edit Customer - "
                        + selectedCustomer.getCustId()
        );

        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setResizable(false);

        // 6. تحديث الجدول بعد إغلاق النافذة
        stage.setOnHiding(e -> loadCustomers());

        stage.show();

    } catch (IOException e) {

        e.printStackTrace();
    }
}



@FXML
void handleaddcust(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/CustomerForm.fxml")
        );

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Add Customer");

        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setResizable(false);

        stage.setOnHiding(e -> loadCustomers());

        stage.show();

    } catch (IOException e) {

        e.printStackTrace();
    }
}






    @FXML
    void handledeletecust(ActionEvent event) {

        Customer selectedCustomer =
                tblcust.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {

            showWarning(
                    "No Customer Selected",
                    "Please select a customer to delete."
            );

            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Customer");
        confirmation.setHeaderText("Delete Customer");
        confirmation.setContentText(
                "Are you sure you want to delete customer "
                        + selectedCustomer.getCustId()
                        + "?"
        );

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {

            return;
        }


        String sql =
                "DELETE FROM customer WHERE cust_id = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, selectedCustomer.getCustId());

            ps.executeUpdate();

            loadCustomers();

            showInformation(
                    "Deleted",
                    "Customer deleted successfully."
            );

        } catch (Exception e) {

            showError(
                    "Delete Error",
                    "Could not delete customer.",
                    e.getMessage()
            );
        }
    }


  
@FXML
void handlesearchcustomer(ActionEvent event) {

    String search = txtsearchcust.getText().trim();

    // إذا كان البحث فارغًا، نعرض جميع العملاء
    if (search.isEmpty()) {
        loadCustomers();
        return;
    }

    customerList.clear();

    String sql;

    try (Connection conn = DatabaseConnection.getConnection()) {

        /*
         * إذا كان المستخدم كتب أكثر من كلمة:
         * نفترض أنها First Name + Last Name
         */
        if (search.contains(" ")) {

            String[] parts = search.split("\\s+");

            String firstName = parts[0];
            String lastName = parts[parts.length - 1];

            sql = """
                    SELECT cust_id,
                           first_name,
                           middle_name,
                           third_name,
                           last_name,
                           phone,
                           medicalnotes,
                           allergies
                    FROM customer
                    WHERE LOWER(first_name) = LOWER(?)
                      AND LOWER(last_name) = LOWER(?)
                    ORDER BY cust_id
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, firstName);
                ps.setString(2, lastName);

                loadSearchResults(ps);
            }

        } else {

            /*
             * كلمة واحدة:
             * إما Customer ID
             * أو First Name
             */
            sql = """
                    SELECT cust_id,
                           first_name,
                           middle_name,
                           third_name,
                           last_name,
                           phone,
                           medicalnotes,
                           allergies
                    FROM customer
                    WHERE LOWER(cust_id) = LOWER(?)
                       OR LOWER(first_name) = LOWER(?)
                    ORDER BY cust_id
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, search);
                ps.setString(2, search);

                loadSearchResults(ps);
            }
        }

        tblcust.setItems(customerList);

    } catch (Exception e) {

        showError(
                "Search Error",
                "Could not search customers.",
                e.getMessage()
        );
    }
}

private void loadSearchResults(PreparedStatement ps)
        throws Exception {

    try (ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Customer customer = new Customer(
                    rs.getString("cust_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("third_name"),
                    rs.getString("last_name"),
                    rs.getString("phone"),
                    rs.getString("medicalnotes"),
                    rs.getString("allergies")
            );

            customerList.add(customer);
        }
    }
}





    @FXML
    void handleclearcustomer(ActionEvent event) {

        txtsearchcust.clear();

        loadCustomers();
    }


    @FXML
    void handlecleartbl(ActionEvent event) {

        tblcust.getSelectionModel().clearSelection();
    }


    @FXML
    void handleclearsearch(ActionEvent event) {

        txtsearchcust.clear();

        loadCustomers();
    }


    private void showWarning(String title, String message) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void showInformation(String title, String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void showError(
            String title,
            String header,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}

