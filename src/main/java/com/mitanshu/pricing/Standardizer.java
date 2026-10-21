package com.mitanshu.pricing;

import java.util.List;

public final class Standardizer {
    private final double[] means = new double[4];
    private final double[] deviations = new double[4];

    public Standardizer(List<Transaction> training) {
        if (training.isEmpty()) throw new IllegalArgumentException("Training rows are required.");
        for (Transaction row : training) {
            double[] values = row.features();
            for (int column = 0; column < 4; column++) means[column] += values[column];
        }
        for (int column = 0; column < 4; column++) means[column] /= training.size(); // Training-only feature means.
        for (Transaction row : training) {
            double[] values = row.features();
            for (int column = 0; column < 4; column++) {
                double difference = values[column] - means[column];
                deviations[column] += difference * difference; // Sum squared distances from each feature mean.
            }
        }
        for (int column = 0; column < 4; column++) {
            deviations[column] = Math.sqrt(deviations[column] / training.size());
            if (deviations[column] < 1e-12) deviations[column] = 1; // Avoid dividing by zero for constant columns.
        }
    }

    public double[] transform(double[] values) {
        if (values.length != 4) throw new IllegalArgumentException("Expected four features.");
        double[] scaled = new double[4]; // Leave the caller's original values untouched.
        for (int column = 0; column < 4; column++) {
            if (!Double.isFinite(values[column])) throw new IllegalArgumentException("Feature is not finite.");
            scaled[column] = (values[column] - means[column]) / deviations[column]; // z = (x - mean) / standard deviation.
        }
        return scaled;
    }
}
