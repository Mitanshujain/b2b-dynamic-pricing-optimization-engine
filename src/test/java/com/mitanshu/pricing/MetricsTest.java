package com.mitanshu.pricing;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {
    @Test void computesKnownErrorsAndRejectsEmptyEvaluation() {
        var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 100, 120, 1, 1, 80, 10),
                new Transaction(LocalDate.of(2024, 1, 2), "P1", 100, 120, 1, 1, 80, 14));
        DemandModel model = new DemandModel() {
            public String name() { return "Fixed"; }
            public double predict(double[] values) { return 12; }
        };
        Metrics result = Metrics.evaluate(model, rows);
        assertEquals(2, result.mae());
        assertEquals(2, result.rmse());
        assertThrows(IllegalArgumentException.class, () -> Metrics.evaluate(model, List.of()));
    }
}
