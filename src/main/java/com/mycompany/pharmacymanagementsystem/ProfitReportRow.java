/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

public class ProfitReportRow {

    private final String invoiceNo;
    private final LocalDate date;
    private final double sales;
    private final double cost;
    private final double profit;

    public ProfitReportRow(
            String invoiceNo,
            LocalDate date,
            double sales,
            double cost,
            double profit) {

        this.invoiceNo = invoiceNo;
        this.date = date;
        this.sales = sales;
        this.cost = cost;
        this.profit = profit;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getSales() {
        return sales;
    }

    public double getCost() {
        return cost;
    }

    public double getProfit() {
        return profit;
    }
}

