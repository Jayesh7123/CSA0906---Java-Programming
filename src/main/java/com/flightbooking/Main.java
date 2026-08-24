package com.flightbooking;

import com.flightbooking.db.DatabaseConnection;
import com.flightbooking.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Application entry point.
 * Verifies the MySQL connection before launching the Swing GUI.
 */
public class Main {

    public static void main(String[] args) {

        // Apply system look-and-feel for better native rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default L&F – not critical
        }

        // Override a few global UI defaults for the dark theme
        UIManager.put("OptionPane.background",       new Color(30, 41, 59));
        UIManager.put("Panel.background",            new Color(30, 41, 59));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);

        // Launch on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {

            // ---- Test MySQL connection before opening the window ----
            if (!DatabaseConnection.testConnection()) {
                JOptionPane.showMessageDialog(
                        null,
                        "⚠ Could not connect to MySQL!\n\n"
                        + "Please make sure:\n"
                        + "  1. MySQL Server is running.\n"
                        + "  2. The database 'flight_booking_db' exists.\n"
                        + "     (Run database.sql first)\n"
                        + "  3. Username and password are correct in\n"
                        + "     DatabaseConnection.java\n"
                        + "  4. MySQL is listening on port 3306.\n\n"
                        + "The application will open anyway, but operations\n"
                        + "will fail until the database is reachable.",
                        "Database Connection Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

            // ---- Open the main window ----
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
