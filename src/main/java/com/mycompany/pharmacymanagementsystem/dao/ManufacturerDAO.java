/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.Manufacturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ManufacturerDAO {

    /*
     * Get all manufacturers or filter by search text.
    
    /*
     * Add new manufacturer.
     */
    public boolean addManufacturer(Manufacturer manufacturer) {

        String sql =
                "INSERT INTO public.manufacturer " +
                "(manufcode, name, phone, country, city, industrialzone) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, manufacturer.getManufCode());
            stmt.setString(2, manufacturer.getName());
            stmt.setString(3, manufacturer.getPhone());
            stmt.setString(4, manufacturer.getCountry());
            stmt.setString(5, manufacturer.getCity());
            stmt.setString(6, manufacturer.getIndustrialZone());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
public boolean manufacturerExists(String manufCode) {

    String sql =
            "SELECT COUNT(*) " +
            "FROM public.manufacturer " +
            "WHERE manufcode = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, manufCode);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

    /*
     * Update manufacturer.
     */
    public boolean updateManufacturer(Manufacturer manufacturer) {

        String sql =
                "UPDATE public.manufacturer SET " +
                "name = ?, " +
                "phone = ?, " +
                "country = ?, " +
                "city = ?, " +
                "industrialzone = ? " +
                "WHERE manufcode = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, manufacturer.getName());
            stmt.setString(2, manufacturer.getPhone());
            stmt.setString(3, manufacturer.getCountry());
            stmt.setString(4, manufacturer.getCity());
            stmt.setString(5, manufacturer.getIndustrialZone());
            stmt.setString(6, manufacturer.getManufCode());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
     * Check whether manufacturer is connected to any product.
     *
     * product.manufcode -> manufacturer.manufcode
     *
     * If at least one product exists, deletion is not allowed.
     */
    public boolean hasProducts(String manufCode) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM public.product " +
                "WHERE manufcode = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, manufCode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    
   public ObservableList<Manufacturer> getManufacturers(String searchText) {

    ObservableList<Manufacturer> list =
            FXCollections.observableArrayList();

    String sql =
            "SELECT manufcode, name, phone, country, city, industrialzone " +
            "FROM public.manufacturer " +
            "WHERE 1=1 ";

    if (searchText != null && !searchText.trim().isEmpty()) {

        sql +=
                "AND (" +
                "manufcode ILIKE ? " +
                "OR name ILIKE ? " +
                "OR phone ILIKE ? " +
                "OR country ILIKE ? " +
                "OR city ILIKE ? " +
                "OR industrialzone ILIKE ?" +
                ") ";
    }

    sql += "ORDER BY manufcode";


    try (Connection conn =
                 DatabaseConnection.getConnection();

         PreparedStatement stmt =
                 conn.prepareStatement(sql)) {


        if (searchText != null
                && !searchText.trim().isEmpty()) {

            String pattern =
                    "%" + searchText.trim() + "%";

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);
            stmt.setString(6, pattern);
        }


        ResultSet rs =
                stmt.executeQuery();


        while (rs.next()) {

            Manufacturer manufacturer =
                    new Manufacturer(
                            rs.getString("manufcode"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("country"),
                            rs.getString("city"),
                            rs.getString("industrialzone")
                    );

            list.add(manufacturer);
        }


    } catch (SQLException e) {

        e.printStackTrace();
    }


    return list;
}public boolean deleteManufacturer(String manufCode) {

    String sql =
            "DELETE FROM public.manufacturer " +
            "WHERE manufcode = ?";


    try (Connection conn =
                 DatabaseConnection.getConnection();

         PreparedStatement stmt =
                 conn.prepareStatement(sql)) {


        stmt.setString(1, manufCode);


        int rows =
                stmt.executeUpdate();


        return rows > 0;


    } catch (SQLException e) {

        e.printStackTrace();

        return false;
    }
}
}