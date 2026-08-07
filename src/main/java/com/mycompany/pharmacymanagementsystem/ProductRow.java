/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;


import javafx.beans.property.*;

public class ProductRow {

    private final StringProperty code;
    private final StringProperty tradeName;
    private final StringProperty unit;
    private final IntegerProperty stock;
    private final DoubleProperty price;

    public ProductRow(String code,String tradeName,String unit,int stock,double price){

        this.code=new SimpleStringProperty(code);
        this.tradeName=new SimpleStringProperty(tradeName);
        this.unit=new SimpleStringProperty(unit);
        this.stock=new SimpleIntegerProperty(stock);
        this.price=new SimpleDoubleProperty(price);

    }

    public String getCode(){ return code.get();}
    public String getTradeName(){ return tradeName.get();}
    public String getUnit(){ return unit.get();}
    public int getStock(){ return stock.get();}
    public double getPrice(){ return price.get();}

}
