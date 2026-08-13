/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PurchaseDAO {


// =========================================================
// LOAD ALL PURCHASES
// =========================================================

public ObservableList<Purchase> loadPurchases() {

    ObservableList<Purchase> list =
            FXCollections.observableArrayList();

    String sql = """
            SELECT
                purch_inv_no,
                date,
                COALESCE(totalamount, 0) AS totalamount
            FROM purchase_invoice
            ORDER BY date DESC, purch_inv_no DESC
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {

            Date sqlDate = rs.getDate("date");

            LocalDate date =
                    sqlDate != null
                    ? sqlDate.toLocalDate()
                    : null;

            list.add(
                    new Purchase(
                            rs.getString("purch_inv_no"),
                            date,
                            rs.getDouble("totalamount")
                    )
            );
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return list;
}


// =========================================================
// SEARCH PURCHASE
// =========================================================

public ObservableList<Purchase> searchPurchases(
        String keyword) {

    ObservableList<Purchase> list =
            FXCollections.observableArrayList();

    String sql = """
            SELECT
                purch_inv_no,
                date,
                COALESCE(totalamount, 0) AS totalamount
            FROM purchase_invoice
            WHERE LOWER(purch_inv_no) LIKE ?
            ORDER BY date DESC, purch_inv_no DESC
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        String value =
                "%" + keyword.trim().toLowerCase() + "%";

        ps.setString(1, value);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Date sqlDate = rs.getDate("date");

                LocalDate date =
                        sqlDate != null
                        ? sqlDate.toLocalDate()
                        : null;

                list.add(
                        new Purchase(
                                rs.getString("purch_inv_no"),
                                date,
                                rs.getDouble("totalamount")
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
// GENERATE PURCHASE NUMBER
// =========================================================

public String generatePurchaseNumber() {

    String sql = """
            SELECT COUNT(*) + 1 AS num
            FROM purchase_invoice
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
    ) {

        if (rs.next()) {

            return String.format(
                    "PUR-%05d",
                    rs.getInt("num")
            );
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return "PUR-00001";
}


// =========================================================
// GET PURCHASE BY NUMBER
// =========================================================

public Purchase getPurchaseByNumber(
        String purchaseNumber) {

    String sql = """
            SELECT
                purch_inv_no,
                date,
                COALESCE(totalamount, 0) AS totalamount
            FROM purchase_invoice
            WHERE purch_inv_no = ?
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setString(1, purchaseNumber);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Date sqlDate =
                        rs.getDate("date");

                LocalDate date =
                        sqlDate != null
                        ? sqlDate.toLocalDate()
                        : null;

                return new Purchase(
                        rs.getString("purch_inv_no"),
                        date,
                        rs.getDouble("totalamount")
                );
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return null;
}


// =========================================================
// GET PURCHASE DETAILS
// =========================================================

public ObservableList<PurchaseBatch> getPurchaseDetails(
        String purchaseNumber) {

    ObservableList<PurchaseBatch> list =
            FXCollections.observableArrayList();

    String sql = """
            SELECT
                b.batch_no,
                b.productcode,
                p.tradename,
                b.purchase_price,
                b.quantity,
                b.expire_date
            FROM batch b
            JOIN product p
                ON b.productcode = p.productcode
            WHERE b.purch_inv_no = ?
            ORDER BY b.batch_no
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setString(1, purchaseNumber);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Date sqlDate =
                        rs.getDate("expire_date");

                LocalDate expireDate =
                        sqlDate != null
                        ? sqlDate.toLocalDate()
                        : null;

                list.add(
                        new PurchaseBatch(
                                rs.getString("batch_no"),
                                rs.getString("productcode"),
                                rs.getString("tradename"),
                                rs.getDouble("purchase_price"),
                                rs.getInt("quantity"),
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
// LOAD PRODUCTS FOR NEW PURCHASE
// =========================================================

public ObservableList<NewPurchaseController.ProductChoice> loadProducts() {

    ObservableList<NewPurchaseController.ProductChoice> list =
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
                    new NewPurchaseController.ProductChoice(
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
// SAVE PURCHASE
// =========================================================

public boolean savePurchase(
        String purchaseNumber,
        LocalDate date,
        double total,
        ObservableList<NewPurchaseController.PurchaseItem> items) {
String invoiceSQL = """
        INSERT INTO purchase_invoice
        (
            purch_inv_no,
            date,
            totalamount
        )
        VALUES (?, ?, ?)
        """;

String batchSQL = """
        INSERT INTO batch
        (
            batch_no,
            productcode,
            purchase_price,
            quantity,
            expire_date,
            purch_inv_no
        )
        VALUES (?, ?, ?, ?, ?, ?)
        """;

String activateProductSQL = """
        UPDATE product
        SET active = TRUE
        WHERE productcode = ?
        """;

    Connection conn = null;

    try {

        conn = DatabaseConnection.getConnection();

        conn.setAutoCommit(false);


        // -----------------------------------------------------
        // INSERT PURCHASE INVOICE
        // -----------------------------------------------------

        try (
                PreparedStatement ps =
                        conn.prepareStatement(invoiceSQL)
        ) {

            ps.setString(
                    1,
                    purchaseNumber
            );

            ps.setDate(
                    2,
                    Date.valueOf(date)
            );

            ps.setDouble(
                    3,
                    total
            );

            ps.executeUpdate();
        }


        // -----------------------------------------------------
// INSERT BATCHES
// -----------------------------------------------------

try (
        PreparedStatement ps =
                conn.prepareStatement(batchSQL)
) {

    for (
            NewPurchaseController.PurchaseItem item
            : items
    ) {

        ps.setString(
                1,
                item.getBatchNumber()
        );

        ps.setString(
                2,
                item.getProductCode()
        );

        ps.setDouble(
                3,
                item.getBuyPrice()
        );

        ps.setInt(
                4,
                item.getQuantity()
        );

        ps.setDate(
                5,
                Date.valueOf(
                        item.getExpiryDate()
                )
        );

        ps.setString(
                6,
                purchaseNumber
        );

        ps.addBatch();
    }

    ps.executeBatch();
}


// -----------------------------------------------------
// UPDATE ACTIVE BASED ON REAL STOCK
// -----------------------------------------------------

String updateActiveSQL = """
        UPDATE product p
        SET active = (
            SELECT COALESCE(SUM(b.quantity), 0) > 0
            FROM batch b
            WHERE b.productcode = p.productcode
        )
        WHERE p.productcode = ?
        """;

try (
        PreparedStatement ps =
                conn.prepareStatement(updateActiveSQL)
) {

    for (
            NewPurchaseController.PurchaseItem item
            : items
    ) {

        ps.setString(
                1,
                item.getProductCode()
        );

        ps.addBatch();
    }

    ps.executeBatch();
}


// -----------------------------------------------------
// COMMIT
// -----------------------------------------------------

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
public int getTotalItems(
        LocalDate startDate,
        LocalDate endDate) {

    String sql = """
            SELECT COALESCE(SUM(b.quantity), 0) AS total_items
            FROM batch b
            JOIN purchase_invoice p
                ON b.purch_inv_no = p.purch_inv_no
            WHERE p.date BETWEEN ? AND ?
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setDate(1, Date.valueOf(startDate));
        ps.setDate(2, Date.valueOf(endDate));

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_items");
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return 0;
}
// =========================================================
// PURCHASE BATCH MODEL
// =========================================================

public static class PurchaseBatch {

    private final String batchNumber;
    private final String productCode;
    private final String productName;
    private final double purchasePrice;
    private final int quantity;
    private final LocalDate expireDate;

    public PurchaseBatch(
            String batchNumber,
            String productCode,
            String productName,
            double purchasePrice,
            int quantity,
            LocalDate expireDate) {

        this.batchNumber = batchNumber;
        this.productCode = productCode;
        this.productName = productName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.expireDate = expireDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public double getTotal() {
        return purchasePrice * quantity;
    }
}


}
