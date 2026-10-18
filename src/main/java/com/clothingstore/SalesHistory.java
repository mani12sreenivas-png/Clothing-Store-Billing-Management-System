package com.clothingstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SalesHistory extends JFrame {

    DefaultTableModel model;
    JTable table;
    JLabel totalLabel;

    Color red = new Color(220, 25, 45);
    Color black = new Color(18, 18, 18);

    public SalesHistory() {
        setTitle("STREET WEAR - Sales History");
        setSize(950, 600);
        setMinimumSize(new Dimension(750, 450));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel(
                "STREET WEAR - SALES HISTORY",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(red);
        title.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Bill ID", "Sale Date", "Total Amount (₹)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(new Color(120, 25, 35));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setBackground(red);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(black);
        scroll.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15));
        add(scroll, BorderLayout.CENTER);

        totalLabel = new JLabel("Total Sales: ₹0.00");
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refresh = button("REFRESH", red);
        JButton details = button(
                "VIEW BILL DETAILS", new Color(45, 45, 45));

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(black);
        bottom.add(totalLabel);
        bottom.add(refresh);
        bottom.add(details);
        add(bottom, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadSales());
        details.addActionListener(e -> openBillDetails());

        loadSales();
    }

    JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    void loadSales() {
        model.setRowCount(0);
        double total = 0;

        String sql = "SELECT sale_id, sale_date, total_amount "
                + "FROM sales ORDER BY sale_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double amount = rs.getDouble("total_amount");
                total += amount;

                model.addRow(new Object[]{
                        rs.getInt("sale_id"),
                        rs.getTimestamp("sale_date"),
                        String.format("%.2f", amount)
                });
            }

            totalLabel.setText(
                    String.format("Total Sales: ₹%.2f", total));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load sales: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void openBillDetails() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a bill first.");
            return;
        }

        int id = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        new BillDetails(id).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new SalesHistory().setVisible(true));
    }
}
