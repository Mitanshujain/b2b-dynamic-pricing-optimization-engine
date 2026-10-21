package com.mitanshu.pricing;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.function.ToDoubleFunction;
import static org.junit.jupiter.api.Assertions.*;

class PricingEngineTest {
    @Test void findsKnownGridOptimum() {
        var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(100, 150, 1, 100, 80));
        assertEquals(PricingEngine.Status.RECOMMENDED, result.status());
        assertEquals(140, result.best().price());
        assertEquals(3600, result.best().contribution(), 1e-9);
        assertEquals(51, result.candidates().size());
    }

    @Test void capsDemandAtAvailableStock() {
        var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(100, 150, 1, 30, 80));
        assertEquals(150, result.best().price());
        assertEquals(30, result.best().sellableUnits());
        assertEquals(2100, result.best().contribution());
    }

    @Test void handlesNoStockAndNoPositiveContribution() {
        assertEquals(PricingEngine.Status.NO_STOCK,
                PricingEngine.optimize(trained(x -> 100), request(100, 150, 1, 0, 80)).status());
        var result = PricingEngine.optimize(trained(x -> 100), request(100, 150, 1, 100, 160));
        assertEquals(PricingEngine.Status.NO_POSITIVE_CONTRIBUTION, result.status());
        assertNull(result.best());
    }

    @Test void includesMaximumWhenStepDoesNotLandOnIt() {
        var result = PricingEngine.optimize(trained(x -> 10), request(100, 150, 7, 100, 80));
        assertEquals(150, result.best().price());
        assertEquals(9, result.candidates().size());
    }

    @Test void withholdsRecommendationWhenOnlyBaselineIsSelected() {
        var stub = trained(x -> 100);
        DemandModel baseline = new DemandModel() {
            public String name() { return "Mean baseline"; }
            public double predict(double[] x) { return 100; }
        };
        var run = new TrainingPipeline.Result("P1", stub.training(), stub.validation(), stub.test(), List.of(), baseline);
        assertEquals(PricingEngine.Status.BASELINE_SELECTED,
                PricingEngine.optimize(run, request(100, 150, 1, 100, 80)).status());
    }

    @Test void breaksExactTiesInFavourOfLowerPrice() {
        var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(139, 141, 2, 100, 80));
        assertEquals(139, result.best().price());
    }

    @Test void supportsSingleCandidateAndCentSteps() {
        assertEquals(100, PricingEngine.optimize(trained(x -> 10), request(100, 100, 1, 100, 80)).best().price());
        var result = PricingEngine.optimize(trained(x -> 10), request(100, 100.02, 0.01, 100, 80));
        assertEquals(3, result.candidates().size());
        assertEquals(100.02, result.best().price());
    }

    @Test void clipsNegativePredictionsButRejectsNonfinitePredictions() {
        var result = PricingEngine.optimize(trained(x -> -10), request(100, 150, 1, 100, 80));
        assertEquals(PricingEngine.Status.NO_POSITIVE_CONTRIBUTION, result.status());
        assertTrue(result.notes().stream().anyMatch(note -> note.contains("clipped")));
        assertThrows(IllegalStateException.class,
                () -> PricingEngine.optimize(trained(x -> Double.POSITIVE_INFINITY), request(100, 150, 1, 100, 80)));
    }

    @Test void rejectsBadBoundsPrecisionStockAndLargeSearches() {
        for (var input : List.of(request(150, 100, 1, 100, 80), request(100, 150, 0, 100, 80),
                request(100.001, 150, 1, 100, 80), request(100, 150, 1, -1, 80),
                request(90, 160, 0.01, 100, 80), request(100, 170, 1, 100, 80),
                request(100, 150, 1, 100, Double.NaN))) {
            assertThrows(IllegalArgumentException.class, () -> PricingEngine.optimize(trained(x -> 10), input));
        }
    }

    private PricingEngine.Request request(double min, double max, double step, int stock, double cost) {
        return new PricingEngine.Request(120, cost, min, max, step, stock, 125, 1, 1);
    }

    private TrainingPipeline.Result trained(ToDoubleFunction<double[]> prediction) {
        var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 90, 100, 0, 0.8, 80, 20),
                new Transaction(LocalDate.of(2024, 1, 2), "P1", 160, 150, 1, 1.2, 80, 50));
        DemandModel model = new DemandModel() {
            public String name() { return "Test model"; }
            public double predict(double[] x) { return prediction.applyAsDouble(x); }
        };
        return new TrainingPipeline.Result("P1", rows, rows, rows, List.of(), model);
    }
}
