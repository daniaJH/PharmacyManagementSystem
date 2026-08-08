/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PurchaseReportController {

    @FXML
    private TableColumn<Purchase, LocalDate> Date1;

    @FXML
    private TableColumn<Purchase, String> Invoice1;

    @FXML
    private TableColumn<Purchase, Integer> Items1;

    @FXML
    private TableColumn<Purchase, Double> Total1;

    @FXML
    private Label avgpurchase;

    @FXML
    private DatePicker enddate1;

    @FXML
    private Button generate;

    @FXML
    private DatePicker statedate1;

    @FXML
    private TableView<Purchase> tblfprrappusheas;

    @FXML
    private Label totalinvoice;

    @FXML
    private Label totalitem;

    @FXML
    private Label totalpur;

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    private final ObservableList<Purchase> purchaseList =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        Invoice1.setCellValueFactory(
                new PropertyValueFactory<>("purchaseNumber")
        );

        Date1.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        Total1.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        tblfprrappusheas.setItems(purchaseList);

        totalpur.setText("0.00");
        totalinvoice.setText("0");
        totalitem.setText("0");
        avgpurchase.setText("0.00");
    }


    @FXML
private void generateReport() {

    LocalDate start = statedate1.getValue();
    LocalDate end = enddate1.getValue();

    if (start == null || end == null) {
        return;
    }

    if (start.isAfter(end)) {
        return;
    }

    ObservableList<Purchase> allPurchases =
            purchaseDAO.loadPurchases();

    purchaseList.clear();

    double totalPurchases = 0;
    int invoiceCount = 0;

    for (Purchase purchase : allPurchases) {

        LocalDate date = purchase.getDate();

        if (date == null) {
            continue;
        }

        if (!date.isBefore(start)
                && !date.isAfter(end)) {

            purchaseList.add(purchase);

            totalPurchases += purchase.getTotal();

            invoiceCount++;
        }
    }

    // Total Purchases
    totalpur.setText(
            String.format("%.2f", totalPurchases)
    );

    // Total Invoices
    totalinvoice.setText(
            String.valueOf(invoiceCount)
    );

    // Average Purchase
    double average = 0;

    if (invoiceCount > 0) {
        average = totalPurchases / invoiceCount;
    }

    avgpurchase.setText(
            String.format("%.2f", average)
    );

    // Total Items
    int totalItems =
            purchaseDAO.getTotalItems(start, end);

    totalitem.setText(
            String.valueOf(totalItems)
    );
}}