/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.StockTakingController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StockTakingDAO {

    // =========================================================
    // GENERATE STOCK TAKING NUMBER
    // =========================================================

    public String generateStockTakingNumber() {

        String sql = """
                SELECT COUNT(*) + 1 AS num
                FROM stocktaking
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                return String.format(
                        "STK-%05d",
                        rs.getInt("num")
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return "STK-00001";
    }


    // =========================================================
    // LOAD PRODUCTS
    // =========================================================

    public ObservableList<StockTakingController.ProductChoice>
    loadProducts() {

        ObservableList<StockTakingController.ProductChoice> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    productcode,
                    tradename
                FROM product
                ORDER BY tradename
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(
                        new StockTakingController.ProductChoice(
                                rs.getString("productcode"),
                                rs.getString("tradename")
                        )
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return list;
    }


    // =========================================================
    // GET PRODUCT STOCK
    // =========================================================

    public StockTakingController.StockItem
    getProductStock(String productCode) {

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    COALESCE(SUM(b.quantity), 0) AS system_quantity
                FROM product p
                LEFT JOIN batch b
                    ON p.productcode = b.productcode
                WHERE p.productcode = ?
                GROUP BY
                    p.productcode,
                    p.tradename
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, productCode);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int systemQuantity =
                            rs.getInt("system_quantity");

                    /*
                     * مهم:
                     *
                     * بالبداية Actual = System
                     *
                     * لأن الصيدلي لسه ما عدّ الكمية.
                     */

                    return new StockTakingController.StockItem(
                            rs.getString("productcode"),
                            rs.getString("tradename"),
                            systemQuantity,
                            systemQuantity
                    );
                }
            }

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // SAVE STOCK TAKING
    // =========================================================

    public boolean saveStockTaking(
            String countNumber,
            LocalDate date,
            ObservableList<StockTakingController.StockItem> items) {

        String stockTakingSQL = """
                INSERT INTO stocktaking
                (
                    countno,
                    date
                )
                VALUES (?, ?)
                """;


        String detailSQL = """
                INSERT INTO stock_count_detail
                (
                    countno,
                    productcode,
                    emp_id,
                    counted_quantity,
                    difference
                )
                VALUES (?, ?, ?, ?, ?)
                """;


        Connection conn = null;

        try {

            conn = DatabaseConnection.getConnection();

            conn.setAutoCommit(false);


            // =================================================
            // 1. INSERT STOCK TAKING HEADER
            // =================================================

            try (
                    PreparedStatement ps =
                            conn.prepareStatement(stockTakingSQL)
            ) {

                ps.setString(
                        1,
                        countNumber
                );

                ps.setDate(
                        2,
                        Date.valueOf(date)
                );

                ps.executeUpdate();
            }


            // =================================================
            // 2. INSERT DETAILS
            // =================================================

            try (
                    PreparedStatement ps =
                            conn.prepareStatement(detailSQL)
            ) {

                for (
                        StockTakingController.StockItem item
                        : items
                ) {

                    ps.setString(
                            1,
                            countNumber
                    );

                    ps.setString(
                            2,
                            item.getProductCode()
                    );

                    // emp_id nullable for now
                    ps.setNull(
                            3,
                            java.sql.Types.VARCHAR
                    );

                    /*
                     * الكمية التي عدّها الصيدلي فعليًا
                     */
                    ps.setInt(
                            4,
                            item.getActualQuantity()
                    );

                    /*
                     * الفرق:
                     *
                     * Actual - System
                     */
                    ps.setInt(
                            5,
                            item.getDifference()
                    );

                    ps.addBatch();
                }

                ps.executeBatch();
            }


            // =================================================
            // 3. UPDATE REAL STOCK
            // =================================================

            /*
             * هون أهم جزء.
             *
             * بعد Stock Taking:
             *
             * System Quantity = Actual Quantity
             *
             * يعني نعدل batch حتى يصبح مجموع الكمية
             * مساويًا للكمية التي عدّها الصيدلي.
             */

            for (
                    StockTakingController.StockItem item
                    : items
            ) {

                updateProductStock(
                        conn,
                        item.getProductCode(),
                        item.getActualQuantity()
                );
            }


            // =================================================
            // 4. COMMIT
            // =================================================

            conn.commit();

            return true;


        } catch (Exception ex) {

            ex.printStackTrace();

            try {

                if (conn != null) {
                    conn.rollback();
                }

            } catch (SQLException ignored) {
            }

            return false;


        } finally {

            try {

                if (conn != null) {

                    conn.setAutoCommit(true);
                    conn.close();
                }

            } catch (SQLException ignored) {
            }
        }
    }


    // =========================================================
    // UPDATE PRODUCT STOCK TO ACTUAL QUANTITY
    // =========================================================

    private void updateProductStock(
            Connection conn,
            String productCode,
            int actualQuantity)
            throws SQLException {

        /*
         * نحصل على جميع batches للدواء.
         *
         * نرتبها حسب expiry date.
         */

        String selectSQL = """
                SELECT
                    batch_no,
                    quantity
                FROM batch
                WHERE productcode = ?
                ORDER BY expire_date, batch_no
                """;


        ObservableList<BatchStock> batches =
                FXCollections.observableArrayList();


        try (
                PreparedStatement ps =
                        conn.prepareStatement(selectSQL)
        ) {

            ps.setString(1, productCode);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    batches.add(
                            new BatchStock(
                                    rs.getString("batch_no"),
                                    rs.getInt("quantity")
                            )
                    );
                }
            }
        }


        // =====================================================
        // NO BATCHES
        // =====================================================

        if (batches.isEmpty()) {

            throw new SQLException(
                    "No batches found for product: "
                    + productCode
            );
        }


        // =====================================================
        // DISTRIBUTE ACTUAL QUANTITY
        // =====================================================

        int remaining =
                actualQuantity;


        String updateSQL = """
                UPDATE batch
                SET quantity = ?
                WHERE batch_no = ?
                """;


        try (
                PreparedStatement ps =
                        conn.prepareStatement(updateSQL)
        ) {

            for (BatchStock batch : batches) {

                int newQuantity =
                        Math.min(
                                remaining,
                                batch.quantity
                        );

                /*
                 * إذا remaining = 0
                 * تصبح باقي batches = 0.
                 */

                if (remaining <= 0) {

                    newQuantity = 0;
                }


                ps.setInt(
                        1,
                        newQuantity
                );

                ps.setString(
                        2,
                        batch.batchNo
                );

                ps.addBatch();


                remaining -= newQuantity;
            }


            /*
             * إذا Actual أكبر من مجموع الـ batches
             *
             * نضيف الفرق إلى أول batch.
             *
             * مثال:
             *
             * System = 10
             * Actual = 15
             *
             * يصبح مجموع الـ batches = 15.
             */

            if (remaining > 0) {

                BatchStock firstBatch =
                        batches.get(0);

                int newQuantity =
                        firstBatch.quantity
                        + remaining;


                ps.setInt(
                        1,
                        newQuantity
                );

                ps.setString(
                        2,
                        firstBatch.batchNo
                );

                ps.addBatch();
            }


            ps.executeBatch();
        }
    }


    // =========================================================
    // BATCH STOCK MODEL
    // =========================================================

    private static class BatchStock {

        private final String batchNo;
        private final int quantity;


        private BatchStock(
                String batchNo,
                int quantity) {

            this.batchNo = batchNo;
            this.quantity = quantity;
        }
    }
}

