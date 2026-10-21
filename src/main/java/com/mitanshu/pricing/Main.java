package com.mitanshu.pricing;

import java.nio.file.*;
import java.util.Locale;

public final class Main {
    public static final Path DEFAULT_DATA = Path.of("data", "synthetic_transactions.csv");

    public static void main(String[] args) {
        try {
            run(args); // Separate dispatch logic from process exit so tests can call it safely.
        } catch (Exception failure) {
            System.err.println("ERROR: " + failure.getMessage()); // Explain failures instead of printing a misleading recommendation.
            System.exit(1); // Nonzero exit tells a terminal or CI job that execution failed.
        }
    }

    static void run(String[] args) throws Exception {
        String command = args.length == 0 ? "demo" : args[0].toLowerCase(Locale.ROOT);
        Path data = args.length >= 2 ? Path.of(args[1]) : DEFAULT_DATA;
        if (command.equals("help")) {
            System.out.println("""
                    Commands:
                      demo [csv]                Train, evaluate and recommend using demo inputs.
                      generate [csv] [rows]     Create deterministic synthetic data; existing files are protected.
                      train [csv]               Train/evaluate and export reports.
                      gui [csv]                 Open the Java desktop interface.
                      recommend [csv]           Use demo pricing inputs.
                      recommend csv current cost min max step stock competitor segment season
                    Example:
                      recommend data/synthetic_transactions.csv 120 80 100 150 1 80 125 1 1.0
                    All currency values are INR; one quantity unit is one 100-bolt pack.
                    """);
            return;
        }
        if (command.equals("generate")) {
            if (args.length > 3) throw new IllegalArgumentException("Usage: generate [csv] [rows]");
            int count = args.length == 3 ? Integer.parseInt(args[2]) : 720;
            SyntheticData.generate(data, count, 42);
            System.out.println("Generated " + count + " synthetic observations at " + data.toAbsolutePath());
            return;
        }
        if (!command.equals("recommend") && args.length > 2) throw new IllegalArgumentException("Too many command arguments.");
        if (command.equals("demo") && !Files.exists(data)) SyntheticData.generate(data, 720, 42);
        if (command.equals("gui")) {
            if (java.awt.GraphicsEnvironment.isHeadless()) throw new IllegalStateException("Desktop display unavailable. Run demo or train instead.");
            DesktopApp.open(data);
            return;
        }
        if (!command.equals("demo") && !command.equals("train") && !command.equals("recommend")) {
            throw new IllegalArgumentException("Unknown command. Use help.");
        }
        if (command.equals("recommend") && args.length != 1 && args.length != 2 && args.length != 11) {
            throw new IllegalArgumentException("Use recommend [csv], or all nine pricing inputs. See help.");
        }
        TrainingPipeline.Result trained = TrainingPipeline.train(CsvData.load(data));
        System.out.println(Reports.summary(trained));
        PricingEngine.Result recommendation = null;
        if (!command.equals("train")) {
            PricingEngine.Request request = args.length == 11 ? new PricingEngine.Request(
                    Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]),
                    Double.parseDouble(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]),
                    Double.parseDouble(args[8]), Integer.parseInt(args[9]), Double.parseDouble(args[10])) : demoRequest();
            recommendation = PricingEngine.optimize(trained, request);
            System.out.println(PricingEngine.describe(recommendation, trained.selected().name()));
        }
        Reports.write(trained, recommendation, Path.of("output"));
        System.out.println("Reports written to " + Path.of("output").toAbsolutePath());
    }

    public static PricingEngine.Request demoRequest() {
        return new PricingEngine.Request(120, 80, 100, 150, 1, 80, 125, 1, 1.0);
    }
}
