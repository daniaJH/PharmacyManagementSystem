/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;



import javafx.beans.property.*;

public class InvoiceRow {

    private final StringProperty code;
    private final StringProperty tradeName;
    private final StringProperty unit;
    private final IntegerProperty qty;
    private final DoubleProperty price;
    private final DoubleProperty total;

    public InvoiceRow(String code,
                      String tradeName,
                      String unit,
                      int qty,
                      double price){

        this.code=new SimpleStringProperty(code);
        this.tradeName=new SimpleStringProperty(tradeName);
        this.unit=new SimpleStringProperty(unit);
        this.qty=new SimpleIntegerProperty(qty);
        this.price=new SimpleDoubleProperty(price);
        this.total=new SimpleDoubleProperty(price*qty);

    }

    public String getCode(){return code.get();}
    public String getTradeName(){return tradeName.get();}
    public String getUnit(){return unit.get();}
    public int getQty(){return qty.get();}
    public double getPrice(){return price.get();}
    public double getTotal(){return total.get();}

    public void setQty(int q){

        qty.set(q);
        total.set(q*price.get());

    }

}