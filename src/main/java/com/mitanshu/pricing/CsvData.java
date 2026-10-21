package com.mitanshu.pricing;

import org.apache.commons.csv.CSVFormat; // Parses CSV quoting and column boundaries correctly.
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.DuplicateHeaderMode;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public final class CsvData {
    public static final String[] HEADER = {"date", "product_id", "price", "competitor_price",
            "customer_segment", "season_index", "unit_cost", "quantity"};

    public static List<Transaction> load(Path file) throws IOException {
        List<Transaction> rows = new ArrayList<>(); // Stores successfully validated observations.
        Set<LocalDate> dates = new HashSet<>(); // Detects duplicate daily observations.
        Set<String> products = new HashSet<>(); // This first version intentionally models one SKU.
        CSVFormat format = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true)
                .setTrim(true).setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW).get();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {
            if (!parser.getHeaderNames().equals(Arrays.asList(HEADER))) {
                throw new IllegalArgumentException("Expected exactly these ordered CSV headers: " + String.join(",", HEADER));
            }
            for (CSVRecord row : parser) {
                if (rows.size() >= 10_000) throw new IllegalArgumentException("Learning edition supports at most 10000 rows.");
                try {
                    if (!row.isConsistent()) throw new IllegalArgumentException("Incorrect number of columns.");
                    for (String column : HEADER) {
                        if (row.get(column).isBlank()) throw new IllegalArgumentException("Missing value: " + column);
                    }
                    Transaction item = new Transaction(LocalDate.parse(row.get("date")), row.get("product_id"),
                            Double.parseDouble(row.get("price")), Double.parseDouble(row.get("competitor_price")),
                            Integer.parseInt(row.get("customer_segment")), Double.parseDouble(row.get("season_index")),
                            Double.parseDouble(row.get("unit_cost")), Double.parseDouble(row.get("quantity")));
                    if (!dates.add(item.date())) throw new IllegalArgumentException("Duplicate date: " + item.date());
                    products.add(item.productId());
                    if (products.size() > 1) throw new IllegalArgumentException("Use one product per training file.");
                    rows.add(item);
                } catch (RuntimeException failure) {
                    throw new IllegalArgumentException("CSV record " + row.getRecordNumber() + ": " + failure.getMessage(), failure);
                }
            }
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("CSV contains no observations.");
        rows.sort(Comparator.comparing(Transaction::date)); // Oldest dates come first regardless of original row order.
        return List.copyOf(rows); // Callers cannot accidentally change the loaded dataset.
    }
}
