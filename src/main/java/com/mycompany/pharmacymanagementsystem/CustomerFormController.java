/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerFormController {

    @FXML
    private TextField txtidcust;

    @FXML
    private TextField txtfnamecust;

    @FXML
    private TextField txtmnamecust;

    @FXML
    private TextField txttnamecust;

    @FXML
    private TextField txtlnamecust;

    @FXML
    private TextField txtphonecust;

    @FXML
    private TextArea txtmedicalcust;

    @FXML
    private TextArea txtallergiescust;

    @FXML
    private Button btnsavecust;

    @FXML
    private Button btncancelcust;


    private Customer customer;

    private boolean updateMode = false;


    public void setCustomer(Customer customer) {

        this.customer = customer;


        if (customer == null) {

            // ADD MODE
            updateMode = false;

            txtidcust.setDisable(false);

            clearFields();

        } else {

            // UPDATE MODE
            updateMode = true;

            txtidcust.setDisable(true);

            fillFields();
        }
    }


    private void fillFields() {

        txtidcust.setText(customer.getCustId());

        txtfnamecust.setText(customer.getFirstName());

        txtmnamecust.setText(customer.getMiddleName());

        txttnamecust.setText(customer.getThirdName());

        txtlnamecust.setText(customer.getLastName());

        txtphonecust.setText(customer.getPhone());

        txtmedicalcust.setText(customer.getMedicalNotes());

        txtallergiescust.setText(customer.getAllergies());
    }


    private void clearFields() {

        txtidcust.clear();
        txtfnamecust.clear();
        txtmnamecust.clear();
        txttnamecust.clear();
        txtlnamecust.clear();
        txtphonecust.clear();
        txtmedicalcust.clear();
        txtallergiescust.clear();
    }


    @FXML
    void handleSave(ActionEvent event) {

        if (!validateFields()) {
            return;
        }


        if (updateMode) {

            updateCustomer();

        } else {

            addCustomer();
        }
    }


public void setCustomerData(Customer customer) {

    this.customer = customer;

    // ID لا يمكن تغييره أثناء التعديل
    txtidcust.setText(customer.getCustId());
    txtidcust.setDisable(true);

    // تعبئة باقي البيانات
    txtfnamecust.setText(customer.getFirstName());
    txtmnamecust.setText(customer.getMiddleName());
    txttnamecust.setText(customer.getThirdName());
    txtlnamecust.setText(customer.getLastName());
    txtphonecust.setText(customer.getPhone());
    txtmedicalcust.setText(customer.getMedicalNotes());
    txtallergiescust.setText(customer.getAllergies());
}


    private void addCustomer() {

        String sql = """
                INSERT INTO customer
                (
                    cust_id,
                    first_name,
                    middle_name,
                    third_name,
                    last_name,
                    phone,
                    medicalnotes,
                    allergies
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, txtidcust.getText().trim());

            ps.setString(2, txtfnamecust.getText().trim());

            ps.setString(3, emptyToNull(txtmnamecust.getText()));

            ps.setString(4, emptyToNull(txttnamecust.getText()));

            ps.setString(5, txtlnamecust.getText().trim());

            ps.setString(6, emptyToNull(txtphonecust.getText()));

            ps.setString(7, emptyToNull(txtmedicalcust.getText()));

            ps.setString(8, emptyToNull(txtallergiescust.getText()));


            ps.executeUpdate();


            showInformation(
                    "Success",
                    "Customer added successfully."
            );


            closeWindow();


        } catch (Exception e) {

            if (e.getMessage() != null &&
                    e.getMessage().contains("customer_pkey")) {

                showError(
                        "Duplicate ID",
                        "Customer ID already exists."
                );

            } else {

                showError(
                        "Database Error",
                        e.getMessage()
                );
            }
        }
    }


    private void updateCustomer() {

        String sql = """
                UPDATE customer
                SET first_name = ?,
                    middle_name = ?,
                    third_name = ?,
                    last_name = ?,
                    phone = ?,
                    medicalnotes = ?,
                    allergies = ?
                WHERE cust_id = ?
                """;


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, txtfnamecust.getText().trim());

            ps.setString(2, emptyToNull(txtmnamecust.getText()));

            ps.setString(3, emptyToNull(txttnamecust.getText()));

            ps.setString(4, txtlnamecust.getText().trim());

            ps.setString(5, emptyToNull(txtphonecust.getText()));

            ps.setString(6, emptyToNull(txtmedicalcust.getText()));

            ps.setString(7, emptyToNull(txtallergiescust.getText()));

            ps.setString(8, customer.getCustId());


            ps.executeUpdate();


            showInformation(
                    "Success",
                    "Customer updated successfully."
            );


            closeWindow();


        } catch (Exception e) {

            showError(
                    "Database Error",
                    e.getMessage()
            );
        }
    }


    private boolean validateFields() {

        if (txtidcust.getText().trim().isEmpty()) {

            showWarning(
                    "Validation",
                    "Customer ID is required."
            );

            txtidcust.requestFocus();

            return false;
        }


        if (txtfnamecust.getText().trim().isEmpty()) {

            showWarning(
                    "Validation",
                    "First name is required."
            );

            txtfnamecust.requestFocus();

            return false;
        }


        if (txtlnamecust.getText().trim().isEmpty()) {

            showWarning(
                    "Validation",
                    "Last name is required."
            );

            txtlnamecust.requestFocus();

            return false;
        }


        return true;
    }


    private String emptyToNull(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }


    @FXML
    void handleCancel(ActionEvent event) {

        closeWindow();
    }


    private void closeWindow() {

        Stage stage =
                (Stage) btnsavecust.getScene().getWindow();

        stage.close();
    }


    private void showWarning(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void showInformation(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }


    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}

