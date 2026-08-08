/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PurchasesController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private Button addnewpurchasebtn;

    @FXML
    private Button btnsearchpur;

    @FXML
    private Button clearbtnsearch;

    @FXML
    private Button cleartblbtn;

    @FXML
    private TableColumn<Purchase, LocalDate> coldate;

    @FXML
    private TableColumn<Purchase, String> colpurshnum;

    @FXML
    private TableColumn<Purchase, Double> coltotal;

    @FXML
    private TableColumn<Purchase, Void> colview;

    

    @FXML
    private TableView<Purchase> tblpurchase;

    @FXML
    private TextField txtsearchpurchase;


    // =========================================================
    // DAO + LIST
    // =========================================================

    private final PurchaseDAO purchaseDAO =
            new PurchaseDAO();

    private final ObservableList<Purchase> purchaseList =
            FXCollections.observableArrayList();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        initializePurchaseTable();

        loadPurchases();
    }


    // =========================================================
    // TABLE
    // =========================================================

    private void initializePurchaseTable() {

        colpurshnum.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue()
                                        .getPurchaseNumber()
                        )
        );


        coldate.setCellValueFactory(
                data ->
                        new SimpleObjectProperty<>(
                                data.getValue()
                                        .getDate()
                        )
        );


        coltotal.setCellValueFactory(
                data ->
                        new SimpleDoubleProperty(
                                data.getValue()
                                        .getTotal()
                        )
                        .asObject()
        );


        /*
         * Supplier is not available in the current
         * database design.
         *
         * We leave the column empty instead of
         * inventing supplier information.
         */

       


        addViewButtonToTable();

        tblpurchase.setItems(purchaseList);
    }


    // =========================================================
    // LOAD PURCHASES
    // =========================================================

    private void loadPurchases() {

        purchaseList.setAll(
                purchaseDAO.loadPurchases()
        );
    }


    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    void handlesearchpurches(ActionEvent event) {

        String keyword =
                txtsearchpurchase.getText()
                        .trim();

        if (keyword.isEmpty()) {

            loadPurchases();

            return;
        }


        purchaseList.setAll(
                purchaseDAO.searchPurchases(
                        keyword
                )
        );


        if (purchaseList.isEmpty()) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Search",
                    "No purchase invoice found."
            );
        }
    }


    // =========================================================
    // CLEAR SEARCH
    // =========================================================

    @FXML
    void handleclearsearch(ActionEvent event) {

        txtsearchpurchase.clear();

        loadPurchases();
    }


    // =========================================================
    // CLEAR TABLE
    // =========================================================

    @FXML
    void handleclearpurshase(ActionEvent event) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Clear Purchases"
        );

        confirmation.setHeaderText(null);

        confirmation.setContentText(
                "Do you want to clear the purchase table?"
        );


        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response ==
                            ButtonType.OK) {

                        purchaseList.clear();
                    }
                });
    }


    // =========================================================
    // NEW PURCHASE
    // =========================================================

    @FXML
    void handlenewpurchase(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/NewPurchase.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            Stage stage =
                    new Stage();

            stage.setTitle(
                    "New Purchase"
            );

            stage.setScene(
                    new Scene(root)
            );


            stage.initModality(
                    Modality.APPLICATION_MODAL
            );


            stage.showAndWait();


            /*
             * Reload the table after the
             * New Purchase window closes.
             */

            loadPurchases();


        } catch (IOException ex) {

            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Unable to open New Purchase page."
            );
        }
    }


    // =========================================================
    // VIEW BUTTON
    // =========================================================
// =========================================================
// VIEW BUTTON
// =========================================================

private void addViewButtonToTable() {

    Callback<
            TableColumn<Purchase, Void>,
            TableCell<Purchase, Void>
            > cellFactory =

            param -> new TableCell<>() {

                private final Button btn =
                        new Button("View");

                {
                    btn.setOnAction(
                            event -> {

                                Purchase purchase =
                                        getTableView()
                                                .getItems()
                                                .get(
                                                        getIndex()
                                                );

                                openPurchaseDetails(
                                        purchase
                                );
                            }
                    );
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

    colview.setCellFactory(
            cellFactory
    );
}


    // =========================================================
    // OPEN PURCHASE DETAILS
    // =========================================================

    // =========================================================
// OPEN PURCHASE DETAILS PAGE
// =========================================================

private void openPurchaseDetails(Purchase purchase) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/fxml/PurchaseDetails.fxml"
                )
        );

        Parent root = loader.load();

        PurchaseDetailsController controller =
                loader.getController();

        controller.setPurchaseNumber(
                purchase.getPurchaseNumber()
        );

        Stage stage = new Stage();

        stage.setTitle("Purchase Details");

        stage.setScene(
                new Scene(root)
        );

        stage.initModality(
                Modality.APPLICATION_MODAL
        );

        stage.showAndWait();

    } catch (IOException ex) {

        ex.printStackTrace();

        showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Unable to open Purchase Details page."
        );
    }
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