package com.mitanshu.pricing;

import java.math.BigDecimal;
import java.util.*;

public final class PricingEngine {
    public enum Status { RECOMMENDED, NO_STOCK, NO_POSITIVE_CONTRIBUTION, BASELINE_SELECTED }
    public record Request(double currentPrice, double unitCost, double minimumPrice, double maximumPrice,
                          double step, int stock, double competitorPrice, int segment, double seasonIndex) {}
    public record Candidate(double price, double predictedDemand, double sellableUnits, double contribution) {}
    public record Result(Status status, Candidate best, Candidate current, List<Candidate> candidates, List<String> notes) {}

    public static Result optimize(TrainingPipeline.Result trained, Request request) {
        validate(trained, request); // Refuse unsupported inputs before asking a model to predict.
        List<String> notes = new ArrayList<>();
        notes.add("Synthetic-data demonstration; quantities and gross contribution are estimates, not realised sales or profit.");
        notes.add("Optimal only within the tested price grid; fixed costs, taxes, bargaining and competitor reactions are excluded.");
        DemandModel model = trained.selected();
        Candidate current = evaluate(model, request, cents(request.currentPrice()), notes);
        if (request.stock() == 0) return new Result(Status.NO_STOCK, null, current, List.of(), List.copyOf(notes));
        if (model.name().equals("Mean baseline")) {
            notes.add("Neither ML model beat the mean baseline on validation. A price-sensitive recommendation is withheld.");
            return new Result(Status.BASELINE_SELECTED, null, current, List.of(), List.copyOf(notes));
        }
        long minimum = cents(request.minimumPrice());
        long maximum = cents(request.maximumPrice());
        long step = cents(request.step());
        List<Candidate> candidates = new ArrayList<>();
        Candidate best = null;
        for (long price = minimum; price <= maximum; price += step) {
            Candidate candidate = evaluate(model, request, price, notes);
            candidates.add(candidate);
            if (best == null || candidate.contribution() > best.contribution() + 1e-9) best = candidate;
        }
        if (cents(candidates.get(candidates.size() - 1).price()) != maximum) {
            Candidate candidate = evaluate(model, request, maximum, notes); // Include the maximum even if the grid does not land exactly on it.
            candidates.add(candidate);
            if (candidate.contribution() > best.contribution() + 1e-9) best = candidate;
        }
        if (best.contribution() <= 0) {
            return new Result(Status.NO_POSITIVE_CONTRIBUTION, null, current, List.copyOf(candidates), List.copyOf(notes));
        }
        if (best.contribution() < current.contribution() - 1e-9) {
            notes.add("The best allowed candidate estimates less contribution than the current price; review the requested price bounds.");
        }
        return new Result(Status.RECOMMENDED, best, current, List.copyOf(candidates), List.copyOf(notes));
    }

    private static Candidate evaluate(DemandModel model, Request request, long priceInCents, List<String> notes) {
        double price = priceInCents / 100.0; // Integer cents avoid floating-point increments in the search grid.
        double demand = model.predict(new double[]{price, request.competitorPrice(), request.segment(), request.seasonIndex()});
        if (!Double.isFinite(demand) || demand > 1_000_000) throw new IllegalStateException("Model produced an invalid demand estimate.");
        if (demand < 0) {
            String note = "A negative model demand estimate was clipped to zero.";
            if (!notes.contains(note)) notes.add(note);
            demand = 0;
        }
        double units = Math.min(demand, request.stock()); // Cannot sell more units than available inventory.
        double contribution = (price - request.unitCost()) * units;
        return new Candidate(price, demand, units, contribution);
    }

    private static void validate(TrainingPipeline.Result trained, Request request) {
        Transaction.checkNumber("Current price", request.currentPrice(), 0.01, 1_000_000);
        Transaction.checkNumber("Unit cost", request.unitCost(), 0.01, 1_000_000);
        Transaction.checkNumber("Minimum price", request.minimumPrice(), 0.01, 1_000_000);
        Transaction.checkNumber("Maximum price", request.maximumPrice(), 0.01, 1_000_000);
        Transaction.checkNumber("Price step", request.step(), 0.01, 1_000_000);
        Transaction.checkNumber("Competitor price", request.competitorPrice(), 0.01, 1_000_000);
        Transaction.checkNumber("Season index", request.seasonIndex(), 0.1, 3);
        if (request.segment() != 0 && request.segment() != 1) throw new IllegalArgumentException("Segment must be 0 or 1.");
        if (request.stock() < 0 || request.stock() > 1_000_000) throw new IllegalArgumentException("Stock must be between 0 and 1000000.");
        if (request.minimumPrice() > request.maximumPrice()) throw new IllegalArgumentException("Minimum price exceeds maximum price.");
        long minimum = cents(request.minimumPrice());
        long maximum = cents(request.maximumPrice());
        long step = cents(request.step());
        cents(request.currentPrice());
        cents(request.unitCost());
        cents(request.competitorPrice());
        if ((maximum - minimum) / step + 2 > 2001) throw new IllegalArgumentException("Use a larger step: at most 2001 candidates are supported.");
        within(trained, 0, request.minimumPrice(), "Minimum price");
        within(trained, 0, request.maximumPrice(), "Maximum price");
        within(trained, 0, request.currentPrice(), "Current price");
        within(trained, 1, request.competitorPrice(), "Competitor price");
        within(trained, 2, request.segment(), "Segment");
        within(trained, 3, request.seasonIndex(), "Season index");
    }

    private static void within(TrainingPipeline.Result trained, int feature, double value, String name) {
        if (value < trained.minimum(feature) - 1e-9 || value > trained.maximum(feature) + 1e-9) {
            throw new IllegalArgumentException(name + " is outside the training range [" +
                    trained.minimum(feature) + ", " + trained.maximum(feature) + "].");
        }
    }

    private static long cents(double money) {
        try {
            return BigDecimal.valueOf(money).movePointRight(2).longValueExact();
        } catch (ArithmeticException failure) {
            throw new IllegalArgumentException("Money and price steps must have at most two decimal places.", failure);
        }
    }

    public static String describe(Result result, String model) {
        StringBuilder text = new StringBuilder("Status: " + result.status() + "\nModel: " + model + "\n");
        if (result.status() == Status.NO_STOCK) text.append("No inventory is available. No price is recommended.\n");
        if (result.status() == Status.NO_POSITIVE_CONTRIBUTION) text.append("No allowed price has a positive estimated gross contribution.\n");
        if (result.best() != null) {
            text.append(String.format(Locale.ROOT,
                    "Recommended price: INR %.2f%nPredicted demand: %.2f units%nStock-limited units: %.2f%nEstimated gross contribution: INR %.2f%n",
                    result.best().price(), result.best().predictedDemand(), result.best().sellableUnits(), result.best().contribution()));
            text.append(String.format(Locale.ROOT, "Current-price gross contribution estimate: INR %.2f%n",
                    result.current().contribution()));
        }
        for (String note : result.notes()) text.append("- ").append(note).append("\n");
        return text.toString();
    }
}
