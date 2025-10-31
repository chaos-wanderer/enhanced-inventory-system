package com.chaoswanderer.inventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    private PriceUtils() {
    }

    public static BigDecimal toPrice(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}
