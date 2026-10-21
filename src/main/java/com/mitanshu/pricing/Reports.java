package com.mitanshu.pricing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class Reports {
    public static void write(TrainingPipeline.Result trained, PricingEngine.Result recommendation, Path folder) throws IOException {
        Files.createDirectories(folder);
        try (CSVPrinter csv = printer(folder.resolve("evaluation.csv"),
                "model", "validation_mae", "validation_rmse", "test_mae", "test_rmse", "selected")) {
            for (TrainingPipeline.Evaluation item : trained.evaluations()) {
                csv.printRecord(item.model(), item.validation().mae(), item.validation().rmse(),
                        item.test().mae(), item.test().rmse(), item.model().equals(trained.selected().name()));
            }
        }
        try (CSVPrinter csv = printer(folder.resolve("test_predictions.csv"),
                "date", "actual_units", "predicted_units", "error_units")) {
            for (Transaction row : trained.test()) {
                double predicted = Math.max(0, trained.selected().predict(row.features()));
                csv.printRecord(row.date(), row.quantity(), predicted, predicted - row.quantity());
            }
        }
        Files.writeString(folder.resolve("eda.txt"), summary(trained), StandardCharsets.UTF_8);
        if (recommendation != null) {
            try (CSVPrinter csv = printer(folder.resolve("candidate_prices.csv"),
                    "price_inr", "predicted_units", "stock_limited_units", "estimated_gross_contribution_inr")) {
                for (PricingEngine.Candidate candidate : recommendation.candidates()) {
                    csv.printRecord(candidate.price(), candidate.predictedDemand(), candidate.sellableUnits(), candidate.contribution());
                }
            }
            Files.writeString(folder.resolve("recommendation.txt"),
                    PricingEngine.describe(recommendation, trained.selected().name()), StandardCharsets.UTF_8);
        } else {
            Files.deleteIfExists(folder.resolve("candidate_prices.csv")); // Remove stale pricing reports after a training-only run.
            Files.deleteIfExists(folder.resolve("recommendation.txt"));
        }
    }

    public static String summary(TrainingPipeline.Result trained) {
        List<Transaction> rows = trained.training(); // EDA below uses only the training partition.
        double priceMean = rows.stream().mapToDouble(Transaction::price).average().orElseThrow();
        double quantityMean = rows.stream().mapToDouble(Transaction::quantity).average().orElseThrow();
        double covariance = 0;
        double priceVariance = 0;
        double quantityVariance = 0;
        for (Transaction row : rows) {
            double priceDifference = row.price() - priceMean;
            double quantityDifference = row.quantity() - quantityMean;
            covariance += priceDifference * quantityDifference;
            priceVariance += priceDifference * priceDifference;
            quantityVariance += quantityDifference * quantityDifference;
        }
        double correlation = priceVariance * quantityVariance == 0 ? Double.NaN :
                covariance / Math.sqrt(priceVariance * quantityVariance);
        StringBuilder text = new StringBuilder("SYNTHETIC DATA - one SKU, one daily observation\n");
        text.append("Product: ").append(trained.productId()).append("\n");
        text.append("Training / validation / test rows: ").append(rows.size()).append(" / ")
                .append(trained.validation().size()).append(" / ").append(trained.test().size()).append("\n");
        text.append("Training dates: ").append(rows.get(0).date()).append(" to ").append(rows.get(rows.size() - 1).date()).append("\n");
        text.append("Validation dates: ").append(trained.validation().get(0).date()).append(" to ")
                .append(trained.validation().get(trained.validation().size() - 1).date()).append("\n");
        text.append("Test dates: ").append(trained.test().get(0).date()).append(" to ")
                .append(trained.test().get(trained.test().size() - 1).date()).append("\n");
        text.append(String.format(Locale.ROOT, "Training price range: %.2f to %.2f INR%nMean training quantity: %.2f%nPrice/quantity correlation: %.4f%n",
                trained.minimum(0), trained.maximum(0), quantityMean, correlation));
        text.append("Correlation alone is not causal price elasticity. This simulator randomises prices by design.\n");
        for (TrainingPipeline.Evaluation item : trained.evaluations()) {
            text.append(String.format(Locale.ROOT, "%s: validation RMSE %.3f; test MAE %.3f; test RMSE %.3f%n",
                    item.model(), item.validation().rmse(), item.test().mae(), item.test().rmse()));
        }
        text.append("Selected using validation RMSE: ").append(trained.selected().name()).append("\n");
        return text.toString();
    }

    private static CSVPrinter printer(Path destination, String... headers) throws IOException {
        return new CSVPrinter(Files.newBufferedWriter(destination, StandardCharsets.UTF_8),
                CSVFormat.RFC4180.builder().setHeader(headers).get());
    }
}
