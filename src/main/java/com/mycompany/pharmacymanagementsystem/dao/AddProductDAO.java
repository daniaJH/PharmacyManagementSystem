package com.mycompany.pharmacymanagementsystem.dao;
import java.sql.ResultSet;
import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import com.mycompany.pharmacymanagementsystem.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mycompany.pharmacymanagementsystem.Manufacturer;
import com.mycompany.pharmacymanagementsystem.ProductController;
public class AddProductDAO {
   
private boolean productCodeExists(Connection conn, String code) throws SQLException {
    String sql = "SELECT 1 FROM public.product WHERE productcode = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, code);

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }
} public boolean updateProduct(Product product, String category) {
    String sqlProduct = "UPDATE public.product SET tradename = ?, unit = ?, minstocklevel = ?, manufcode = ? WHERE productcode = ?";
    String sqlMedicine = "UPDATE public.medicine SET scientificname = ?, activeingredients = ?, dosage = ?, prescriptionrequired = ? WHERE productcode = ?";
    String sqlCare = "UPDATE public.care_product SET producttype = ?, usagemethod = ? WHERE productcode = ?";

    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        // 1. تحديث جدول Product الأساسي
        try (PreparedStatement pstmt = conn.prepareStatement(sqlProduct)) {
            pstmt.setString(1, product.getTradeName());
            pstmt.setString(2, product.getUnit());
            pstmt.setInt(3, product.getMinStock());
            pstmt.setString(4, product.getCompany());
            pstmt.setString(5, product.getCode());
            pstmt.executeUpdate();
        }

        // 2. تحديث جدول Medicine أو Care Product
        if ("Medicine".equalsIgnoreCase(category)) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlMedicine)) {
                pstmt.setString(1, product.getScientificName());
                pstmt.setString(2, product.getActiveIngredients());
                pstmt.setString(3, product.getDosage());
                pstmt.setBoolean(4, product.isPrescriptionRequired());
                pstmt.setString(5, product.getCode());
                pstmt.executeUpdate();
            }
        } else {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCare)) {
                pstmt.setString(1, product.getCareType());
                pstmt.setString(2, product.getUsageMethod());
                pstmt.setString(3, product.getCode());
                pstmt.executeUpdate();
            }
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
} public ObservableList<Manufacturer> getManufacturers() {

    ObservableList<Manufacturer> list = FXCollections.observableArrayList();

    String sql = "SELECT manufcode, name FROM manufacturer ORDER BY name";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            list.add(new Manufacturer(
                rs.getString("manufcode"),
                rs.getString("name")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    public boolean addProduct(Product product, String category) {
        String sqlProduct = "INSERT INTO public.product (productcode, tradename, unit, minstocklevel, manufcode) VALUES (?, ?, ?, ?, ?)";
        String sqlMedicine = "INSERT INTO public.medicine (productcode, scientificname, activeingredients, dosage, prescriptionrequired) VALUES (?, ?, ?, ?, ?)";
        String sqlCare = "INSERT INTO public.care_product (productcode, producttype, usagemethod) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

if (productCodeExists(conn, product.getCode())) {
    return false;
}

conn.setAutoCommit(false);

            // 1. Insert into general product table
            try (PreparedStatement pstmtProduct = conn.prepareStatement(sqlProduct)) {
                pstmtProduct.setString(1, product.getCode());
                pstmtProduct.setString(2, product.getTradeName());
                pstmtProduct.setString(3, product.getUnit());
                pstmtProduct.setInt(4, product.getMinStock());
                pstmtProduct.setString(5, product.getCompany());
                pstmtProduct.executeUpdate();
            }

            // 2. Insert into specific subclass table based on category
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

            conn.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
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