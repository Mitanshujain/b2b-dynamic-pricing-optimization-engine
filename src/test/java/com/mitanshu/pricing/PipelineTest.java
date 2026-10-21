package com.mitanshu.pricing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {
    @TempDir static Path folder;
    static TrainingPipeline.Result trained;

    @BeforeAll static void trainRealModels() throws Exception {
        Path file = folder.resolve("dataset.csv");
        SyntheticData.generate(file, 720, 42);
        trained = TrainingPipeline.train(CsvData.load(file));
    }

    @Test void partitionsChronologicallyWithoutOverlap() {
        assertEquals(432, trained.training().size());
        assertEquals(144, trained.validation().size());
        assertEquals(144, trained.test().size());
        assertTrue(trained.training().get(431).date().isBefore(trained.validation().get(0).date()));
        assertTrue(trained.validation().get(143).date().isBefore(trained.test().get(0).date()));
    }

    @Test void selectsByValidationOnlyAndBeatsMeanBaselineOnSyntheticData() {
        var bestValidation = trained.evaluations().stream()
                .min(java.util.Comparator.comparingDouble(row -> row.validation().rmse())).orElseThrow();
        assertEquals(bestValidation.model(), trained.selected().name());
        double baseline = trained.evaluations().get(0).test().rmse();
        double selected = trained.evaluations().stream().filter(row -> row.model().equals(trained.selected().name()))
                .findFirst().orElseThrow().test().rmse();
        assertTrue(selected < baseline);
        assertTrue(selected < 12, "A gross integration/model error should fail this fixture.");
    }

    @Test void writesReportsAndRecommendationsEndToEnd() throws Exception {
        var result = PricingEngine.optimize(trained, Main.demoRequest());
        assertEquals(PricingEngine.Status.RECOMMENDED, result.status());
        Reports.write(trained, result, folder.resolve("reports"));
        for (String name : List.of("evaluation.csv", "test_predictions.csv", "candidate_prices.csv", "recommendation.txt", "eda.txt")) {
            assertTrue(Files.size(folder.resolve("reports").resolve(name)) > 0);
        }
        assertEquals(145, Files.readAllLines(folder.resolve("reports/test_predictions.csv")).size());
    }

    @Test void scalerUsesOnlyTrainingStatistics() {
        var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 100, 125, 1, 1, 80, 50),
                new Transaction(LocalDate.of(2024, 1, 2), "P1", 120, 125, 1, 1, 80, 60));
        Standardizer scaler = new Standardizer(rows);
        assertEquals(0, scaler.transform(new double[]{110, 125, 1, 1})[0]);
        assertEquals(89, scaler.transform(new double[]{1000, 125, 1, 1})[0]);
        assertEquals(0, scaler.transform(new double[]{110, 125, 1, 1})[1]);
    }

    @Test void rejectsTooLittleOrConstantTargetTrainingData() {
        assertThrows(IllegalArgumentException.class, () -> TrainingPipeline.train(trained.training().subList(0, 10)));
        var constant = java.util.stream.IntStream.range(0, 200)
                .mapToObj(index -> new Transaction(LocalDate.of(2024, 1, 1).plusDays(index), "P1", 100, 125, 1, 1, 80, 50)).toList();
        assertThrows(IllegalArgumentException.class, () -> TrainingPipeline.train(constant));
    }

    @Test void commandDispatcherReportsInvalidCommandsAndArguments() {
        assertThrows(IllegalArgumentException.class, () -> Main.run(new String[]{"unknown"}));
        assertThrows(IllegalArgumentException.class, () -> Main.run(new String[]{"recommend", "x.csv", "120"}));
    }
}
