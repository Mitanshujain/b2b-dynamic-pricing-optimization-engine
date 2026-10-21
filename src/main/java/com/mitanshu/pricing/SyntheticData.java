package com.mitanshu.pricing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

public final class SyntheticData {
    public static void generate(Path destination, int count, long seed) throws IOException {
        if (count < 200 || count > 10_000) throw new IllegalArgumentException("Generate between 200 and 10000 rows.");
        Path parent = destination.toAbsolutePath().getParent();
        Files.createDirectories(parent); // Create the data folder if necessary.
        Random random = new Random(seed); // The same seed reproduces the same dataset.
        CSVFormat format = CSVFormat.RFC4180.builder().setHeader(CsvData.HEADER).get();
        try (CSVPrinter writer = new CSVPrinter(Files.newBufferedWriter(destination, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW), format)) { // Never silently overwrite an existing dataset.
            for (int day = 0; day < count; day++) {
                LocalDate date = LocalDate.of(2024, 1, 1).plusDays(day);
                double price = round(90 + random.nextDouble() * 70); // Randomised prices separate price effects from segment effects.
                double competitor = round(100 + random.nextDouble() * 50);
                int segment = random.nextBoolean() ? 1 : 0; // 0 = small buyer; 1 = larger buyer.
                double season = round(0.8 + random.nextDouble() * 0.4); // Known external market/season index, not the target.
                double cost = round(70 + random.nextDouble() * 20);
                double quantity = Math.max(0, Math.round(expectedDemand(price, competitor, segment, season)
                        + random.nextGaussian() * 3)); // Add observation noise, then round to nonnegative units.
                writer.printRecord(date, "BOLT-M8-100", number(price), number(competitor), segment,
                        number(season), number(cost), number(quantity));
            }
        }
    }

    public static double expectedDemand(double price, double competitor, int segment, double season) {
        return Math.max(0, 115 + 25 * segment + 30 * (season - 1)
                - 1.4 * (price - 100) + 0.45 * (competitor - 120));
        // This formula exists only in the simulator; models never receive it during training.
    }

    private static double round(double number) {
        return Math.round(number * 100) / 100.0; // Two-decimal formatting for generated numeric inputs.
    }

    private static String number(double number) {
        return String.format(Locale.ROOT, "%.2f", number); // A decimal point works independently of computer locale.
    }
}
