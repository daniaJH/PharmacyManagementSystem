/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

public class SalesInvoice {

    private String invoiceNo;
    private String date;
    private String customer;
    private String employee;
    private double total;

    public SalesInvoice(
            String invoiceNo,
            String date,
            String customer,
            String employee,
            double total) {

        this.invoiceNo = invoiceNo;
        this.date = date;
        this.customer = customer;
        this.employee = employee;
        this.total = total;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public String getDate() {
        return date;
    }

    public String getCustomer() {
        return customer;
    }

    public String getEmployee() {
        return employee;
    }

    public double getTotal() {
        return total;
    }
}