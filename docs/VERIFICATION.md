# Verification record

Verified 13 October 2026 using OpenJDK 17.0.20 and Maven 3.9.9.

- Maven verify: 22 tests, 0 failures, 0 errors, 0 skipped.
- Executable JAR demo: successful.
- Desktop panel: model training, button calculation, and invalid-text handling tested headlessly; rendered preview inspected. A native desktop window was not opened in this environment.
- Maven wrapper scripts generated; the complete build used the pinned Maven distribution. Windows PowerShell launch instructions were not executed on Windows here.
- GitHub Actions configuration supplied; no GitHub-hosted workflow run has occurred for this project.

## Synthetic demo metrics

| Model | Validation RMSE | Test MAE | Test RMSE |
| --- | --- | --- | --- |
| Mean baseline | 32.306 | 26.683 | 32.677 |
| Random Forest | 5.240 | 4.376 | 5.504 |
| Support Vector Regression | 3.665 | 3.058 | 3.861 |

SVR was selected using validation RMSE. Errors are packs, not percentages.

Default scenario: current price INR 120, unit cost INR 80, candidate range INR 100-150 in INR 1 steps, stock 80 packs, competitor INR 125, segment 1, season index 1.0.

Result: candidate price INR 146, predicted demand approximately 78.91 packs, estimated gross contribution INR 5208.07 versus INR 3200 at the current price. These values are model estimates on synthetic data, not realised profit or net profit.
