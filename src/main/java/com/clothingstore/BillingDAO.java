
package com.clothingstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.Map;

public class BillingDAO {

    public int createBill(Map<Integer, Integer> cart) throws SQLException {
        if (cart == null || cart.isEmpty()) {
            throw new SQLException("The cart is empty.");
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                BigDecimal total = BigDecimal.ZERO;

                String stockSql =
                        "SELECT product_name, price, quantity " +
                                "FROM products WHERE product_id=? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                    for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                        int productId = item.getKey();
                        int quantity = item.getValue();

                        if (quantity <= 0) {
                            throw new SQLException("Invalid quantity.");
                        }

                        ps.setInt(1, productId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "Product not found: " + productId);
                            }

                            int stock = rs.getInt("quantity");
                            BigDecimal price = rs.getBigDecimal("price");

                            if (quantity > stock) {
                                throw new SQLException(
                                        "Insufficient stock for " +
                                                rs.getString("product_name"));
                            }

                            total = total.add(
                                    price.multiply(BigDecimal.valueOf(quantity)));
                        }
                    }
                }

                int saleId;

                String saleSql =
                        "INSERT INTO sales (total_amount) VALUES (?)";

                try (PreparedStatement ps = con.prepareStatement(
                        saleSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setBigDecimal(1, total);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not create bill.");
                        }
                        saleId = keys.getInt(1);
                    }
                }

                String itemSql =
                        "INSERT INTO sale_items " +
                                "(sale_id, product_id, product_name, quantity, price, subtotal) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

                String updateStockSql =
                        "UPDATE products SET quantity = quantity - ? " +
                                "WHERE product_id = ? AND quantity >= ?";

                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                    int productId = item.getKey();
                    int quantity = item.getValue();

                    try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                        ps.setInt(1, productId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Product not found.");
                            }

                            String name = rs.getString("product_name");
                            BigDecimal price = rs.getBigDecimal("price");
                            BigDecimal subtotal = price.multiply(
                                    BigDecimal.valueOf(quantity));

                            try (PreparedStatement insert =
                                         con.prepareStatement(itemSql)) {
                                insert.setInt(1, saleId);
                                insert.setInt(2, productId);
                                insert.setString(3, name);
                                insert.setInt(4, quantity);
                                insert.setBigDecimal(5, price);
                                insert.setBigDecimal(6, subtotal);
                                insert.executeUpdate();
                            }
                        }
                    }

                    try (PreparedStatement ps =
                                 con.prepareStatement(updateStockSql)) {
                        ps.setInt(1, quantity);
                        ps.setInt(2, productId);
                        ps.setInt(3, quantity);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Stock update failed for product " + productId);
                        }
                    }
                }

                con.commit();
                return saleId;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}