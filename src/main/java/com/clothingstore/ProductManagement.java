package com.clothingstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductManagement extends JFrame {
    JTextField[] fields = new JTextField[6];
    String[] labels = {"Product Name", "Category", "Size",
            "Color", "Price (₹)", "Quantity"};
    DefaultTableModel model;
    JTable table;
    ProductDAO dao = new ProductDAO();
    Color red = new Color(220, 25, 45);
    Color black = new Color(18, 18, 18);

    public ProductManagement() {
        setTitle("STREET WEAR - Products");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(black);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "STREET WEAR - PRODUCT MANAGEMENT",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(red);
        title.setBorder(BorderFactory.createEmptyBorder(
                20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBackground(black);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(red),
                "PRODUCT DETAILS", 0, 0,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE));

        for (int i = 0; i < fields.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setForeground(Color.WHITE);
            form.add(label);

            fields[i] = new JTextField();
            fields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            form.add(fields[i]);
        }

        String[] names = {"Add Product", "Update Product",
                "Delete Product", "Refresh Table", "Clear Fields"};

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));
        buttons.setBackground(black);
        JButton[] b = new JButton[names.length];

        for (int i = 0; i < b.length; i++) {
            b[i] = new JButton(names[i]);
            b[i].setBackground(
                    i == 0 ? red : new Color(45, 45, 45));
            b[i].setForeground(Color.WHITE);
            b[i].setFocusPainted(false);
            buttons.add(b[i]);
        }

        JPanel left = new JPanel(new BorderLayout(10, 15));
        left.setBackground(black);
        left.setBorder(BorderFactory.createEmptyBorder(
                10, 15, 15, 5));
        left.add(form, BorderLayout.CENTER);
        left.add(buttons, BorderLayout.SOUTH);
        add(left, BorderLayout.WEST);

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Category", "Size",
                        "Color", "Price (₹)", "Quantity"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.getTableHeader().setBackground(red);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(120, 25, 35));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(
                10, 5, 15, 15));
        add(scroll, BorderLayout.CENTER);

        b[0].addActionListener(e -> saveProduct(false));
        b[1].addActionListener(e -> saveProduct(true));
        b[2].addActionListener(e -> deleteProduct());
        b[3].addActionListener(e -> loadProducts());
        b[4].addActionListener(e -> clearFields());

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && r >= 0) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText(String.valueOf(
                            model.getValueAt(r, i + 1)));
                }
            }
        });

        loadProducts();
    }

    private void saveProduct(boolean update) {
        int row = table.getSelectedRow();

        if (update && row < 0) {
            message("Select a product first.");
            return;
        }

        try {
            for (JTextField f : fields) {
                if (f.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Fill in all fields.");
                }
            }

            double price = Double.parseDouble(
                    fields[4].getText().trim());
            int qty = Integer.parseInt(
                    fields[5].getText().trim());

            if (!Double.isFinite(price) || price <= 0 || qty < 0) {
                throw new IllegalArgumentException(
                        "Price must be positive and quantity non-negative.");
            }

            int id = update
                    ? Integer.parseInt(
                    model.getValueAt(row, 0).toString())
                    : 0;

            Product p = new Product(id,
                    fields[0].getText().trim(),
                    fields[1].getText().trim(),
                    fields[2].getText().trim(),
                    fields[3].getText().trim(),
                    price, qty);

            boolean ok = update
                    ? dao.updateProduct(p)
                    : dao.addProduct(p);

            if (ok) {
                message(update
                        ? "Product updated successfully!"
                        : "Product added successfully!");
                clearFields();
                loadProducts();
            } else {
                message("Operation failed. Check ProductDAO.");
            }

        } catch (IllegalArgumentException ex) {
            message(ex.getMessage());
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();

        if (row < 0) {
            message("Select a product first.");
            return;
        }

        int id = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        if (JOptionPane.showConfirmDialog(
                this, "Delete this product?", "Confirm",
                JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {

            if (dao.deleteProduct(id)) {
                message("Product deleted successfully!");
                clearFields();
                loadProducts();
            } else {
                message("Delete failed.");
            }
        }
    }

    private void clearFields() {
        for (JTextField f : fields) {
            f.setText("");
        }
        table.clearSelection();
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> products = dao.getAllProducts();

        for (Product p : products) {
            model.addRow(new Object[]{
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getSize(),
                    p.getColor(),
                    p.getPrice(),
                    p.getQuantity()
            });
        }
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ProductManagement().setVisible(true));
    }
}
