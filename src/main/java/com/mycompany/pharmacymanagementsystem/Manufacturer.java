/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

public class Manufacturer {

    private String manufCode;
    private String name;
    private String phone;
    private String country;
    private String city;
    private String industrialZone;

    public Manufacturer() {
    }
public Manufacturer(String manufCode, String name) {
    this.manufCode = manufCode;
    this.name = name;
}
    public Manufacturer(String manufCode, String name, String phone,
                         String country, String city, String industrialZone) {
        this.manufCode = manufCode;
        this.name = name;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.industrialZone = industrialZone;
    }

    public String getManufCode() {
        return manufCode;
    }

    public void setManufCode(String manufCode) {
        this.manufCode = manufCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIndustrialZone() {
        return industrialZone;
    }

    public void setIndustrialZone(String industrialZone) {
        this.industrialZone = industrialZone;
    }

    @Override
    public String toString() {
        return name;
    }
}