package com.mitanshu.pricing;

import java.util.List;

public record Metrics(double mae, double rmse) {
    public static Metrics evaluate(DemandModel model, List<Transaction> rows) {
        if (rows.isEmpty()) throw new IllegalArgumentException("Evaluation data is empty.");
        double absoluteError = 0;
        double squaredError = 0;
        for (Transaction row : rows) {
            double predicted = model.predict(row.features());
            if (!Double.isFinite(predicted)) throw new IllegalStateException("Model produced a non-finite prediction.");
            predicted = Math.max(0, predicted); // Evaluation uses the same nonnegative-demand rule as recommendations.
            double error = predicted - row.quantity();
            absoluteError += Math.abs(error);
            squaredError += error * error;
        }
        return new Metrics(absoluteError / rows.size(), Math.sqrt(squaredError / rows.size()));
    }
}
