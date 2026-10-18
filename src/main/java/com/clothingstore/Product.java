
package com.clothingstore;

public class Product {

    private int productId;
    private String productName;
    private String category;
    private String size;
    private String color;
    private double price;
    private int quantity;

    public Product() {
    }

    public Product(int productId, String productName,
                   String category, String size, String color,
                   double price, int quantity) {

        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
