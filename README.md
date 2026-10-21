# B2B Dynamic Pricing Optimization Engine — Java Learning Edition

A runnable Java portfolio project by Mitanshu Jain. It predicts daily demand for one B2B product and compares candidate prices using estimated gross contribution.

**Data provenance:** all included observations are synthetic. They do not come from TCS, Goa Shipyard, or any real customer. Results are simulation results, not production profit improvements.

![Java desktop pricing interface](examples/desktop-preview.png)

## How it works

1. Validate and sort the synthetic daily observations.
2. Split them chronologically into training (60%), validation (20%), and test (20%) periods.
3. Train a mean-demand baseline, Random Forest, and Support Vector Regression; select by validation RMSE.
4. Predict demand for each allowed price and apply the inventory limit.
5. Compare estimated gross contribution: `(price - unit cost) × min(predicted demand, available stock)`.

### Synthetic benchmark

| Model | Validation RMSE | Test MAE | Test RMSE |
| --- | --- | --- | --- |
| Mean baseline | 32.306 | 26.683 | 32.677 |
| Random Forest | 5.240 | 4.376 | 5.504 |
| Support Vector Regression | 3.665 | 3.058 | 3.861 |

Errors are in packs. These figures describe the bundled synthetic dataset and fixed configuration. Full run details are in [the verification record](docs/VERIFICATION.md).

## Included

- Java 17 source code and a Maven build.
- A deterministic generator and 720-row synthetic CSV.
- Strict CSV validation, training-only descriptive analysis, and chronological 60/20/20 splitting.
- Mean-demand baseline, Random Forest regression, and Gaussian-kernel Support Vector Regression.
- Model selection by validation RMSE; separate test MAE/RMSE.
- Constrained price-grid comparison, inventory caps, and explicit failure/no-recommendation outcomes.
- Console commands, a Java Swing desktop interface, report exports, and automated tests.
- A Maven wrapper and a GitHub Actions workflow.
- A practical build guide and a numbered source walkthrough.

## Open in IntelliJ

1. Clone this repository or download and extract its source ZIP into a writable folder.
2. Use **File → Open**, select this project's **pom.xml**, and open it as a project.
3. Choose **JDK 17 or newer** in **File → Project Structure → Project SDK**. The project compiles for Java 17.
4. Let Maven finish downloading the dependencies. Internet access is required on the first build.
5. First run **SetupCheck.java**. Its example prints INR 2000.0.
6. Then run **Main.java**, with the working directory set to the project root. No arguments runs the full demo.
7. To open the desktop interface, run Main with program arguments **gui**.

Use the bundled Maven configured by IntelliJ, or the wrapper below. No Spring plugin or separate paid IDE feature is required for the Java/Swing project.

## Commands

Run these from the folder containing pom.xml.

Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd compile exec:java
.\mvnw.cmd compile exec:java "-Dexec.args=gui"
.\mvnw.cmd package
java -jar target/b2b-pricing-java-1.0.0.jar demo
```

Linux/macOS:

```bash
./mvnw test
./mvnw compile exec:java
./mvnw compile exec:java -Dexec.args=gui
./mvnw package
java -jar target/b2b-pricing-java-1.0.0.jar demo
```

If your unzip utility removes the executable flag on Linux/macOS, use `chmod +x mvnw`.

Other Main program arguments:

```text
help
generate data/new_transactions.csv 720
train data/synthetic_transactions.csv
recommend data/synthetic_transactions.csv 120 80 100 150 1 80 125 1 1.0
```

Recommendation arguments after the CSV path are current price, unit cost, minimum price, maximum price, step, stock, competitor price, buyer segment, and season index.

## Outputs

The most recent run writes reports under **output/**:

| File | Meaning |
| --- | --- |
| evaluation.csv | Validation and test errors for all three models; selected model flag |
| test_predictions.csv | Selected-model predictions and errors on unseen test rows |
| eda.txt | Training data summary, date partitions, and metrics |
| candidate_prices.csv | Every evaluated candidate price and its estimated contribution |
| recommendation.txt | Result status, best candidate when available, and assumptions |

A training-only run removes stale candidate/recommendation reports. The **examples/** folder preserves one verified demo run separately.

## Scope and limitations

This is a single-product, two-segment, daily-observation learning application. A quantity unit is one pack of 100 M8 bolts. Prices and costs are INR per pack. Model outputs are expected quantities and can be fractional; actual transactions would have whole-number quantities.

Price recommendations are best only within the enumerated grid and the model's assumptions. They are not continuous global optima, uncertainty bounds, or demonstrated real-world profit maxima. Marginal feature-range checks reduce obvious extrapolation but do not guarantee support for every combination of inputs.

Historical quantities are deliberately uncensored by inventory in the simulator. Real transaction data would require treatment of stockouts, selection bias, price endogeneity, and changing market conditions before making causal pricing claims.

The app has no live database, REST server, authentication, ERP integration, automatic price deployment, or persisted model artefact. Models are trained when the app starts. These are possible later extensions, not implemented features.

## Learn the code

Start with the [build guide](docs/BUILD_GUIDE.md), then the [line-by-line walkthrough](docs/LINE_BY_LINE.md). Use **SetupCheck.java → Transaction.java → SyntheticData.java → CsvData.java → Standardizer.java → DemandModel.java → Metrics.java → TrainingPipeline.java → PricingEngine.java → Reports.java → Main.java → DesktopApp.java** as the learning order.

See [data and credential handling](SECURITY.md) and [third-party notices](docs/THIRD_PARTY.md).

## Resume wording after you understand and run the project

**B2B Pricing Recommendation Engine | Java, Smile, Random Forest, Support Vector Regression**

- Built a Java application to validate synthetic B2B transaction data, train and evaluate demand models using chronological splits, and export model diagnostics.
- Implemented constrained price-grid recommendations using predicted demand, unit costs, inventory limits, and explicit no-recommendation outcomes.

Do not describe this Java project as implemented with Python/Pandas/Scikit-learn unless you also build and maintain an actual Python version.
