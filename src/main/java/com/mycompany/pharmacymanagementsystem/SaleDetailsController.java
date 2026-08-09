/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SaleDetailsController implements Initializable {

    // =========================================================
    // TABLE
    // =========================================================

 @FXML
private TableView<SalesDAO.SaleDetail> saleDetailsTable;

    @FXML
    private TableColumn<SalesDAO.SaleDetail, String> invoiceNumberColumn;

    @FXML
    private TableColumn<SalesDAO.SaleDetail, String> ProductName;

    @FXML
    private TableColumn<SalesDAO.SaleDetail, Integer> quantityColumn;

    @FXML
    private TableColumn<SalesDAO.SaleDetail, Double> sellingPriceColumn;

    @FXML
    private TableColumn<SalesDAO.SaleDetail, Double> totalColumn;

    // =========================================================
    // DAO
    // =========================================================

    private final SalesDAO salesDAO = new SalesDAO();

    private final ObservableList<SalesDAO.SaleDetail> saleDetailsList =
            FXCollections.observableArrayList();

    // =========================================================
    // INITIALIZE
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeTable();

        saleDetailsTable.setItems(saleDetailsList);
    }

    // =========================================================
    // INITIALIZE TABLE
    // =========================================================

    private void initializeTable() {

        invoiceNumberColumn.setCellValueFactory(
                new PropertyValueFactory<>("invoiceNumber")
        );

        ProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        sellingPriceColumn.setCellValueFactory(
                new PropertyValueFactory<>("sellingPrice")
        );

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );
    }

    // =========================================================
    // LOAD SALE DETAILS
    // =========================================================

    public void setInvoiceNumber(String invoiceNumber) {

        saleDetailsList.clear();

        if (invoiceNumber == null ||
                invoiceNumber.trim().isEmpty()) {

            return;
        }

        saleDetailsList.setAll(
                salesDAO.getSaleDetails(invoiceNumber)
        );
    }
}




