package com.chaoswanderer.inventory.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Product {
    private String id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String id, String name, int quantity, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedUpdatedAt() {
        return updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
