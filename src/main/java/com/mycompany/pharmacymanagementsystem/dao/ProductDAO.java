package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.ProductController;
import com.mycompany.pharmacymanagementsystem.ProductController.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductDAO {

    public ObservableList<ProductController.Product> getFilteredProducts(String categoryFilter, String searchText) {
        ObservableList<ProductController.Product> productList = FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder(
            "SELECT p.productcode, p.tradename, p.unit, p.minstocklevel, " +
            "man.name AS companyname, " +
            "m.dosage, m.scientificname, m.activeingredients, m.prescriptionrequired, " +
            "c.producttype, c.usagemethod " +
            "FROM public.product p " +
            "LEFT JOIN public.manufacturer man ON p.manufcode = man.manufcode " +
            "LEFT JOIN public.medicine m ON p.productcode = m.productcode " +
            "LEFT JOIN public.care_product c ON p.productcode = c.productcode " +
            "WHERE 1=1 "
        );

        if ("Medicine".equalsIgnoreCase(categoryFilter)) {
            sql.append(" AND m.productcode IS NOT NULL ");
        } else if ("Care Product".equalsIgnoreCase(categoryFilter)) {
            sql.append(" AND c.productcode IS NOT NULL ");
        }

        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND (p.productcode ILIKE ? OR p.tradename ILIKE ?) ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.trim() + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String code = rs.getString("productcode");
                String tName = rs.getString("tradename");
                String unit = rs.getString("unit");
                int minStock = rs.getInt("minstocklevel");
                String company = rs.getString("companyname");

                String dosage = rs.getString("dosage");
                String sName = rs.getString("scientificname");

                String type = rs.getString("producttype");
                String usage = rs.getString("usagemethod");
                
                String activeIng = rs.getString("activeingredients");
                boolean prescription = rs.getBoolean("prescriptionrequired");

                // إنشاء كائن المنتج مرة واحدة فقط وتعبئة حقوله
                Product product = new Product(
                    code, tName, unit, minStock, 
                    company != null ? company : "", 
                    dosage != null ? dosage : "", 
                    sName != null ? sName : "", 
                    type != null ? type : "", 
                    usage != null ? usage : ""
                );
                
                product.setActiveIngredients(activeIng != null ? activeIng : "");
                product.setPrescriptionRequired(prescription);
                
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    } 
    public boolean deleteProduct(String productCode) {
    // الحذف مباشرة من الجدول الرئيسي، والـ CASCADE في الداتابيز سيتكفل بالأبناء تلقائياً
    String sql = "DELETE FROM public.product WHERE productcode = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, productCode);
        int rowsAffected = pstmt.executeUpdate();
        
        // إذا تم حذف صف واحد على الأقل، العملية نجحت
        return rowsAffected > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean addProduct(ProductController.Product product, String category) {
        String sqlProduct = "INSERT INTO public.product (productcode, tradename, unit, minstocklevel, manufcode) VALUES (?, ?, ?, ?, ?)";
        String sqlMedicine = "INSERT INTO public.medicine (productcode, scientificname, activeingredients, dosage, prescriptionrequired) VALUES (?, ?, ?, ?, ?)";
        String sqlCare = "INSERT INTO public.care_product (productcode, producttype, usagemethod) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtProduct = conn.prepareStatement(sqlProduct)) {
                pstmtProduct.setString(1, product.getCode());
                pstmtProduct.setString(2, product.getTradeName());
                pstmtProduct.setString(3, product.getUnit());
                pstmtProduct.setInt(4, product.getMinStock());
                pstmtProduct.setString(5, product.getCompany());
                pstmtProduct.executeUpdate();
            }

            if ("Medicine".equalsIgnoreCase(category)) {
                try (PreparedStatement pstmtMed = conn.prepareStatement(sqlMedicine)) {
                    pstmtMed.setString(1, product.getCode());
                    pstmtMed.setString(2, product.getScientificName());
                    pstmtMed.setString(3, product.getActiveIngredients());
                    pstmtMed.setString(4, product.getDosage());
                    pstmtMed.setBoolean(5, product.isPrescriptionRequired());
                    pstmtMed.executeUpdate();
                }
            } else if ("CareProduct".equalsIgnoreCase(category) || "Care Product".equalsIgnoreCase(category)) {
                try (PreparedStatement pstmtCare = conn.prepareStatement(sqlCare)) {
                    pstmtCare.setString(1, product.getCode());
                    pstmtCare.setString(2, product.getCareType());
                    pstmtCare.setString(3, product.getUsageMethod());
                    pstmtCare.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
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
}