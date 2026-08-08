/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.ProfileDAO;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML
    private TextField Building;

    @FXML
    private TextField City;

    @FXML
    private TextField Fname;

    @FXML
    private TextField ID;

    @FXML
    private TextField Lname;

    @FXML
    private TextField Mname;

    @FXML
    private TextField Phone;

    @FXML
    private TextField Street;

    @FXML
    private TextField Tname;

    @FXML
    private Button cancelbtnpro;

    @FXML
    private Button savechangesbtn;


    private final ProfileDAO profileDAO = new ProfileDAO();


    // =========================
    // Initialize
    // =========================
    @FXML
    public void initialize() {

        // ID لا يمكن تعديله
        ID.setDisable(true);

        // تحميل بيانات المستخدم الحالي
        loadProfile();
    }


    // =========================
    // Load Profile
    // =========================
    private void loadProfile() {

        String userId = UserSession.getUserId();
        String userType = UserSession.getUserType();

        if (userId == null || userType == null) {

            showError(
                    "Profile Error",
                    "No logged-in user was found."
            );

            return;
        }

        try {

            ResultSet rs;

            if (UserSession.isOwner()) {

                rs = profileDAO.getOwnerProfile(userId);

            } else {

                rs = profileDAO.getEmployeeProfile(userId);
            }


            if (rs.next()) {

                if (UserSession.isOwner()) {

                    ID.setText(
                            rs.getString("o_id")
                    );

                } else {

                    ID.setText(
                            rs.getString("emp_id")
                    );
                }


                Fname.setText(
                        getSafeString(
                                rs.getString("first_name")
                        )
                );

                Mname.setText(
                        getSafeString(
                                rs.getString("middle_name")
                        )
                );

                Tname.setText(
                        getSafeString(
                                rs.getString("third_name")
                        )
                );

                Lname.setText(
                        getSafeString(
                                rs.getString("last_name")
                        )
                );

                Phone.setText(
                        getSafeString(
                                rs.getString("phone")
                        )
                );

                City.setText(
                        getSafeString(
                                rs.getString("city")
                        )
                );

                Street.setText(
                        getSafeString(
                                rs.getString("street")
                        )
                );

                Building.setText(
                        getSafeString(
                                rs.getString("building")
                        )
                );

            } else {

                showError(
                        "Profile Error",
                        "Could not find your profile information."
                );
            }

            rs.close();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Database Error",
                    "Could not load profile information."
            );
        }
    }


    // =========================
    // Save Changes
    // =========================
    @FXML
    void handlesave(ActionEvent event) {

        String userId = UserSession.getUserId();

        if (userId == null) {

            showError(
                    "Profile Error",
                    "No logged-in user was found."
            );

            return;
        }


        // قراءة البيانات من TextFields
        String firstName = Fname.getText().trim();
        String middleName = Mname.getText().trim();
        String thirdName = Tname.getText().trim();
        String lastName = Lname.getText().trim();
        String phone = Phone.getText().trim();
        String city = City.getText().trim();
        String street = Street.getText().trim();
        String building = Building.getText().trim();


        // التحقق من الحقول المطلوبة
        if (firstName.isEmpty()) {

            showWarning(
                    "Validation",
                    "First name cannot be empty."
            );

            Fname.requestFocus();

            return;
        }


        if (lastName.isEmpty()) {

            showWarning(
                    "Validation",
                    "Last name cannot be empty."
            );

            Lname.requestFocus();

            return;
        }


        boolean updated;


        // =========================
        // Owner
        // =========================
        if (UserSession.isOwner()) {

            updated = profileDAO.updateOwnerProfile(
                    userId,
                    firstName,
                    middleName,
                    thirdName,
                    lastName,
                    phone,
                    city,
                    street,
                    building
            );

        }

        // =========================
        // Employee
        // =========================
        else {

            updated = profileDAO.updateEmployeeProfile(
                    userId,
                    firstName,
                    middleName,
                    thirdName,
                    lastName,
                    phone,
                    city,
                    street,
                    building
            );
        }


        // =========================
        // Result
        // =========================
        if (updated) {

            showInformation(
                    "Success",
                    "Your profile has been updated successfully."
            );

        } else {

            showError(
                    "Update Failed",
                    "No changes were saved."
            );
        }
    }


    // =========================
    // Cancel
    // =========================
    @FXML
    void handlecancelpro(ActionEvent event) {

        // إلغاء التغييرات وإعادة البيانات الأصلية
        loadProfile();
    }


    // =========================
    // Safe String
    // =========================
    private String getSafeString(String value) {

        return value == null ? "" : value;
    }


    // =========================
    // Warning
    // =========================
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


    // =========================
    // Error
    // =========================
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


    // =========================
    // Information
    // =========================
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

