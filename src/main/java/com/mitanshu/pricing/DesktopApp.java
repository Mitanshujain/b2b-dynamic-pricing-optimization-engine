package com.mitanshu.pricing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public final class DesktopApp {
    private DesktopApp() {}

    public static void open(Path data) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Mitanshu Jain | B2B Pricing Lab");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(new PricingPanel(data));
            window.setSize(1080, 760);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        }); // Swing component creation happens on the Event Dispatch Thread.
    }

    public static final class PricingPanel extends JPanel {
        private final JTextArea output = new JTextArea();
        private final JButton recommend = new JButton("Compare prices");
        private final JTextField[] fields = new JTextField[9];
        private TrainingPipeline.Result trained;

        public PricingPanel(Path data) {
            super(new BorderLayout(20, 20));
            setBorder(new EmptyBorder(24, 24, 24, 24));
            setBackground(new Color(242, 246, 250));
            JLabel title = new JLabel("<html><h1>B2B Pricing Lab</h1><p>Synthetic demand modelling &amp; price comparison</p></html>");
            add(title, BorderLayout.NORTH);
            JPanel form = new JPanel(new GridLayout(10, 2, 10, 12));
            form.setOpaque(false);
            String[] labels = {"Current price (INR)", "Unit cost (INR)", "Minimum price (INR)", "Maximum price (INR)",
                    "Price step (INR)", "Available packs", "Competitor price (INR)", "Buyer segment (0 or 1)", "Season index"};
            String[] values = {"120", "80", "100", "150", "1", "80", "125", "1", "1.0"};
            for (int index = 0; index < fields.length; index++) {
                form.add(new JLabel(labels[index]));
                fields[index] = new JTextField(values[index]);
                form.add(fields[index]);
            }
            recommend.setEnabled(false); // A recommendation needs a trained model first.
            form.add(new JLabel("One product per model"));
            form.add(recommend);
            add(form, BorderLayout.WEST);
            output.setEditable(false);
            output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            output.setLineWrap(true);
            output.setWrapStyleWord(true);
            output.setMargin(new Insets(12, 12, 12, 12));
            output.setText("Loading and validating CSV, then training models...\n" + data.toAbsolutePath());
            add(new JScrollPane(output), BorderLayout.CENTER);
            add(new JLabel("Learning project | Estimates from synthetic data | Not a live pricing service"), BorderLayout.SOUTH);
            recommend.addActionListener(event -> calculate());
            new SwingWorker<TrainingPipeline.Result, Void>() {
                protected TrainingPipeline.Result doInBackground() throws Exception {
                    return TrainingPipeline.train(CsvData.load(data)); // Expensive training runs off the UI thread.
                }
                protected void done() {
                    try {
                        trained = get(); // Retrieve the result after background work completes.
                        output.setText(Reports.summary(trained) + "\nEnter pricing inputs, then click Compare prices.");
                        recommend.setEnabled(true);
                    } catch (InterruptedException failure) {
                        Thread.currentThread().interrupt();
                        output.setText("Training interrupted.");
                    } catch (ExecutionException failure) {
                        output.setText("Training failed: " + failure.getCause().getMessage());
                    }
                }
            }.execute();
        }

        private void calculate() {
            try {
                PricingEngine.Request request = new PricingEngine.Request(
                        decimal(0), decimal(1), decimal(2), decimal(3), decimal(4),
                        Integer.parseInt(fields[5].getText().trim()), decimal(6),
                        Integer.parseInt(fields[7].getText().trim()), decimal(8));
                recommend.setEnabled(false);
                new SwingWorker<PricingEngine.Result, Void>() {
                    protected PricingEngine.Result doInBackground() throws Exception {
                        PricingEngine.Result result = PricingEngine.optimize(trained, request);
                        Reports.write(trained, result, Path.of("output"));
                        return result;
                    }
                    protected void done() {
                        try {
                            output.setText(PricingEngine.describe(get(), trained.selected().name()) + "\nCSV reports: output/");
                        } catch (InterruptedException failure) {
                            Thread.currentThread().interrupt();
                            output.setText("Calculation interrupted.");
                        } catch (ExecutionException failure) {
                            output.setText("Input/calculation error: " + failure.getCause().getMessage());
                        } finally {
                            recommend.setEnabled(true);
                        }
                    }
                }.execute();
            } catch (NumberFormatException failure) {
                output.setText("Enter valid numbers. Stock and segment must be whole numbers; use a decimal point for money.");
            }
        }

        private double decimal(int index) {
            return Double.parseDouble(fields[index].getText().trim());
        }
    }
}
