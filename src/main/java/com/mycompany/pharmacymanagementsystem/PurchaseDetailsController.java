/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PurchaseDetailsController {


@FXML
private TableColumn<PurchaseDAO.PurchaseBatch, String> Batchid;

@FXML
private TableColumn<PurchaseDAO.PurchaseBatch, String> ProductName;

@FXML
private Label dateinpurcD;

@FXML
private TableColumn<PurchaseDAO.PurchaseBatch, String> expiry;

@FXML
private TableColumn<PurchaseDAO.PurchaseBatch, Number> price;

@FXML
private Label purchaseinpurD;

@FXML
private TableColumn<PurchaseDAO.PurchaseBatch, Number> quantity;

@FXML
private TableView<PurchaseDAO.PurchaseBatch> tblpurcD;

@FXML
private Label totalinpurD;

private final PurchaseDAO purchaseDAO =
        new PurchaseDAO();

private String purchaseNumber;


@FXML
public void initialize() {

    Batchid.setCellValueFactory(
            new PropertyValueFactory<>("batchNumber")
    );

    ProductName.setCellValueFactory(
            new PropertyValueFactory<>("productName")
    );

    price.setCellValueFactory(
            new PropertyValueFactory<>("purchasePrice")
    );

    quantity.setCellValueFactory(
            new PropertyValueFactory<>("quantity")
    );

    expiry.setCellValueFactory(
            new PropertyValueFactory<>("expireDate")
    );


    price.setCellFactory(column ->
            new TableCell<
                    PurchaseDAO.PurchaseBatch,
                    Number>() {

                @Override
                protected void updateItem(
                        Number item,
                        boolean empty) {

                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(
                                String.format(
                                        "$%.2f",
                                        item.doubleValue()
                                )
                        );
                    }
                }
            }
    );
}


// =========================================================
// RECEIVE PURCHASE NUMBER
// =========================================================

public void setPurchaseNumber(
        String purchaseNumber) {

    this.purchaseNumber = purchaseNumber;

    loadPurchase();
}


// =========================================================
// LOAD PURCHASE
// =========================================================

private void loadPurchase() {

    if (purchaseNumber == null ||
            purchaseNumber.isBlank()) {

        return;
    }

    Purchase purchase =
            purchaseDAO.getPurchaseByNumber(
                    purchaseNumber
            );


    if (purchase == null) {

        purchaseinpurD.setText("Not Found");
        dateinpurcD.setText("-");
        totalinpurD.setText("$0.00");

        tblpurcD.getItems().clear();

        return;
    }


    purchaseinpurD.setText(
            purchase.getPurchaseNumber()
    );


    if (purchase.getDate() != null) {

        dateinpurcD.setText(
                purchase.getDate().toString()
        );

    } else {

        dateinpurcD.setText("-");
    }


    totalinpurD.setText(
            String.format(
                    "$%.2f",
                    purchase.getTotal()
            )
    );


    ObservableList<
            PurchaseDAO.PurchaseBatch
            > details =
            purchaseDAO.getPurchaseDetails(
                    purchaseNumber
            );


    tblpurcD.setItems(details);
}


}


