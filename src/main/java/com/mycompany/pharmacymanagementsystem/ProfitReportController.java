/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfitReportController {

    @FXML
    private TableColumn<ProfitReportRow, Double> Cost1;

    @FXML
    private TableColumn<ProfitReportRow, LocalDate> Date1;

    @FXML
    private Button GenerateReport1;

    @FXML
    private TableColumn<ProfitReportRow, String> Invoice1;

    @FXML
    private Label MARGIN1;

    @FXML
    private TableColumn<ProfitReportRow, Double> Profit;

    @FXML
    private TableColumn<ProfitReportRow, Double> Sales1;

    @FXML
    private DatePicker enddatei;

    @FXML
    private DatePicker startdate1;

    @FXML
    private TableView<ProfitReportRow> tblofprofits;

    @FXML
    private Label totalprofits;

    @FXML
    private Label totalpurchase;

    @FXML
    private Label totalsales1;


    private final ProfitReportDAO profitDAO =
            new ProfitReportDAO();


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        Invoice1.setCellValueFactory(
                new PropertyValueFactory<>("invoiceNo")
        );

        Date1.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        Sales1.setCellValueFactory(
                new PropertyValueFactory<>("sales")
        );

        Cost1.setCellValueFactory(
                new PropertyValueFactory<>("cost")
        );

        Profit.setCellValueFactory(
                new PropertyValueFactory<>("profit")
        );

        totalsales1.setText("0.00");
        totalpurchase.setText("0.00");
        totalprofits.setText("0.00");
        MARGIN1.setText("0.00%");
    }


    // =========================================================
    // GENERATE REPORT
    // =========================================================

    @FXML
    private void GenerateReport1(ActionEvent event) {

        if (startdate1.getValue() == null
                || enddatei.getValue() == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Date",
                    "Please select start and end date."
            );

            return;
        }


        LocalDate startDate =
                startdate1.getValue();

        LocalDate endDate =
                enddatei.getValue();


        if (startDate.isAfter(endDate)) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Date",
                    "Start date cannot be after end date."
            );

            return;
        }


        ObservableList<ProfitReportRow> list =
                profitDAO.getProfitReport(
                        startDate,
                        endDate
                );


        tblofprofits.setItems(list);


        calculateSummary(list);
    }


    // =========================================================
    // SUMMARY
    // =========================================================

    private void calculateSummary(
            ObservableList<ProfitReportRow> list) {

        double totalSales = 0;
        double totalCost = 0;
        double totalProfit = 0;


        for (ProfitReportRow row : list) {

            totalSales += row.getSales();

            totalCost += row.getCost();

            totalProfit += row.getProfit();
        }


        double margin = 0;

        if (totalSales > 0) {

            margin =
                    (totalProfit / totalSales) * 100;
        }


        totalsales1.setText(
                String.format(
                        "%.2f",
                        totalSales
                )
        );


        totalpurchase.setText(
                String.format(
                        "%.2f",
                        totalCost
                )
        );


        totalprofits.setText(
                String.format(
                        "%.2f",
                        totalProfit
                )
        );


        MARGIN1.setText(
                String.format(
                        "%.2f%%",
                        margin
                )
        );
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


