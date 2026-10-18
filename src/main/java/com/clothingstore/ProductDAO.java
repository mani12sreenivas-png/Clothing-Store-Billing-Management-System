
package com.clothingstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products " +
                "(product_name, category, size, color, price, quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getProductName());
            ps.setString(2, product.getCategory());
            ps.setString(3, product.getSize());
            ps.setString(4, product.getColor());
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getQuantity());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products ORDER BY product_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("size"),
                        rs.getString("color"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name=?, category=?, " +
                "size=?, color=?, price=?, quantity=? WHERE product_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getProductName());
            ps.setString(2, product.getCategory());
            ps.setString(3, product.getSize());
            ps.setString(4, product.getColor());
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getQuantity());
            ps.setInt(7, product.getProductId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
