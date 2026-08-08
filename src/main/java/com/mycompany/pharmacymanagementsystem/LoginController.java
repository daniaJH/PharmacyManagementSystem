/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.pharmacymanagementsystem.dao.UserAccountDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField idfield;

    @FXML
    private RadioButton ownerRadio;

    @FXML
    private RadioButton employeeRadio;

    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

   @FXML
public void handleLogin(ActionEvent event) {

    String name = nameField.getText().trim();
    String id = idfield.getText().trim();

    if (name.isEmpty() || id.isEmpty()) {
        showError("Please enter your name and ID.");
        return;
    }

    boolean loginSuccess = false;
    String userType = "";

    if (ownerRadio.isSelected()) {

        
if (userAccountDAO.checkOwner(name, id)) {

    loginSuccess = true;
    userType = "Owner";

    UserSession.setUser(id, userType, name);

} else {

    showError("Invalid Owner name or ID.");
    return;
}



    } else if (employeeRadio.isSelected()) {

       
if (userAccountDAO.checkEmployee(name, id)) {

    loginSuccess = true;
    userType = "Employee";

    UserSession.setUser(id, userType, name);

} else {

    showError("Invalid Employee name or ID.");
    return;
}



    } else {

        showError("Please select Owner or Employee.");
        return;
    }

if (loginSuccess) {

    UserSession.setUser(
        id,
        userType,
        name
    );

    openDashboard(event);
}
}
private void openDashboard(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/dashboard.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}