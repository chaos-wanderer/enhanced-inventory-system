package com.chaoswanderer.inventory;

import com.chaoswanderer.inventory.model.Inventory;
import com.chaoswanderer.inventory.service.InventoryService;
import com.chaoswanderer.inventory.ui.InventoryUI;

import java.util.Scanner;

public class Main {

    @SuppressWarnings("checkstyle:OperatorWrap")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory();
        InventoryService inventoryService = new InventoryService(inventory);

        // shutdown safety net
        Runtime.getRuntime().addShutdownHook(new Thread(inventoryService::saveProductsToFile));

        loadFile(inventoryService, "data/products.csv");
        pause(scanner);

        //TODO add InventoryController?
        //TODO add createdAt and updatedAt in products.csv

        InventoryUI ui = new InventoryUI(inventory, inventoryService, scanner);
        ui.start();
    }

    private static void loadFile(InventoryService service, String filename) {
        boolean loaded = service.addProductsFromFile(filename);

        if (!loaded) {
            System.out.println("Warning: Could not load products from '" + filename + "' – Starting with empty inventory.");
        } else {
            System.out.println("Successfully loaded products from '" + filename + "'");
        }
    }

    private static void pause(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

}
