

package com.mycompany.pharmacymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProfitReportDAO {

   
public ObservableList<ProfitReportRow> getProfitReport(
        LocalDate startDate,
        LocalDate endDate) {

    ObservableList<ProfitReportRow> list =
            FXCollections.observableArrayList();

    String sql = """
            SELECT
                si.invoiceno,
                si.date,

                SUM(
                    sd.quantity * sd.selling_price
                ) AS sales,

                SUM(
                    sd.quantity * b.purchase_price
                ) AS cost,

                SUM(
                    sd.quantity *
                    (sd.selling_price - b.purchase_price)
                ) AS profit

            FROM sales_invoice si

            JOIN sale_detail sd
                ON si.invoiceno = sd.invoiceno

            JOIN batch b
                ON sd.batch_no = b.batch_no

            WHERE si.date >= ?
              AND si.date <= ?

            GROUP BY
                si.invoiceno,
                si.date

            ORDER BY
                si.date DESC
            """;

    System.out.println("=================================");
    System.out.println("PROFIT REPORT TEST");
    System.out.println("Start Date = " + startDate);
    System.out.println("End Date   = " + endDate);
    System.out.println("=================================");

    try (
            Connection conn =
                    DatabaseConnection.getConnection();

            PreparedStatement ps =
                    conn.prepareStatement(sql)
    ) {

        ps.setDate(
                1,
                java.sql.Date.valueOf(startDate)
        );

        ps.setDate(
                2,
                java.sql.Date.valueOf(endDate)
        );

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String invoiceNo =
                        rs.getString("invoiceno");

                LocalDate date =
                        rs.getDate("date").toLocalDate();

                double sales =
                        rs.getDouble("sales");

                double cost =
                        rs.getDouble("cost");

                double profit =
                        rs.getDouble("profit");

                System.out.println(
                        "Invoice: " + invoiceNo
                        + " | Date: " + date
                        + " | Sales: " + sales
                        + " | Cost: " + cost
                        + " | Profit: " + profit
                );

                list.add(
                        new ProfitReportRow(
                                invoiceNo,
                                date,
                                sales,
                                cost,
                                profit
                        )
                );
            }

        }

    } catch (SQLException ex) {

        ex.printStackTrace();
    }

    System.out.println(
            "Rows returned = " + list.size()
    );

    return list;
}

}
