package com.clothingstore;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;

public class DashboardStatistics extends JFrame {

    JLabel products = new JLabel("0");
    JLabel stock = new JLabel("0");
    JLabel sales = new JLabel("₹0.00");
    JLabel bills = new JLabel("0");

    Color red = new Color(220, 25, 45);
    Color black = new Color(18, 18, 18);

    public DashboardStatistics() {
        setTitle("Store Statistics");
        setSize(850, 550);
        setMinimumSize(new Dimension(650, 450));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("STORE STATISTICS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(red);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 18, 18));
        grid.setBackground(black);
        grid.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        grid.add(card("TOTAL PRODUCTS", products));
        grid.add(card("TOTAL STOCK", stock));
        grid.add(card("TOTAL SALES", sales));
        grid.add(card("BILLS GENERATED", bills));
        add(grid, BorderLayout.CENTER);

        JButton refresh = new JButton("REFRESH STATISTICS");
        refresh.setBackground(red);
        refresh.setForeground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> loadStatistics());

        JPanel bottom = new JPanel();
        bottom.setBackground(black);
        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);

        loadStatistics();
    }

    JPanel card(String title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(red), title,
                0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));

        value.setForeground(red);
        value.setFont(new Font("Arial", Font.BOLD, 30));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    void loadStatistics() {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM products) AS p, " +
                "(SELECT COALESCE(SUM(quantity),0) FROM products) AS q, " +
                "(SELECT COALESCE(SUM(total_amount),0) FROM sales) AS s, " +
                "(SELECT COUNT(*) FROM sales) AS b";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet r = ps.executeQuery()) {

            if (r.next()) {
                products.setText(String.valueOf(r.getInt("p")));
                stock.setText(String.valueOf(r.getLong("q")));

                BigDecimal amount = r.getBigDecimal("s");
                sales.setText("₹" + amount.setScale(
                        2, java.math.RoundingMode.HALF_UP));

                bills.setText(String.valueOf(r.getInt("b")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load statistics: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new DashboardStatistics().setVisible(true));
    }
}
