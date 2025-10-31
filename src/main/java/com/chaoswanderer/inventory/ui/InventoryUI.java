package com.chaoswanderer.inventory.ui;

import com.chaoswanderer.inventory.model.Inventory;
import com.chaoswanderer.inventory.model.Product;
import com.chaoswanderer.inventory.util.PriceUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class InventoryUI {
    private final Inventory inventory;
    private Scanner scanner;

    public InventoryUI(Inventory inventory, Scanner scanner) {
        this.inventory = inventory;
        this.scanner = scanner;
    }

    // fake clear console
    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public void start() {
        while (true) {
            clearConsole();
            System.out.println("==================================================================");
            System.out.println("                    INVENTORY MANAGEMENT SYSTEM                   ");
            System.out.println("==================================================================");
            System.out.println("[1] View Products");
            System.out.println("[2] Add Product");
            System.out.println("[3] Update Product");
            System.out.println("[4] Remove Product");
            System.out.println("[5] Search Products");
            System.out.println("[6] View Summary");
            System.out.println("[0] Exit");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Select an option: ");
            String choice = sanitizeString(scanner.nextLine());

            if (choice.equals("0")) {
                break;
            }

            handleCommand(choice);
        }

        exitProgram();
    }

    private void handleCommand(String choice) {
        switch (choice) {
            case "1" -> listAllProducts(inventory.getAllProducts());
            case "2" -> addProduct();
            case "3" -> updateProduct();
            case "4" -> removeProduct();
            case "5" -> searchProducts();
            case "6" -> displaySummary();
            default -> {
                System.out.println("\nInvalid input - try again!");
                Thread.sleep(2000);
            }
        }

    }

    /* ------------------------------------- List All Products ------------------------------------- */
    private void listAllProducts(List<Product> products) {
        // display products
        clearConsole();
        boolean viewing = true;

        while (viewing) {
            displayProductList(products);

            String choice = handleListMenu();

            switch (choice) {
                case "1" -> {
                    clearConsole();
                    products = handleSortOptions(products);
                }
                case "2" -> viewing = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option - Returning to Main Menu...");
                    viewing = false;
                    pause();
                }
            }
        }
    }

    // prints all products in a table format
    private void displayProductList(List<Product> products) {
        System.out.println("------------------------------------------------------------------");
        System.out.println("CURRENT INVENTORY");
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-10s | %-35s | %-4s | %-10s%n", "ID", "Name", "Qty", "Price");
        System.out.println("------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-10s | %-35s | %-4d | $%-10s%n",
                    product.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice().toPlainString());
        }

        System.out.println("------------------------------------------------------------------");
    }

    // shows menu options while viewing products list
    private String handleListMenu() {
        System.out.println("[1] Sort Options");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Select an option: ");

        return sanitizeString(scanner.nextLine());
    }

    // handles sort options
    private List<Product> handleSortOptions(List<Product> products) {
        System.out.println("------------------------------------------------------------------");
        System.out.println("SORT OPTIONS");
        System.out.println("------------------------------------------------------------------");
        System.out.println("[1] Sort by Name (A-Z)");
        System.out.println("[2] Sort by Name (Z-A)");
        System.out.println("[3] Sort by Price (Lowest to Highest)");
        System.out.println("[4] Sort by Price (Highest to Lowest)");
        System.out.println("[5] Default");
//        System.out.println("[5] Sort by Date (Newest First)"); // will be added in Enhanced Inventory Management
//        System.out.println("[6] Sort by Date (Oldest First)"); // TODO sort by date
        System.out.println("[0] Exit");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Select an option: ");
        String choice = sanitizeString(scanner.nextLine());

        if (choice.equals("0")) {
            exitProgram();
        }

        return getSortedList(choice, products);
    }

    // returns the sorted list based on choice from handleSortOptions()
    private List<Product> getSortedList(String choice, List<Product> products) {
        return switch (choice) {
            case "1" -> inventory.sortByName(true);
            case "2" -> inventory.sortByName(false);
            case "3" -> inventory.sortByPrice(true);
            case "4" -> inventory.sortByPrice(false);
            case "5" -> products;
            default -> {
                System.out.println("Invalid option - Keeping current order.");
                yield products;
            }
        };
    }

    /* ------------------------------------- Add Products ---------------------------------------- */
    private void addProduct() {
        clearConsole();
        boolean adding = true;

        while (adding) {
            displayAddProductHeader();

            Product product = promptForProductDetails();

            if (inventory.addProduct(product)) {
                System.out.println("Product '" + product.getName() + "' added successfully!");
            } else {
                System.out.println("Product already exists!");
            }

            pause();
            System.out.println();

            String choice = displayAddProductMenu();

            switch (choice) {
                case "1" -> {
                }
                case "2" -> adding = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option - Returning to Main Menu...");
                    pause();
                    adding = false;
                }
            }
        }
    }

    private void displayAddProductHeader() {
        System.out.println("------------------------------------------------------------------");
        System.out.println("ADD PRODUCT");
        System.out.println("------------------------------------------------------------------");
    }

    private Product promptForProductDetails() {
        System.out.print("Enter Product ID: ");
        String id = sanitizeString(scanner.nextLine());

        System.out.print("Enter Product Name: ");
        String name = sanitizeString(scanner.nextLine());

        System.out.print("Enter Quantity: ");
        int quantity = readIntInput("quantity");

        System.out.print("Enter Price: ");
        BigDecimal price = readPriceInput();

        return new Product(id, name, quantity, price);
    }

    private String displayAddProductMenu() {
        System.out.println("------------------------------------------------------------------");
        System.out.println("[1] Add Another Product");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Select an option: ");
        return sanitizeString(scanner.nextLine());
    }

    // prevents invalid inputs for quantity field
    private int readIntInput(String fieldName) {
        while (true) {
            try {
                String input = sanitizeString(scanner.nextLine());
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid " + fieldName + " — please enter a number: ");
            }
        }
    }

    //  prevents invalid inputs for price field
    private BigDecimal readPriceInput() {
        while (true) {
            try {
                String input = sanitizeString(scanner.nextLine());
                return PriceUtils.toPrice(input);
            } catch (Exception e) {
                System.out.print("Invalid price — please enter again: ");
            }
        }
    }

    /* ------------------------------------- Update Products ------------------------------------- */
    private void updateProduct() throws InterruptedException {
        boolean updating = true;

        while (updating) {
            clearConsole();
            System.out.println("------------------------------------------------------------------");
            System.out.println("UPDATE PRODUCT");
            System.out.println("------------------------------------------------------------------");

            System.out.print("Enter Product ID to update (press Enter to return): ");
            String id = sanitizeString(scanner.nextLine());

            if (id.isEmpty()) {
                break;
            }

            System.out.println();
            Product product = inventory.searchProductById(id);

            if (product == null) {
                System.out.println("Product ID " + "'" + id + "'" + " does not exists!");
                System.out.println("Returning...");
                Thread.sleep(2000);
                continue;
            }

            System.out.println("------------------------------------------------------------------");
            System.out.println("Current Information:\n");

            System.out.println("Product ID: " + product.getId());
            System.out.println("Product Name: " + product.getName());
            System.out.println("Quantity: " + product.getQuantity());
            System.out.println("Price: " + "$" + product.getPrice());

            System.out.println();

            // store the new details
            System.out.println("Enter new quantity (or press Enter to skip): ");
            String newQuantityInput = sanitizeString(scanner.nextLine());
            int newQuantity = 0;
            if (!(newQuantityInput.isEmpty())) {
                newQuantity = Integer.parseInt(newQuantityInput);
            }

            System.out.println("Enter new price (or press Enter to skip): ");
            String newPriceInput = sanitizeString(scanner.nextLine());
            BigDecimal newPrice = null;
            if (!(newPriceInput.isEmpty())) {
                newPrice = PriceUtils.toPrice(newPriceInput);
            }

            // update product details
            if (newQuantity != 0) {
                product.setQuantity(newQuantity);
            }

            if (newPrice != null) {
                product.setPrice(newPrice);
            }

            System.out.println("------------------------------------------------------------------");
            System.out.println("Product " + "'" + product.getName() + "' " + "updated successfully!");
            Thread.sleep(2000);
            System.out.println();
            System.out.println("[1] Update Another Product");
            System.out.println("[2] Return to Main Menu");
            System.out.println("[3] Exit Program");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Select an option: ");
            String choice = sanitizeString(scanner.nextLine());

            switch (choice) {
                case "1" -> {
                }
                case "2" -> updating = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option - Returning to Main Menu...");
                    Thread.sleep(2000);
                    updating = false;
                }
            }
        }

    }

    /* ------------------------------------- Remove Products ------------------------------------- */
    private void removeProduct() throws InterruptedException {
        boolean removing = true;

        while (removing) {
            clearConsole();
            System.out.println("------------------------------------------------------------------");
            System.out.println("REMOVE PRODUCT");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Enter Product ID to remove (press Enter to return): ");

            String id = sanitizeString(scanner.nextLine());

            System.out.println();
            Product product = inventory.searchProductById(id);

            if (product == null) {
                System.out.println("Product ID " + "'" + id + "'" + " does not exists!");
                System.out.println("Returning...");
                Thread.sleep(2000);
                continue;
            }

            if (id.isEmpty()) {
                break;
            }

            System.out.print("Are you sure you want to remove '" + product.getName() + "' ? (y/n): ");
            String choice = sanitizeString(scanner.nextLine());

            if (choice.equals("y")) {
                this.inventory.removeProduct(id);
            } else {
                System.out.println("Returning...");
                Thread.sleep(2000);
                break;
            }

            System.out.println("------------------------------------------------------------------");
            System.out.println("Product removed successfully!");
            Thread.sleep(2000);
            System.out.println();
            System.out.println("[1] Remove Another Product");
            System.out.println("[2] Return to Main Menu");
            System.out.println("[3] Exit Program");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Select an option: ");
            String input = sanitizeString(scanner.nextLine());

            switch (input) {
                case "1" -> {
                }
                case "2" -> removing = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option - Returning to Main Menu...");
                    Thread.sleep(2000);
                    removing = false;
                }
            }
        }
    }

    /* ------------------------------------- Search Products ------------------------------------- */
    private void searchProducts() throws InterruptedException {
        boolean searching = true;
        while (searching) {
            clearConsole();

            System.out.println("------------------------------------------------------------------");
            System.out.println("SEARCH PRODUCTS");
            System.out.println("------------------------------------------------------------------");

            if (inventory.getInventory().isEmpty() || inventory.getInventory() == null) {
                System.out.println("The inventory is empty.");
                System.out.println("Returning...");
                Thread.sleep(2000);
                break;
            }

            System.out.print("Enter keyword: ");
            String keyword = sanitizeString(scanner.nextLine());

            if (keyword.isEmpty()) {
                break;
            }

            System.out.println("------------------------------------------------------------------");
            System.out.println("Results:");
            System.out.println("------------------------------------------------------------------");
            System.out.printf("%-10s | %-35s | %-4s | %-10s%n", "ID", "Name", "Qty", "Price");
            System.out.println("------------------------------------------------------------------");

            List<Product> products = inventory.searchProductsbyName(keyword);

            if (products.isEmpty()) {
                System.out.println("No products found");
                System.out.println("Returning...");
                Thread.sleep(2000);
                break;
            }

            for (Product product : products) {
                System.out.printf("%-10s | %-35s | %-4d | $%-10s%n",
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getPrice().toPlainString());
            }

            System.out.println("------------------------------------------------------------------");
            System.out.println("[1] Search Again");
            System.out.println("[2] Return to Main Menu");
            System.out.println("[3] Exit Program");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Select an option: ");
            String choice = sanitizeString(scanner.nextLine());

            switch (choice) {
                case "1" -> {
                }
                case "2" -> searching = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option - Returning to Main Menu...");
                    Thread.sleep(2000);
                    searching = false;
                }
            }
        }
    }

    /* ------------------------------------- Display Summary ------------------------------------- */
    private void displaySummary() throws InterruptedException {
        clearConsole();
        System.out.println("------------------------------------------------------------------");
        System.out.println("INVENTORY SUMMARY");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Total Products: " + inventory.getTotalProducts());
        System.out.println("Total Stock Quantity: " + inventory.getTotalStockQuantity());
        System.out.println("Total Inventory Value: " + "$" + inventory.getTotalInventoryValue());
        System.out.println("------------------------------------------------------------------");
        System.out.println("[1] Return to Main Menu");
        System.out.println("[2] Exit Program");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Select an option: ");
        String choice = sanitizeString(scanner.nextLine());

        switch (choice) {
            case "1" -> {
            }
            case "2" -> exitProgram();
            default -> {
                System.out.println("Invalid option - Returning to Main Menu...");
                Thread.sleep(2000);
            }
        }
    }

    /* ------------------------------------- Utility Methods ------------------------------------- */
    private static String sanitizeString(String string) {
        if (string == null) {
            return "";
        }

        return string
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-zA-Z0-9\\s\\-]", "")
                .toLowerCase();
    }


    // pauses the console
    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void exitProgram() {
        System.out.println("\nSaving data... done!");
        System.out.println("Thank you for using Inventory Manager!");
        System.out.println("Program closing...");
        System.exit(0);
    }
}
