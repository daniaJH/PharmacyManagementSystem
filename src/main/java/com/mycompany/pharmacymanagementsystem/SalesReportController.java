/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.EmployeeDAO;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalesReportController {

    @FXML
    private TableColumn<SalesInvoice, String> Customer1;

    @FXML
    private TableColumn<SalesInvoice, String> Date1;

    @FXML
    private TableColumn<SalesInvoice, String> Employee1;

    @FXML
    private Button Generate;

    @FXML
    private TableColumn<SalesInvoice, String> Invoiceno1;

    @FXML
    private TableColumn<SalesInvoice, Double> Total1;

    @FXML
    private Label avgsales;

    @FXML
    private ComboBox<Employee> comboemp;

    @FXML
    private DatePicker dateend;

    @FXML
    private DatePicker datestarte;

    @FXML
    private Label itemsoled;

    @FXML
    private TableView<SalesInvoice> tblrep;

    @FXML
    private Label totalInvoices;

    @FXML
    private Label totalsales;


    private final SalesDAO salesDAO = new SalesDAO();

    private final EmployeeDAO employeeDAO = new EmployeeDAO();


    @FXML
    public void initialize() {

        initializeTable();

        loadEmployees();

        // التاريخ الافتراضي: هذا الشهر
        datestarte.setValue(
                LocalDate.now().withDayOfMonth(1)
        );

        dateend.setValue(
                LocalDate.now()
        );

        // أول ما تفتح الشاشة، اعرض التقرير
        generateReport();
    }


    // =========================================================
    // TABLE
    // =========================================================

    private void initializeTable() {

        Invoiceno1.setCellValueFactory(
                new PropertyValueFactory<>("invoiceNo")
        );

        Date1.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        Customer1.setCellValueFactory(
                new PropertyValueFactory<>("customer")
        );

        Employee1.setCellValueFactory(
                new PropertyValueFactory<>("employee")
        );

        Total1.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );
    }


    // =========================================================
    // EMPLOYEES
    // =========================================================

    private void loadEmployees() {

        ObservableList<Employee> employees =
                employeeDAO.getAllEmployees();

        comboemp.setItems(employees);

        // خيار جميع الموظفين
        comboemp.getItems().add(0, null);

        comboemp.getSelectionModel().selectFirst();
    }


    // =========================================================
    // GENERATE REPORT
    // =========================================================

    @FXML
    void Generatebtnsalerep(ActionEvent event) {

        generateReport();
    }


    private void generateReport() {

        LocalDate startDate =
                datestarte.getValue();

        LocalDate endDate =
                dateend.getValue();


        if (startDate == null || endDate == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Date",
                    "Please select start and end dates."
            );

            return;
        }


        if (startDate.isAfter(endDate)) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Date",
                    "Start date cannot be after end date."
            );

            return;
        }


        ObservableList<SalesInvoice> allInvoices =
                salesDAO.loadInvoices();


        ObservableList<SalesInvoice> filtered =
                FXCollections.observableArrayList();


        Employee selectedEmployee =
                comboemp.getValue();


        for (SalesInvoice invoice : allInvoices) {

            LocalDate invoiceDate =
                    LocalDate.parse(invoice.getDate());


            // Date filter
            if (invoiceDate.isBefore(startDate)
                    || invoiceDate.isAfter(endDate)) {

                continue;
            }


            // Employee filter
            if (selectedEmployee != null) {

                String selectedName =
                        selectedEmployee.getFirstName()
                        + " "
                        + selectedEmployee.getLastName();


                if (!selectedName.equalsIgnoreCase(
                        invoice.getEmployee())) {

                    continue;
                }
            }


            filtered.add(invoice);
        }


        tblrep.setItems(filtered);

        calculateSummary(filtered);
    }


    // =========================================================
    // SUMMARY CARDS
    // =========================================================

    private void calculateSummary(
            ObservableList<SalesInvoice> invoices) {

        int invoiceCount =
                invoices.size();


        double total =
                0;


        for (SalesInvoice invoice : invoices) {

            total += invoice.getTotal();
        }


        double average =
                invoiceCount > 0
                ? total / invoiceCount
                : 0;


        totalInvoices.setText(
                String.valueOf(invoiceCount)
        );


        totalsales.setText(
                String.format("$%.2f", total)
        );


        avgsales.setText(
                String.format("$%.2f", average)
        );


        // حالياً نحسب عدد الوحدات من sale_detail
        int items =
                calculateItemsSold(invoices);


        itemsoled.setText(
                String.valueOf(items)
        );
    }


    // =========================================================
    // ITEMS SOLD
    // =========================================================

    private int calculateItemsSold(
            ObservableList<SalesInvoice> invoices) {

        int totalItems = 0;


        for (SalesInvoice invoice : invoices) {

            ObservableList<SalesDAO.SaleDetail> details =
                    salesDAO.getSaleDetails(
                            invoice.getInvoiceNo()
                    );


            for (SalesDAO.SaleDetail detail : details) {

                totalItems += detail.getQuantity();
            }
        }


        return totalItems;
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