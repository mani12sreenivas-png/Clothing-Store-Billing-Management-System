package com.clothingstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class BillingManagement extends JFrame {

    JComboBox<Product> productCombo = new JComboBox<>();
    JTextField quantityField = new JTextField(5);
    DefaultTableModel model;
    JTable table;
    JLabel totalLabel = new JLabel("Total: ₹0.00");

    ProductDAO productDAO = new ProductDAO();
    BillingDAO billingDAO = new BillingDAO();

    Map<Integer, Integer> cart = new LinkedHashMap<>();
    Map<Integer, Product> cartProducts = new LinkedHashMap<>();

    Color red = new Color(220, 25, 45);
    Color black = new Color(18, 18, 18);

    public BillingManagement() {
        setTitle("STREET WEAR - Billing");
        setSize(950, 600);
        setMinimumSize(new Dimension(750, 450));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("STREET WEAR - CUSTOMER BILLING",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(red);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);
        JPanel top = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 15));
        top.setBackground(black);
        productCombo.setPreferredSize(new Dimension(280, 32));
        JButton add = button("ADD TO CART");
        JButton refresh = button("REFRESH PRODUCTS");
        top.add(label("Product:"));
        top.add(productCombo);
        top.add(label("Quantity:"));
        top.add(quantityField);
        top.add(add);
        top.add(refresh);
        model = new DefaultTableModel(
                new String[]{"ID", "Product", "Quantity",
                        "Price (₹)", "Subtotal (₹)"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(new Color(35, 35, 35));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(red);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(red);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(black);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(black);
        center.add(top, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.setBorder(BorderFactory.createEmptyBorder(
                0, 15, 10, 15));
        add(center, BorderLayout.CENTER);

        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton remove = button("REMOVE ITEM");
        JButton clear = button("CLEAR CART");
        JButton checkout = button("CHECKOUT");

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottom.setBackground(black);
        bottom.add(totalLabel);
        bottom.add(remove);
        bottom.add(clear);
        bottom.add(checkout);
        add(bottom, BorderLayout.SOUTH);

        add.addActionListener(e -> addToCart());
        refresh.addActionListener(e -> loadProducts());
        remove.addActionListener(e -> removeSelected());
        clear.addActionListener(e -> clearCart());
        checkout.addActionListener(e -> checkout());

        loadProducts();
    }

    JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    JButton button(String text) {
        JButton b = new JButton(text);
        b.setBackground(red);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    void loadProducts() {
        productCombo.removeAllItems();

        for (Product p : productDAO.getAllProducts()) {
            if (p.getQuantity() > 0) {
                productCombo.addItem(p);
            }
        }

        productCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {

                super.getListCellRendererComponent(
                        list, value, index, selected, focus);

                if (value instanceof Product p) {
                    setText(p.getProductName() + " - ₹" + p.getPrice()
                            + " (Stock: " + p.getQuantity() + ")");
                }
                return this;
            }
        });
    }
    void addToCart() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(
                    this, "No products in stock.");
            return;
        }

        try {
            int qty = Integer.parseInt(
                    quantityField.getText().trim());

            int id = p.getProductId();
            int newQty = cart.getOrDefault(id, 0) + qty;

            if (qty <= 0 || newQty > p.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid quantity or insufficient stock.");
                return;
            }

            cart.put(id, newQty);
            cartProducts.put(id, p);
            updateCart();
            quantityField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid whole-number quantity.");
        }
    }
    void updateCart() {
        model.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;

        for (int id : cart.keySet()) {
            Product p = cartProducts.get(id);
            int qty = cart.get(id);

            BigDecimal price = BigDecimal.valueOf(p.getPrice());
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            model.addRow(new Object[]{
                    id, p.getProductName(), qty, price, subtotal
            });
        }

        totalLabel.setText("Total: ₹" + total.setScale(
                2, java.math.RoundingMode.HALF_UP));
    }
    void removeSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Select an item first.");
            return;
        }

        int id = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        cart.remove(id);
        cartProducts.remove(id);
        updateCart();
    }
    void clearCart() {
        cart.clear();
        cartProducts.clear();
        updateCart();
    }
    void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Your cart is empty.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Confirm checkout?\n" + totalLabel.getText(),
                "STREET WEAR - Confirm Bill",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int saleId = billingDAO.createBill(
                    new LinkedHashMap<>(cart));

            JOptionPane.showMessageDialog(this,
                    "STREET WEAR\nBill created successfully!"
                            + "\nBill ID: " + saleId
                            + "\n" + totalLabel.getText());

            clearCart();
            loadProducts();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Checkout failed: " + e.getMessage());
            loadProducts();
        }
    }
 public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new BillingManagement().setVisible(true));
    }
}