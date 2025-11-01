package com.chaoswanderer.inventory.ui;

import com.chaoswanderer.inventory.model.Inventory;
import com.chaoswanderer.inventory.model.Product;
import com.chaoswanderer.inventory.service.InventoryService;
import com.chaoswanderer.inventory.util.InventoryUtils;
import com.chaoswanderer.inventory.util.MenuState;
import com.chaoswanderer.inventory.util.SortField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class InventoryUI {
    private final Inventory inventory;
    private final InventoryService inventoryService;
    private Scanner scanner;
    private MenuState state = MenuState.MAIN_MENU;

    public InventoryUI(Inventory inventory, InventoryService inventoryService, Scanner scanner) {
        this.inventory = inventory;
        this.inventoryService = inventoryService;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            switch (state) {
                case MAIN_MENU -> displayMainMenu();
                case VIEW_PRODUCTS -> listAllProducts(inventory.getAllProducts());
                case ADD_PRODUCT -> addProduct();
                case UPDATE_PRODUCT -> updateProduct();
                case REMOVE_PRODUCT -> removeProduct();
                case SEARCH_PRODUCT -> searchProduct();
                case DISPLAY_SUMMARY -> displaySummary();
                case EXIT_PROGRAM -> {
                    exitProgram();
                    return;
                }
            }
        }
    }

    // print the centered header text
    private static void printCenteredHeader(String headerText) {
        int separatorLength = 116; // Length of the separator line
        int textLength = headerText.length();

        int padding = (separatorLength - textLength) / 2;

        String centeredHeader = " ".repeat(padding) + headerText + " ".repeat(padding);

        if ((separatorLength - textLength) % 2 != 0) {
            centeredHeader += " ";
        }

        System.out.println(centeredHeader);
    }

    private void displayMainMenu() {
        clearConsole();
        System.out.println(printSeparator('='));
        printCenteredHeader("INVENTORY MANAGEMENT SYSTEM");
        System.out.println(printSeparator('='));
        System.out.println("[1] View Products");
        System.out.println("[2] Add Product");
        System.out.println("[3] Update Product");
        System.out.println("[4] Remove Product");
        System.out.println("[5] Search Product");
        System.out.println("[6] View Summary");
        System.out.println("[0] Exit");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());

        if (choice.equals("0")) {
            state = MenuState.EXIT_PROGRAM;
            return;
        }

        handleCommand(choice);
    }

    private void handleCommand(String choice) {
        switch (choice) {
            case "1" -> state = MenuState.VIEW_PRODUCTS;
            case "2" -> state = MenuState.ADD_PRODUCT;
            case "3" -> state = MenuState.UPDATE_PRODUCT;
            case "4" -> state = MenuState.REMOVE_PRODUCT;
            case "5" -> state = MenuState.SEARCH_PRODUCT;
            case "6" -> state = MenuState.DISPLAY_SUMMARY;
            default -> {
                System.out.println("\nInvalid input - Returning...");
                pause();
            }
        }

    }

    // region List All Products
    /* ------------------------------------- List All Products ------------------------------------- */
    private void listAllProducts(List<Product> products) {
        clearConsole();
        boolean viewing = true;

        products = inventory.sortBy(SortField.CREATED_AT, false); // default list is sorted by CREATION DATE (NEWEST)
        while (viewing) {

            if (products == null) {
                break;
            }

            displayProductListTableFormat(products, "CURRENT INVENTORY");

            String choice = handleListMenu();

            switch (choice) {
                case "1" -> {
                    clearConsole();
                    products = handleSortOptions(products);
                    if (products == null) {
                        break;
                    }
                    state = MenuState.VIEW_PRODUCTS;
                }
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> state = MenuState.EXIT_PROGRAM;
                default -> {
                    state = MenuState.MAIN_MENU;
                    System.out.println("\nInvalid option - Returning to Main Menu...");
                    pause();
                }
            }

            if (state != MenuState.VIEW_PRODUCTS) {
                viewing = false;
            }
        }
    }

    // prints all products in a table format
    private void displayProductListTableFormat(List<Product> products, String header) {
        printHeader(header);
        System.out.printf("%-10s | %-35s | %-4s | %-11s | %-19s | %-20s%n", "ID", "Name", "Qty", "Price", "Created At", "Updated At");
        System.out.println(printSeparator('-'));

        for (Product product : products) {
            System.out.printf("%-10s | %-35s | %-4d | $%-10s | %-10s | %-10s%n",
                    product.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice().toPlainString(),
                    product.getFormattedCreatedAt(),
                    product.getFormattedUpdatedAt());
        }

        System.out.println(printSeparator('-'));
    }

    // shows menu options while viewing products list
    private String handleListMenu() {
        System.out.println("[1] Sort Options");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");

        return InventoryUtils.sanitizeString(scanner.nextLine());
    }

    // handles sort options
    private List<Product> handleSortOptions(List<Product> products) {
        System.out.println(printSeparator('-'));
        System.out.println("SORT OPTIONS");
        System.out.println(printSeparator('-'));
        System.out.println("[A] Sort by ID (Ascending)");
        System.out.println("[B] Sort by ID (Descending)");
        System.out.println("[1] Sort by Name (Ascending)");
        System.out.println("[2] Sort by Name (Descending)");
        System.out.println("[3] Sort by Price (Lowest to Highest)");
        System.out.println("[4] Sort by Price (Highest to Lowest)");
        System.out.println("[5] Sort by Creation Date (Newest First)");
        System.out.println("[6] Sort by Creation Date (Oldest First)");
        System.out.println("[7] Sort by Last Updated (Newest First)");
        System.out.println("[8] Sort by Last Updated (Oldest First)");
        System.out.println("[9] Default");
        System.out.println("[0] Return to Main Menu");
        System.out.println("[X] Exit Program");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());

        if (choice.equals("0")) {
            state = MenuState.EXIT_PROGRAM;
            exitProgram();
        }

        return getSortedList(choice, products);
    }

    // returns the sorted list based on choice from handleSortOptions()
    // default order is sorted by Creation Date (Newest)
    private List<Product> getSortedList(String choice, List<Product> products) {

        return switch (choice) {
            case "a" -> inventory.sortBy(SortField.ID, true);
            case "b" -> inventory.sortBy(SortField.ID, false);
            case "1" -> inventory.sortBy(SortField.NAME, true);
            case "2" -> inventory.sortBy(SortField.NAME, false);
            case "3" -> inventory.sortBy(SortField.PRICE, true);
            case "4" -> inventory.sortBy(SortField.PRICE, false);
            case "5" -> inventory.sortBy(SortField.CREATED_AT, false); // newest first
            case "6" -> inventory.sortBy(SortField.CREATED_AT, true);
            case "7" -> inventory.sortBy(SortField.UPDATED_AT, false); // newest first
            case "8" -> inventory.sortBy(SortField.UPDATED_AT, true);
            case "9" -> inventory.sortBy(SortField.CREATED_AT, false); // return newest first list as default
            case "0" -> { // Return to Main Menu option
                state = MenuState.MAIN_MENU;
                yield null;
            }
            case "x" -> {
                state = MenuState.EXIT_PROGRAM;
                yield null;
            }
            default -> {
                System.out.println("\nInvalid option — Keeping current order.");
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

            if (!getConfirmation()) {
                return;
            }

            System.out.println(printSeparator('-'));

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

    private Product promptForProductDetails() {
        System.out.print("Enter Product ID (press Enter to return): ");
        String id = InventoryUtils.sanitizeString(scanner.nextLine());
        if (id.isEmpty()) {
            return null;
        }

        System.out.print("Enter Product Name (press Enter to return): ");
        String name = InventoryUtils.sanitizeStringName(scanner.nextLine());
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
        return InventoryUtils.sanitizeString(scanner.nextLine());
    }

    // prevents invalid inputs for quantity field
    private int readIntInput(String fieldName) {
        while (true) {
            try {
                String input = InventoryUtils.sanitizeString(scanner.nextLine());
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
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    return null;
                }

                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    throw new Exception("Negative numbers are not allowed!");
                }

                return InventoryUtils.toPrice(input);
            } catch (Exception e) {
                System.out.print("Invalid price — please enter again: ");
            }
        }
    }
    // endregion

    // region Update Products
    /* ------------------------------------- Update Products ------------------------------------- */
    private void updateProduct() {
        boolean updating = true;

        while (updating) {
            clearConsole();

            printHeader("UPDATE PRODUCT");

            System.out.print("Enter Product ID to update (press Enter to return): ");
            String id = InventoryUtils.sanitizeString(scanner.nextLine());
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
            System.out.println(printSeparator('-'));
            System.out.println("[1] Update Another Product");
            System.out.println("[2] Return to Main Menu");
            System.out.println("[3] Exit Program");
            System.out.println(printSeparator('-'));
            System.out.print("Select an option: ");
            String choice = InventoryUtils.sanitizeString(scanner.nextLine());

            switch (choice) {
                case "1" -> state = MenuState.UPDATE_PRODUCT;
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> state = MenuState.EXIT_PROGRAM;
                default -> {
                    state = MenuState.MAIN_MENU;
                    System.out.println("Invalid option — Returning to Main Menu...");
                    pause();
                }
            }

            if (state != MenuState.UPDATE_PRODUCT) {
                updating = false;
            }
        }
    }

    private void printCurrentInformation(Product product) {
        System.out.println(printSeparator('-'));
        System.out.println("Current Information:");
        System.out.println(printSeparator('-'));
        System.out.println("Product ID: " + product.getId());
        System.out.println("Product Name: " + product.getName());
        System.out.println("Quantity: " + product.getQuantity());
        System.out.println("Price: " + "$" + product.getPrice());
        System.out.println("Created At: " + product.getFormattedCreatedAt());
        System.out.println("Updated At: " + product.getFormattedUpdatedAt());
        System.out.println(printSeparator('-'));
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
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());
        System.out.println(printSeparator('-'));

        switch (choice) {
            case "1" -> updateProductName(product);
            case "2" -> updateProductPrice(product);
            case "3" -> updateProductQuantity(product);
            case "4" -> increaseProductQuantity(product);
            case "5" -> decreaseProductQuantity(product);
            case "6" -> {
                state = MenuState.MAIN_MENU;
                return false;
            }
            case "0" -> {
                state = MenuState.EXIT_PROGRAM;
                exitProgram();
            }
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
        String newName = InventoryUtils.sanitizeStringName(scanner.nextLine());
        if (newName.isEmpty()) {
            return;
        }

        if (!getConfirmation()) {
            return;
        }

        System.out.println();

        product.setName(newName);
        System.out.println("Product [" + product.getId() + "] name updated to '" + product.getName() + "'.");
        product.updateUpdatedAt();
    }

    private void updateProductPrice(Product product) {
        System.out.print("Enter new product price (press Enter to skip): ");
        BigDecimal newPrice = readPriceInput();
        if (newPrice == null) {
            return;
        }

        if (!getConfirmation()) {
            return;
        }

        System.out.println();

        product.setPrice(newPrice);
        System.out.println("Product [" + product.getId() + "] price updated to '$" + product.getPrice() + "'.");
        product.updateUpdatedAt();
    }

    private void updateProductQuantity(Product product) {
        System.out.print("Enter new quantity (press Enter to skip): ");
        int newQuantity = readIntInput("quantity");
        if (newQuantity == -1) {
            return;
        }

        if (!getConfirmation()) {
            return;
        }

        System.out.println();

        product.setQuantity(newQuantity);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
        product.updateUpdatedAt();
    }

    private void increaseProductQuantity(Product product) {
        System.out.print("Enter quantity to add to current stock (press Enter to skip): ");
        int amount = readIntInput("quantity");
        if (amount == -1) {
            return;
        }

        if (!getConfirmation()) {
            return;
        }

        System.out.println();

        product.increaseQuantity(amount);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
        product.updateUpdatedAt();
    }

    private void decreaseProductQuantity(Product product) {
        System.out.print("Enter quantity to subtract to current stock (press Enter to skip): ");
        int amount = readIntInput("quantity");
        if (amount == -1) {
            return;
        }

        if (!getConfirmation()) {
            return;
        }

        System.out.println();

        product.decreaseQuantity(amount);
        System.out.println("Product [" + product.getId() + "] quantity updated to '" + product.getQuantity() + "'.");
        product.updateUpdatedAt();
    }
    // endregion

    // region Remove Products
    /* ------------------------------------- Remove Products ------------------------------------- */
    private void removeProduct() {
        boolean removing = true;

        while (removing) {
            clearConsole();
            printHeader("REMOVE PRODUCT");

            System.out.print("Enter Product ID to remove (press Enter to return): ");
            String id = InventoryUtils.sanitizeString(scanner.nextLine());

            if (id.isEmpty()) {
                state = MenuState.MAIN_MENU;
                break;
            }

            Product product = inventory.searchProductById(id);

            if (product == null) {
                System.out.println("\nProduct ID " + "[" + id + "]" + " not found!");
                System.out.println(printSeparator('-'));
                pause();
                continue;
            }

            System.out.println(printSeparator('-'));

            boolean successful = displayRemoveProductConfirmation(product);

            if (!successful) {
                pause();
                continue;
            }

            System.out.println("\nProduct removed successfully!");
            System.out.println(printSeparator('-'));
            pause();
            clearConsole();
            System.out.println(printSeparator('-'));

            String input = handleRemoveAnotherProductMenu();

            switch (input) {
                case "1" -> state = MenuState.REMOVE_PRODUCT;
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> {
                    state = MenuState.EXIT_PROGRAM;
                }
                default -> {
                    state = MenuState.MAIN_MENU;
                    System.out.println("Invalid option - Returning to Main Menu...");
                    pause();
                }
            }

            if (state != MenuState.REMOVE_PRODUCT) {
                removing = false;
            }
        }
    }

    private boolean displayRemoveProductConfirmation(Product product) {
        System.out.print("Are you sure you want to remove " + "[" + product.getId() + "] " + "'" + product.getName() + "' ? (y/n): ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());

        if (choice.equals("y")) {
            this.inventory.removeProduct(product.getId());
            return true;
        } else {
            System.out.println(printSeparator('-'));
            return false;
        }
    }

    private String handleRemoveAnotherProductMenu() {
        System.out.println("[1] Remove Another Product");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        System.out.println("------------------------------------------------------------------");
        System.out.print("Select an option: ");
        return InventoryUtils.sanitizeString(scanner.nextLine());
    }

    // endregion

    // region Search Products
    /* ------------------------------------- Search Products ------------------------------------- */
    private void searchProduct() {
        boolean searching = true;

        while (searching) {
            clearConsole();

            printHeader("SEARCH PRODUCT");

            if (isInventoryEmpty()) {
                pause();
                return;
            }

            // handle repeat search menu if users choose to (press Enter to return)
            switch (handleSearchMenu()) {
                case MenuState.SEARCH_PRODUCT -> {
                    continue;
                }

                case MenuState.MAIN_MENU -> {
                    state = MenuState.MAIN_MENU;
                    return;
                }

                case MenuState.EXIT_PROGRAM -> {
                    state = MenuState.EXIT_PROGRAM;
                    return;
                }

                case null, default -> {
                }
            }

            clearConsole();

            String choice = handleSearchAgainMenu();

            switch (choice) {
                case "1" -> state = MenuState.SEARCH_PRODUCT;
                case "2" -> state = MenuState.MAIN_MENU;
                case "3" -> state = MenuState.EXIT_PROGRAM;
                default -> {
                    state = MenuState.MAIN_MENU;
                    System.out.println("\nInvalid option - Returning to Main Menu...");
                    pause();
                }
            }

            if (state != MenuState.SEARCH_PRODUCT) {
                searching = false;
            }
        }
    }

    private boolean isInventoryEmpty() {
        if (inventory.getInventory().isEmpty() || inventory.getInventory() == null) {
            System.out.println("The inventory is empty.");
            System.out.println("Returning...");
            System.out.println(printSeparator('-'));
            return true;
        } else {
            return false;
        }
    }

    private String displaySearchMenu() {
        System.out.println("[1] Search by ID");
        System.out.println("[2] Search by Name");
        System.out.println("[3] Return to Main Menu");
        System.out.println("[4] Exit Program");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());

        System.out.println(printSeparator('-'));
        return choice;
    }

    private MenuState handleSearchMenu() {
        boolean repeatSearchMenu = true;

        switch (displaySearchMenu()) {
            case "1" -> repeatSearchMenu = searchProductsById();
            case "2" -> repeatSearchMenu = searchProductsByName();
            case "3" -> {
                return MenuState.MAIN_MENU;
            }
            case "4" -> {
                return MenuState.EXIT_PROGRAM;
            }
            default -> {
                System.out.println("\nInvalid option – Please select a valid option.");
                pause();
            }
        }

        if (repeatSearchMenu) {
            return MenuState.SEARCH_PRODUCT;
        }

        return null;
    }

    // return value used for handleSearchMenu()
    private boolean searchProductsById() {
        System.out.print("Enter Product ID (press Enter to return): ");
        String id = InventoryUtils.sanitizeString(scanner.nextLine());

        if (id.isEmpty()) {
            return true;
        }

        List<Product> products = inventory.searchProductsById(id);

        displaySearchResults(products);
        return false;
    }

    private boolean searchProductsByName() {
        System.out.print("Enter keyword (press Enter to return): ");
        String name = InventoryUtils.sanitizeString(scanner.nextLine());

        if (name.isEmpty()) {
            return true;
        }

        List<Product> products = inventory.searchProductsbyName(name);

        displaySearchResults(products);
        return false;
    }

    private void displaySearchResults(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("\nNo products found");
            System.out.println("Returning...");
            System.out.println(printSeparator('-'));
            pause();
            return;
        }

        displayProductListTableFormat(products, "RESULTS");
        pause();
    }

    private String handleSearchAgainMenu() {
        System.out.println(printSeparator('-'));
        System.out.println("[1] Search Again");
        System.out.println("[2] Return to Main Menu");
        System.out.println("[3] Exit Program");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");

        return InventoryUtils.sanitizeString(scanner.nextLine());
    }

    // endregion

    // region Display Summary
    /* ------------------------------------- Display Summary ------------------------------------- */
    private void displaySummary() {
        clearConsole();
        printHeader("INVENTORY SUMMARY");
        System.out.println("Total Products: " + inventory.getTotalProducts());
        System.out.println("Total Stock Quantity: " + inventory.getTotalStockQuantity());
        System.out.println("Total Inventory Value: " + "$" + inventory.getTotalInventoryValue());
        System.out.println(printSeparator('-'));
        System.out.println("[1] Return to Main Menu");
        System.out.println("[2] Exit Program");
        System.out.println(printSeparator('-'));
        System.out.print("Select an option: ");
        String choice = InventoryUtils.sanitizeString(scanner.nextLine());

        switch (choice) {
            case "1" -> {
                state = MenuState.MAIN_MENU;
            }
            case "2" -> {
                state = MenuState.EXIT_PROGRAM;
            }
            default -> {
                System.out.println("\nInvalid option - Returning to Main Menu...");
                state = MenuState.MAIN_MENU;
                pause();
            }
        }
    }
    // endregion

    //region Utility Methods
    /* ------------------------------------- Utility Methods ------------------------------------- */

    private boolean getConfirmation() {
        System.out.print("Are you sure (y/n): ");
        return InventoryUtils.sanitizeString(scanner.nextLine()).equals("y");
    }

    private static String printSeparator(char character) {
        return String.valueOf(character).repeat(116);
    }

    private static void printHeader(String header) {
        System.out.println(printSeparator('-'));
        System.out.println(header);
        System.out.println(printSeparator('-'));
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

    private void exitProgram() {
        System.out.println("\nSaving data...");
        boolean success = inventoryService.saveProductsToFile();

        if (success) {
            System.out.println("Data saved successfully!");
        } else {
            System.out.println("Failed to save data!");
        }

        System.out.println("Thank you for using Inventory Manager!");
        System.out.println("Program closing...");
        System.exit(0);
    }
    // endregion
}
