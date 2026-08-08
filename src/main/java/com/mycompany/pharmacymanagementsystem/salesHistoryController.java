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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class salesHistoryController implements javafx.fxml.Initializable {

    @FXML
    private Button clearinvoiceser;

    @FXML
    private Button cleartblsalesinvoic;

    @FXML
    private Button deletesale;

    @FXML
    private Button saledatailsbutton;

    @FXML
    private Button searchbtninvoicen;

    @FXML
    private TableView<SalesInvoice> tblsaleshistory;

    @FXML
    private TableColumn<SalesInvoice, String> codeinvoice;

    @FXML
    private TableColumn<SalesInvoice, String> dateinvoice;

    @FXML
    private TableColumn<SalesInvoice, String> cusinvoice;

    @FXML
    private TableColumn<SalesInvoice, String> employeeinvice;

    @FXML
    private TableColumn<SalesInvoice, Double> totalinvoice;

    @FXML
    private TextField txtinvoicenum;

    private final SalesDAO salesDAO = new SalesDAO();

    private final ObservableList<SalesInvoice> invoiceList =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupTable();

        loadInvoices();
    }

    // =========================================================
    // TABLE
    // =========================================================

    private void setupTable() {

        codeinvoice.setCellValueFactory(
                new PropertyValueFactory<>("invoiceNo")
        );

        dateinvoice.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        cusinvoice.setCellValueFactory(
                new PropertyValueFactory<>("customer")
        );

        employeeinvice.setCellValueFactory(
                new PropertyValueFactory<>("employee")
        );

        totalinvoice.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        tblsaleshistory.setItems(invoiceList);
    }

    // =========================================================
    // LOAD
    // =========================================================

    private void loadInvoices() {

        invoiceList.setAll(
                salesDAO.loadInvoices()
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    @FXML
    private void handleSearchInvoice(ActionEvent event) {

        String keyword =
                txtinvoicenum.getText().trim();

        if (keyword.isEmpty()) {

            loadInvoices();

        } else {

            invoiceList.setAll(
                    salesDAO.searchInvoice(keyword)
            );
        }
    }

    // =========================================================
    // CLEAR SEARCH
    // =========================================================

    @FXML
    private void handleClearSearch(ActionEvent event) {

        txtinvoicenum.clear();

        loadInvoices();
    }

    // =========================================================
    // CLEAR TABLE
    // =========================================================

    @FXML
    private void handleClearTable(ActionEvent event) {

        invoiceList.clear();

        tblsaleshistory.getSelectionModel()
                .clearSelection();
    }

    // =========================================================
    // DETAILS
    // =========================================================

    @FXML
    private void handleSaleDetails(ActionEvent event) {

        SalesInvoice selected =
                tblsaleshistory
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Selection",
                    "Please select an invoice first."
            );

            return;
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/saledetails.fxml"
                            )
                    );

            Parent root = loader.load();

            SaleDetailsController controller =
                    loader.getController();

            controller.setInvoiceNumber(
                    selected.getInvoiceNo()
            );

            Stage stage = new Stage();

            stage.setTitle("Sale Details");

            stage.setScene(new Scene(root));

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.showAndWait();

        } catch (Exception ex) {

            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Unable to open sale details."
            );
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @FXML
private void handleDeleteSale(ActionEvent event) {

    SalesInvoice selected =
            tblsaleshistory
                    .getSelectionModel()
                    .getSelectedItem();

    // -------------------------------------------------
    // No invoice selected
    // -------------------------------------------------

    if (selected == null) {

        showAlert(
                Alert.AlertType.WARNING,
                "No Selection",
                "Please select an invoice first."
        );

        return;
    }

    // -------------------------------------------------
    // Confirmation
    // -------------------------------------------------

    Alert confirmation =
            new Alert(Alert.AlertType.CONFIRMATION);

    confirmation.setTitle("Delete Invoice");
    confirmation.setHeaderText("Delete Invoice");
    confirmation.setContentText(
            "Are you sure you want to delete invoice "
            + selected.getInvoiceNo()
            + "?\n\n"
            + "The invoice details will be deleted and "
            + "the sold quantities will be returned to stock."
    );

    var result =
            confirmation.showAndWait();

    if (result.isEmpty() ||
            result.get() != javafx.scene.control.ButtonType.OK) {

        return;
    }

    // -------------------------------------------------
    // Delete from database
    // -------------------------------------------------

    boolean deleted =
            salesDAO.deleteSale(
                    selected.getInvoiceNo()
            );

    if (deleted) {

        // Remove from TableView
        invoiceList.remove(selected);

        tblsaleshistory
                .getSelectionModel()
                .clearSelection();

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Invoice "
                + selected.getInvoiceNo()
                + " was deleted successfully."
        );

    } else {

        showAlert(
                Alert.AlertType.ERROR,
                "Delete Failed",
                "Unable to delete invoice "
                + selected.getInvoiceNo()
                + "."
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

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
