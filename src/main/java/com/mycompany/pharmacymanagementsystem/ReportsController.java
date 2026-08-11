/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ReportsController {

    @FXML
    private Button instreport;

    @FXML
    private Button profitreport;

    @FXML
    private Button purchasesreport;

    @FXML
    private Button salesreport;


    @FXML
    private Button btnopenjasfrepinven;

    @FXML
    private Button btnopenjasrepsales;
    // =========================================================
    // INVENTORY / STOCK REPORT
    // =========================================================

    @FXML
    void openreportinst(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/InStReport.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Inventory / Stock Report");
            stage.setScene(new Scene(root));

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open Inventory / Stock Report."
            );
        }
    }


    // =========================================================
    // PROFIT REPORT
    // =========================================================

    @FXML
    void openreportprofit(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/ProfitReport.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Profit Report");
            stage.setScene(new Scene(root));

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open Profit Report."
            );
        }
    }


    // =========================================================
    // PURCHASE REPORT
    // =========================================================

    @FXML
    void openreportpurchase(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/PurchasesReport.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Purchase Report");
            stage.setScene(new Scene(root));

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open Purchase Report."
            );
        }
    }


    // =========================================================
    // SALES REPORT
    // =========================================================

    @FXML
    void openreportsale(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/SalesReport.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Sales Report");
            stage.setScene(new Scene(root));

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open Sales Report."
            );
        }
    }

@FXML
void handleopenjasfrepinven(ActionEvent event) {

    try {
        JasperReport report = JasperCompileManager.compileReport(
                getClass().getResourceAsStream(
                        "/com/mycompany/pharmacymanagementsystem/reports/InventoryReport.jrxml"
                )
        );

        JasperPrint print = JasperFillManager.fillReport(
                report,
                new HashMap<>(),
                DatabaseConnection.getConnection()
        );

        JasperViewer.viewReport(print, false);

    } catch (Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Unable to open Inventory Report.");
        alert.showAndWait();
    }
}

@FXML
void handleopenjasrepsales(ActionEvent event) {

    try {
        JasperReport report = JasperCompileManager.compileReport(
                getClass().getResourceAsStream(
                        "/com/mycompany/pharmacymanagementsystem/reports/SalesReport.jrxml"
                )
        );

        JasperPrint print = JasperFillManager.fillReport(
                report,
                new HashMap<>(),
                DatabaseConnection.getConnection()
        );

        JasperViewer.viewReport(print, false);

    } catch (Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Unable to open Sales Report.");
        alert.showAndWait();
    }
}
    // =========================================================
    // ERROR
    // =========================================================

    private void showError(String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
