package com.clothingstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillDetails extends JFrame {

    private final int saleId;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel;

    public BillDetails(int saleId) {
        this.saleId = saleId;

        setTitle("Bill Details - Bill ID: " + saleId);
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("BILL DETAILS - BILL ID: " + saleId,
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        String[] columns = {
                "Product ID", "Product Name", "Quantity", "Price (₹)", "Subtotal (₹)"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable detailsTable = new JTable(tableModel);
        detailsTable.setRowHeight(25);

        totalLabel = new JLabel("Bill Total: ₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(totalLabel);
        bottomPanel.add(refreshButton);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(detailsTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadBillDetails());

        loadBillDetails();
    }

    private void loadBillDetails() {
        tableModel.setRowCount(0);

        String sql = "SELECT product_id, product_name, quantity, price, subtotal " +
                "FROM sale_items WHERE sale_id = ? ORDER BY sale_item_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object productId = rs.getObject("product_id");

                    tableModel.addRow(new Object[]{
                            productId == null ? "Deleted" : productId,
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price"),
                            rs.getBigDecimal("subtotal")
                    });
                }
            }

            String totalSql = "SELECT total_amount FROM sales WHERE sale_id = ?";

            try (PreparedStatement totalPs = con.prepareStatement(totalSql)) {
                totalPs.setInt(1, saleId);

                try (ResultSet rs = totalPs.executeQuery()) {
                    if (rs.next()) {
                        totalLabel.setText("Bill Total: ₹" + rs.getBigDecimal("total_amount"));
                    } else {
                        totalLabel.setText("Bill not found.");
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Could not load bill details: " + e.getMessage());
        }
    }
}