/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.EmployeeDAO;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmployeeController {

    @FXML
    private TableColumn<Employee, String> Idempcol;

    @FXML
    private Button addempbtn;

    @FXML
    private TableColumn<Employee, String> buildingcol;

    @FXML
    private TableColumn<Employee, String> cityempcol;

    @FXML
    private Button clearempsearchbtn;

    @FXML
    private Button cleartblempbtn;

    @FXML
    private Button deleteempbtn;

    @FXML
    private Button editempbtn;

    @FXML
    private TableColumn<Employee, String> fnameempcol;

    @FXML
    private TableColumn<Employee, String> hiredatecol;

    @FXML
    private TableColumn<Employee, String> jobempcol;

    @FXML
    private TableColumn<Employee, String> lnameempcol;

    @FXML
    private TableColumn<Employee, String> phoneempcol;

    @FXML
    private TableColumn<Employee, Double> salarycol;

    @FXML
    private Button searchempbtn;

    @FXML
    private TableColumn<Employee, String> streetcol;

    @FXML
    private TableView<Employee> tblemp;

    @FXML
    private TextField txtsearchemp;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private final ObservableList<Employee> employeeList =
            FXCollections.observableArrayList();

    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        setupTableColumns();

        loadEmployees();
    }

    // =========================================================
    // TABLE COLUMNS
    // =========================================================

    private void setupTableColumns() {

        Idempcol.setCellValueFactory(
                new PropertyValueFactory<>("empId")
        );

        fnameempcol.setCellValueFactory(
                new PropertyValueFactory<>("firstName")
        );

        lnameempcol.setCellValueFactory(
                new PropertyValueFactory<>("lastName")
        );

        phoneempcol.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        cityempcol.setCellValueFactory(
                new PropertyValueFactory<>("city")
        );

        streetcol.setCellValueFactory(
                new PropertyValueFactory<>("street")
        );

        buildingcol.setCellValueFactory(
                new PropertyValueFactory<>("building")
        );

        hiredatecol.setCellValueFactory(
                new PropertyValueFactory<>("hireDate")
        );

        jobempcol.setCellValueFactory(
                new PropertyValueFactory<>("jobTitle")
        );

        salarycol.setCellValueFactory(
                new PropertyValueFactory<>("salary")
        );
    }

    // =========================================================
    // LOAD EMPLOYEES
    // =========================================================

    private void loadEmployees() {

        employeeList.setAll(
                employeeDAO.getAllEmployees()
        );

        tblemp.setItems(employeeList);
    }

    // =========================================================
    // ADD EMPLOYEE
    // =========================================================

    @FXML
    private void handleAddEmployee(ActionEvent event) {

        openEmployeeForm(null);
    }

    // =========================================================
    // EDIT EMPLOYEE
    // =========================================================

    @FXML
    private void handleEditEmployee(ActionEvent event) {

        Employee selectedEmployee =
                tblemp.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {

            showWarning(
                    "No Selection",
                    "Please select an employee from the table first."
            );

            return;
        }

        openEmployeeForm(selectedEmployee);
    }

    // =========================================================
    // OPEN ADD / EDIT FORM
    // =========================================================

    private void openEmployeeForm(Employee employee) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/EmployeeEdit.fxml"
                    )
            );

            Parent root = loader.load();

            EmpolyeeEditController controller =
                    loader.getController();

            // إذا كان Edit
            if (employee != null) {

                controller.setEmployeeData(employee);
            }

            Stage stage = new Stage();

            stage.setTitle(
                    employee == null
                            ? "Add Employee"
                            : "Edit Employee"
            );

            stage.setScene(new Scene(root));

            // تحديث الجدول بعد إغلاق نافذة الإضافة/التعديل
            stage.setOnHidden(e -> loadEmployees());

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

            showError(
                    "Error",
                    "Unable to open employee form."
            );
        }
    }

    // =========================================================
    // DELETE EMPLOYEE
    // =========================================================

    @FXML
    private void handleDeleteEmployee(ActionEvent event) {

        Employee selectedEmployee =
                tblemp.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {

            showWarning(
                    "No Selection",
                    "Please select an employee to delete."
            );

            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Delete Employee");
        confirmation.setHeaderText(null);

        confirmation.setContentText(
                "Are you sure you want to delete employee "
                + selectedEmployee.getFirstName()
                + " "
                + selectedEmployee.getLastName()
                + "?"
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                boolean success =
                        employeeDAO.deleteEmployee(
                                selectedEmployee.getEmpId()
                        );

                if (success) {

                    loadEmployees();

                    showInformation(
                            "Success",
                            "Employee deleted successfully."
                    );

                } else {

                    showError(
                            "Database Error",
                            "Failed to delete employee."
                    );
                }
            }
        });
    }

    // =========================================================
    // SEARCH EMPLOYEE
    // =========================================================

    @FXML
    private void handleSearchEmployee(ActionEvent event) {

        String search =
                txtsearchemp.getText().trim();

        if (search.isEmpty()) {

            loadEmployees();

            return;
        }

        ObservableList<Employee> results =
                employeeDAO.searchEmployees(search);

        tblemp.setItems(results);
    }

    // =========================================================
    // CLEAR SEARCH
    // =========================================================

    @FXML
    private void handleClearEmployeeSearch(ActionEvent event) {

        txtsearchemp.clear();

        loadEmployees();
    }

    // =========================================================
    // CLEAR TABLE
    // =========================================================

    @FXML
    private void handleClearEmployeeTable(ActionEvent event) {

        tblemp.getItems().clear();
    }

    // =========================================================
    // WARNING
    // =========================================================

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

    // =========================================================
    // ERROR
    // =========================================================

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

    // =========================================================
    // INFORMATION
    // =========================================================

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
}