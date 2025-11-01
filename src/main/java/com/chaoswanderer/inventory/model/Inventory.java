package com.chaoswanderer.inventory.model;

import com.chaoswanderer.inventory.util.SortField;

import java.math.BigDecimal;
import java.util.*;

public class Inventory {
    private final Map<String, Product> inventory;

    public Inventory() {
        this.inventory = new HashMap<>();
    }

    // all functions related to inventory (add, remove, search) are associated with its ID instead of name

    public boolean addProduct(Product product) {
        if (this.inventory.containsKey(product.getId())) {
            return false;
        }

        this.inventory.put(product.getId(), product);
        return true;
    }

    public boolean removeProduct(String id) {
        if (!(this.inventory.containsKey(id))) {
            System.out.println("A product with id " + id + " does not exists in the inventory.");
            return false;
        }

        this.inventory.remove(id);
        return true;
    }

    public Product searchProductById(String id) {
        return this.inventory.getOrDefault(id, null);
    }

    public boolean productExists(Product product) {
        return searchProductById(product.getId()) != null;
    }

    public boolean productExists(String id) {
        return searchProductById(id) != null;
    }

    public List<Product> searchProductsById(String id) {
        List<Product> matches = new ArrayList<>();

        for (Product product : inventory.values()) {
            if (product.getId().toLowerCase().contains(id)) {
                matches.add(product);
            }
        }

        return matches;
    }

    public List<Product> searchProductsbyName(String name) {
        List<Product> matches = new ArrayList<>();

        for (Product product : inventory.values()) {
            if (product.getName().toLowerCase().contains(name)) {
                matches.add(product);
            }
        }

        return matches;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }

    // ------------------- Sort options -------------------
    public List<Product> sortBy(SortField field, boolean ascending) {
        Comparator<Product> comparator = switch (field) {
            case NAME -> Comparator.comparing(Product::getName);
            case PRICE -> Comparator.comparing(Product::getPrice);
            case CREATED_AT -> Comparator.comparing(Product::getCreatedAt);
            case UPDATED_AT -> Comparator.comparing(Product::getUpdatedAt);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return inventory.values().stream()
                .sorted(comparator)
                .toList();
    }


    // ------------------------------------------------------
    public int getTotalProducts() {
        return inventory.size();
    }

    public int getTotalStockQuantity() {
        return inventory.values().stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

    public BigDecimal getTotalInventoryValue() {
        return inventory.values().stream()
                .map(Product::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Product> getInventory() {
        return inventory;
    }
}
