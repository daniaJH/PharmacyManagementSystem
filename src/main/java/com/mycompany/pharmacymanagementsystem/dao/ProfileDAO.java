/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {

    // =========================
    // Get Owner Profile
    // =========================
    public ResultSet getOwnerProfile(String id) throws SQLException {

        String sql = """
            SELECT
                o_id,
                first_name,
                middle_name,
                third_name,
                last_name,
                phone,
                city,
                street,
                building
            FROM public.owner
            WHERE o_id = ?
            """;

        Connection conn = DatabaseConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, id);

        return stmt.executeQuery();
    }


    // =========================
    // Get Employee Profile
    // =========================
    public ResultSet getEmployeeProfile(String id) throws SQLException {

        String sql = """
            SELECT
                emp_id,
                first_name,
                middle_name,
                third_name,
                last_name,
                phone,
                city,
                street,
                building
            FROM public.employee
            WHERE emp_id = ?
            """;

        Connection conn = DatabaseConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, id);

        return stmt.executeQuery();
    }


    // =========================
    // Update Owner Profile
    // =========================
    public boolean updateOwnerProfile(
            String id,
            String firstName,
            String middleName,
            String thirdName,
            String lastName,
            String phone,
            String city,
            String street,
            String building) {

        String sql = """
            UPDATE public.owner
            SET
                first_name = ?,
                middle_name = ?,
                third_name = ?,
                last_name = ?,
                phone = ?,
                city = ?,
                street = ?,
                building = ?
            WHERE o_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, middleName);
            stmt.setString(3, thirdName);
            stmt.setString(4, lastName);
            stmt.setString(5, phone);
            stmt.setString(6, city);
            stmt.setString(7, street);
            stmt.setString(8, building);
            stmt.setString(9, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================
    // Update Employee Profile
    // =========================
    public boolean updateEmployeeProfile(
            String id,
            String firstName,
            String middleName,
            String thirdName,
            String lastName,
            String phone,
            String city,
            String street,
            String building) {

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
                building = ?
            WHERE emp_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, middleName);
            stmt.setString(3, thirdName);
            stmt.setString(4, lastName);
            stmt.setString(5, phone);
            stmt.setString(6, city);
            stmt.setString(7, street);
            stmt.setString(8, building);
            stmt.setString(9, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }
}


