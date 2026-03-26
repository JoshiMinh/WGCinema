package com.joshiminh.wgcinema.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtils {
    private static final Locale VI_LOCALE = new Locale("vi", "VN");

    public static String formatPrice(BigDecimal price) {
        if (price == null) return "0vnđ";
        NumberFormat numberFormat = NumberFormat.getNumberInstance(VI_LOCALE);
        return numberFormat.format(price.doubleValue()) + "vnđ";
    }

    public static String formatPrice(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(VI_LOCALE);
        return numberFormat.format(price) + "vnđ";
    }

    public static BigDecimal parsePrice(String priceString) {
        if (priceString == null || priceString.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Remove non-numeric characters except comma and dot
            String cleanPrice = priceString.replaceAll("[^0-9,.]", "").trim();
            NumberFormat parser = NumberFormat.getNumberInstance(VI_LOCALE);
            Number parsedNumber = parser.parse(cleanPrice);
            return BigDecimal.valueOf(parsedNumber.doubleValue());
        } catch (Exception e) {
            System.err.println("Error parsing price: " + priceString + " - " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    public static String getSeatType(String seatIdentifier) {
        if (seatIdentifier == null || seatIdentifier.isEmpty()) return "regular";
        char rowChar = Character.toUpperCase(seatIdentifier.charAt(0));
        return (rowChar >= 'G' && rowChar <= 'L') ? "vip" : "regular";
    }
}
