/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InventoryDAO {

    // =========================================================
    // LOAD INVENTORY
    // =========================================================

    public ObservableList<InventoryItem> loadInventory() {

        ObservableList<InventoryItem> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price,
                    COALESCE(SUM(b.quantity), 0) AS stock,
                    MIN(b.expire_date) AS expire_date
                FROM product p
                LEFT JOIN batch b
                    ON p.productcode = b.productcode
                GROUP BY
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price
                ORDER BY p.tradename
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                java.sql.Date sqlDate =
                        rs.getDate("expire_date");

                LocalDate expireDate =
                        sqlDate != null
                                ? sqlDate.toLocalDate()
                                : null;

                list.add(
                        new InventoryItem(
                                rs.getString("productcode"),
                                rs.getString("tradename"),
                                rs.getString("unit"),
                                rs.getDouble("price"),
                                rs.getInt("stock"),
                                expireDate
                        )
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    // =========================================================
    // SEARCH INVENTORY
    // =========================================================

    public ObservableList<InventoryItem> searchInventory(
            String keyword) {

        ObservableList<InventoryItem> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price,
                    COALESCE(SUM(b.quantity), 0) AS stock,
                    MIN(b.expire_date) AS expire_date
                FROM product p
                LEFT JOIN batch b
                    ON p.productcode = b.productcode
                WHERE
                    LOWER(p.productcode) LIKE ?
                    OR LOWER(p.tradename) LIKE ?
                GROUP BY
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price
                ORDER BY p.tradename
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            String value =
                    "%" + keyword.trim().toLowerCase() + "%";

            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    java.sql.Date sqlDate =
                            rs.getDate("expire_date");

                    LocalDate expireDate =
                            sqlDate != null
                                    ? sqlDate.toLocalDate()
                                    : null;

                    list.add(
                            new InventoryItem(
                                    rs.getString("productcode"),
                                    rs.getString("tradename"),
                                    rs.getString("unit"),
                                    rs.getDouble("price"),
                                    rs.getInt("stock"),
                                    expireDate
                            )
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    // =========================================================
    // SUMMARY
    // =========================================================

    public int getTotalProducts() {

        String sql = """
                SELECT COUNT(*)
                FROM product
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    public int getTotalStock() {

        String sql = """
                SELECT COALESCE(SUM(quantity), 0)
                FROM batch
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    public int getLowStockCount() {

        String sql = """
                SELECT COUNT(*)
                FROM (
                    SELECT productcode
                    FROM batch
                    GROUP BY productcode
                    HAVING COALESCE(SUM(quantity), 0) <= 10
                ) x
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    public int getExpiredBatchesCount() {

        String sql = """
                SELECT COUNT(*)
                FROM batch
                WHERE expire_date < CURRENT_DATE
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    // =========================================================
    // MODEL
    // =========================================================

    public static class InventoryItem {

        private final String productCode;
        private final String productName;
        private final String unit;
        private final double price;
        private final int stock;
        private final LocalDate expireDate;

        public InventoryItem(
                String productCode,
                String productName,
                String unit,
                double price,
                int stock,
                LocalDate expireDate) {

            this.productCode = productCode;
            this.productName = productName;
            this.unit = unit;
            this.price = price;
            this.stock = stock;
            this.expireDate = expireDate;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductName() {
            return productName;
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

        public LocalDate getExpireDate() {
            return expireDate;
        }
    }
}