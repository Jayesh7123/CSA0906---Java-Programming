package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection utility class.
 *
 * Responsible for establishing and returning a JDBC connection to MySQL. JDBC
 * (Java Database Connectivity) is a standard Java API that allows Java programs
 * to connect and interact with relational databases like MySQL.
 *
 * How JDBC works: 1. Load the JDBC driver (MySQL Connector/J does this
 * automatically via DriverManager) 2. Call DriverManager.getConnection() with
 * the database URL, username, and password 3. Use the returned Connection
 * object to create statements and execute SQL queries
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // DATABASE CONFIGURATION — Change these values to match your setup
    // ---------------------------------------------------------------
    // JDBC URL with MySQL 8.x compatibility parameters:
    //   allowPublicKeyRetrieval=true  -> needed for MySQL 8 caching_sha2_password auth
    //   useSSL=false                  -> disables SSL (not needed for localhost)
    //   serverTimezone=Asia/Kolkata   -> fixes timezone mismatch warning
    private static final String URL = "jdbc:mysql://localhost:3306/flight_booking_db"
            + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Kolkata";

    // Your MySQL username (default is usually "root")
    private static final String USERNAME = "root";

    // Your MySQL password — change this to your actual MySQL root password
    private static final String PASSWORD = "your_password_here";

    // ---------------------------------------------------------------
    /**
     * Returns a Connection object to the flight_booking_db MySQL database.
     *
     * DriverManager.getConnection() is the core JDBC method that: - Finds the
     * appropriate JDBC driver for the given URL - Establishes a physical TCP
     * connection to the MySQL server - Authenticates using the provided
     * credentials - Returns a Connection object representing this session
     *
     * @return Connection — active JDBC connection to MySQL
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL JDBC driver class
            // (Required for some older Java environments; harmless in newer ones)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Make sure mysql-connector-j.jar is in your classpath.");
            throw new SQLException("JDBC Driver not found.", e);
        }

        // Establish and return the connection using DriverManager
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Safely closes a JDBC Connection. Always close the connection after use to
     * release database resources.
     *
     * @param connection — the Connection to close (can be null)
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Warning: Could not close database connection. " + e.getMessage());
            }
        }
    }
}
