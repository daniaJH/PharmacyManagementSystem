/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

public class Customer {

    private String custId;
    private String firstName;
    private String middleName;
    private String thirdName;
    private String lastName;
    private String phone;
    private String medicalNotes;
    private String allergies;

    public Customer() {
    }

    public Customer(String custId,
                    String firstName,
                    String middleName,
                    String thirdName,
                    String lastName,
                    String phone,
                    String medicalNotes,
                    String allergies) {

        this.custId = custId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.thirdName = thirdName;
        this.lastName = lastName;
        this.phone = phone;
        this.medicalNotes = medicalNotes;
        this.allergies = allergies;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}

