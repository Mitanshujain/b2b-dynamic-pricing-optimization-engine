package com.mitanshu.pricing; // Groups related classes under one unique Java package.

import java.time.LocalDate; // Represents dates without a clock time or timezone.

public record Transaction(LocalDate date, String productId, double price,
                          double competitorPrice, int segment, double seasonIndex,
                          double unitCost, double quantity) {
    // A record gives us a constructor, getters, equality, and immutable fields.
    public Transaction {
        if (date == null) throw new IllegalArgumentException("Date is required.");
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID is required.");
        checkNumber("Price", price, 0.01, 1_000_000); // Refuse missing, infinite, negative, or implausibly large prices.
        checkNumber("Competitor price", competitorPrice, 0.01, 1_000_000);
        if (segment != 0 && segment != 1) throw new IllegalArgumentException("Segment must be 0 or 1.");
        checkNumber("Season index", seasonIndex, 0.1, 3);
        checkNumber("Unit cost", unitCost, 0.01, 1_000_000);
        checkNumber("Quantity", quantity, 0, 1_000_000); // Zero-demand observations are valid training examples.
    }

    public double[] features() {
        return new double[]{price, competitorPrice, segment, seasonIndex}; // Fixed feature order is essential.
    }

    public static void checkNumber(String name, double value, double minimum, double maximum) {
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be finite and between " + minimum + " and " + maximum + ".");
        }
    }
}
