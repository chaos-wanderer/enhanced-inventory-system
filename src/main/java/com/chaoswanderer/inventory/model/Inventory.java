package com.chaoswanderer.inventory.model;

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
    public List<Product> sortByName(boolean ascending) {
        return inventory.values().stream()
                .sorted(Comparator.comparing(Product::getName,
                        (ascending) ? Comparator.naturalOrder() : Comparator.reverseOrder()))
                .toList();
    }

    public List<Product> sortByPrice(boolean ascending) {
        return inventory.values().stream()
                .sorted(Comparator.comparing(Product::getPrice,
                        (ascending) ? Comparator.naturalOrder() : Comparator.reverseOrder()))
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
