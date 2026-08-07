package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.ManufacturerDAO;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class AddManufController implements Initializable {

    @FXML
    private Button btnsavecompany;

    @FXML
    private Button cancelsave;

    @FXML
    private ComboBox<String> combcompanycountry;

    @FXML
    private TextField txtcompanecode;

    @FXML
    private TextField txtcompanycity;

    @FXML
    private TextField txtcompanyinz;

    @FXML
    private TextField txtcompanyname;

    @FXML
    private TextField txtcompanyphone;


    private final ManufacturerDAO manufacturerDAO =
            new ManufacturerDAO();


    /*
     * True  = Edit mode
     * False = Add mode
     */
    private boolean editMode = false;


    /*
     * Manufacturer currently being edited.
     */
    private Manufacturer selectedManufacturer;


    /*
     * =========================================================
     * INITIALIZE
     * =========================================================
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupCountryComboBox();

        // Default mode = Add
        editMode = false;

        // Company code can be entered when adding
        txtcompanecode.setDisable(false);
    }


    /*
     * =========================================================
     * COUNTRY COMBO BOX
     * =========================================================
     */

    private void setupCountryComboBox() {

        combcompanycountry.getItems().addAll(
                "Palestine",
                "Jordan",
                "Saudi Arabia",
                "Egypt",
                "United Arab Emirates",
                "Turkey",
                "Germany",
                "United Kingdom",
                "United States"
        );
    }


    /*
     * =========================================================
     * SET DATA FOR EDIT MODE
     * =========================================================
     */

    public void setManufacturerData(Manufacturer manufacturer) {

        if (manufacturer == null) {
            return;
        }

        editMode = true;

        selectedManufacturer = manufacturer;


        /*
         * Fill existing data.
         */

        txtcompanecode.setText(
                manufacturer.getManufCode()
        );

        txtcompanyname.setText(
                manufacturer.getName()
        );

        txtcompanyphone.setText(
                manufacturer.getPhone()
        );

        txtcompanycity.setText(
                manufacturer.getCity()
        );

        txtcompanyinz.setText(
                manufacturer.getIndustrialZone()
        );


        /*
         * Set country.
         */

        if (manufacturer.getCountry() != null
                && !manufacturer.getCountry().isBlank()) {

            combcompanycountry.setValue(
                    manufacturer.getCountry()
            );
        }


        /*
         * Manufacturer code cannot be changed
         * during UPDATE.
         */

        txtcompanocodeDisable();
    }


    /*
     * Disable manufacturer code during edit.
     */
    private void txtcompanocodeDisable() {

        txtcompanecode.setDisable(true);
    }


    /*
     * =========================================================
     * SAVE BUTTON
     * =========================================================
     */

    @FXML
    void handleSaveButton(ActionEvent event) {

        /*
         * 1. Validate fields.
         */

        if (!validateFields()) {
            return;
        }


        /*
         * 2. Get values from form.
         */

        String code =
                txtcompanecode.getText().trim();

        String name =
                txtcompanyname.getText().trim();

        String phone =
                txtcompanyphone.getText().trim();

        String country =
                combcompanycountry.getValue();

        if (country == null) {
            country = "";
        }

        String city =
                txtcompanycity.getText().trim();

        String industrialZone =
                txtcompanyinz.getText().trim();


        /*
         * 3. Create Manufacturer object.
         */

        Manufacturer manufacturer =
                new Manufacturer(
                        code,
                        name,
                        phone,
                        country,
                        city,
                        industrialZone
                );


        boolean success;


        /*
         * =====================================================
         * ADD MODE
         * =====================================================
         */

        if (!editMode) {


            /*
             * Check duplicate company code
             * before inserting.
             */

            if (manufacturerDAO.manufacturerExists(code)) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Duplicate Code",
                        "This company code already exists.\n"
                        + "Please enter a different code."
                );

                txtcompanecode.requestFocus();

                return;
            }


            /*
             * Add new manufacturer.
             */

            success =
                    manufacturerDAO.addManufacturer(
                            manufacturer
                    );


            if (success) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Manufacturing company added successfully."
                );

                closeWindow();

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Add Failed",
                        "Could not add the manufacturing company."
                );
            }
        }


        /*
         * =====================================================
         * UPDATE MODE
         * =====================================================
         */

        else {


            /*
             * Update existing manufacturer.
             */

            success =
                    manufacturerDAO.updateManufacturer(
                            manufacturer
                    );


            if (success) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Manufacturing company updated successfully."
                );

                closeWindow();

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Update Failed",
                        "Could not update the manufacturing company."
                );
            }
        }
    }


    /*
     * =========================================================
     * VALIDATE FIELDS
     * =========================================================
     */

    private boolean validateFields() {

        String code =
                txtcompanecode.getText().trim();

        String name =
                txtcompanyname.getText().trim();


        /*
         * Code
         */

        if (code.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Code",
                    "Please enter the company code."
            );

            txtcompanecode.requestFocus();

            return false;
        }


        /*
         * Company Name
         */

        if (name.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Company Name",
                    "Please enter the company name."
            );

            txtcompanyname.requestFocus();

            return false;
        }


        /*
         * Country is optional.
         */

        return true;
    }


    /*
     * =========================================================
     * CANCEL BUTTON
     * =========================================================
     */

    @FXML
    void handleCancelButton(ActionEvent event) {

        closeWindow();
    }


    /*
     * =========================================================
     * CLOSE WINDOW
     * =========================================================
     */

    private void closeWindow() {

        Stage stage =
                (Stage) cancelsave.getScene().getWindow();

        stage.close();
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