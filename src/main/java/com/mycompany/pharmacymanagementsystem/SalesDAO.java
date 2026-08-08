
package com.mycompany.pharmacymanagementsystem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SalesDAO {

    // =========================================================
    // PRODUCTS
    // =========================================================

    public ObservableList<SaleProduct> loadProducts() {

        ObservableList<SaleProduct> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price,
                    b.batch_no,
                    b.quantity
                FROM product p
                JOIN batch b
                    ON p.productcode = b.productcode
                WHERE b.quantity > 0
                ORDER BY p.tradename
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(
                        new SaleProduct(
                                rs.getString("productcode"),
                                rs.getString("tradename"),
                                rs.getString("unit"),
                                rs.getDouble("price"),
                                rs.getString("batch_no"),
                                rs.getInt("quantity")
                        )
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    public ObservableList<SaleProduct> searchProducts(String keyword) {

        ObservableList<SaleProduct> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    p.productcode,
                    p.tradename,
                    p.unit,
                    p.price,
                    COALESCE(SUM(b.quantity), 0) AS stock
                FROM product p
                LEFT JOIN batch b
                    ON p.productcode = b.productcode
                WHERE LOWER(p.tradename) LIKE ?
                   OR LOWER(p.productcode) LIKE ?
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
                    "%" + keyword.toLowerCase() + "%";

            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            new SaleProduct(
                                    rs.getString("productcode"),
                                    rs.getString("tradename"),
                                    rs.getString("unit"),
                                    rs.getDouble("price"),
                                    rs.getInt("stock")
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
    // CUSTOMERS
    // =========================================================

    public ObservableList<String> loadCustomers() {

        ObservableList<String> list =
                FXCollections.observableArrayList();

        list.add("Walk-In Customer");

        String sql = """
                SELECT
                    cust_id,
                    first_name,
                    last_name
                FROM customer
                ORDER BY first_name
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                String customer =
                        rs.getString("cust_id")
                        + " - "
                        + rs.getString("first_name")
                        + " "
                        + rs.getString("last_name");

                list.add(customer);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    public String getCustomerId(String selectedCustomer) {

        if (selectedCustomer == null) {
            return null;
        }

        if (selectedCustomer.equals("Walk-In Customer")) {
            return null;
        }

        return selectedCustomer.split(" - ")[0];
    }


    // =========================================================
    // INVOICE NUMBER
    // =========================================================

    public String generateInvoiceNumber() {

        String sql = """
                SELECT COUNT(*) + 1 AS num
                FROM sales_invoice
                """;

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                return String.format(
                        "INV-%05d",
                        rs.getInt("num")
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "INV-00001";
    }


    // =========================================================
    // SAVE SALE
    // =========================================================

    public boolean saveSale(
            String invoiceNo,
            String empId,
            String custId,
            double total,
            ObservableList<InvoiceItem> items) {

        String invoiceSQL = """
                INSERT INTO sales_invoice
                (invoiceno, date, totalamount, emp_id, cust_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psInvoice =
                         con.prepareStatement(invoiceSQL)) {

                psInvoice.setString(1, invoiceNo);

                psInvoice.setDate(
                        2,
                        Date.valueOf(LocalDate.now())
                );

                psInvoice.setDouble(3, total);
                psInvoice.setString(4, empId);
                psInvoice.setString(5, custId);

                psInvoice.executeUpdate();
            }


            for (InvoiceItem item : items) {

                sellProduct(
                        con,
                        invoiceNo,
                        item.getCode(),
                        item.getQuantity(),
                        item.getPrice()
                );
            }


            con.commit();

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (SQLException ignored) {
            }

            return false;

        } finally {

            try {

                if (con != null) {

                    con.setAutoCommit(true);
                    con.close();
                }

            } catch (SQLException ignored) {
            }
        }
    }


    // =========================================================
    // SELL PRODUCT
    // =========================================================

    private void sellProduct(
            Connection con,
            String invoiceNo,
            String productCode,
            int quantity,
            double sellingPrice)
            throws SQLException {

        String batchSQL = """
                SELECT
                    batch_no,
                    quantity
                FROM batch
                WHERE productcode = ?
                  AND quantity > 0
                ORDER BY expire_date
                """;

        try (PreparedStatement psBatch =
                     con.prepareStatement(batchSQL)) {

            psBatch.setString(1, productCode);

            try (ResultSet rs =
                         psBatch.executeQuery()) {

                while (quantity > 0 && rs.next()) {

                    String batchNo =
                            rs.getString("batch_no");

                    int batchQty =
                            rs.getInt("quantity");

                    int sold =
                            Math.min(
                                    quantity,
                                    batchQty
                            );


                    insertSaleDetail(
                            con,
                            invoiceNo,
                            batchNo,
                            sold,
                            sellingPrice
                    );


                    updateBatchQuantity(
                            con,
                            batchNo,
                            sold
                    );


                    quantity -= sold;
                }
            }
        }


        if (quantity > 0) {

            throw new SQLException(
                    "Not enough stock."
            );
        }
    }


    // =========================================================
    // INSERT SALE DETAIL
    // =========================================================

    private void insertSaleDetail(
            Connection con,
            String invoiceNo,
            String batchNo,
            int qty,
            double price)
            throws SQLException {

        String sql = """
                INSERT INTO sale_detail
                (invoiceno, batch_no, quantity, selling_price)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, invoiceNo);
            ps.setString(2, batchNo);
            ps.setInt(3, qty);
            ps.setDouble(4, price);

            ps.executeUpdate();
        }
    }


    // =========================================================
    // UPDATE BATCH
    // =========================================================

    private void updateBatchQuantity(
            Connection con,
            String batchNo,
            int sold)
            throws SQLException {

        String sql = """
                UPDATE batch
                SET quantity = quantity - ?
                WHERE batch_no = ?
                """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, sold);
            ps.setString(2, batchNo);

            ps.executeUpdate();
        }
    }


    // =========================================================
    // SALES HISTORY
    // =========================================================

    public ObservableList<SalesInvoice> loadInvoices() {

        ObservableList<SalesInvoice> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    s.invoiceno,
                    s.date,
                    COALESCE(
                        c.cust_id,
                        'Walk-In'
                    ) AS customer,
                    e.first_name || ' ' || e.last_name
                        AS employee,
                    s.totalamount
                FROM sales_invoice s
                LEFT JOIN customer c
                    ON s.cust_id = c.cust_id
                LEFT JOIN employee e
                    ON s.emp_id = e.emp_id
                ORDER BY s.date DESC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(
                        new SalesInvoice(
                                rs.getString("invoiceno"),
                                rs.getString("date"),
                                rs.getString("customer"),
                                rs.getString("employee"),
                                rs.getDouble("totalamount")
                        )
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    public ObservableList<SalesInvoice> searchInvoice(
            String invoiceNo) {

        ObservableList<SalesInvoice> list =
                FXCollections.observableArrayList();

        String sql = """
                SELECT
                    s.invoiceno,
                    s.date,
                    COALESCE(
                        c.cust_id,
                        'Walk-In'
                    ) AS customer,
                    e.first_name || ' ' || e.last_name
                        AS employee,
                    s.totalamount
                FROM sales_invoice s
                LEFT JOIN customer c
                    ON s.cust_id = c.cust_id
                LEFT JOIN employee e
                    ON s.emp_id = e.emp_id
                WHERE LOWER(s.invoiceno) LIKE ?
                ORDER BY s.date DESC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    "%" + invoiceNo.toLowerCase() + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            new SalesInvoice(
                                    rs.getString("invoiceno"),
                                    rs.getString("date"),
                                    rs.getString("customer"),
                                    rs.getString("employee"),
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
// SALE DETAILS
// =========================================================

public ObservableList<SaleDetail> getSaleDetails(
        String invoiceNo) {

    ObservableList<SaleDetail> list =
            FXCollections.observableArrayList();

    String sql = """
            SELECT
                sd.invoiceno,
                sd.batch_no,
                sd.quantity,
                sd.selling_price
            FROM sale_detail sd
            WHERE sd.invoiceno = ?
            ORDER BY sd.batch_no
            """;

    try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setString(1, invoiceNo);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(
                        new SaleDetail(
                                rs.getString("invoiceno"),
                                rs.getString("batch_no"),
                                rs.getInt("quantity"),
                                rs.getDouble("selling_price")
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
// SALE DETAIL MODEL
// =========================================================

public static class SaleDetail {

    private final String invoiceNumber;
    private final String batchNumber;
    private final int quantity;
    private final double sellingPrice;

    public SaleDetail(
            String invoiceNumber,
            String batchNumber,
            int quantity,
            double sellingPrice) {

        this.invoiceNumber = invoiceNumber;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getTotal() {
        return quantity * sellingPrice;
    }
}


}

