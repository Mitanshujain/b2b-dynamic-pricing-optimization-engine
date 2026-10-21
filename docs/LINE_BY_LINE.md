# Java source, line by line

Read BUILD_GUIDE.md first for business assumptions, algorithms and application flow. Line numbers below refer to the delivered source files. Each nonblank physical line is shown, including braces and continuation lines. Blank lines only separate sections and have no runtime effect.

This explains our application/test code. Smile supplies the ML solvers, Commons CSV supplies CSV parsing, and Maven Wrapper is upstream build bootstrap; their internal implementations are not recreated here.

## src/main/java/com/mitanshu/pricing/SetupCheck.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing; // This class belongs to our project's package.` | This class belongs to our project's package. |
| 3 | `public class SetupCheck { // A class groups the instructions for this small setup check.` | A class groups the instructions for this small setup check. |
| 4 | `public static void main(String[] args) { // Java starts execution here when we run this class.` | Java starts execution here when we run this class. |
| 5 | `String projectName = "B2B Dynamic Pricing Optimization Engine"; // Save text in a String variable.` | Save text in a String variable. |
| 6 | `double unitCost = 80.0; // Assume a pack costs INR 80.` | Assume a pack costs INR 80. |
| 7 | `double sellingPrice = 120.0; // Assume its selling price is INR 120.` | Assume its selling price is INR 120. |
| 8 | `int quantity = 50; // Assume we sell 50 packs.` | Assume we sell 50 packs. |
| 9 | `double contribution = (sellingPrice - unitCost) * quantity; // Unit margin multiplied by packs sold.` | Unit margin multiplied by packs sold. |
| 10 | `System.out.println(projectName); // Display the project name.` | Display the project name. |
| 11 | `System.out.println("Java version: " + System.getProperty("java.version")); // Show the JDK used by IntelliJ.` | Show the JDK used by IntelliJ. |
| 12 | `System.out.println("Estimated gross contribution: INR " + contribution); // Display INR 2000.0.` | Display INR 2000.0. |
| 13 | `System.out.println("Setup check passed."); // Confirms this simple class reached its final statement.` | Confirms this simple class reached its final statement. |
| 14 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 15 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/Transaction.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing; // Groups related classes under one unique Java package.` | Groups related classes under one unique Java package. |
| 3 | `import java.time.LocalDate; // Represents dates without a clock time or timezone.` | Represents dates without a clock time or timezone. |
| 5 | `public record Transaction(LocalDate date, String productId, double price,` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 6 | `double competitorPrice, int segment, double seasonIndex,` | Declares/initialises these values from the shown expression in the current operation. |
| 7 | `double unitCost, double quantity) {` | Declares/initialises these values from the shown expression in the current operation. |
| 8 | `// A record gives us a constructor, getters, equality, and immutable fields.` | A record gives us a constructor, getters, equality, and immutable fields. |
| 9 | `public Transaction {` | Starts the record’s compact constructor, which validates every new observation. |
| 10 | `if (date == null) throw new IllegalArgumentException("Date is required.");` | Checks the shown condition and executes its validation/action only if true. |
| 11 | `if (productId == null \|\| productId.isBlank()) throw new IllegalArgumentException("Product ID is required.");` | Checks the shown condition and executes its validation/action only if true. |
| 12 | `checkNumber("Price", price, 0.01, 1_000_000); // Refuse missing, infinite, negative, or implausibly large prices.` | Refuse missing, infinite, negative, or implausibly large prices. |
| 13 | `checkNumber("Competitor price", competitorPrice, 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 14 | `if (segment != 0 && segment != 1) throw new IllegalArgumentException("Segment must be 0 or 1.");` | Checks the shown condition and executes its validation/action only if true. |
| 15 | `checkNumber("Season index", seasonIndex, 0.1, 3);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 16 | `checkNumber("Unit cost", unitCost, 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 17 | `checkNumber("Quantity", quantity, 0, 1_000_000); // Zero-demand observations are valid training examples.` | Zero-demand observations are valid training examples. |
| 18 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 20 | `public double[] features() {` | Declares features: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 21 | `return new double[]{price, competitorPrice, segment, seasonIndex}; // Fixed feature order is essential.` | Fixed feature order is essential. |
| 22 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 24 | `public static void checkNumber(String name, double value, double minimum, double maximum) {` | Declares checkNumber: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 25 | `if (!Double.isFinite(value) \|\| value < minimum \|\| value > maximum) {` | Checks the shown condition and executes its validation/action only if true. |
| 26 | `throw new IllegalArgumentException(name + " must be finite and between " + minimum + " and " + maximum + ".");` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 27 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 28 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 29 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/SyntheticData.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.apache.commons.csv.CSVFormat;` | Imports org.apache.commons.csv.CSVFormat so its types are available by short names. |
| 4 | `import org.apache.commons.csv.CSVPrinter;` | Imports org.apache.commons.csv.CSVPrinter so its types are available by short names. |
| 5 | `import java.io.IOException;` | Imports java.io.IOException so its types are available by short names. |
| 6 | `import java.nio.charset.StandardCharsets;` | Imports java.nio.charset.StandardCharsets so its types are available by short names. |
| 7 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 8 | `import java.time.LocalDate;` | Imports java.time.LocalDate so its types are available by short names. |
| 9 | `import java.util.Locale;` | Imports java.util.Locale so its types are available by short names. |
| 10 | `import java.util.Random;` | Imports java.util.Random so its types are available by short names. |
| 12 | `public final class SyntheticData {` | Declares the named class; final prevents subclassing if present. |
| 13 | `public static void generate(Path destination, int count, long seed) throws IOException {` | Declares generate: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 14 | `if (count < 200 \|\| count > 10_000) throw new IllegalArgumentException("Generate between 200 and 10000 rows.");` | Checks the shown condition and executes its validation/action only if true. |
| 15 | `Path parent = destination.toAbsolutePath().getParent();` | Declares/initialises parent from the shown expression in the current operation. |
| 16 | `Files.createDirectories(parent); // Create the data folder if necessary.` | Create the data folder if necessary. |
| 17 | `Random random = new Random(seed); // The same seed reproduces the same dataset.` | The same seed reproduces the same dataset. |
| 18 | `CSVFormat format = CSVFormat.RFC4180.builder().setHeader(CsvData.HEADER).get();` | Configures correct comma/quote CSV parsing or writing rather than splitting text naively. |
| 19 | `try (CSVPrinter writer = new CSVPrinter(Files.newBufferedWriter(destination, StandardCharsets.UTF_8,` | Starts try-with-resources so opened file/CSV resources close automatically even after failure. |
| 20 | `StandardOpenOption.CREATE_NEW), format)) { // Never silently overwrite an existing dataset.` | Never silently overwrite an existing dataset. |
| 21 | `for (int day = 0; day < count; day++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 22 | `LocalDate date = LocalDate.of(2024, 1, 1).plusDays(day);` | Assigns one unique observation date per loop iteration. |
| 23 | `double price = round(90 + random.nextDouble() * 70); // Randomised prices separate price effects from segment effects.` | Randomised prices separate price effects from segment effects. |
| 24 | `double competitor = round(100 + random.nextDouble() * 50);` | Samples a competitor price independently of offered price. |
| 25 | `int segment = random.nextBoolean() ? 1 : 0; // 0 = small buyer; 1 = larger buyer.` | 0 = small buyer; 1 = larger buyer. |
| 26 | `double season = round(0.8 + random.nextDouble() * 0.4); // Known external market/season index, not the target.` | Known external market/season index, not the target. |
| 27 | `double cost = round(70 + random.nextDouble() * 20);` | Declares/initialises cost from the shown expression in the current operation. |
| 28 | `double quantity = Math.max(0, Math.round(expectedDemand(price, competitor, segment, season)` | Declares/initialises quantity from the shown expression in the current operation. |
| 29 | `+ random.nextGaussian() * 3)); // Add observation noise, then round to nonnegative units.` | Add observation noise, then round to nonnegative units. |
| 30 | `writer.printRecord(date, "BOLT-M8-100", number(price), number(competitor), segment,` | Writes the supplied values as one correctly escaped CSV record. |
| 31 | `number(season), number(cost), number(quantity));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 32 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 33 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 34 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 36 | `public static double expectedDemand(double price, double competitor, int segment, double season) {` | Declares expectedDemand: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 37 | `return Math.max(0, 115 + 25 * segment + 30 * (season - 1)` | Starts the simulator quantity expectation with base demand, segment and season effects. |
| 38 | `- 1.4 * (price - 100) + 0.45 * (competitor - 120));` | Applies negative own-price and positive competitor-price effects in the simulator. |
| 39 | `// This formula exists only in the simulator; models never receive it during training.` | This formula exists only in the simulator; models never receive it during training. |
| 40 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 42 | `private static double round(double number) {` | Declares round: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 43 | `return Math.round(number * 100) / 100.0; // Two-decimal formatting for generated numeric inputs.` | Two-decimal formatting for generated numeric inputs. |
| 44 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 46 | `private static String number(double number) {` | Declares number: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 47 | `return String.format(Locale.ROOT, "%.2f", number); // A decimal point works independently of computer locale.` | A decimal point works independently of computer locale. |
| 48 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 49 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/CsvData.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.apache.commons.csv.CSVFormat; // Parses CSV quoting and column boundaries correctly.` | Parses CSV quoting and column boundaries correctly. |
| 4 | `import org.apache.commons.csv.CSVParser;` | Imports org.apache.commons.csv.CSVParser so its types are available by short names. |
| 5 | `import org.apache.commons.csv.CSVRecord;` | Imports org.apache.commons.csv.CSVRecord so its types are available by short names. |
| 6 | `import org.apache.commons.csv.DuplicateHeaderMode;` | Imports org.apache.commons.csv.DuplicateHeaderMode so its types are available by short names. |
| 7 | `import java.io.IOException;` | Imports java.io.IOException so its types are available by short names. |
| 8 | `import java.io.Reader;` | Imports java.io.Reader so its types are available by short names. |
| 9 | `import java.nio.charset.StandardCharsets;` | Imports java.nio.charset.StandardCharsets so its types are available by short names. |
| 10 | `import java.nio.file.Files;` | Imports java.nio.file.Files so its types are available by short names. |
| 11 | `import java.nio.file.Path;` | Imports java.nio.file.Path so its types are available by short names. |
| 12 | `import java.time.LocalDate;` | Imports java.time.LocalDate so its types are available by short names. |
| 13 | `import java.util.*;` | Imports java.util.* so its types are available by short names. |
| 15 | `public final class CsvData {` | Declares the named class; final prevents subclassing if present. |
| 16 | `public static final String[] HEADER = {"date", "product_id", "price", "competitor_price",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 17 | `"customer_segment", "season_index", "unit_cost", "quantity"};` | Completes the shown Java statement using the variables/methods described in this block. |
| 19 | `public static List<Transaction> load(Path file) throws IOException {` | Declares load: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 20 | `List<Transaction> rows = new ArrayList<>(); // Stores successfully validated observations.` | Stores successfully validated observations. |
| 21 | `Set<LocalDate> dates = new HashSet<>(); // Detects duplicate daily observations.` | Detects duplicate daily observations. |
| 22 | `Set<String> products = new HashSet<>(); // This first version intentionally models one SKU.` | This first version intentionally models one SKU. |
| 23 | `CSVFormat format = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true)` | Configures correct comma/quote CSV parsing or writing rather than splitting text naively. |
| 24 | `.setTrim(true).setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW).get();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 25 | `try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);` | Starts try-with-resources so opened file/CSV resources close automatically even after failure. |
| 26 | `CSVParser parser = format.parse(reader)) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 27 | `if (!parser.getHeaderNames().equals(Arrays.asList(HEADER))) {` | Requires the header names/order to match the supported CSV schema. |
| 28 | `throw new IllegalArgumentException("Expected exactly these ordered CSV headers: " + String.join(",", HEADER));` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 29 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 30 | `for (CSVRecord row : parser) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 31 | `if (rows.size() >= 10_000) throw new IllegalArgumentException("Learning edition supports at most 10000 rows.");` | Checks the shown condition and executes its validation/action only if true. |
| 32 | `try {` | Begins work protected by the following exception handlers. |
| 33 | `if (!row.isConsistent()) throw new IllegalArgumentException("Incorrect number of columns.");` | Checks that the record has the expected number of CSV fields. |
| 34 | `for (String column : HEADER) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 35 | `if (row.get(column).isBlank()) throw new IllegalArgumentException("Missing value: " + column);` | Rejects empty required data rather than silently imputing it. |
| 36 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 37 | `Transaction item = new Transaction(LocalDate.parse(row.get("date")), row.get("product_id"),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 38 | `Double.parseDouble(row.get("price")), Double.parseDouble(row.get("competitor_price")),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 39 | `Integer.parseInt(row.get("customer_segment")), Double.parseDouble(row.get("season_index")),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 40 | `Double.parseDouble(row.get("unit_cost")), Double.parseDouble(row.get("quantity")));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 41 | `if (!dates.add(item.date())) throw new IllegalArgumentException("Duplicate date: " + item.date());` | A set insertion returning false signals a duplicate date. |
| 42 | `products.add(item.productId());` | Records product identity to detect accidental mixed-product data. |
| 43 | `if (products.size() > 1) throw new IllegalArgumentException("Use one product per training file.");` | Refuses mixed products because this edition trains one product per file. |
| 44 | `rows.add(item);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 45 | `} catch (RuntimeException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 46 | `throw new IllegalArgumentException("CSV record " + row.getRecordNumber() + ": " + failure.getMessage(), failure);` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 47 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 48 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 49 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 50 | `if (rows.isEmpty()) throw new IllegalArgumentException("CSV contains no observations.");` | Checks the shown condition and executes its validation/action only if true. |
| 51 | `rows.sort(Comparator.comparing(Transaction::date)); // Oldest dates come first regardless of original row order.` | Oldest dates come first regardless of original row order. |
| 52 | `return List.copyOf(rows); // Callers cannot accidentally change the loaded dataset.` | Callers cannot accidentally change the loaded dataset. |
| 53 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 54 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/DemandModel.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `public interface DemandModel {` | Defines the shared model contract used by baseline, Random Forest and SVR wrappers. |
| 4 | `String name(); // Reports the model type used in output.` | Reports the model type used in output. |
| 5 | `double predict(double[] features); // Predicts units for one feature vector.` | Predicts units for one feature vector. |
| 6 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/Standardizer.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import java.util.List;` | Imports java.util.List so its types are available by short names. |
| 5 | `public final class Standardizer {` | Declares the named class; final prevents subclassing if present. |
| 6 | `private final double[] means = new double[4];` | Completes the shown Java statement using the variables/methods described in this block. |
| 7 | `private final double[] deviations = new double[4];` | Completes the shown Java statement using the variables/methods described in this block. |
| 9 | `public Standardizer(List<Transaction> training) {` | Declares Standardizer: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 10 | `if (training.isEmpty()) throw new IllegalArgumentException("Training rows are required.");` | Checks the shown condition and executes its validation/action only if true. |
| 11 | `for (Transaction row : training) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 12 | `double[] values = row.features();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 13 | `for (int column = 0; column < 4; column++) means[column] += values[column];` | Accumulates each feature column for its training mean. |
| 14 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 15 | `for (int column = 0; column < 4; column++) means[column] /= training.size(); // Training-only feature means.` | Training-only feature means. |
| 16 | `for (Transaction row : training) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 17 | `double[] values = row.features();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 18 | `for (int column = 0; column < 4; column++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 19 | `double difference = values[column] - means[column];` | Declares/initialises difference from the shown expression in the current operation. |
| 20 | `deviations[column] += difference * difference; // Sum squared distances from each feature mean.` | Sum squared distances from each feature mean. |
| 21 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 22 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 23 | `for (int column = 0; column < 4; column++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 24 | `deviations[column] = Math.sqrt(deviations[column] / training.size());` | Takes the square root of mean squared distance to obtain standard deviation. |
| 25 | `if (deviations[column] < 1e-12) deviations[column] = 1; // Avoid dividing by zero for constant columns.` | Avoid dividing by zero for constant columns. |
| 26 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 27 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 29 | `public double[] transform(double[] values) {` | Declares transform: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 30 | `if (values.length != 4) throw new IllegalArgumentException("Expected four features.");` | Checks the shown condition and executes its validation/action only if true. |
| 31 | `double[] scaled = new double[4]; // Leave the caller's original values untouched.` | Leave the caller's original values untouched. |
| 32 | `for (int column = 0; column < 4; column++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 33 | `if (!Double.isFinite(values[column])) throw new IllegalArgumentException("Feature is not finite.");` | Checks the shown condition and executes its validation/action only if true. |
| 34 | `scaled[column] = (values[column] - means[column]) / deviations[column]; // z = (x - mean) / standard deviation.` | z = (x - mean) / standard deviation. |
| 35 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 36 | `return scaled;` | Returns the shown value to the caller and ends this method/callback. |
| 37 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 38 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/Metrics.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import java.util.List;` | Imports java.util.List so its types are available by short names. |
| 5 | `public record Metrics(double mae, double rmse) {` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 6 | `public static Metrics evaluate(DemandModel model, List<Transaction> rows) {` | Declares evaluate: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 7 | `if (rows.isEmpty()) throw new IllegalArgumentException("Evaluation data is empty.");` | Checks the shown condition and executes its validation/action only if true. |
| 8 | `double absoluteError = 0;` | Declares/initialises absoluteError from the shown expression in the current operation. |
| 9 | `double squaredError = 0;` | Declares/initialises squaredError from the shown expression in the current operation. |
| 10 | `for (Transaction row : rows) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 11 | `double predicted = model.predict(row.features());` | Declares/initialises predicted from the shown expression in the current operation. |
| 12 | `if (!Double.isFinite(predicted)) throw new IllegalStateException("Model produced a non-finite prediction.");` | Checks the shown condition and executes its validation/action only if true. |
| 13 | `predicted = Math.max(0, predicted); // Evaluation uses the same nonnegative-demand rule as recommendations.` | Evaluation uses the same nonnegative-demand rule as recommendations. |
| 14 | `double error = predicted - row.quantity();` | Calculates signed prediction error in packs. |
| 15 | `absoluteError += Math.abs(error);` | Accumulates absolute errors for MAE. |
| 16 | `squaredError += error * error;` | Accumulates squared errors for RMSE. |
| 17 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 18 | `return new Metrics(absoluteError / rows.size(), Math.sqrt(squaredError / rows.size()));` | Returns mean absolute error and the square root of mean squared error. |
| 19 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 20 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/TrainingPipeline.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import smile.data.DataFrame;` | Imports smile.data.DataFrame so its types are available by short names. |
| 4 | `import smile.data.formula.Formula;` | Imports smile.data.formula.Formula so its types are available by short names. |
| 5 | `import smile.math.kernel.GaussianKernel;` | Imports smile.math.kernel.GaussianKernel so its types are available by short names. |
| 6 | `import smile.regression.RandomForest;` | Imports smile.regression.RandomForest so its types are available by short names. |
| 7 | `import smile.regression.SVM;` | Imports smile.regression.SVM so its types are available by short names. |
| 8 | `import java.util.*;` | Imports java.util.* so its types are available by short names. |
| 9 | `import java.util.function.ToDoubleFunction;` | Imports java.util.function.ToDoubleFunction so its types are available by short names. |
| 10 | `import java.util.stream.LongStream;` | Imports java.util.stream.LongStream so its types are available by short names. |
| 12 | `public final class TrainingPipeline {` | Declares the named class; final prevents subclassing if present. |
| 13 | `private static final String[] COLUMNS = {"price", "competitor_price", "segment", "season_index", "quantity"};` | Completes the shown Java statement using the variables/methods described in this block. |
| 15 | `public record Evaluation(String model, Metrics validation, Metrics test) {}` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 16 | `public record Result(String productId, List<Transaction> training, List<Transaction> validation,` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 17 | `List<Transaction> test, List<Evaluation> evaluations, DemandModel selected) {` | Declares/initialises these values from the shown expression in the current operation. |
| 18 | `public double minimum(int feature) {` | Declares minimum: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 19 | `return training.stream().mapToDouble(row -> row.features()[feature]).min().orElseThrow();` | Returns the shown value to the caller and ends this method/callback. |
| 20 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 21 | `public double maximum(int feature) {` | Declares maximum: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 22 | `return training.stream().mapToDouble(row -> row.features()[feature]).max().orElseThrow();` | Returns the shown value to the caller and ends this method/callback. |
| 23 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 24 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 26 | `public static Result train(List<Transaction> observations) {` | Declares train: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 27 | `if (observations.size() < 200) throw new IllegalArgumentException("At least 200 daily observations are required.");` | Checks the shown condition and executes its validation/action only if true. |
| 28 | `List<Transaction> sorted = observations.stream().sorted(Comparator.comparing(Transaction::date)).toList();` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 29 | `String product = sorted.get(0).productId();` | Declares/initialises product from the shown expression in the current operation. |
| 30 | `if (sorted.stream().anyMatch(row -> !row.productId().equals(product))) {` | Checks the shown condition and executes its validation/action only if true. |
| 31 | `throw new IllegalArgumentException("Train a separate model for each product.");` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 32 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 33 | `if (sorted.stream().map(Transaction::date).distinct().count() != sorted.size()) {` | Checks the shown condition and executes its validation/action only if true. |
| 34 | `throw new IllegalArgumentException("Duplicate daily observations are not allowed.");` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 35 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 36 | `int trainingEnd = (int) (sorted.size() * 0.6);` | Places the training boundary after the oldest 60 percent of observations. |
| 37 | `int validationEnd = (int) (sorted.size() * 0.8);` | Places the validation boundary after 80 percent, leaving the latest 20 percent for testing. |
| 38 | `List<Transaction> training = List.copyOf(sorted.subList(0, trainingEnd));` | Copies the oldest observations into the training partition. |
| 39 | `List<Transaction> validation = List.copyOf(sorted.subList(trainingEnd, validationEnd));` | Copies the middle observations into the validation partition. |
| 40 | `List<Transaction> test = List.copyOf(sorted.subList(validationEnd, sorted.size()));` | Copies the newest observations into the test partition. |
| 41 | `double average = training.stream().mapToDouble(Transaction::quantity).average().orElseThrow();` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 42 | `double variance = training.stream().mapToDouble(row -> Math.pow(row.quantity() - average, 2)).average().orElseThrow();` | Computes training target variance from squared deviations around the training mean. |
| 43 | `if (variance < 1e-12) throw new IllegalArgumentException("Training quantities have no variation.");` | Checks the shown condition and executes its validation/action only if true. |
| 45 | `DemandModel baseline = named("Mean baseline", features -> average); // A real ML model must be compared with a simple alternative.` | A real ML model must be compared with a simple alternative. |
| 46 | `DemandModel forest = randomForest(training);` | Declares/initialises forest from the shown expression in the current operation. |
| 47 | `DemandModel svr = supportVectorRegression(training, average, Math.sqrt(variance));` | Declares/initialises svr from the shown expression in the current operation. |
| 48 | `List<DemandModel> models = List.of(baseline, forest, svr);` | Declares/initialises models from the shown expression in the current operation. |
| 49 | `List<Metrics> validationMetrics = models.stream().map(model -> Metrics.evaluate(model, validation)).toList();` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 50 | `int best = 0;` | Declares/initialises best from the shown expression in the current operation. |
| 51 | `for (int index = 1; index < models.size(); index++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 52 | `if (validationMetrics.get(index).rmse() < validationMetrics.get(best).rmse()) best = index;` | Selects the smaller validation error; test errors are not used for selection. |
| 53 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 54 | `DemandModel selected = models.get(best); // Selection uses validation data only.` | Selection uses validation data only. |
| 55 | `List<Evaluation> evaluations = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 56 | `for (int index = 0; index < models.size(); index++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 57 | `evaluations.add(new Evaluation(models.get(index).name(), validationMetrics.get(index),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 58 | `Metrics.evaluate(models.get(index), test))); // Test metrics do not change the selected model.` | Test metrics do not change the selected model. |
| 59 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 60 | `return new Result(product, training, validation, test, List.copyOf(evaluations), selected);` | Returns the shown value to the caller and ends this method/callback. |
| 61 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 63 | `private static DemandModel randomForest(List<Transaction> training) {` | Declares randomForest: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 64 | `double[][] matrix = new double[training.size()][5];` | Completes the shown Java statement using the variables/methods described in this block. |
| 65 | `for (int index = 0; index < training.size(); index++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 66 | `System.arraycopy(training.get(index).features(), 0, matrix[index], 0, 4);` | Copies the four predictors into this training row in their fixed feature order. |
| 67 | `matrix[index][4] = training.get(index).quantity(); // Target is separate from the four input features.` | Target is separate from the four input features. |
| 68 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 69 | `DataFrame frame = DataFrame.of(matrix, COLUMNS);` | Wraps numeric rows in a named Smile table so formula/model code can identify the columns. |
| 70 | `RandomForest forest = RandomForest.fit(Formula.lhs("quantity"), frame,` | Fits the forest with the quantity response; the following arguments specify its hyperparameters. |
| 71 | `80, 3, 12, 100, 5, 1.0, LongStream.range(0, 80).map(index -> 2026 + index));` | Sets 80 trees, 3 features per split, depth 12, at most 100 leaves, minimum node size 5, bootstrap sampling and seeded trees. |
| 72 | `// 80 trees, 3 candidate features per split, depth 12, max 100 leaves, leaf size 5, bootstrap sampling.` | 80 trees, 3 candidate features per split, depth 12, max 100 leaves, leaf size 5, bootstrap sampling. |
| 73 | `return named("Random Forest", features -> {` | Returns the shown value to the caller and ends this method/callback. |
| 74 | `double[] row = Arrays.copyOf(features, 5); // The unused target slot is zero for an inference row.` | The unused target slot is zero for an inference row. |
| 75 | `return forest.predict(DataFrame.of(new double[][]{row}, COLUMNS).get(0));` | Wraps numeric rows in a named Smile table so formula/model code can identify the columns. |
| 76 | `});` | Closes the current method, conditional, loop, class or callback scope. |
| 77 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 79 | `private static DemandModel supportVectorRegression(List<Transaction> training, double targetMean, double targetDeviation) {` | Declares supportVectorRegression: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 80 | `Standardizer scaler = new Standardizer(training); // Fit scaling on training rows, never on validation/test.` | Fit scaling on training rows, never on validation/test. |
| 81 | `double[][] features = training.stream().map(row -> scaler.transform(row.features())).toArray(double[][]::new);` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 82 | `double[] targets = training.stream().mapToDouble(row -> (row.quantity() - targetMean) / targetDeviation).toArray();` | Normalises training targets using training target statistics. |
| 83 | `var model = SVM.fit(features, targets, new GaussianKernel(1.5), 0.05, 10.0, 1e-3);` | Fits Gaussian-kernel SVR to scaled features and targets using sigma 1.5, epsilon 0.05, C 10 and tolerance 0.001. |
| 84 | `// Gaussian sigma 1.5, epsilon 0.05, C 10, solver tolerance 0.001; all are fixed before test evaluation.` | Gaussian sigma 1.5, epsilon 0.05, C 10, solver tolerance 0.001; all are fixed before test evaluation. |
| 85 | `return named("Support Vector Regression", values ->` | Returns the shown value to the caller and ends this method/callback. |
| 86 | `model.predict(scaler.transform(values)) * targetDeviation + targetMean); // Convert normalised output back to units.` | Convert normalised output back to units. |
| 87 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 89 | `private static DemandModel named(String name, ToDoubleFunction<double[]> prediction) {` | Declares named: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 90 | `return new DemandModel() {` | Returns the shown value to the caller and ends this method/callback. |
| 91 | `public String name() { return name; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 92 | `public double predict(double[] features) {` | Declares predict: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 93 | `if (features.length != 4) throw new IllegalArgumentException("Expected four features.");` | Checks the shown condition and executes its validation/action only if true. |
| 94 | `for (double value : features) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 95 | `if (!Double.isFinite(value)) throw new IllegalArgumentException("Features must be finite.");` | Checks the shown condition and executes its validation/action only if true. |
| 96 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 97 | `return prediction.applyAsDouble(features);` | Returns the shown value to the caller and ends this method/callback. |
| 98 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 99 | `};` | Closes the current method, conditional, loop, class or callback scope. |
| 100 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 101 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/PricingEngine.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import java.math.BigDecimal;` | Imports java.math.BigDecimal so its types are available by short names. |
| 4 | `import java.util.*;` | Imports java.util.* so its types are available by short names. |
| 6 | `public final class PricingEngine {` | Declares the named class; final prevents subclassing if present. |
| 7 | `public enum Status { RECOMMENDED, NO_STOCK, NO_POSITIVE_CONTRIBUTION, BASELINE_SELECTED }` | Declares the supported pricing outcome labels. |
| 8 | `public record Request(double currentPrice, double unitCost, double minimumPrice, double maximumPrice,` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 9 | `double step, int stock, double competitorPrice, int segment, double seasonIndex) {}` | Declares/initialises these values from the shown expression in the current operation. |
| 10 | `public record Candidate(double price, double predictedDemand, double sellableUnits, double contribution) {}` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 11 | `public record Result(Status status, Candidate best, Candidate current, List<Candidate> candidates, List<String> notes) {}` | Declares an immutable data carrier with generated accessors, constructor and equality. |
| 13 | `public static Result optimize(TrainingPipeline.Result trained, Request request) {` | Declares optimize: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 14 | `validate(trained, request); // Refuse unsupported inputs before asking a model to predict.` | Refuse unsupported inputs before asking a model to predict. |
| 15 | `List<String> notes = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 16 | `notes.add("Synthetic-data demonstration; quantities and gross contribution are estimates, not realised sales or profit.");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 17 | `notes.add("Optimal only within the tested price grid; fixed costs, taxes, bargaining and competitor reactions are excluded.");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 18 | `DemandModel model = trained.selected();` | Declares/initialises model from the shown expression in the current operation. |
| 19 | `Candidate current = evaluate(model, request, cents(request.currentPrice()), notes);` | Declares/initialises current from the shown expression in the current operation. |
| 20 | `if (request.stock() == 0) return new Result(Status.NO_STOCK, null, current, List.of(), List.copyOf(notes));` | Checks the shown condition and executes its validation/action only if true. |
| 21 | `if (model.name().equals("Mean baseline")) {` | Prevents a constant-demand baseline from being presented as a price-sensitive model. |
| 22 | `notes.add("Neither ML model beat the mean baseline on validation. A price-sensitive recommendation is withheld.");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 23 | `return new Result(Status.BASELINE_SELECTED, null, current, List.of(), List.copyOf(notes));` | Returns the shown value to the caller and ends this method/callback. |
| 24 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 25 | `long minimum = cents(request.minimumPrice());` | Declares/initialises minimum from the shown expression in the current operation. |
| 26 | `long maximum = cents(request.maximumPrice());` | Declares/initialises maximum from the shown expression in the current operation. |
| 27 | `long step = cents(request.step());` | Declares/initialises step from the shown expression in the current operation. |
| 28 | `List<Candidate> candidates = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 29 | `Candidate best = null;` | Declares/initialises best from the shown expression in the current operation. |
| 30 | `for (long price = minimum; price <= maximum; price += step) {` | Enumerates candidate prices in ascending integer-cent steps. |
| 31 | `Candidate candidate = evaluate(model, request, price, notes);` | Declares/initialises candidate from the shown expression in the current operation. |
| 32 | `candidates.add(candidate);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 33 | `if (best == null \|\| candidate.contribution() > best.contribution() + 1e-9) best = candidate;` | Keeps a higher-contribution candidate; ties retain the earlier/lower price. |
| 34 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 35 | `if (cents(candidates.get(candidates.size() - 1).price()) != maximum) {` | Checks whether the last grid candidate equals the maximum endpoint. |
| 36 | `Candidate candidate = evaluate(model, request, maximum, notes); // Include the maximum even if the grid does not land exactly on it.` | Include the maximum even if the grid does not land exactly on it. |
| 37 | `candidates.add(candidate);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 38 | `if (candidate.contribution() > best.contribution() + 1e-9) best = candidate;` | Keeps a higher-contribution candidate; ties retain the earlier/lower price. |
| 39 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 40 | `if (best.contribution() <= 0) {` | Withholds a price when every allowed candidate has nonpositive contribution. |
| 41 | `return new Result(Status.NO_POSITIVE_CONTRIBUTION, null, current, List.copyOf(candidates), List.copyOf(notes));` | Returns the shown value to the caller and ends this method/callback. |
| 42 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 43 | `if (best.contribution() < current.contribution() - 1e-9) {` | Flags a constrained optimum estimated worse than the current-price comparison point. |
| 44 | `notes.add("The best allowed candidate estimates less contribution than the current price; review the requested price bounds.");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 45 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 46 | `return new Result(Status.RECOMMENDED, best, current, List.copyOf(candidates), List.copyOf(notes));` | Returns the shown value to the caller and ends this method/callback. |
| 47 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 49 | `private static Candidate evaluate(DemandModel model, Request request, long priceInCents, List<String> notes) {` | Declares evaluate: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 50 | `double price = priceInCents / 100.0; // Integer cents avoid floating-point increments in the search grid.` | Integer cents avoid floating-point increments in the search grid. |
| 51 | `double demand = model.predict(new double[]{price, request.competitorPrice(), request.segment(), request.seasonIndex()});` | Declares/initialises demand from the shown expression in the current operation. |
| 52 | `if (!Double.isFinite(demand) \|\| demand > 1_000_000) throw new IllegalStateException("Model produced an invalid demand estimate.");` | Checks the shown condition and executes its validation/action only if true. |
| 53 | `if (demand < 0) {` | Checks the shown condition and executes its validation/action only if true. |
| 54 | `String note = "A negative model demand estimate was clipped to zero.";` | Declares/initialises note from the shown expression in the current operation. |
| 55 | `if (!notes.contains(note)) notes.add(note);` | Checks the shown condition and executes its validation/action only if true. |
| 56 | `demand = 0;` | Completes the shown Java statement using the variables/methods described in this block. |
| 57 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 58 | `double units = Math.min(demand, request.stock()); // Cannot sell more units than available inventory.` | Cannot sell more units than available inventory. |
| 59 | `double contribution = (price - request.unitCost()) * units;` | Multiplies unit margin by expected sellable quantity. |
| 60 | `return new Candidate(price, demand, units, contribution);` | Returns the shown value to the caller and ends this method/callback. |
| 61 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 63 | `private static void validate(TrainingPipeline.Result trained, Request request) {` | Declares validate: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 64 | `Transaction.checkNumber("Current price", request.currentPrice(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 65 | `Transaction.checkNumber("Unit cost", request.unitCost(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 66 | `Transaction.checkNumber("Minimum price", request.minimumPrice(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 67 | `Transaction.checkNumber("Maximum price", request.maximumPrice(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 68 | `Transaction.checkNumber("Price step", request.step(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 69 | `Transaction.checkNumber("Competitor price", request.competitorPrice(), 0.01, 1_000_000);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 70 | `Transaction.checkNumber("Season index", request.seasonIndex(), 0.1, 3);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 71 | `if (request.segment() != 0 && request.segment() != 1) throw new IllegalArgumentException("Segment must be 0 or 1.");` | Checks the shown condition and executes its validation/action only if true. |
| 72 | `if (request.stock() < 0 \|\| request.stock() > 1_000_000) throw new IllegalArgumentException("Stock must be between 0 and 1000000.");` | Checks the shown condition and executes its validation/action only if true. |
| 73 | `if (request.minimumPrice() > request.maximumPrice()) throw new IllegalArgumentException("Minimum price exceeds maximum price.");` | Checks the shown condition and executes its validation/action only if true. |
| 74 | `long minimum = cents(request.minimumPrice());` | Declares/initialises minimum from the shown expression in the current operation. |
| 75 | `long maximum = cents(request.maximumPrice());` | Declares/initialises maximum from the shown expression in the current operation. |
| 76 | `long step = cents(request.step());` | Declares/initialises step from the shown expression in the current operation. |
| 77 | `cents(request.currentPrice());` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 78 | `cents(request.unitCost());` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 79 | `cents(request.competitorPrice());` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 80 | `if ((maximum - minimum) / step + 2 > 2001) throw new IllegalArgumentException("Use a larger step: at most 2001 candidates are supported.");` | Caps the number of candidates to bound search work. |
| 81 | `within(trained, 0, request.minimumPrice(), "Minimum price");` | Checks this predictor against its observed training support range. |
| 82 | `within(trained, 0, request.maximumPrice(), "Maximum price");` | Checks this predictor against its observed training support range. |
| 83 | `within(trained, 0, request.currentPrice(), "Current price");` | Checks this predictor against its observed training support range. |
| 84 | `within(trained, 1, request.competitorPrice(), "Competitor price");` | Checks this predictor against its observed training support range. |
| 85 | `within(trained, 2, request.segment(), "Segment");` | Checks this predictor against its observed training support range. |
| 86 | `within(trained, 3, request.seasonIndex(), "Season index");` | Checks this predictor against its observed training support range. |
| 87 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 89 | `private static void within(TrainingPipeline.Result trained, int feature, double value, String name) {` | Declares within: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 90 | `if (value < trained.minimum(feature) - 1e-9 \|\| value > trained.maximum(feature) + 1e-9) {` | Tests the lower and upper training-support bounds. |
| 91 | `throw new IllegalArgumentException(name + " is outside the training range [" +` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 92 | `trained.minimum(feature) + ", " + trained.maximum(feature) + "].");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 93 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 94 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 96 | `private static long cents(double money) {` | Declares cents: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 97 | `try {` | Begins work protected by the following exception handlers. |
| 98 | `return BigDecimal.valueOf(money).movePointRight(2).longValueExact();` | Converts monetary input to exact cents; excess decimal precision raises an error. |
| 99 | `} catch (ArithmeticException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 100 | `throw new IllegalArgumentException("Money and price steps must have at most two decimal places.", failure);` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 101 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 102 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 104 | `public static String describe(Result result, String model) {` | Declares describe: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 105 | `StringBuilder text = new StringBuilder("Status: " + result.status() + "\nModel: " + model + "\n");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 106 | `if (result.status() == Status.NO_STOCK) text.append("No inventory is available. No price is recommended.\n");` | Checks the shown condition and executes its validation/action only if true. |
| 107 | `if (result.status() == Status.NO_POSITIVE_CONTRIBUTION) text.append("No allowed price has a positive estimated gross contribution.\n");` | Checks the shown condition and executes its validation/action only if true. |
| 108 | `if (result.best() != null) {` | Checks the shown condition and executes its validation/action only if true. |
| 109 | `text.append(String.format(Locale.ROOT,` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 110 | `"Recommended price: INR %.2f%nPredicted demand: %.2f units%nStock-limited units: %.2f%nEstimated gross contribution: INR %.2f%n",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 111 | `result.best().price(), result.best().predictedDemand(), result.best().sellableUnits(), result.best().contribution()));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 112 | `text.append(String.format(Locale.ROOT, "Current-price gross contribution estimate: INR %.2f%n",` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 113 | `result.current().contribution()));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 114 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 115 | `for (String note : result.notes()) text.append("- ").append(note).append("\n");` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 116 | `return text.toString();` | Returns the shown value to the caller and ends this method/callback. |
| 117 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 118 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/Reports.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.apache.commons.csv.CSVFormat;` | Imports org.apache.commons.csv.CSVFormat so its types are available by short names. |
| 4 | `import org.apache.commons.csv.CSVPrinter;` | Imports org.apache.commons.csv.CSVPrinter so its types are available by short names. |
| 5 | `import java.io.IOException;` | Imports java.io.IOException so its types are available by short names. |
| 6 | `import java.nio.charset.StandardCharsets;` | Imports java.nio.charset.StandardCharsets so its types are available by short names. |
| 7 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 8 | `import java.util.*;` | Imports java.util.* so its types are available by short names. |
| 10 | `public final class Reports {` | Declares the named class; final prevents subclassing if present. |
| 11 | `public static void write(TrainingPipeline.Result trained, PricingEngine.Result recommendation, Path folder) throws IOException {` | Declares write: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 12 | `Files.createDirectories(folder);` | Creates required folders without failing if they already exist. |
| 13 | `try (CSVPrinter csv = printer(folder.resolve("evaluation.csv"),` | Starts try-with-resources so opened file/CSV resources close automatically even after failure. |
| 14 | `"model", "validation_mae", "validation_rmse", "test_mae", "test_rmse", "selected")) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 15 | `for (TrainingPipeline.Evaluation item : trained.evaluations()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 16 | `csv.printRecord(item.model(), item.validation().mae(), item.validation().rmse(),` | Writes the supplied values as one correctly escaped CSV record. |
| 17 | `item.test().mae(), item.test().rmse(), item.model().equals(trained.selected().name()));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 18 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 19 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 20 | `try (CSVPrinter csv = printer(folder.resolve("test_predictions.csv"),` | Starts try-with-resources so opened file/CSV resources close automatically even after failure. |
| 21 | `"date", "actual_units", "predicted_units", "error_units")) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 22 | `for (Transaction row : trained.test()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 23 | `double predicted = Math.max(0, trained.selected().predict(row.features()));` | Declares/initialises predicted from the shown expression in the current operation. |
| 24 | `csv.printRecord(row.date(), row.quantity(), predicted, predicted - row.quantity());` | Calculates signed prediction error in packs. |
| 25 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 26 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 27 | `Files.writeString(folder.resolve("eda.txt"), summary(trained), StandardCharsets.UTF_8);` | Writes the generated report as UTF-8 text. |
| 28 | `if (recommendation != null) {` | Checks the shown condition and executes its validation/action only if true. |
| 29 | `try (CSVPrinter csv = printer(folder.resolve("candidate_prices.csv"),` | Starts try-with-resources so opened file/CSV resources close automatically even after failure. |
| 30 | `"price_inr", "predicted_units", "stock_limited_units", "estimated_gross_contribution_inr")) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 31 | `for (PricingEngine.Candidate candidate : recommendation.candidates()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 32 | `csv.printRecord(candidate.price(), candidate.predictedDemand(), candidate.sellableUnits(), candidate.contribution());` | Writes the supplied values as one correctly escaped CSV record. |
| 33 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 34 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 35 | `Files.writeString(folder.resolve("recommendation.txt"),` | Writes the generated report as UTF-8 text. |
| 36 | `PricingEngine.describe(recommendation, trained.selected().name()), StandardCharsets.UTF_8);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 37 | `} else {` | Starts the alternative branch when the preceding condition is false. |
| 38 | `Files.deleteIfExists(folder.resolve("candidate_prices.csv")); // Remove stale pricing reports after a training-only run.` | Remove stale pricing reports after a training-only run. |
| 39 | `Files.deleteIfExists(folder.resolve("recommendation.txt"));` | Removes a stale generated price report after a training-only run. |
| 40 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 41 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 43 | `public static String summary(TrainingPipeline.Result trained) {` | Declares summary: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 44 | `List<Transaction> rows = trained.training(); // EDA below uses only the training partition.` | EDA below uses only the training partition. |
| 45 | `double priceMean = rows.stream().mapToDouble(Transaction::price).average().orElseThrow();` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 46 | `double quantityMean = rows.stream().mapToDouble(Transaction::quantity).average().orElseThrow();` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 47 | `double covariance = 0;` | Declares/initialises covariance from the shown expression in the current operation. |
| 48 | `double priceVariance = 0;` | Declares/initialises priceVariance from the shown expression in the current operation. |
| 49 | `double quantityVariance = 0;` | Declares/initialises quantityVariance from the shown expression in the current operation. |
| 50 | `for (Transaction row : rows) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 51 | `double priceDifference = row.price() - priceMean;` | Declares/initialises priceDifference from the shown expression in the current operation. |
| 52 | `double quantityDifference = row.quantity() - quantityMean;` | Declares/initialises quantityDifference from the shown expression in the current operation. |
| 53 | `covariance += priceDifference * quantityDifference;` | Accumulates centred price-quantity products for Pearson correlation. |
| 54 | `priceVariance += priceDifference * priceDifference;` | Completes the shown Java statement using the variables/methods described in this block. |
| 55 | `quantityVariance += quantityDifference * quantityDifference;` | Completes the shown Java statement using the variables/methods described in this block. |
| 56 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 57 | `double correlation = priceVariance * quantityVariance == 0 ? Double.NaN :` | Declares/initialises correlation from the shown expression in the current operation. |
| 58 | `covariance / Math.sqrt(priceVariance * quantityVariance);` | Divides covariance by the two feature standard-deviation factors. |
| 59 | `StringBuilder text = new StringBuilder("SYNTHETIC DATA - one SKU, one daily observation\n");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 60 | `text.append("Product: ").append(trained.productId()).append("\n");` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 61 | `text.append("Training / validation / test rows: ").append(rows.size()).append(" / ")` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 62 | `.append(trained.validation().size()).append(" / ").append(trained.test().size()).append("\n");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 63 | `text.append("Training dates: ").append(rows.get(0).date()).append(" to ").append(rows.get(rows.size() - 1).date()).append("\n");` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 64 | `text.append("Validation dates: ").append(trained.validation().get(0).date()).append(" to ")` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 65 | `.append(trained.validation().get(trained.validation().size() - 1).date()).append("\n");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 66 | `text.append("Test dates: ").append(trained.test().get(0).date()).append(" to ")` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 67 | `.append(trained.test().get(trained.test().size() - 1).date()).append("\n");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 68 | `text.append(String.format(Locale.ROOT, "Training price range: %.2f to %.2f INR%nMean training quantity: %.2f%nPrice/quantity correlation: %.4f%n",` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 69 | `trained.minimum(0), trained.maximum(0), quantityMean, correlation));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 70 | `text.append("Correlation alone is not causal price elasticity. This simulator randomises prices by design.\n");` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 71 | `for (TrainingPipeline.Evaluation item : trained.evaluations()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 72 | `text.append(String.format(Locale.ROOT, "%s: validation RMSE %.3f; test MAE %.3f; test RMSE %.3f%n",` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 73 | `item.model(), item.validation().rmse(), item.test().mae(), item.test().rmse()));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 74 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 75 | `text.append("Selected using validation RMSE: ").append(trained.selected().name()).append("\n");` | Appends the shown diagnostics, numbers or explanation to the generated report text. |
| 76 | `return text.toString();` | Returns the shown value to the caller and ends this method/callback. |
| 77 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 79 | `private static CSVPrinter printer(Path destination, String... headers) throws IOException {` | Declares printer: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 80 | `return new CSVPrinter(Files.newBufferedWriter(destination, StandardCharsets.UTF_8),` | Returns the shown value to the caller and ends this method/callback. |
| 81 | `CSVFormat.RFC4180.builder().setHeader(headers).get());` | Configures correct comma/quote CSV parsing or writing rather than splitting text naively. |
| 82 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 83 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/Main.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 4 | `import java.util.Locale;` | Imports java.util.Locale so its types are available by short names. |
| 6 | `public final class Main {` | Declares the named class; final prevents subclassing if present. |
| 7 | `public static final Path DEFAULT_DATA = Path.of("data", "synthetic_transactions.csv");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 9 | `public static void main(String[] args) {` | Declares main: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 10 | `try {` | Begins work protected by the following exception handlers. |
| 11 | `run(args); // Separate dispatch logic from process exit so tests can call it safely.` | Separate dispatch logic from process exit so tests can call it safely. |
| 12 | `} catch (Exception failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 13 | `System.err.println("ERROR: " + failure.getMessage()); // Explain failures instead of printing a misleading recommendation.` | Explain failures instead of printing a misleading recommendation. |
| 14 | `System.exit(1); // Nonzero exit tells a terminal or CI job that execution failed.` | Nonzero exit tells a terminal or CI job that execution failed. |
| 15 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 16 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 18 | `static void run(String[] args) throws Exception {` | Declares run: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 19 | `String command = args.length == 0 ? "demo" : args[0].toLowerCase(Locale.ROOT);` | Declares/initialises command from the shown expression in the current operation. |
| 20 | `Path data = args.length >= 2 ? Path.of(args[1]) : DEFAULT_DATA;` | Declares/initialises data from the shown expression in the current operation. |
| 21 | `if (command.equals("help")) {` | Checks the shown condition and executes its validation/action only if true. |
| 22 | `System.out.println("""` | Prints the supplied information to the console. |
| 23 | `Commands:` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 24 | `demo [csv]                Train, evaluate and recommend using demo inputs.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 25 | `generate [csv] [rows]     Create deterministic synthetic data; existing files are protected.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 26 | `train [csv]               Train/evaluate and export reports.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 27 | `gui [csv]                 Open the Java desktop interface.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 28 | `recommend [csv]           Use demo pricing inputs.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 29 | `recommend csv current cost min max step stock competitor segment season` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 30 | `Example:` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 31 | `recommend data/synthetic_transactions.csv 120 80 100 150 1 80 125 1 1.0` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 32 | `All currency values are INR; one quantity unit is one 100-bolt pack.` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 33 | `""");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 34 | `return;` | Completes the shown Java statement using the variables/methods described in this block. |
| 35 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 36 | `if (command.equals("generate")) {` | Checks the shown condition and executes its validation/action only if true. |
| 37 | `if (args.length > 3) throw new IllegalArgumentException("Usage: generate [csv] [rows]");` | Checks the shown condition and executes its validation/action only if true. |
| 38 | `int count = args.length == 3 ? Integer.parseInt(args[2]) : 720;` | Declares/initialises count from the shown expression in the current operation. |
| 39 | `SyntheticData.generate(data, count, 42);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 40 | `System.out.println("Generated " + count + " synthetic observations at " + data.toAbsolutePath());` | Prints the supplied information to the console. |
| 41 | `return;` | Completes the shown Java statement using the variables/methods described in this block. |
| 42 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 43 | `if (!command.equals("recommend") && args.length > 2) throw new IllegalArgumentException("Too many command arguments.");` | Checks the shown condition and executes its validation/action only if true. |
| 44 | `if (command.equals("demo") && !Files.exists(data)) SyntheticData.generate(data, 720, 42);` | Checks the shown condition and executes its validation/action only if true. |
| 45 | `if (command.equals("gui")) {` | Checks the shown condition and executes its validation/action only if true. |
| 46 | `if (java.awt.GraphicsEnvironment.isHeadless()) throw new IllegalStateException("Desktop display unavailable. Run demo or train instead.");` | Checks the shown condition and executes its validation/action only if true. |
| 47 | `DesktopApp.open(data);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 48 | `return;` | Completes the shown Java statement using the variables/methods described in this block. |
| 49 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 50 | `if (!command.equals("demo") && !command.equals("train") && !command.equals("recommend")) {` | Checks the shown condition and executes its validation/action only if true. |
| 51 | `throw new IllegalArgumentException("Unknown command. Use help.");` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 52 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 53 | `if (command.equals("recommend") && args.length != 1 && args.length != 2 && args.length != 11) {` | Checks the shown condition and executes its validation/action only if true. |
| 54 | `throw new IllegalArgumentException("Use recommend [csv], or all nine pricing inputs. See help.");` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 55 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 56 | `TrainingPipeline.Result trained = TrainingPipeline.train(CsvData.load(data));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 57 | `System.out.println(Reports.summary(trained));` | Prints the supplied information to the console. |
| 58 | `PricingEngine.Result recommendation = null;` | Completes the shown Java statement using the variables/methods described in this block. |
| 59 | `if (!command.equals("train")) {` | Checks the shown condition and executes its validation/action only if true. |
| 60 | `PricingEngine.Request request = args.length == 11 ? new PricingEngine.Request(` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 61 | `Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 62 | `Double.parseDouble(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 63 | `Double.parseDouble(args[8]), Integer.parseInt(args[9]), Double.parseDouble(args[10])) : demoRequest();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 64 | `recommendation = PricingEngine.optimize(trained, request);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 65 | `System.out.println(PricingEngine.describe(recommendation, trained.selected().name()));` | Prints the supplied information to the console. |
| 66 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 67 | `Reports.write(trained, recommendation, Path.of("output"));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 68 | `System.out.println("Reports written to " + Path.of("output").toAbsolutePath());` | Prints the supplied information to the console. |
| 69 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 71 | `public static PricingEngine.Request demoRequest() {` | Declares demoRequest: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 72 | `return new PricingEngine.Request(120, 80, 100, 150, 1, 80, 125, 1, 1.0);` | Returns the shown value to the caller and ends this method/callback. |
| 73 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 74 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/main/java/com/mitanshu/pricing/DesktopApp.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import javax.swing.*;` | Imports javax.swing.* so its types are available by short names. |
| 4 | `import javax.swing.border.EmptyBorder;` | Imports javax.swing.border.EmptyBorder so its types are available by short names. |
| 5 | `import java.awt.*;` | Imports java.awt.* so its types are available by short names. |
| 6 | `import java.nio.file.Path;` | Imports java.nio.file.Path so its types are available by short names. |
| 7 | `import java.util.concurrent.ExecutionException;` | Imports java.util.concurrent.ExecutionException so its types are available by short names. |
| 9 | `public final class DesktopApp {` | Declares the named class; final prevents subclassing if present. |
| 10 | `private DesktopApp() {}` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 12 | `public static void open(Path data) {` | Declares open: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 13 | `SwingUtilities.invokeLater(() -> {` | Schedules desktop UI creation on the Event Dispatch Thread. |
| 14 | `JFrame window = new JFrame("Mitanshu Jain \| B2B Pricing Lab");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 15 | `window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 16 | `window.setContentPane(new PricingPanel(data));` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 17 | `window.setSize(1080, 760);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 18 | `window.setLocationRelativeTo(null);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 19 | `window.setVisible(true);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 20 | `}); // Swing component creation happens on the Event Dispatch Thread.` | Swing component creation happens on the Event Dispatch Thread. |
| 21 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 23 | `public static final class PricingPanel extends JPanel {` | Declares this: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 24 | `private final JTextArea output = new JTextArea();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 25 | `private final JButton recommend = new JButton("Compare prices");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 26 | `private final JTextField[] fields = new JTextField[9];` | Completes the shown Java statement using the variables/methods described in this block. |
| 27 | `private TrainingPipeline.Result trained;` | Completes the shown Java statement using the variables/methods described in this block. |
| 29 | `public PricingPanel(Path data) {` | Declares PricingPanel: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 30 | `super(new BorderLayout(20, 20));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 31 | `setBorder(new EmptyBorder(24, 24, 24, 24));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 32 | `setBackground(new Color(242, 246, 250));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 33 | `JLabel title = new JLabel("<html><h1>B2B Pricing Lab</h1><p>Synthetic demand modelling &amp; price comparison</p></html>");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 34 | `add(title, BorderLayout.NORTH);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 35 | `JPanel form = new JPanel(new GridLayout(10, 2, 10, 12));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 36 | `form.setOpaque(false);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 37 | `String[] labels = {"Current price (INR)", "Unit cost (INR)", "Minimum price (INR)", "Maximum price (INR)",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 38 | `"Price step (INR)", "Available packs", "Competitor price (INR)", "Buyer segment (0 or 1)", "Season index"};` | Completes the shown Java statement using the variables/methods described in this block. |
| 39 | `String[] values = {"120", "80", "100", "150", "1", "80", "125", "1", "1.0"};` | Completes the shown Java statement using the variables/methods described in this block. |
| 40 | `for (int index = 0; index < fields.length; index++) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 41 | `form.add(new JLabel(labels[index]));` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 42 | `fields[index] = new JTextField(values[index]);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 43 | `form.add(fields[index]);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 44 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 45 | `recommend.setEnabled(false); // A recommendation needs a trained model first.` | A recommendation needs a trained model first. |
| 46 | `form.add(new JLabel("One product per model"));` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 47 | `form.add(recommend);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 48 | `add(form, BorderLayout.WEST);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 49 | `output.setEditable(false);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 50 | `output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 51 | `output.setLineWrap(true);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 52 | `output.setWrapStyleWord(true);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 53 | `output.setMargin(new Insets(12, 12, 12, 12));` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 54 | `output.setText("Loading and validating CSV, then training models...\n" + data.toAbsolutePath());` | Displays the result or error in the desktop output area. |
| 55 | `add(new JScrollPane(output), BorderLayout.CENTER);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 56 | `add(new JLabel("Learning project \| Estimates from synthetic data \| Not a live pricing service"), BorderLayout.SOUTH);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 57 | `recommend.addActionListener(event -> calculate());` | Connects the button to the calculation method. |
| 58 | `new SwingWorker<TrainingPipeline.Result, Void>() {` | Defines background work and a UI-thread completion callback. |
| 59 | `protected TrainingPipeline.Result doInBackground() throws Exception {` | Declares doInBackground: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 60 | `return TrainingPipeline.train(CsvData.load(data)); // Expensive training runs off the UI thread.` | Expensive training runs off the UI thread. |
| 61 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 62 | `protected void done() {` | Declares done: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 63 | `try {` | Begins work protected by the following exception handlers. |
| 64 | `trained = get(); // Retrieve the result after background work completes.` | Retrieve the result after background work completes. |
| 65 | `output.setText(Reports.summary(trained) + "\nEnter pricing inputs, then click Compare prices.");` | Displays the result or error in the desktop output area. |
| 66 | `recommend.setEnabled(true);` | Re-enables the action after work finishes. |
| 67 | `} catch (InterruptedException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 68 | `Thread.currentThread().interrupt();` | Restores interruption status instead of swallowing it. |
| 69 | `output.setText("Training interrupted.");` | Displays the result or error in the desktop output area. |
| 70 | `} catch (ExecutionException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 71 | `output.setText("Training failed: " + failure.getCause().getMessage());` | Displays the worker failure underlying the wrapper exception. |
| 72 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 73 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 74 | `}.execute();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 75 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 77 | `private void calculate() {` | Declares calculate: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 78 | `try {` | Begins work protected by the following exception handlers. |
| 79 | `PricingEngine.Request request = new PricingEngine.Request(` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 80 | `decimal(0), decimal(1), decimal(2), decimal(3), decimal(4),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 81 | `Integer.parseInt(fields[5].getText().trim()), decimal(6),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 82 | `Integer.parseInt(fields[7].getText().trim()), decimal(8));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 83 | `recommend.setEnabled(false);` | Disables the action until training/computation is ready. |
| 84 | `new SwingWorker<PricingEngine.Result, Void>() {` | Defines background work and a UI-thread completion callback. |
| 85 | `protected PricingEngine.Result doInBackground() throws Exception {` | Declares doInBackground: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 86 | `PricingEngine.Result result = PricingEngine.optimize(trained, request);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 87 | `Reports.write(trained, result, Path.of("output"));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 88 | `return result;` | Returns the shown value to the caller and ends this method/callback. |
| 89 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 90 | `protected void done() {` | Declares done: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 91 | `try {` | Begins work protected by the following exception handlers. |
| 92 | `output.setText(PricingEngine.describe(get(), trained.selected().name()) + "\nCSV reports: output/");` | Displays the result or error in the desktop output area. |
| 93 | `} catch (InterruptedException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 94 | `Thread.currentThread().interrupt();` | Restores interruption status instead of swallowing it. |
| 95 | `output.setText("Calculation interrupted.");` | Displays the result or error in the desktop output area. |
| 96 | `} catch (ExecutionException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 97 | `output.setText("Input/calculation error: " + failure.getCause().getMessage());` | Displays the worker failure underlying the wrapper exception. |
| 98 | `} finally {` | Begins cleanup that runs after success or failure. |
| 99 | `recommend.setEnabled(true);` | Re-enables the action after work finishes. |
| 100 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 101 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 102 | `}.execute();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 103 | `} catch (NumberFormatException failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 104 | `output.setText("Enter valid numbers. Stock and segment must be whole numbers; use a decimal point for money.");` | Displays the result or error in the desktop output area. |
| 105 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 106 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 108 | `private double decimal(int index) {` | Declares decimal: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 109 | `return Double.parseDouble(fields[index].getText().trim());` | Returns the shown value to the caller and ends this method/callback. |
| 110 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 111 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 112 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/test/java/com/mitanshu/pricing/DataTest.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.junit.jupiter.api.Test;` | Imports org.junit.jupiter.api.Test so its types are available by short names. |
| 4 | `import org.junit.jupiter.api.io.TempDir;` | Imports org.junit.jupiter.api.io.TempDir so its types are available by short names. |
| 5 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 6 | `import static org.junit.jupiter.api.Assertions.*;` | Imports static so its types are available by short names. |
| 8 | `class DataTest {` | Declares the named class; final prevents subclassing if present. |
| 9 | `@TempDir Path folder;` | Asks JUnit to provide and clean up an isolated temporary folder. |
| 11 | `@Test void generatorIsReproducibleAndDoesNotOverwrite() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 12 | `Path first = folder.resolve("first.csv");` | Declares/initialises first from the shown expression in the current operation. |
| 13 | `Path second = folder.resolve("second.csv");` | Declares/initialises second from the shown expression in the current operation. |
| 14 | `SyntheticData.generate(first, 200, 42);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 15 | `SyntheticData.generate(second, 200, 42);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 16 | `assertEquals(Files.readString(first), Files.readString(second));` | Checks the actual result against the stated expected value. |
| 17 | `assertEquals(200, CsvData.load(first).size());` | Checks the actual result against the stated expected value. |
| 18 | `assertThrows(FileAlreadyExistsException.class, () -> SyntheticData.generate(first, 200, 42));` | Requires the operation to fail with the specified exception type. |
| 19 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 21 | `@Test void rejectsDuplicateDates() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 22 | `Path file = write("2024-01-01,P1,120,125,1,1,80,50\n2024-01-01,P1,121,125,1,1,80,49\n");` | Declares/initialises file from the shown expression in the current operation. |
| 23 | `assertTrue(assertThrows(IllegalArgumentException.class, () -> CsvData.load(file)).getMessage().contains("Duplicate"));` | Fails the test if this behavioural condition is false. |
| 24 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 26 | `@Test void rejectsMissingNonfiniteAndMixedProductValues() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 27 | `for (String body : new String[]{` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 28 | `"2024-01-01,P1,,125,1,1,80,50\n",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 29 | `"2024-01-01,P1,NaN,125,1,1,80,50\n",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 30 | `"2024-01-01,P1,120,125,2,1,80,50\n",` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 31 | `"2024-01-01,P1,120,125,1,1,80,50\n2024-01-02,P2,120,125,1,1,80,50\n"}) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 32 | `assertThrows(IllegalArgumentException.class, () -> CsvData.load(write(body)));` | Requires the operation to fail with the specified exception type. |
| 33 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 34 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 36 | `@Test void parsesQuotedProductAndSortsDates() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 37 | `Path file = write("2024-01-02,\"Bolts, pack\",120,125,1,1,80,0\n2024-01-01,\"Bolts, pack\",120,125,1,1,80,50\n");` | Declares/initialises file from the shown expression in the current operation. |
| 38 | `var rows = CsvData.load(file);` | Declares/initialises rows from the shown expression in the current operation. |
| 39 | `assertEquals("Bolts, pack", rows.get(0).productId());` | Checks the actual result against the stated expected value. |
| 40 | `assertEquals("2024-01-01", rows.get(0).date().toString());` | Checks the actual result against the stated expected value. |
| 41 | `assertEquals(0, rows.get(1).quantity());` | Checks the actual result against the stated expected value. |
| 42 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 44 | `@Test void rejectsHeaderOnlyAndWrongHeader() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 45 | `assertThrows(IllegalArgumentException.class, () -> CsvData.load(write("")));` | Requires the operation to fail with the specified exception type. |
| 46 | `Path file = folder.resolve("wrong.csv");` | Declares/initialises file from the shown expression in the current operation. |
| 47 | `Files.writeString(file, "date,price\n2024-01-01,100\n");` | Writes the generated report as UTF-8 text. |
| 48 | `assertThrows(IllegalArgumentException.class, () -> CsvData.load(file));` | Requires the operation to fail with the specified exception type. |
| 49 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 51 | `private Path write(String body) throws Exception {` | Declares write: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 52 | `Path file = folder.resolve("test.csv");` | Declares/initialises file from the shown expression in the current operation. |
| 53 | `Files.writeString(file, String.join(",", CsvData.HEADER) + "\n" + body);` | Writes the generated report as UTF-8 text. |
| 54 | `return file;` | Returns the shown value to the caller and ends this method/callback. |
| 55 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 56 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/test/java/com/mitanshu/pricing/DesktopTest.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.junit.jupiter.api.Test;` | Imports org.junit.jupiter.api.Test so its types are available by short names. |
| 4 | `import javax.imageio.ImageIO;` | Imports javax.imageio.ImageIO so its types are available by short names. |
| 5 | `import javax.swing.*;` | Imports javax.swing.* so its types are available by short names. |
| 6 | `import java.awt.*;` | Imports java.awt.* so its types are available by short names. |
| 7 | `import java.awt.image.BufferedImage;` | Imports java.awt.image.BufferedImage so its types are available by short names. |
| 8 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 9 | `import java.util.*;` | Imports java.util.* so its types are available by short names. |
| 10 | `import java.util.concurrent.atomic.AtomicReference;` | Imports java.util.concurrent.atomic.AtomicReference so its types are available by short names. |
| 11 | `import static org.junit.jupiter.api.Assertions.*;` | Imports static so its types are available by short names. |
| 13 | `class DesktopTest {` | Declares the named class; final prevents subclassing if present. |
| 14 | `@Test void desktopPanelTrainsCalculatesAndHandlesInvalidText() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 15 | `AtomicReference<DesktopApp.PricingPanel> reference = new AtomicReference<>();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 16 | `SwingUtilities.invokeAndWait(() -> reference.set(new DesktopApp.PricingPanel(Main.DEFAULT_DATA)));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 17 | `DesktopApp.PricingPanel panel = reference.get();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 18 | `java.util.List<JButton> buttons = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 19 | `java.util.List<JTextField> fields = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 20 | `java.util.List<JTextArea> outputs = new ArrayList<>();` | Creates a mutable collection; lists retain order, sets detect duplicate values. |
| 21 | `SwingUtilities.invokeAndWait(() -> collect(panel, buttons, fields, outputs));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 22 | `JButton button = buttons.get(0);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 23 | `awaitEnabled(button);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 24 | `SwingUtilities.invokeAndWait(button::doClick);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 25 | `awaitEnabled(button);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 26 | `SwingUtilities.invokeAndWait(() -> {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 27 | `assertTrue(outputs.get(0).getText().contains("RECOMMENDED"));` | Fails the test if this behavioural condition is false. |
| 28 | `assertTrue(outputs.get(0).getText().contains("Recommended price:"));` | Fails the test if this behavioural condition is false. |
| 29 | `panel.setSize(1080, 700);` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 30 | `layout(panel);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 31 | `BufferedImage image = new BufferedImage(1080, 700, BufferedImage.TYPE_INT_RGB);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 32 | `Graphics2D graphics = image.createGraphics();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 33 | `panel.printAll(graphics); // Render our own Swing component tree without controlling any external app.` | Render our own Swing component tree without controlling any external app. |
| 34 | `graphics.dispose();` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 35 | `try {` | Begins work protected by the following exception handlers. |
| 36 | `Files.createDirectories(Path.of("target", "preview"));` | Creates required folders without failing if they already exist. |
| 37 | `ImageIO.write(image, "png", Path.of("target", "preview", "desktop.png").toFile());` | Saves the rendered component preview as a PNG. |
| 38 | `} catch (Exception failure) {` | Begins a handler for the stated exception type; the following statements report/recover from that failure. |
| 39 | `throw new RuntimeException(failure);` | Raises the specified exception and explanatory message rather than continuing with invalid state. |
| 40 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 41 | `fields.get(0).setText("not-a-price");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 42 | `button.doClick();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 43 | `assertTrue(outputs.get(0).getText().contains("valid numbers"));` | Fails the test if this behavioural condition is false. |
| 44 | `});` | Closes the current method, conditional, loop, class or callback scope. |
| 45 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 47 | `private void awaitEnabled(JButton button) throws Exception {` | Declares awaitEnabled: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 48 | `long deadline = System.nanoTime() + 20_000_000_000L;` | Declares/initialises deadline from the shown expression in the current operation. |
| 49 | `while (System.nanoTime() < deadline) {` | Repeats while this condition holds; the desktop test’s deadline prevents an unbounded wait. |
| 50 | `AtomicReference<Boolean> enabled = new AtomicReference<>();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 51 | `SwingUtilities.invokeAndWait(() -> enabled.set(button.isEnabled()));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 52 | `if (enabled.get()) return;` | Checks the shown condition and executes its validation/action only if true. |
| 53 | `Thread.sleep(20);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 54 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 55 | `fail("Desktop worker did not complete.");` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 56 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 58 | `private void layout(Container component) {` | Declares layout: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 59 | `component.doLayout();` | Calls the displayed method to configure, populate, render or interact with this application’s own UI component. |
| 60 | `for (Component child : component.getComponents()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 61 | `if (child instanceof Container container) layout(container);` | Checks the shown condition and executes its validation/action only if true. |
| 62 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 63 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 65 | `private void collect(Container parent, java.util.List<JButton> buttons, java.util.List<JTextField> fields,` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 66 | `java.util.List<JTextArea> outputs) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 67 | `for (Component child : parent.getComponents()) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 68 | `if (child instanceof JButton button) buttons.add(button);` | Checks the shown condition and executes its validation/action only if true. |
| 69 | `if (child instanceof JTextField field) fields.add(field);` | Checks the shown condition and executes its validation/action only if true. |
| 70 | `if (child instanceof JTextArea text) outputs.add(text);` | Checks the shown condition and executes its validation/action only if true. |
| 71 | `if (child instanceof Container container) collect(container, buttons, fields, outputs);` | Checks the shown condition and executes its validation/action only if true. |
| 72 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 73 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 74 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/test/java/com/mitanshu/pricing/MetricsTest.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.junit.jupiter.api.Test;` | Imports org.junit.jupiter.api.Test so its types are available by short names. |
| 4 | `import java.time.LocalDate;` | Imports java.time.LocalDate so its types are available by short names. |
| 5 | `import java.util.List;` | Imports java.util.List so its types are available by short names. |
| 6 | `import static org.junit.jupiter.api.Assertions.*;` | Imports static so its types are available by short names. |
| 8 | `class MetricsTest {` | Declares the named class; final prevents subclassing if present. |
| 9 | `@Test void computesKnownErrorsAndRejectsEmptyEvaluation() {` | Declares a JUnit test for the behaviour named by this method. |
| 10 | `var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 100, 120, 1, 1, 80, 10),` | Declares/initialises rows from the shown expression in the current operation. |
| 11 | `new Transaction(LocalDate.of(2024, 1, 2), "P1", 100, 120, 1, 1, 80, 14));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 12 | `DemandModel model = new DemandModel() {` | Declares/initialises model from the shown expression in the current operation. |
| 13 | `public String name() { return "Fixed"; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 14 | `public double predict(double[] values) { return 12; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 15 | `};` | Closes the current method, conditional, loop, class or callback scope. |
| 16 | `Metrics result = Metrics.evaluate(model, rows);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 17 | `assertEquals(2, result.mae());` | Checks the actual result against the stated expected value. |
| 18 | `assertEquals(2, result.rmse());` | Checks the actual result against the stated expected value. |
| 19 | `assertThrows(IllegalArgumentException.class, () -> Metrics.evaluate(model, List.of()));` | Requires the operation to fail with the specified exception type. |
| 20 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 21 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/test/java/com/mitanshu/pricing/PipelineTest.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.junit.jupiter.api.*;` | Imports org.junit.jupiter.api.* so its types are available by short names. |
| 4 | `import org.junit.jupiter.api.io.TempDir;` | Imports org.junit.jupiter.api.io.TempDir so its types are available by short names. |
| 5 | `import java.nio.file.*;` | Imports java.nio.file.* so its types are available by short names. |
| 6 | `import java.time.LocalDate;` | Imports java.time.LocalDate so its types are available by short names. |
| 7 | `import java.util.List;` | Imports java.util.List so its types are available by short names. |
| 8 | `import static org.junit.jupiter.api.Assertions.*;` | Imports static so its types are available by short names. |
| 10 | `class PipelineTest {` | Declares the named class; final prevents subclassing if present. |
| 11 | `@TempDir static Path folder;` | Asks JUnit to provide and clean up an isolated temporary folder. |
| 12 | `static TrainingPipeline.Result trained;` | Completes the shown Java statement using the variables/methods described in this block. |
| 14 | `@BeforeAll static void trainRealModels() throws Exception {` | Runs setup once before this class’s test methods. |
| 15 | `Path file = folder.resolve("dataset.csv");` | Declares/initialises file from the shown expression in the current operation. |
| 16 | `SyntheticData.generate(file, 720, 42);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 17 | `trained = TrainingPipeline.train(CsvData.load(file));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 18 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 20 | `@Test void partitionsChronologicallyWithoutOverlap() {` | Declares a JUnit test for the behaviour named by this method. |
| 21 | `assertEquals(432, trained.training().size());` | Checks the actual result against the stated expected value. |
| 22 | `assertEquals(144, trained.validation().size());` | Checks the actual result against the stated expected value. |
| 23 | `assertEquals(144, trained.test().size());` | Checks the actual result against the stated expected value. |
| 24 | `assertTrue(trained.training().get(431).date().isBefore(trained.validation().get(0).date()));` | Fails the test if this behavioural condition is false. |
| 25 | `assertTrue(trained.validation().get(143).date().isBefore(trained.test().get(0).date()));` | Fails the test if this behavioural condition is false. |
| 26 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 28 | `@Test void selectsByValidationOnlyAndBeatsMeanBaselineOnSyntheticData() {` | Declares a JUnit test for the behaviour named by this method. |
| 29 | `var bestValidation = trained.evaluations().stream()` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 30 | `.min(java.util.Comparator.comparingDouble(row -> row.validation().rmse())).orElseThrow();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 31 | `assertEquals(bestValidation.model(), trained.selected().name());` | Checks the actual result against the stated expected value. |
| 32 | `double baseline = trained.evaluations().get(0).test().rmse();` | Declares/initialises baseline from the shown expression in the current operation. |
| 33 | `double selected = trained.evaluations().stream().filter(row -> row.model().equals(trained.selected().name()))` | Transforms or aggregates the named observation sequence with the shown lambda. |
| 34 | `.findFirst().orElseThrow().test().rmse();` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 35 | `assertTrue(selected < baseline);` | Fails the test if this behavioural condition is false. |
| 36 | `assertTrue(selected < 12, "A gross integration/model error should fail this fixture.");` | Fails the test if this behavioural condition is false. |
| 37 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 39 | `@Test void writesReportsAndRecommendationsEndToEnd() throws Exception {` | Declares a JUnit test for the behaviour named by this method. |
| 40 | `var result = PricingEngine.optimize(trained, Main.demoRequest());` | Declares/initialises result from the shown expression in the current operation. |
| 41 | `assertEquals(PricingEngine.Status.RECOMMENDED, result.status());` | Checks the actual result against the stated expected value. |
| 42 | `Reports.write(trained, result, folder.resolve("reports"));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 43 | `for (String name : List.of("evaluation.csv", "test_predictions.csv", "candidate_prices.csv", "recommendation.txt", "eda.txt")) {` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 44 | `assertTrue(Files.size(folder.resolve("reports").resolve(name)) > 0);` | Fails the test if this behavioural condition is false. |
| 45 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 46 | `assertEquals(145, Files.readAllLines(folder.resolve("reports/test_predictions.csv")).size());` | Checks the actual result against the stated expected value. |
| 47 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 49 | `@Test void scalerUsesOnlyTrainingStatistics() {` | Declares a JUnit test for the behaviour named by this method. |
| 50 | `var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 100, 125, 1, 1, 80, 50),` | Declares/initialises rows from the shown expression in the current operation. |
| 51 | `new Transaction(LocalDate.of(2024, 1, 2), "P1", 120, 125, 1, 1, 80, 60));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 52 | `Standardizer scaler = new Standardizer(rows);` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 53 | `assertEquals(0, scaler.transform(new double[]{110, 125, 1, 1})[0]);` | Checks the actual result against the stated expected value. |
| 54 | `assertEquals(89, scaler.transform(new double[]{1000, 125, 1, 1})[0]);` | Checks the actual result against the stated expected value. |
| 55 | `assertEquals(0, scaler.transform(new double[]{110, 125, 1, 1})[1]);` | Checks the actual result against the stated expected value. |
| 56 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 58 | `@Test void rejectsTooLittleOrConstantTargetTrainingData() {` | Declares a JUnit test for the behaviour named by this method. |
| 59 | `assertThrows(IllegalArgumentException.class, () -> TrainingPipeline.train(trained.training().subList(0, 10)));` | Requires the operation to fail with the specified exception type. |
| 60 | `var constant = java.util.stream.IntStream.range(0, 200)` | Declares/initialises constant from the shown expression in the current operation. |
| 61 | `.mapToObj(index -> new Transaction(LocalDate.of(2024, 1, 1).plusDays(index), "P1", 100, 125, 1, 1, 80, 50)).toList();` | Collects the preceding transformation into the requested list or array. |
| 62 | `assertThrows(IllegalArgumentException.class, () -> TrainingPipeline.train(constant));` | Requires the operation to fail with the specified exception type. |
| 63 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 65 | `@Test void commandDispatcherReportsInvalidCommandsAndArguments() {` | Declares a JUnit test for the behaviour named by this method. |
| 66 | `assertThrows(IllegalArgumentException.class, () -> Main.run(new String[]{"unknown"}));` | Requires the operation to fail with the specified exception type. |
| 67 | `assertThrows(IllegalArgumentException.class, () -> Main.run(new String[]{"recommend", "x.csv", "120"}));` | Requires the operation to fail with the specified exception type. |
| 68 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 69 | `}` | Closes the current method, conditional, loop, class or callback scope. |

## src/test/java/com/mitanshu/pricing/PricingEngineTest.java

| Line | Source | Explanation |
| --- | --- | --- |
| 1 | `package com.mitanshu.pricing;` | Declares the package; it must match the source folder hierarchy. |
| 3 | `import org.junit.jupiter.api.Test;` | Imports org.junit.jupiter.api.Test so its types are available by short names. |
| 4 | `import java.time.LocalDate;` | Imports java.time.LocalDate so its types are available by short names. |
| 5 | `import java.util.List;` | Imports java.util.List so its types are available by short names. |
| 6 | `import java.util.function.ToDoubleFunction;` | Imports java.util.function.ToDoubleFunction so its types are available by short names. |
| 7 | `import static org.junit.jupiter.api.Assertions.*;` | Imports static so its types are available by short names. |
| 9 | `class PricingEngineTest {` | Declares the named class; final prevents subclassing if present. |
| 10 | `@Test void findsKnownGridOptimum() {` | Declares a JUnit test for the behaviour named by this method. |
| 11 | `var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(100, 150, 1, 100, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 12 | `assertEquals(PricingEngine.Status.RECOMMENDED, result.status());` | Checks the actual result against the stated expected value. |
| 13 | `assertEquals(140, result.best().price());` | Checks the actual result against the stated expected value. |
| 14 | `assertEquals(3600, result.best().contribution(), 1e-9);` | Checks the actual result against the stated expected value. |
| 15 | `assertEquals(51, result.candidates().size());` | Checks the actual result against the stated expected value. |
| 16 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 18 | `@Test void capsDemandAtAvailableStock() {` | Declares a JUnit test for the behaviour named by this method. |
| 19 | `var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(100, 150, 1, 30, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 20 | `assertEquals(150, result.best().price());` | Checks the actual result against the stated expected value. |
| 21 | `assertEquals(30, result.best().sellableUnits());` | Checks the actual result against the stated expected value. |
| 22 | `assertEquals(2100, result.best().contribution());` | Checks the actual result against the stated expected value. |
| 23 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 25 | `@Test void handlesNoStockAndNoPositiveContribution() {` | Declares a JUnit test for the behaviour named by this method. |
| 26 | `assertEquals(PricingEngine.Status.NO_STOCK,` | Checks the actual result against the stated expected value. |
| 27 | `PricingEngine.optimize(trained(x -> 100), request(100, 150, 1, 0, 80)).status());` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 28 | `var result = PricingEngine.optimize(trained(x -> 100), request(100, 150, 1, 100, 160));` | Declares/initialises result from the shown expression in the current operation. |
| 29 | `assertEquals(PricingEngine.Status.NO_POSITIVE_CONTRIBUTION, result.status());` | Checks the actual result against the stated expected value. |
| 30 | `assertNull(result.best());` | Checks that a no-recommendation outcome has no best-price object. |
| 31 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 33 | `@Test void includesMaximumWhenStepDoesNotLandOnIt() {` | Declares a JUnit test for the behaviour named by this method. |
| 34 | `var result = PricingEngine.optimize(trained(x -> 10), request(100, 150, 7, 100, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 35 | `assertEquals(150, result.best().price());` | Checks the actual result against the stated expected value. |
| 36 | `assertEquals(9, result.candidates().size());` | Checks the actual result against the stated expected value. |
| 37 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 39 | `@Test void withholdsRecommendationWhenOnlyBaselineIsSelected() {` | Declares a JUnit test for the behaviour named by this method. |
| 40 | `var stub = trained(x -> 100);` | Declares/initialises stub from the shown expression in the current operation. |
| 41 | `DemandModel baseline = new DemandModel() {` | Declares/initialises baseline from the shown expression in the current operation. |
| 42 | `public String name() { return "Mean baseline"; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 43 | `public double predict(double[] x) { return 100; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 44 | `};` | Closes the current method, conditional, loop, class or callback scope. |
| 45 | `var run = new TrainingPipeline.Result("P1", stub.training(), stub.validation(), stub.test(), List.of(), baseline);` | Declares/initialises run from the shown expression in the current operation. |
| 46 | `assertEquals(PricingEngine.Status.BASELINE_SELECTED,` | Checks the actual result against the stated expected value. |
| 47 | `PricingEngine.optimize(run, request(100, 150, 1, 100, 80)).status());` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 48 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 50 | `@Test void breaksExactTiesInFavourOfLowerPrice() {` | Declares a JUnit test for the behaviour named by this method. |
| 51 | `var result = PricingEngine.optimize(trained(x -> 200 - x[0]), request(139, 141, 2, 100, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 52 | `assertEquals(139, result.best().price());` | Checks the actual result against the stated expected value. |
| 53 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 55 | `@Test void supportsSingleCandidateAndCentSteps() {` | Declares a JUnit test for the behaviour named by this method. |
| 56 | `assertEquals(100, PricingEngine.optimize(trained(x -> 10), request(100, 100, 1, 100, 80)).best().price());` | Checks the actual result against the stated expected value. |
| 57 | `var result = PricingEngine.optimize(trained(x -> 10), request(100, 100.02, 0.01, 100, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 58 | `assertEquals(3, result.candidates().size());` | Checks the actual result against the stated expected value. |
| 59 | `assertEquals(100.02, result.best().price());` | Checks the actual result against the stated expected value. |
| 60 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 62 | `@Test void clipsNegativePredictionsButRejectsNonfinitePredictions() {` | Declares a JUnit test for the behaviour named by this method. |
| 63 | `var result = PricingEngine.optimize(trained(x -> -10), request(100, 150, 1, 100, 80));` | Declares/initialises result from the shown expression in the current operation. |
| 64 | `assertEquals(PricingEngine.Status.NO_POSITIVE_CONTRIBUTION, result.status());` | Checks the actual result against the stated expected value. |
| 65 | `assertTrue(result.notes().stream().anyMatch(note -> note.contains("clipped")));` | Fails the test if this behavioural condition is false. |
| 66 | `assertThrows(IllegalStateException.class,` | Requires the operation to fail with the specified exception type. |
| 67 | `() -> PricingEngine.optimize(trained(x -> Double.POSITIVE_INFINITY), request(100, 150, 1, 100, 80)));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 68 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 70 | `@Test void rejectsBadBoundsPrecisionStockAndLargeSearches() {` | Declares a JUnit test for the behaviour named by this method. |
| 71 | `for (var input : List.of(request(150, 100, 1, 100, 80), request(100, 150, 0, 100, 80),` | Iterates over the named collection or the shown counter range; the body executes for each item. |
| 72 | `request(100.001, 150, 1, 100, 80), request(100, 150, 1, -1, 80),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 73 | `request(90, 160, 0.01, 100, 80), request(100, 170, 1, 100, 80),` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 74 | `request(100, 150, 1, 100, Double.NaN))) {` | Opens the shown method, callback or control block and establishes scope for its statements. |
| 75 | `assertThrows(IllegalArgumentException.class, () -> PricingEngine.optimize(trained(x -> 10), input));` | Requires the operation to fail with the specified exception type. |
| 76 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 77 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 79 | `private PricingEngine.Request request(double min, double max, double step, int stock, double cost) {` | Declares request: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 80 | `return new PricingEngine.Request(120, cost, min, max, step, stock, 125, 1, 1);` | Returns the shown value to the caller and ends this method/callback. |
| 81 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 83 | `private TrainingPipeline.Result trained(ToDoubleFunction<double[]> prediction) {` | Declares trained: its parameters are accepted inputs; its return type describes the output. Follow the body below. |
| 84 | `var rows = List.of(new Transaction(LocalDate.of(2024, 1, 1), "P1", 90, 100, 0, 0.8, 80, 20),` | Declares/initialises rows from the shown expression in the current operation. |
| 85 | `new Transaction(LocalDate.of(2024, 1, 2), "P1", 160, 150, 1, 1.2, 80, 50));` | Completes the preceding method/constructor call; these arguments belong to the operation started above. |
| 86 | `DemandModel model = new DemandModel() {` | Declares/initialises model from the shown expression in the current operation. |
| 87 | `public String name() { return "Test model"; }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 88 | `public double predict(double[] x) { return prediction.applyAsDouble(x); }` | Continues the preceding declaration, expression or output-text literal; read it together with the preceding statement. |
| 89 | `};` | Closes the current method, conditional, loop, class or callback scope. |
| 90 | `return new TrainingPipeline.Result("P1", rows, rows, rows, List.of(), model);` | Returns the shown value to the caller and ends this method/callback. |
| 91 | `}` | Closes the current method, conditional, loop, class or callback scope. |
| 92 | `}` | Closes the current method, conditional, loop, class or callback scope. |
