/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.EmployeeDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmpolyeeEditController {

    @FXML
    private TextField buildingemptxt;

    @FXML
    private Button cancelbtnemp;

    @FXML
    private TextField cityemptxt;

    @FXML
    private TextField fnameemptxt;

    @FXML
    private TextField hiredateemptx;

    @FXML
    private TextField idemptxt;

    @FXML
    private TextField jobemptxt;

    @FXML
    private TextField lnameemptxt;

    @FXML
    private TextField mnameemptxt;

    @FXML
    private TextField salaryemptxt;

    @FXML
    private Button savebtnemp;

    @FXML
    private ComboBox<String> shiftemptxt;

    @FXML
    private TextField streetemptxt;

    @FXML
    private TextField tnameemptxt;

    @FXML
    private TextField phoneemptxt;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private boolean editMode = false;

    @FXML
    public void initialize() {

        // تحميل أسماء الشفتات
        shiftemptxt.setItems(employeeDAO.getShiftNames());

        // الوضع الافتراضي = إضافة
        editMode = false;

        // ID مسموح كتابته عند الإضافة
        idemptxt.setDisable(false);
    }

    // =========================================================
    // SET EMPLOYEE DATA
    // =========================================================

   public void setEmployeeData(Employee employee) {

    editMode = true;

    // ID لا يمكن تغييره
    idemptxt.setDisable(true);

    // =========================
    // Personal InformationC
    // =========================

    fnameemptxt.setText(employee.getFirstName());
    mnameemptxt.setText(employee.getMiddleName());
    tnameemptxt.setText(employee.getThirdName());
    lnameemptxt.setText(employee.getLastName());
    phoneemptxt.setText(employee.getPhone());
    cityemptxt.setText(employee.getCity());
    streetemptxt.setText(employee.getStreet());
    buildingemptxt.setText(employee.getBuilding());

    // جعل المعلومات الشخصية غير قابلة للتعديل
    fnameemptxt.setDisable(true);
    mnameemptxt.setDisable(true);
    tnameemptxt.setDisable(true);
    lnameemptxt.setDisable(true);
    phoneemptxt.setDisable(true);
    cityemptxt.setDisable(true);
    streetemptxt.setDisable(true);
    buildingemptxt.setDisable(true);

    // =========================
    // Job Information
    // =========================

    if (employee.getHireDate() != null) {
        hiredateemptx.setText(employee.getHireDate());
    }

    jobemptxt.setText(employee.getJobTitle());

    salaryemptxt.setText(
            String.valueOf(employee.getSalary())
    );

    shiftemptxt.setValue(employee.getShiftName());
}

    // =========================================================
    // SAVE
    // =========================================================

    @FXML
    private void handlesaveemp() {

        if (!validateFields()) {
            return;
        }

        double salary;

        try {

            salary = Double.parseDouble(
                    salaryemptxt.getText().trim()
            );

        } catch (NumberFormatException e) {

            showError(
                    "Invalid Salary",
                    "Please enter a valid salary."
            );

            return;
        }

        // إنشاء Employee object
        Employee employee = new Employee();

        employee.setEmpId(idemptxt.getText().trim());
        employee.setFirstName(fnameemptxt.getText().trim());
        employee.setMiddleName(mnameemptxt.getText().trim());
        employee.setThirdName(tnameemptxt.getText().trim());
        employee.setLastName(lnameemptxt.getText().trim());

        employee.setPhone(phoneemptxt.getText().trim());

        employee.setCity(cityemptxt.getText().trim());
        employee.setStreet(streetemptxt.getText().trim());
        employee.setBuilding(buildingemptxt.getText().trim());

        employee.setHireDate(
                hiredateemptx.getText().trim()
        );

        employee.setJobTitle(
                jobemptxt.getText().trim()
        );

        employee.setSalary(salary);

        employee.setShiftName(
                shiftemptxt.getValue()
        );

        boolean success;

        // =====================================================
        // UPDATE
        // =====================================================

        if (editMode) {

            success = employeeDAO.updateEmployee(employee);

        // =====================================================
        // ADD
        // =====================================================

        } else {

            success = employeeDAO.addEmployee(employee);
        }

        // =====================================================
        // RESULT
        // =====================================================

        if (success) {

            showInformation(
                    "Success",
                    editMode
                            ? "Employee updated successfully."
                            : "Employee added successfully."
            );

            closeWindow();

        } else {

            showError(
                    "Database Error",
                    editMode
                            ? "Failed to update employee."
                            : "Failed to add employee."
            );
        }
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private boolean validateFields() {

        if (idemptxt.getText().trim().isEmpty()) {

            showError(
                    "Validation",
                    "Employee ID is required."
            );

            return false;
        }

        if (fnameemptxt.getText().trim().isEmpty()) {

            showError(
                    "Validation",
                    "First name is required."
            );

            return false;
        }

        if (lnameemptxt.getText().trim().isEmpty()) {

            showError(
                    "Validation",
                    "Last name is required."
            );

            return false;
        }

        if (salaryemptxt.getText().trim().isEmpty()) {

            showError(
                    "Validation",
                    "Salary is required."
            );

            return false;
        }

        try {

            double salary = Double.parseDouble(
                    salaryemptxt.getText().trim()
            );

            if (salary <= 0) {

                showError(
                        "Validation",
                        "Salary must be greater than 0."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            showError(
                    "Validation",
                    "Please enter a valid salary."
            );

            return false;
        }

        if (shiftemptxt.getValue() == null) {

            showError(
                    "Validation",
                    "Please select a shift."
            );

            return false;
        }

        return true;
    }

    // =========================================================
    // CANCEL
    // =========================================================

    @FXML
    private void handlecancelemp() {
        closeWindow();
    }

    // =========================================================
    // CLOSE WINDOW
    // =========================================================

    private void closeWindow() {

        Stage stage = (Stage) cancelbtnemp
                .getScene()
                .getWindow();

        stage.close();
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String title,
            String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

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

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}