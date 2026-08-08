/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class InStReportController implements Initializable {


@FXML
private TableColumn<InventoryDAO.InventoryItem, Double> Price6;

@FXML
private TableColumn<InventoryDAO.InventoryItem, String> Productcode1;

@FXML
private TableColumn<InventoryDAO.InventoryItem, String> Productname1;

@FXML
private TableColumn<InventoryDAO.InventoryItem, Integer> Stock3;

@FXML
private TableColumn<InventoryDAO.InventoryItem, String> expdate7;

@FXML
private TableColumn<InventoryDAO.InventoryItem, String> unitcolinstreport;

@FXML
private Label expierdbatshes1;



@FXML
private Label lowstock1;

@FXML
private TableView<InventoryDAO.InventoryItem> tblforinstrep;

@FXML
private Label totalproducts1;

@FXML
private Label totalstock1;

@FXML
private ComboBox<ProductChoice> cmbProduct;


private final InventoryDAO inventoryDAO =
        new InventoryDAO();

private final ObservableList<InventoryDAO.InventoryItem> inventoryList =
        FXCollections.observableArrayList();


@Override
public void initialize(URL url, ResourceBundle rb) {

    initializeTable();

    tblforinstrep.setItems(inventoryList);

    loadProductsIntoComboBox();

    loadReport();

    cmbProduct.valueProperty().addListener(
            (obs, oldValue, newValue) -> {

                if (newValue == null) {

                    loadReport();

                } else if (newValue.getProductCode().equals("ALL")) {

                    loadReport();

                } else {

                    loadSelectedProduct(
                            newValue.getProductCode()
                    );
                }
            }
    );
}


// =========================================================
// TABLE
// =========================================================

private void initializeTable() {

    Productcode1.setCellValueFactory(
            new PropertyValueFactory<>("productCode")
    );

    Productname1.setCellValueFactory(
            new PropertyValueFactory<>("productName")
    );

    unitcolinstreport.setCellValueFactory(
            new PropertyValueFactory<>("unit")
    );

    Price6.setCellValueFactory(
            new PropertyValueFactory<>("price")
    );

    Stock3.setCellValueFactory(
            new PropertyValueFactory<>("stock")
    );

    expdate7.setCellValueFactory(
            new PropertyValueFactory<>("expireDate")
    );
}


// =========================================================
// LOAD PRODUCTS INTO COMBOBOX
// =========================================================

private void loadProductsIntoComboBox() {

    ObservableList<ProductChoice> choices =
            FXCollections.observableArrayList();

    choices.add(
            new ProductChoice(
                    "ALL",
                    "All Products"
            )
    );

    ObservableList<InventoryDAO.InventoryItem> products =
            inventoryDAO.loadInventory();

    for (InventoryDAO.InventoryItem item : products) {

        choices.add(
                new ProductChoice(
                        item.getProductCode(),
                        item.getProductCode()
                        + " - "
                        + item.getProductName()
                )
        );
    }

    cmbProduct.setItems(choices);

    cmbProduct.getSelectionModel().selectFirst();
}


// =========================================================
// LOAD ALL REPORT
// =========================================================

private void loadReport() {

    inventoryList.setAll(
            inventoryDAO.loadInventory()
    );

    updateSummary();
}


// =========================================================
// LOAD SELECTED PRODUCT
// =========================================================

private void loadSelectedProduct(
        String productCode) {

    inventoryList.setAll(
            inventoryDAO.searchInventory(productCode)
    );
}


// =========================================================
// SUMMARY
// =========================================================

private void updateSummary() {

    totalproducts1.setText(
            String.valueOf(
                    inventoryDAO.getTotalProducts()
            )
    );

    totalstock1.setText(
            String.valueOf(
                    inventoryDAO.getTotalStock()
            )
    );

    lowstock1.setText(
            String.valueOf(
                    inventoryDAO.getLowStockCount()
            )
    );

    expierdbatshes1.setText(
            String.valueOf(
                    inventoryDAO.getExpiredBatchesCount()
            )
    );
}


// =========================================================
// GENERATE
// =========================================================




// =========================================================
// PRODUCT CHOICE MODEL
// =========================================================

public static class ProductChoice {

    private final String productCode;
    private final String displayName;


    public ProductChoice(
            String productCode,
            String displayName) {

        this.productCode = productCode;
        this.displayName = displayName;
    }


    public String getProductCode() {

        return productCode;
    }


    public String getDisplayName() {

        return displayName;
    }


    @Override
    public String toString() {

        return displayName;
    }
}


}
