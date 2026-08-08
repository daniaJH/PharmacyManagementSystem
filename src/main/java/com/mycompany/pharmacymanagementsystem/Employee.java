/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

public class Employee {

    private String empId;
    private String firstName;
    private String middleName;
    private String thirdName;
    private String lastName;
    private String phone;
    private String city;
    private String street;
    private String building;
    private String hireDate;
    private String jobTitle;
    private double salary;
    private String shiftName;

    public Employee() {
    }

    public Employee(String empId, String firstName, String middleName,
                     String thirdName, String lastName, String phone,
                     String city, String street, String building,
                     String hireDate, String jobTitle, double salary,
                     String shiftName) {

        this.empId = empId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.thirdName = thirdName;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
        this.street = street;
        this.building = building;
        this.hireDate = hireDate;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.shiftName = shiftName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }
    @Override
public String toString() {
    return empId + " - " + firstName + " " + lastName;
}
}