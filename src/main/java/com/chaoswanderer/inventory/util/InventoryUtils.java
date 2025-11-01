package com.chaoswanderer.inventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InventoryUtils {
    private InventoryUtils() {
    }

    // parse String value to BigDecimal with 2-fixed decimals
    public static BigDecimal toPrice(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static String sanitizeString(String string) {
        if (string == null) {
            return "";
        }

        return string
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-zA-Z0-9\\s\\-\\[\\]()]", "")
                .toLowerCase();
    }

    // String sanitizer for names - to keep chosen capitalization
    public static String sanitizeStringName(String string) {
        if (string == null) {
            return "";
        }

        return string
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-zA-Z0-9\\s\\-\\[\\]()]", "");
    }
}
