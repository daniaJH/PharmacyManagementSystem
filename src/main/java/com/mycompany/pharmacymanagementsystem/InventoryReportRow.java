

package com.mycompany.pharmacymanagementsystem;

import java.time.LocalDate;

public class InventoryReportRow {

    private final String productCode;
    private final String productName;
    private final String unit;
    private final int stock;
    private final double price;
    private final LocalDate expireDate;

    public InventoryReportRow(
            String productCode,
            String productName,
            String unit,
            int stock,
            double price,
            LocalDate expireDate) {

        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.stock = stock;
        this.price = price;
        this.expireDate = expireDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnit() {
        return unit;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }
}


