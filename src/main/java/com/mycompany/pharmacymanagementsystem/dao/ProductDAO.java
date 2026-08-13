package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductDAO {

    // =========================================================
    // GET PRODUCTS
    // =========================================================

    public ObservableList<Product> getFilteredProducts(
            String categoryFilter,
            String searchText) {

        ObservableList<Product> productList =
                FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "p.productcode, " +
            "p.tradename, " +
            "p.unit, " +
            "p.minstocklevel, " +
            "p.manufcode, " +
            "p.price, " +
            "man.name AS companyname, " +
            "m.scientificname, " +
            "m.activeingredients, " +
            "m.dosage, " +
            "m.prescriptionrequired, " +
            "c.producttype, " +
            "c.usagemethod " +

            "FROM public.product p " +

            "LEFT JOIN public.manufacturer man " +
            "ON p.manufcode = man.manufcode " +

            "LEFT JOIN public.medicine m " +
            "ON p.productcode = m.productcode " +

            "LEFT JOIN public.care_product c " +
            "ON p.productcode = c.productcode " +

            "WHERE p.active = TRUE "
        );


        // =====================================================
        // CATEGORY FILTER
        // =====================================================

        if ("Medicine".equalsIgnoreCase(categoryFilter)) {

            sql.append(
                "AND m.productcode IS NOT NULL "
            );

        } else if ("Care Product".equalsIgnoreCase(categoryFilter)
                || "CareProduct".equalsIgnoreCase(categoryFilter)) {

            sql.append(
                "AND c.productcode IS NOT NULL "
            );
        }


        // =====================================================
        // SEARCH
        // =====================================================

        if (searchText != null
                && !searchText.trim().isEmpty()) {

            sql.append(
                "AND (p.productcode ILIKE ? " +
                "OR p.tradename ILIKE ?) "
            );
        }


        sql.append(
            "ORDER BY p.productcode"
        );


        // =====================================================
        // DATABASE
        // =====================================================

        try (Connection conn =
                DatabaseConnection.getConnection();

             PreparedStatement stmt =
                conn.prepareStatement(sql.toString())) {


            // -------------------------------------------------
            // Search parameters
            // -------------------------------------------------

            if (searchText != null
                    && !searchText.trim().isEmpty()) {

                String pattern =
                    "%" + searchText.trim() + "%";

                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
            }


            // -------------------------------------------------
            // Execute
            // -------------------------------------------------

            try (ResultSet rs =
                    stmt.executeQuery()) {


                while (rs.next()) {

                    Product product =
                            new Product();


                    product.setCode(
                        rs.getString("productcode")
                    );

                    product.setTradeName(
                        rs.getString("tradename")
                    );

                    product.setUnit(
                        rs.getString("unit")
                    );

                    product.setMinStock(
                        rs.getInt("minstocklevel")
                    );

                    product.setCompany(
                        getStringOrEmpty(
                            rs,
                            "companyname"
                        )
                    );

                    product.setPrice(
                        rs.getDouble("price")
                    );

                    product.setDosage(
                        getStringOrEmpty(
                            rs,
                            "dosage"
                        )
                    );

                    product.setScientificName(
                        getStringOrEmpty(
                            rs,
                            "scientificname"
                        )
                    );

                    product.setActiveIngredients(
                        getStringOrEmpty(
                            rs,
                            "activeingredients"
                        )
                    );

                    product.setPrescriptionRequired(
                        rs.getBoolean(
                            "prescriptionrequired"
                        )
                    );

                    product.setProductType(
                        getStringOrEmpty(
                            rs,
                            "producttype"
                        )
                    );

                    product.setUsageMethod(
                        getStringOrEmpty(
                            rs,
                            "usagemethod"
                        )
                    );


                    productList.add(product);
                }
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return productList;
    }


    // =========================================================
    // DELETE / DEACTIVATE PRODUCT
    // =========================================================
    //
    // We do NOT physically delete the product.
    //
    // Product can be deactivated ONLY when current stock = 0.
    //
    // All historical records remain:
    //
    // batch
    // stock_count_detail
    // purchase_invoice
    // sale_detail
    // sales_invoice
    //
    // =========================================================

    public boolean deleteProduct(String productCode) {

        String stockSql =
            "SELECT COALESCE(SUM(quantity), 0) " +
            "FROM public.batch " +
            "WHERE productcode = ?";


        String deactivateSql =
            "UPDATE public.product " +
            "SET active = FALSE " +
            "WHERE productcode = ? " +
            "AND active = TRUE";


        try (Connection conn =
                DatabaseConnection.getConnection()) {


            // =================================================
            // 1. CHECK CURRENT STOCK
            // =================================================

            try (PreparedStatement stmt =
                    conn.prepareStatement(stockSql)) {


                stmt.setString(
                    1,
                    productCode
                );


                try (ResultSet rs =
                        stmt.executeQuery()) {


                    if (rs.next()) {

                        int totalStock =
                                rs.getInt(1);


                        // -------------------------------------
                        // Product still has stock
                        // -------------------------------------

                        if (totalStock > 0) {

                            return false;
                        }
                    }
                }
            }


            // =================================================
            // 2. DEACTIVATE PRODUCT
            // =================================================

            try (PreparedStatement stmt =
                    conn.prepareStatement(deactivateSql)) {


                stmt.setString(
                    1,
                    productCode
                );


                int rowsAffected =
                        stmt.executeUpdate();


                return rowsAffected > 0;
            }


        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // ADD PRODUCT
    // =========================================================

    public boolean addProduct(
            Product product,
            String category) {


        String sqlProduct =
            "INSERT INTO public.product " +
            "(productcode, tradename, unit, " +
            "minstocklevel, manufcode, price, active) " +
            "VALUES (?, ?, ?, ?, ?, ?, TRUE)";


        String sqlMedicine =
            "INSERT INTO public.medicine " +
            "(productcode, scientificname, " +
            "activeingredients, dosage, " +
            "prescriptionrequired) " +
            "VALUES (?, ?, ?, ?, ?)";


        String sqlCare =
            "INSERT INTO public.care_product " +
            "(productcode, producttype, usagemethod) " +
            "VALUES (?, ?, ?)";


        Connection conn = null;


        try {

            conn =
                DatabaseConnection.getConnection();

            conn.setAutoCommit(false);


            // =================================================
            // PRODUCT
            // =================================================

            try (PreparedStatement stmt =
                    conn.prepareStatement(sqlProduct)) {


                stmt.setString(
                    1,
                    product.getCode()
                );

                stmt.setString(
                    2,
                    product.getTradeName()
                );

                stmt.setString(
                    3,
                    product.getUnit()
                );

                stmt.setInt(
                    4,
                    product.getMinStock()
                );

                stmt.setString(
                    5,
                    product.getCompany()
                );

                stmt.setDouble(
                    6,
                    product.getPrice()
                );


                stmt.executeUpdate();
            }


            // =================================================
            // MEDICINE
            // =================================================

            if ("Medicine".equalsIgnoreCase(category)) {

                try (PreparedStatement stmt =
                        conn.prepareStatement(sqlMedicine)) {


                    stmt.setString(
                        1,
                        product.getCode()
                    );

                    stmt.setString(
                        2,
                        product.getScientificName()
                    );

                    stmt.setString(
                        3,
                        product.getActiveIngredients()
                    );

                    stmt.setString(
                        4,
                        product.getDosage()
                    );

                    stmt.setBoolean(
                        5,
                        product.isPrescriptionRequired()
                    );


                    stmt.executeUpdate();
                }
            }


            // =================================================
            // CARE PRODUCT
            // =================================================

            else if (
                "Care Product".equalsIgnoreCase(category)
                ||
                "CareProduct".equalsIgnoreCase(category)
            ) {


                try (PreparedStatement stmt =
                        conn.prepareStatement(sqlCare)) {


                    stmt.setString(
                        1,
                        product.getCode()
                    );

                    stmt.setString(
                        2,
                        product.getProductType()
                    );

                    stmt.setString(
                        3,
                        product.getUsageMethod()
                    );


                    stmt.executeUpdate();
                }
            }


            // =================================================
            // COMMIT
            // =================================================

            conn.commit();

            return true;


        } catch (SQLException e) {


            // =================================================
            // ROLLBACK
            // =================================================

            if (conn != null) {

                try {

                    conn.rollback();

                } catch (SQLException rollbackException) {

                    rollbackException.printStackTrace();
                }
            }


            e.printStackTrace();

            return false;


        } finally {


            if (conn != null) {

                try {

                    conn.setAutoCommit(true);
                    conn.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    // =========================================================
    // HELPER
    // =========================================================

    private String getStringOrEmpty(
            ResultSet rs,
            String columnName)
            throws SQLException {


        String value =
                rs.getString(columnName);


        return value != null
                ? value
                : "";
    }
}