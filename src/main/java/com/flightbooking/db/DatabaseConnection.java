package com.flightbooking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection class. Change DB_URL, DB_USER, DB_PASSWORD to match
 * your MySQL setup.
 */
public class DatabaseConnection {

    // ---------------------------------------------------------------
    //  Configuration – edit these three values only
    // ---------------------------------------------------------------
    private static final String DB_URL = "jdbc:mysql://localhost:3306/flight_booking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";       // your MySQL username
    private static final String DB_PASSWORD = "root";               // ← CHANGE THIS to your MySQL password
    // ---------------------------------------------------------------

    /**
     * Returns a new JDBC Connection every time it is called. Callers are
     * responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver (required for older JVMs)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                    + "Make sure mysql-connector-java is in the classpath.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Quick connectivity test – used at application startup.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
