/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.ManufacturerDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ManufController implements Initializable {

    @FXML
    private Button addbtncompany;

    @FXML
    private Button btnClearCompany;

    @FXML
    private Button btnSearchCompany;

    @FXML
    private Button clearbtncompany;

    @FXML
    private TableView<Manufacturer> companyTable;

    @FXML
    private TableColumn<Manufacturer, String> companycolcity;

    @FXML
    private TableColumn<Manufacturer, String> companycolcode;

    @FXML
    private TableColumn<Manufacturer, String> companycolcountry;

    @FXML
    private TableColumn<Manufacturer, String> companycolinz;

    @FXML
    private TableColumn<Manufacturer, String> companycolname;

    @FXML
    private TableColumn<Manufacturer, String> companycolphone;

    @FXML
    private Button deletebtncompany;

    @FXML
    private Button updatebtncompany;

    @FXML
    private TextField txtSearchCompany;


    private final ManufacturerDAO manufacturerDAO =
            new ManufacturerDAO();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTableColumns();

        loadManufacturers();
    }


    /*
     * =========================================================
     * TABLE COLUMNS
     * =========================================================
     */

   private void setupTableColumns() {

    companycolcode.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getManufCode()
            )
    );

    companycolname.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getName()
            )
    );

    companycolphone.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getPhone()
            )
    );

    companycolcountry.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getCountry()
            )
    );

    companycolcity.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getCity()
            )
    );

    companycolinz.setCellValueFactory(
            data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getIndustrialZone()
            )
    );
}


    /*
     * =========================================================
     * LOAD DATA
     * =========================================================
     */

   


    /*
     * =========================================================
     * ADD COMPANY
     * =========================================================
     */

 @FXML
void handleAddCompany(ActionEvent event) {

    try {

        URL url = getClass().getResource(
                "/fxml/AddManufacturerDialog.fxml"
        );

        System.out.println("FXML URL = " + url);

        FXMLLoader loader = new FXMLLoader(url);

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Add / Edit Company");
        stage.setScene(new Scene(root));

        stage.setOnHiding(e -> loadManufacturers());

        stage.show();

    } catch (Exception e) {

        e.printStackTrace();

        showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Could not open Add Company window.\n\n"
                + e.getClass().getName()
                + "\n"
                + e.getMessage()
        );
    }
}


    /*
     * =========================================================
     * UPDATE COMPANY
     * =========================================================
     */

    @FXML
    void handleUpdateCompany(ActionEvent event) {

        Manufacturer selectedManufacturer =
                companyTable
                        .getSelectionModel()
                        .getSelectedItem();


        /*
         * Nothing selected.
         */
        if (selectedManufacturer == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Selection",
                    "Please select a company from the table first."
            );

            return;
        }


        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/AddManufacturerDialog.fxml"
                            )
                    );

            Parent root = loader.load();


            /*
             * Get Add/Edit controller.
             */
            AddManufController controller =
                    loader.getController();


            /*
             * Send selected manufacturer.
             */
            controller.setManufacturerData(
                    selectedManufacturer
            );


            Stage stage = new Stage();

            stage.setTitle(
                    "Edit Company - "
                    + selectedManufacturer.getManufCode()
            );

            stage.setScene(
                    new Scene(root)
            );


            /*
             * Refresh table automatically.
             */
            stage.setOnHiding(e ->
                    loadManufacturers()
            );


            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not open the Edit Company window."
            );
        }
    }


    /*
     * =========================================================
     * DELETE COMPANY
     * =========================================================
     */

    @FXML
    void handleDeleteCompany(ActionEvent event) {

        Manufacturer selectedManufacturer =
                companyTable
                        .getSelectionModel()
                        .getSelectedItem();


        /*
         * No selection.
         */
        if (selectedManufacturer == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Selection",
                    "Please select a company from the table first."
            );

            return;
        }


        String code =
                selectedManufacturer.getManufCode();

        String name =
                selectedManufacturer.getName();


        /*
         * Confirmation dialog.
         */
        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Delete Company");

        confirmation.setHeaderText(
                "Are you sure you want to delete this company?"
        );

        confirmation.setContentText(
                "Company Code: " + code
                + "\nCompany Name: " + name
        );


        if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {

            return;
        }


        /*
         * First check whether products are using
         * this manufacturer.
         */
        if (manufacturerDAO.hasProducts(code)) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Cannot Delete Company",
                    "This company cannot be deleted because "
                    + "there are products associated with it."
            );

            return;
        }


        /*
         * No products use it -> delete.
         */
        boolean success =
                manufacturerDAO.deleteManufacturer(code);


        if (success) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Company deleted successfully."
            );

            loadManufacturers();

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Delete Failed",
                    "Could not delete the company."
            );
        }
    }

@FXML
void handleClearSearch(ActionEvent event) {

    txtSearchCompany.clear();

    loadManufacturers();

    companyTable.getSelectionModel().clearSelection();
}
    /*
     * =========================================================
     * SEARCH
     * =========================================================
     */

    @FXML
    void handleSearchCompany(ActionEvent event) {

        loadManufacturers();
    }

    /*
     * =========================================================
     * CLEAR SEARCH
     * =========================================================
     */
private void loadManufacturers() {

    String searchText = txtSearchCompany.getText().trim();

    ObservableList<Manufacturer> list =
            manufacturerDAO.getManufacturers(searchText);

    companyTable.setItems(list);
}
    @FXML
   


    /*
     * =========================================================
     * CLEAR TABLE
     * =========================================================
     */

    
    void handleClearTable(ActionEvent event) {

        companyTable.getItems().clear();

        companyTable.getSelectionModel().clearSelection();
    }


    /*
     * =========================================================
     * ALERT
     * =========================================================
     */

    private void showAlert(
            Alert.AlertType type,
            String title,
            String content) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(content);

        alert.showAndWait();
    }
}