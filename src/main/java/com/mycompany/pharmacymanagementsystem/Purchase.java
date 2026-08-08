/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

public class Purchase {

    private final String purchaseNumber;
    private final LocalDate date;
    private final double total;

    public Purchase(
            String purchaseNumber,
            LocalDate date,
            double total) {

        this.purchaseNumber = purchaseNumber;
        this.date = date;
        this.total = total;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotal() {
        return total;
    }
}