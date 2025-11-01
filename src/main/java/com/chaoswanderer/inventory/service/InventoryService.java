package com.chaoswanderer.inventory.service;

import com.chaoswanderer.inventory.model.Inventory;
import com.chaoswanderer.inventory.model.Product;
import com.chaoswanderer.inventory.util.InventoryUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InventoryService {

    private final Inventory inventory;

    public InventoryService(Inventory inventory) {
        this.inventory = inventory;
    }

    public boolean addProductsFromFile(String filename) {
        Path path = Paths.get(filename);

        if (!Files.exists(path)) {
            System.out.println("Error: File '" + filename + "' not found!");
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String id = InventoryUtils.sanitizeString(parts[0]);
                String name = InventoryUtils.sanitizeStringName(parts[1]);
                int quantity = Integer.parseInt(InventoryUtils.sanitizeString(parts[2]));
                BigDecimal price = InventoryUtils.toPrice(parts[3]);

                Product product = new Product(id, name, quantity, price);
                inventory.addProduct(product);
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    public boolean saveProductsToFile() {
        Path path = Paths.get("data/products.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Product product : inventory.getAllProducts()) {
                writer.write(String.format("%s,%s,%d,%s%n",
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice().toPlainString()));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        }
    }
}
