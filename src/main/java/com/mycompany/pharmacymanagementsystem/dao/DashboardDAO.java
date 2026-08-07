/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem.dao;

import com.mycompany.pharmacymanagementsystem.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    // 1. عدد المنتجات (Products)
    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) FROM public.product";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 2. إجمالي المبيعات (Sales)
    public double getTotalSales() {
        String sql = "SELECT COALESCE(SUM(totalamount), 0) FROM public.sales_invoice";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    // 3. إجمالي قطع المخزون المتاحة (Stock)
    public int getTotalStockCount() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM public.batch";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    public Map<String, Double> getWeeklyProfitData() {
    Map<String, Double> profitMap = new LinkedHashMap<>();
    // تهيئة الأيام بقيم صفرية أولاً
    profitMap.put("Mon", 0.0);
    profitMap.put("Tue", 0.0);
    profitMap.put("Wed", 0.0);
    profitMap.put("Thu", 0.0);
    profitMap.put("Fri", 0.0);
    profitMap.put("Sat", 0.0);
    profitMap.put("Sun", 0.0);

    // استعلام يجلب المبيعات اليومية حسب اليوم لهذا الأسبوع
    String sql = "SELECT TO_CHAR(si.date, 'Dy') AS day_name, " +
                 "       COALESCE(SUM(si.totalamount), 0) AS total_sales " +
                 "FROM public.sales_invoice si " +
                 "WHERE si.date >= CURRENT_DATE - INTERVAL '6 days' " +
                 "GROUP BY TO_CHAR(si.date, 'Dy'), si.date " +
                 "ORDER BY si.date";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String day = rs.getString("day_name").trim(); // ترجع Mon, Tue, etc.
            double total = rs.getDouble("total_sales");
            
            if (profitMap.containsKey(day)) {
                profitMap.put(day, total);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return profitMap;
}
public ObservableList<String> getNotificationMessages() {
    ObservableList<String> notifications = FXCollections.observableArrayList();
    
    // 1. Expiry alerts (Expired or Near Expiry within 30 days)
    String expirySql = """
        SELECT p.tradename, b.expire_date 
        FROM public.product p
        JOIN public.batch b ON p.productcode = b.productcode
        WHERE b.expire_date <= CURRENT_DATE + INTERVAL '30 days'
        LIMIT 5;
        """;

    // 2. Low stock alerts (Quantity below minstocklevel)
    String stockSql = """
        SELECT p.tradename, COALESCE(SUM(b.quantity), 0) as total_qty, p.minstocklevel
        FROM public.product p
        LEFT JOIN public.batch b ON p.productcode = b.productcode
        GROUP BY p.productcode, p.tradename, p.minstocklevel
        HAVING COALESCE(SUM(b.quantity), 0) <= p.minstocklevel
        LIMIT 5;
        """;

    try (Connection conn = DatabaseConnection.getConnection()) {
        // Fetch Expiry Alerts
        try (PreparedStatement stmt = conn.prepareStatement(expirySql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notifications.add("⚠️ EXPIRY WARNING: Item (" + rs.getString("tradename") + ") expires on " + rs.getDate("expire_date"));
            }
        }
        
        // Fetch Low Stock Alerts
        try (PreparedStatement stmt = conn.prepareStatement(stockSql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notifications.add("📦 LOW STOCK ALERT: Item (" + rs.getString("tradename") + ") has only " + rs.getInt("total_qty") + " units left!");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    if (notifications.isEmpty()) {
        notifications.add("✅ ALL CLEAR: All inventory levels and expiration dates are optimal.");
    }

    return notifications;
}
}
