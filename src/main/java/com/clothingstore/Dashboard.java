package com.clothingstore;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private final Color red = new Color(220, 25, 45);
    private final Color black = new Color(18, 18, 18);
    private final Color panelColor = new Color(30, 30, 30);

    public Dashboard() {
        setTitle("STREET WEAR - Management System");
        setSize(900, 650);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("STREET WEAR",SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(red);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JLabel subtitle = new JLabel("CLOTHING STORE MANAGEMENT SYSTEM",SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(Color.LIGHT_GRAY);

        JPanel header = new JPanel(new GridLayout(2, 1, 5, 5));
        header.setBackground(black);
        header.add(title);
        header.add(subtitle);
        add(header, BorderLayout.NORTH);

        JButton products = createButton("PRODUCT MANAGEMENT");
        JButton billing = createButton("BILLING MANAGEMENT");
        JButton statistics = createButton("STORE STATISTICS");
        JButton sales = createButton("SALES HISTORY");
        JButton exit = createButton("EXIT APPLICATION");

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBackground(black);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 150, 30, 150));

        buttonPanel.add(products);
        buttonPanel.add(billing);
        buttonPanel.add(statistics);
        buttonPanel.add(sales);
        buttonPanel.add(exit);

        add(buttonPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("STREET WEAR  |  STYLE YOUR WAY",SwingConstants.CENTER);
        footer.setForeground(Color.GRAY);
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setBorder( BorderFactory.createEmptyBorder(5, 10, 15, 10));
        add(footer, BorderLayout.SOUTH);

        products.addActionListener(e ->
                new ProductManagement().setVisible(true));

        billing.addActionListener(e ->
                new BillingManagement().setVisible(true));

        statistics.addActionListener(e ->
                new DashboardStatistics().setVisible(true));

        sales.addActionListener(e ->
                new SalesHistory().setVisible(true));

        exit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit STREET WEAR?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(red);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Dashboard().setVisible(true));
    }
}
