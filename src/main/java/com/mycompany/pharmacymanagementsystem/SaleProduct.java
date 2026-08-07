/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

public class SaleProduct {

    private String productCode;
    private String tradeName;
    private String unit;
    private double price;
    private int stock;
private String batchNo;
    public SaleProduct(String productCode,
                   String tradeName,
                   String unit,
                   double price,
                   String batchNo,
                   int stock) {

    this.productCode = productCode;
    this.tradeName = tradeName;
    this.unit = unit;
    this.price = price;
    this.batchNo = batchNo;
    this.stock = stock;

}

    public String getProductCode() {
        return productCode;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
    public String getBatchNo() {
    return batchNo;
}
}
