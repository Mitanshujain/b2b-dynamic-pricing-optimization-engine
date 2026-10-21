package com.mitanshu.pricing;

import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.math.kernel.GaussianKernel;
import smile.regression.RandomForest;
import smile.regression.SVM;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.LongStream;

public final class TrainingPipeline {
    private static final String[] COLUMNS = {"price", "competitor_price", "segment", "season_index", "quantity"};

    public record Evaluation(String model, Metrics validation, Metrics test) {}
    public record Result(String productId, List<Transaction> training, List<Transaction> validation,
                         List<Transaction> test, List<Evaluation> evaluations, DemandModel selected) {
        public double minimum(int feature) {
            return training.stream().mapToDouble(row -> row.features()[feature]).min().orElseThrow();
        }
        public double maximum(int feature) {
            return training.stream().mapToDouble(row -> row.features()[feature]).max().orElseThrow();
        }
    }

    public static Result train(List<Transaction> observations) {
        if (observations.size() < 200) throw new IllegalArgumentException("At least 200 daily observations are required.");
        List<Transaction> sorted = observations.stream().sorted(Comparator.comparing(Transaction::date)).toList();
        String product = sorted.get(0).productId();
        if (sorted.stream().anyMatch(row -> !row.productId().equals(product))) {
            throw new IllegalArgumentException("Train a separate model for each product.");
        }
        if (sorted.stream().map(Transaction::date).distinct().count() != sorted.size()) {
            throw new IllegalArgumentException("Duplicate daily observations are not allowed.");
        }
        int trainingEnd = (int) (sorted.size() * 0.6);
        int validationEnd = (int) (sorted.size() * 0.8);
        List<Transaction> training = List.copyOf(sorted.subList(0, trainingEnd));
        List<Transaction> validation = List.copyOf(sorted.subList(trainingEnd, validationEnd));
        List<Transaction> test = List.copyOf(sorted.subList(validationEnd, sorted.size()));
        double average = training.stream().mapToDouble(Transaction::quantity).average().orElseThrow();
        double variance = training.stream().mapToDouble(row -> Math.pow(row.quantity() - average, 2)).average().orElseThrow();
        if (variance < 1e-12) throw new IllegalArgumentException("Training quantities have no variation.");

        DemandModel baseline = named("Mean baseline", features -> average); // A real ML model must be compared with a simple alternative.
        DemandModel forest = randomForest(training);
        DemandModel svr = supportVectorRegression(training, average, Math.sqrt(variance));
        List<DemandModel> models = List.of(baseline, forest, svr);
        List<Metrics> validationMetrics = models.stream().map(model -> Metrics.evaluate(model, validation)).toList();
        int best = 0;
        for (int index = 1; index < models.size(); index++) {
            if (validationMetrics.get(index).rmse() < validationMetrics.get(best).rmse()) best = index;
        }
        DemandModel selected = models.get(best); // Selection uses validation data only.
        List<Evaluation> evaluations = new ArrayList<>();
        for (int index = 0; index < models.size(); index++) {
            evaluations.add(new Evaluation(models.get(index).name(), validationMetrics.get(index),
                    Metrics.evaluate(models.get(index), test))); // Test metrics do not change the selected model.
        }
        return new Result(product, training, validation, test, List.copyOf(evaluations), selected);
    }

    private static DemandModel randomForest(List<Transaction> training) {
        double[][] matrix = new double[training.size()][5];
        for (int index = 0; index < training.size(); index++) {
            System.arraycopy(training.get(index).features(), 0, matrix[index], 0, 4);
            matrix[index][4] = training.get(index).quantity(); // Target is separate from the four input features.
        }
        DataFrame frame = DataFrame.of(matrix, COLUMNS);
        RandomForest forest = RandomForest.fit(Formula.lhs("quantity"), frame,
                80, 3, 12, 100, 5, 1.0, LongStream.range(0, 80).map(index -> 2026 + index));
        // 80 trees, 3 candidate features per split, depth 12, max 100 leaves, leaf size 5, bootstrap sampling.
        return named("Random Forest", features -> {
            double[] row = Arrays.copyOf(features, 5); // The unused target slot is zero for an inference row.
            return forest.predict(DataFrame.of(new double[][]{row}, COLUMNS).get(0));
        });
    }

    private static DemandModel supportVectorRegression(List<Transaction> training, double targetMean, double targetDeviation) {
        Standardizer scaler = new Standardizer(training); // Fit scaling on training rows, never on validation/test.
        double[][] features = training.stream().map(row -> scaler.transform(row.features())).toArray(double[][]::new);
        double[] targets = training.stream().mapToDouble(row -> (row.quantity() - targetMean) / targetDeviation).toArray();
        var model = SVM.fit(features, targets, new GaussianKernel(1.5), 0.05, 10.0, 1e-3);
        // Gaussian sigma 1.5, epsilon 0.05, C 10, solver tolerance 0.001; all are fixed before test evaluation.
        return named("Support Vector Regression", values ->
                model.predict(scaler.transform(values)) * targetDeviation + targetMean); // Convert normalised output back to units.
    }

    private static DemandModel named(String name, ToDoubleFunction<double[]> prediction) {
        return new DemandModel() {
            public String name() { return name; }
            public double predict(double[] features) {
                if (features.length != 4) throw new IllegalArgumentException("Expected four features.");
                for (double value : features) {
                    if (!Double.isFinite(value)) throw new IllegalArgumentException("Features must be finite.");
                }
                return prediction.applyAsDouble(features);
            }
        };
    }
}
