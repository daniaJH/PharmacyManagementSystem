/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

public class NewPurchaseItem {

    private final String productCode;
    private final String productName;
    private final String batchNumber;
    private final int quantity;
    private final double purchasePrice;
    private final LocalDate expiryDate;

    public NewPurchaseItem(
            String productCode,
            String productName,
            String batchNumber,
            int quantity,
            double purchasePrice,
            LocalDate expiryDate) {

        this.productCode = productCode;
        this.productName = productName;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.expiryDate = expiryDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public double getTotal() {
        return quantity * purchasePrice;
    }
}

