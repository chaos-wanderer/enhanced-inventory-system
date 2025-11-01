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
    private MenuState state = MenuState.MAIN_MENU;

    public InventoryUI(Inventory inventory, Scanner scanner) {
        this.inventory = inventory;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            switch (state) {
                case MAIN_MENU -> displayMainMenu();
                case ADD_PRODUCT -> addProduct();
                case UPDATE_PRODUCT -> updateProduct();
//                case REMOVE_PRODUCT -> removeProduct();
//                case SEARCH_PRODUCT -> searchProduct();
//                case DISPLAY_SUMMARY -> displaySummary();
                case EXIT_PROGRAM -> {
                    exitProgram();
                    return;
                }
            }
        }
    }

    private void displayMainMenu() {
        clearConsole();
        System.out.println("==================================================================");
        System.out.println("                    INVENTORY MANAGEMENT SYSTEM                   ");
        System.out.println("==================================================================");
        System.out.println("[1] View Products");
        System.out.println("[2] Add Product");
        System.out.println("[3] Update Product");
        System.out.println("[4] Remove Product");
        System.out.println("[5] Search Product");
        System.out.println("[6] View Summary");
        System.out.println("[0] Exit");
        printSeparator();
        System.out.print("Select an option: ");
        String choice = sanitizeString(scanner.nextLine());

        if (choice.equals("0")) {
            state = MenuState.EXIT_PROGRAM;
            return;
        }

        handleCommand(choice);
    }

    private void handleCommand(String choice) {
        switch (choice) {
            case "1" -> listAllProducts(inventory.getAllProducts());
            case "2" -> addProduct();
            case "3" -> updateProduct();
//            case "4" -> removeProduct();
//            case "5" -> searchProducts();
//            case "6" -> displaySummary();
            default -> {
                System.out.println("\nInvalid input - Returning...");
                pause();
            }
        }

    }

    // region List All Products
    /* ------------------------------------- List All Products ------------------------------------- */
    private void listAllProducts(List<Product> products) {
        // display products
        clearConsole();
        boolean viewing = true;

        while (viewing) {

            if (products == null) {
                break;
            }

            displayProductList(products);

            String choice = handleListMenu();

            switch (choice) {
                case "1" -> {
                    clearConsole();
                    products = handleSortOptions(products);
                    state = MenuState.VIEW_PRODUCTS;
                }
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> state = MenuState.EXIT_PROGRAM;
                default -> {
                    state = MenuState.MAIN_MENU;
                    System.out.println("Invalid option - Returning to Main Menu...");
                    pause();
                }
            }

            if (state != MenuState.VIEW_PRODUCTS) {
                viewing = false;
            }
        }
    }

    // prints all products in a table format
    private void displayProductList(List<Product> products) {
        printHeader("CURRENT INVENTORY");
        System.out.printf("%-10s | %-35s | %-4s | %-10s%n", "ID", "Name", "Qty", "Price");
        printSeparator();

        for (Product product : products) {
            System.out.printf("%-10s | %-35s | %-4d | $%-10s%n",
                    product.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice().toPlainString());
        }

        printSeparator();
    }

    // shows menu options while viewing products list
    private String handleListMenu() {
        System.out.println("[1] Sort Options");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        printSeparator();
        System.out.print("Select an option: ");

        return sanitizeString(scanner.nextLine());
    }

    // handles sort options
    private List<Product> handleSortOptions(List<Product> products) {
        printSeparator();
        System.out.println("SORT OPTIONS");
        printSeparator();
        System.out.println("[1] Sort by Name (A-Z)");
        System.out.println("[2] Sort by Name (Z-A)");
        System.out.println("[3] Sort by Price (Lowest to Highest)");
        System.out.println("[4] Sort by Price (Highest to Lowest)");
        System.out.println("[5] Default");
        System.out.println("[6] Return to Main Menu");
//        System.out.println("[5] Sort by Date (Newest First)"); // will be added in Enhanced Inventory Management
//        System.out.println("[6] Sort by Date (Oldest First)"); // TODO sort by date
        System.out.println("[0] Exit Program");
        printSeparator();
        System.out.print("Select an option: ");
        String choice = sanitizeString(scanner.nextLine());

        if (choice.equals("0")) {
            exitProgram();
        }

        return getSortedList(choice, products);
    }

    // TODO SORT BY ID, AND DATE
    // returns the sorted list based on choice from handleSortOptions()
    private List<Product> getSortedList(String choice, List<Product> products) {
        return switch (choice) {
            case "1" -> inventory.sortByName(true);
            case "2" -> inventory.sortByName(false);
            case "3" -> inventory.sortByPrice(true);
            case "4" -> inventory.sortByPrice(false);
            case "5" -> inventory.getAllProducts();
            case "6" -> null; // Return to Main Menu option
            default -> {
                System.out.println("Invalid option — Keeping current order.");
                pause();
                yield inventory.getAllProducts();
            }
        };
    }
    // endregion

    // region Add Products
    /* ------------------------------------- Add Products ---------------------------------------- */
    private void addProduct() {
        boolean adding = true;

        while (adding) {
            clearConsole();
            printHeader("ADD PRODUCT");

            Product product = promptForProductDetails();
            if (product == null) {
                state = MenuState.MAIN_MENU;
                return;
            }

            printSeparator();

            if (inventory.addProduct(product)) {
                System.out.println("Product '" + product.getName() + "' added successfully!");
            } else {
                System.out.println("Product already exists!");
            }

            System.out.println();
            pause();
            clearConsole();

            String choice = displayAddAnotherProductMenu();

            switch (choice) {
                case "1" -> state = MenuState.ADD_PRODUCT;
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> state = MenuState.EXIT_PROGRAM;
                default -> {
                    System.out.println("\nInvalid option - Returning to Main Menu...");
                    state = MenuState.MAIN_MENU;
                    pause();
                }
            }

            if (state != MenuState.ADD_PRODUCT) {
                adding = false;
            }
        }
    }

    // TODO add press enter to skip?
    private Product promptForProductDetails() {
        System.out.print("Enter Product ID (press Enter to return): ");
        String id = sanitizeString(scanner.nextLine());
        if (id.isEmpty()) {
            return null;
        }

        System.out.print("Enter Product Name (press Enter to return): ");
        String name = sanitizeString(scanner.nextLine());
        if (name.isEmpty()) {
            return null;
        }

        System.out.print("Enter Quantity (press Enter to return): ");
        int quantity = readIntInput("quantity");
        if (quantity == -1) {
            return null;
        }

        System.out.print("Enter Price (press Enter to return): ");
        BigDecimal price = readPriceInput();
        if (price == null) {
            return null;
        }

        return new Product(id, name, quantity, price);
    }

    private String displayAddAnotherProductMenu() {
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
                if (input.isEmpty()) {
                    return -1;
                }

                if (Integer.parseInt(input) < 0) {
                    throw new NumberFormatException("Negative numbers are not allowed!");
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid " + fieldName + " — please enter again: ");
            }
        }
    }

    //  prevents invalid inputs for price field
    private BigDecimal readPriceInput() {
        while (true) {
            try {
                String input = sanitizeString(scanner.nextLine());

                if (input.isEmpty()) {
                    return null;
                }

                if (Integer.parseInt(input) < 0) {
                    throw new Exception("Negative numbers are not allowed!");
                }

                return PriceUtils.toPrice(input);
            } catch (Exception e) {
                System.out.print("Invalid price — please enter again: ");
            }
        }
    }
    // endregion

    /* ------------------------------------- Update Products ------------------------------------- */
    private void updateProduct() {
        boolean updating = true;

        while (updating) {
            clearConsole();

            printHeader("UPDATE PRODUCT");

            System.out.print("Enter Product ID to update (press Enter to return): ");
            String id = sanitizeString(scanner.nextLine());
            System.out.println();
            if (id.isEmpty()) {
                System.out.println("Returning to Main Menu...");
                state = MenuState.MAIN_MENU;
                break;
            }

            Product product = inventory.searchProductById(id);

            if (product == null) {
                System.out.println("Product not found!");
                pause();
                continue;
            }

            printCurrentInformation(product);

            System.out.println();
            boolean updateSuccessfully = displayUpdateOptions(product);


            if (!updateSuccessfully) { // returns to Main Menu if user choose to do so, or if invalid input were used
                return;
            }

            printCurrentInformation(product);

            clearConsole();
            printSeparator();
            System.out.println("[1] Update Another Product");
            System.out.println("[2] Return to Main Menu");
            System.out.println("[3] Exit Program");
            printSeparator();
            System.out.print("Select an option: ");
            String choice = sanitizeString(scanner.nextLine());

            switch (choice) {
                case "1" -> {
                }
                case "2" -> updating = false;
                case "3" -> exitProgram();
                default -> {
                    System.out.println("Invalid option — Returning to Main Menu...");
                    pause();
                    updating = false;
                }
            }
        }
    }

    private void printCurrentInformation(Product product) {
        printSeparator();
        System.out.println("Current Information:");
        printSeparator();
        System.out.println("Product ID: " + product.getId());
        System.out.println("Product Name: " + product.getName());
        System.out.println("Quantity: " + product.getQuantity());
        System.out.println("Price: " + "$" + product.getPrice());
        printSeparator();
        pause();
    }

    private boolean displayUpdateOptions(Product product) {
        // store the new details
        printHeader("SELECT FIELD TO UPDATE");
        System.out.println("[1] Update name");
        System.out.println("[2] Update price");
        System.out.println("[3] Update quantity");
        System.out.println("[4] Increase quantity");
        System.out.println("[5] Decrease quantity");
        System.out.println("[6] Return to Main Menu");
        System.out.println("[0] Exit Program");
        printSeparator();
        System.out.print("Select an option: ");
        String choice = sanitizeString(scanner.nextLine());
        printSeparator();

        switch (choice) {
            case "1" -> updateProductName(product);
            case "2" -> updateProductPrice(product);
            case "3" -> updateProductQuantity(product);
            case "4" -> increaseProductQuantity(product);
            case "5" -> decreaseProductQuantity(product);
            case "6" -> {
                return false;
            }
            case "0" -> exitProgram();
            default -> {
                System.out.println("Invalid option – Returning to Main Menu...");
                pause();
                return false;
            }
        }

        System.out.println();
        pause();

        return true;
    }

    private void updateProductName(Product product) {
        System.out.print("Enter new product name (press Enter to skip): ");
        String newName = sanitizeStringName(scanner.nextLine());
        if (newName.isEmpty()) {
            return;
        }

        System.out.println();

        product.setName(newName);
        System.out.println("Product [" + product.getId() + "] name updated to '" + product.getName() + "'.");
    }

    private void updateProductPrice(Product product) {
        System.out.print("Enter new product price (press Enter to skip): ");
        BigDecimal newPrice = readPriceInput();

        if (newPrice == null) {
            return;
        }

        System.out.println();

        product.setPrice(newPrice);
        System.out.println("Product [" + product.getId() + "] price updated to '$" + product.getPrice() + "'.");
    }

    private void updateProductQuantity(Product product) {
        System.out.print("Enter new quantity (press Enter to skip): ");
        int newQuantity = readIntInput("quantity");

        if (newQuantity == -1) {
            return;
        }

        System.out.println();

        product.setQuantity(newQuantity);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
    }

    private void increaseProductQuantity(Product product) {
        System.out.print("Enter quantity to add to current stock (press Enter to skip): ");
        int amount = readIntInput("quantity");

        if (amount == -1) {
            return;
        }

        System.out.println();

        product.increaseQuantity(amount);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
    }

    private void decreaseProductQuantity(Product product) {
        System.out.print("Enter quantity to subtract to current stock (press Enter to skip): ");
        int amount = readIntInput("quantity");

        if (amount == -1) {
            return;
        }

        System.out.println();

        product.decreaseQuantity(amount);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
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
    private void searchProduct() throws InterruptedException {
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

    // String sanitizer for names - to keep chosen capitalization
    private static String sanitizeStringName(String string) {
        if (string == null) {
            return "";
        }

        return string
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-zA-Z0-9\\s\\-]", "");
    }

    private static void printSeparator() {
        System.out.println("------------------------------------------------------------------");
    }

    private static void printHeader(String header) {
        printSeparator();
        System.out.println(header);
        printSeparator();
    }

    // pauses the console
    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    // fake clear console
    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static void exitProgram() {
        System.out.println("\nSaving data... done!");
        System.out.println("Thank you for using Inventory Manager!");
        System.out.println("Program closing...");
        System.exit(0);
    }
}
