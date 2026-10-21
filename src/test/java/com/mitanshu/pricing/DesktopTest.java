package com.mitanshu.pricing;

import org.junit.jupiter.api.Test;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class DesktopTest {
    @Test void desktopPanelTrainsCalculatesAndHandlesInvalidText() throws Exception {
        AtomicReference<DesktopApp.PricingPanel> reference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> reference.set(new DesktopApp.PricingPanel(Main.DEFAULT_DATA)));
        DesktopApp.PricingPanel panel = reference.get();
        java.util.List<JButton> buttons = new ArrayList<>();
        java.util.List<JTextField> fields = new ArrayList<>();
        java.util.List<JTextArea> outputs = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> collect(panel, buttons, fields, outputs));
        JButton button = buttons.get(0);
        awaitEnabled(button);
        SwingUtilities.invokeAndWait(button::doClick);
        awaitEnabled(button);
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(outputs.get(0).getText().contains("RECOMMENDED"));
            assertTrue(outputs.get(0).getText().contains("Recommended price:"));
            panel.setSize(1080, 700);
            layout(panel);
            BufferedImage image = new BufferedImage(1080, 700, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            panel.printAll(graphics); // Render our own Swing component tree without controlling any external app.
            graphics.dispose();
            try {
                Files.createDirectories(Path.of("target", "preview"));
                ImageIO.write(image, "png", Path.of("target", "preview", "desktop.png").toFile());
            } catch (Exception failure) {
                throw new RuntimeException(failure);
            }
            fields.get(0).setText("not-a-price");
            button.doClick();
            assertTrue(outputs.get(0).getText().contains("valid numbers"));
        });
    }

    private void awaitEnabled(JButton button) throws Exception {
        long deadline = System.nanoTime() + 20_000_000_000L;
        while (System.nanoTime() < deadline) {
            AtomicReference<Boolean> enabled = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> enabled.set(button.isEnabled()));
            if (enabled.get()) return;
            Thread.sleep(20);
        }
        fail("Desktop worker did not complete.");
    }

    private void layout(Container component) {
        component.doLayout();
        for (Component child : component.getComponents()) {
            if (child instanceof Container container) layout(container);
        }
    }

    private void collect(Container parent, java.util.List<JButton> buttons, java.util.List<JTextField> fields,
                         java.util.List<JTextArea> outputs) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JButton button) buttons.add(button);
            if (child instanceof JTextField field) fields.add(field);
            if (child instanceof JTextArea text) outputs.add(text);
            if (child instanceof Container container) collect(container, buttons, fields, outputs);
        }
    }
}
