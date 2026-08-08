/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;



import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.InventoryReportRow;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InventoryReportDAO {

    // =========================================================
    // LOAD INVENTORY
    // =========================================================

    public ObservableList<InventoryReportRow> loadInventory() {

        ObservableList<InventoryReportRow> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    b.quantity,
                    p.price,
                    b.expire_date
                FROM product p
                JOIN batch b
                    ON p.productcode = b.productcode
                ORDER BY p.tradename, b.expire_date
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Date sqlDate =
                        rs.getDate("expire_date");

                LocalDate expireDate =
                        sqlDate != null
                        ? sqlDate.toLocalDate()
                        : null;

                list.add(
                        new InventoryReportRow(
                                rs.getString("productcode"),
                                rs.getString("tradename"),
                                rs.getString("unit"),
                                rs.getInt("quantity"),
                                rs.getDouble("price"),
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

    public ObservableList<InventoryReportRow> searchInventory(
            String keyword) {

        ObservableList<InventoryReportRow> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    b.quantity,
                    p.price,
                    b.expire_date
                FROM product p
                JOIN batch b
                    ON p.productcode = b.productcode
                WHERE
                    LOWER(p.productcode) LIKE ?
                    OR LOWER(p.tradename) LIKE ?
                ORDER BY p.tradename, b.expire_date
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

                    Date sqlDate =
                            rs.getDate("expire_date");

                    LocalDate expireDate =
                            sqlDate != null
                            ? sqlDate.toLocalDate()
                            : null;

                    list.add(
                            new InventoryReportRow(
                                    rs.getString("productcode"),
                                    rs.getString("tradename"),
                                    rs.getString("unit"),
                                    rs.getInt("quantity"),
                                    rs.getDouble("price"),
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
    // TOTAL PRODUCTS
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


    // =========================================================
    // TOTAL STOCK
    // =========================================================

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


    // =========================================================
    // LOW STOCK
    // Products whose total stock is 10 or less
    // =========================================================

    public int getLowStockCount() {

        String sql = """
                SELECT COUNT(*)
                FROM (
                    SELECT
                        p.productcode,
                        COALESCE(SUM(b.quantity), 0) AS stock
                    FROM product p
                    LEFT JOIN batch b
                        ON p.productcode = b.productcode
                    GROUP BY p.productcode
                    HAVING COALESCE(SUM(b.quantity), 0) <= 10
                ) AS low_stock
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
    // EXPIRED BATCHES
    // =========================================================

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
}

