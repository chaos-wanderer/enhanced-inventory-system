package com.chaoswanderer.inventory;

import com.chaoswanderer.inventory.model.Inventory;
import com.chaoswanderer.inventory.model.Product;
import com.chaoswanderer.inventory.ui.InventoryUI;
import com.chaoswanderer.inventory.util.PriceUtils;

import java.util.Scanner;

public class Main {

    @SuppressWarnings("checkstyle:OperatorWrap")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory();

        // for testing only
        Inventory testInventory = new Inventory();
        testInventory.addProduct(new Product("100001", "Milk (500 mL)", 12, PriceUtils.toPrice("42.20")));
        testInventory.addProduct(new Product("100002", "Bread Loaf", 8, PriceUtils.toPrice("35.00")));
        testInventory.addProduct(new Product("100003", "Eggs (1 Dozen)", 15, PriceUtils.toPrice("89.50")));
        testInventory.addProduct(new Product("100004", "Sugar (1 kg)", 10, PriceUtils.toPrice("70.25")));
        testInventory.addProduct(new Product("100005", "Coffee Powder (250 g)", 5, PriceUtils.toPrice("120.75")));
        testInventory.addProduct(new Product("100006", "Toothpaste (100 g)", 20, PriceUtils.toPrice("65.00")));
        testInventory.addProduct(new Product("100007", "Shampoo (350 mL)", 9, PriceUtils.toPrice("99.90")));
        testInventory.addProduct(new Product("100008", "Laundry Detergent (1 kg)", 6, PriceUtils.toPrice("150.00")));
        testInventory.addProduct(new Product("100009", "Canned Tuna (155 g)", 25, PriceUtils.toPrice("58.00")));
        testInventory.addProduct(new Product("100010", "Butter (200 g)", 7, PriceUtils.toPrice("82.35")));

        InventoryUI ui = new InventoryUI(testInventory, scanner);
        ui.start();
    }


}
