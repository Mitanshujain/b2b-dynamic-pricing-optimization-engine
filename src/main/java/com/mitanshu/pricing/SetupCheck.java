package com.mitanshu.pricing; // This class belongs to our project's package.

public class SetupCheck { // A class groups the instructions for this small setup check.
    public static void main(String[] args) { // Java starts execution here when we run this class.
        String projectName = "B2B Dynamic Pricing Optimization Engine"; // Save text in a String variable.
        double unitCost = 80.0; // Assume a pack costs INR 80.
        double sellingPrice = 120.0; // Assume its selling price is INR 120.
        int quantity = 50; // Assume we sell 50 packs.
        double contribution = (sellingPrice - unitCost) * quantity; // Unit margin multiplied by packs sold.
        System.out.println(projectName); // Display the project name.
        System.out.println("Java version: " + System.getProperty("java.version")); // Show the JDK used by IntelliJ.
        System.out.println("Estimated gross contribution: INR " + contribution); // Display INR 2000.0.
        System.out.println("Setup check passed."); // Confirms this simple class reached its final statement.
    }
}
