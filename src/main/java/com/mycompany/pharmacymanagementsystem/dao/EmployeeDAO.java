/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.Employee;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeDAO {

    // =========================================================
    // GET ALL EMPLOYEES
    // =========================================================

    public ObservableList<Employee> getAllEmployees() {

        ObservableList<Employee> employees = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                e.emp_id,
                e.first_name,
                e.middle_name,
                e.third_name,
                e.last_name,
                e.phone,
                e.city,
                e.street,
                e.building,
                e.hire_date,
                e.job_title,
                e.salary,
                s.shift_name
            FROM public.employee e
            LEFT JOIN public.shift s
                ON e.shift_id = s.shift_id
            ORDER BY e.emp_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Employee employee = new Employee();

                employee.setEmpId(rs.getString("emp_id"));
                employee.setFirstName(rs.getString("first_name"));
                employee.setMiddleName(rs.getString("middle_name"));
                employee.setThirdName(rs.getString("third_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setPhone(rs.getString("phone"));
                employee.setCity(rs.getString("city"));
                employee.setStreet(rs.getString("street"));
                employee.setBuilding(rs.getString("building"));

                Date hireDate = rs.getDate("hire_date");
                employee.setHireDate(
                        hireDate != null ? hireDate.toString() : ""
                );

                employee.setJobTitle(rs.getString("job_title"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setShiftName(rs.getString("shift_name"));

                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }


    // =========================================================
    // SEARCH
    // ID OR FIRST NAME OR FIRST + LAST NAME
    // =========================================================

    public ObservableList<Employee> searchEmployees(String search) {

        ObservableList<Employee> employees = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                e.emp_id,
                e.first_name,
                e.middle_name,
                e.third_name,
                e.last_name,
                e.phone,
                e.city,
                e.street,
                e.building,
                e.hire_date,
                e.job_title,
                e.salary,
                s.shift_name
            FROM public.employee e
            LEFT JOIN public.shift s
                ON e.shift_id = s.shift_id
            WHERE 
                LOWER(e.emp_id) LIKE LOWER(?)
                OR LOWER(e.first_name) LIKE LOWER(?)
                OR LOWER(e.first_name || ' ' || e.last_name) LIKE LOWER(?)
            ORDER BY e.emp_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String value = "%" + search.trim() + "%";

            stmt.setString(1, value);
            stmt.setString(2, value);
            stmt.setString(3, value);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Employee employee = new Employee();

                    employee.setEmpId(rs.getString("emp_id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setMiddleName(rs.getString("middle_name"));
                    employee.setThirdName(rs.getString("third_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setPhone(rs.getString("phone"));
                    employee.setCity(rs.getString("city"));
                    employee.setStreet(rs.getString("street"));
                    employee.setBuilding(rs.getString("building"));

                    Date hireDate = rs.getDate("hire_date");

                    employee.setHireDate(
                            hireDate != null ? hireDate.toString() : ""
                    );

                    employee.setJobTitle(rs.getString("job_title"));
                    employee.setSalary(rs.getDouble("salary"));
                    employee.setShiftName(rs.getString("shift_name"));

                    employees.add(employee);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }


    // =========================================================
    // ADD EMPLOYEE
    // =========================================================

    public boolean addEmployee(Employee employee) {

        String shiftId = getShiftId(employee.getShiftName());

        if (shiftId == null) {
            return false;
        }

        String sql = """
            INSERT INTO public.employee
            (
                emp_id,
                first_name,
                middle_name,
                third_name,
                last_name,
                phone,
                city,
                street,
                building,
                hire_date,
                job_title,
                salary,
                shift_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmpId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getMiddleName());
            stmt.setString(4, employee.getThirdName());
            stmt.setString(5, employee.getLastName());
            stmt.setString(6, employee.getPhone());
            stmt.setString(7, employee.getCity());
            stmt.setString(8, employee.getStreet());
            stmt.setString(9, employee.getBuilding());

            if (employee.getHireDate() == null
                    || employee.getHireDate().isBlank()) {

                stmt.setDate(10, null);

            } else {
                stmt.setDate(
                        10,
                        Date.valueOf(employee.getHireDate())
                );
            }

            stmt.setString(11, employee.getJobTitle());
            stmt.setDouble(12, employee.getSalary());
            stmt.setString(13, shiftId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // UPDATE EMPLOYEE
    // =========================================================

    public boolean updateEmployee(Employee employee) {

        String shiftId = getShiftId(employee.getShiftName());

        if (shiftId == null) {
            return false;
        }

        String sql = """
            UPDATE public.employee
            SET
                first_name = ?,
                middle_name = ?,
                third_name = ?,
                last_name = ?,
                phone = ?,
                city = ?,
                street = ?,
                building = ?,
                hire_date = ?,
                job_title = ?,
                salary = ?,
                shift_id = ?
            WHERE emp_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getMiddleName());
            stmt.setString(3, employee.getThirdName());
            stmt.setString(4, employee.getLastName());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getCity());
            stmt.setString(7, employee.getStreet());
            stmt.setString(8, employee.getBuilding());

            if (employee.getHireDate() == null
                    || employee.getHireDate().isBlank()) {

                stmt.setDate(9, null);

            } else {
                stmt.setDate(
                        9,
                        Date.valueOf(employee.getHireDate())
                );
            }

            stmt.setString(10, employee.getJobTitle());
            stmt.setDouble(11, employee.getSalary());
            stmt.setString(12, shiftId);
            stmt.setString(13, employee.getEmpId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // DELETE EMPLOYEE
    // =========================================================

    public boolean deleteEmployee(String empId) {

        String sql = """
            DELETE FROM public.employee
            WHERE emp_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // GET SHIFT ID FROM SHIFT NAME
    // =========================================================

    private String getShiftId(String shiftName) {

        String sql = """
            SELECT shift_id
            FROM public.shift
            WHERE shift_name = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, shiftName);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("shift_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // GET SHIFTS
    // =========================================================

    public ObservableList<String> getShiftNames() {

        ObservableList<String> shifts =
                FXCollections.observableArrayList();

        String sql = """
            SELECT shift_name
            FROM public.shift
            ORDER BY shift_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                shifts.add(rs.getString("shift_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shifts;
    }
}
