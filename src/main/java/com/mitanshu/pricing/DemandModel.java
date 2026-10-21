package com.mitanshu.pricing;

public interface DemandModel {
    String name(); // Reports the model type used in output.
    double predict(double[] features); // Predicts units for one feature vector.
}
