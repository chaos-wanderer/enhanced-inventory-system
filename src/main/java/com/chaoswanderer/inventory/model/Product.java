package com.chaoswanderer.inventory.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product {
    private String id;
    private String name;
    private int quantity;
    private BigDecimal price;

    public Product(String id, String name, int quantity, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            return;
        }

        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount < 0) {
            return;
        }

        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            return;
        }

        this.quantity -= amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Business methods
    public BigDecimal totalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Overrides
    @Override
    public String toString() {
        return (id + " | " + name + " | Qty: " + quantity + " | $" + price.toPlainString());
    }

    @Override
    public boolean equals(Object comparedProduct) {
        if (this == comparedProduct) {
            return true;
        }

        if (!(comparedProduct instanceof Product)) {
            return false;
        }

        Product compared = (Product) comparedProduct;

        return (this.id.equals(compared.id));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

}
