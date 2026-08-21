package dao;

import model.Flight;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightDAO — Data Access Object for the 'flights' table.
 *
 * DAO Pattern: Separates all database-related code from the business logic.
 * Each method corresponds to one database operation.
 *
 * JDBC classes used here:
 *   - Connection        : Represents the live session with MySQL
 *   - PreparedStatement : Pre-compiled SQL with ? placeholders (safe from SQL injection)
 *   - Statement         : Used for simple queries without parameters
 *   - ResultSet         : Holds the rows returned by a SELECT query
 */
public class FlightDAO {

    /**
     * Retrieves all flights that have at least one available seat.
     * Uses a simple Statement (no parameters needed).
     *
     * @return List of available Flight objects
     */
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();

        // SQL query to get only flights with available seats
        String sql = "SELECT * FROM flights WHERE available_seats > 0 ORDER BY flight_id";

        Connection connection = null;

        try {
            // Step 1: Get a connection from DBConnection
            connection = DBConnection.getConnection();

            // Step 2: Create a Statement object to execute SQL
            Statement statement = connection.createStatement();

            // Step 3: Execute the query — returns a ResultSet (like a virtual table of rows)
            ResultSet resultSet = statement.executeQuery(sql);

            // Step 4: Iterate over each row in the ResultSet
            while (resultSet.next()) {
                Flight flight = mapRowToFlight(resultSet);
                flights.add(flight);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving flights: " + e.getMessage());
        } finally {
            // Step 5: Always close the connection to free database resources
            DBConnection.closeConnection(connection);
        }

        return flights;
    }

    /**
     * Searches for flights by source and destination (case-insensitive).
     * Uses PreparedStatement with ? placeholders to safely pass user input.
     *
     * @param source      — departure city entered by the user
     * @param destination — arrival city entered by the user
     * @return List of matching Flight objects
     */
    public List<Flight> searchFlights(String source, String destination) {
        List<Flight> flights = new ArrayList<>();

        // LOWER() makes the comparison case-insensitive on both sides
        String sql = "SELECT * FROM flights WHERE LOWER(source) = LOWER(?) " +
                     "AND LOWER(destination) = LOWER(?) AND available_seats > 0";

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // PreparedStatement: ? placeholders are filled in safely before execution
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Set parameter 1 (first ?) = source
            preparedStatement.setString(1, source);
            // Set parameter 2 (second ?) = destination
            preparedStatement.setString(2, destination);

            // Execute and get the ResultSet
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapRowToFlight(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Error searching flights: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }

        return flights;
    }

    /**
     * Retrieves a single flight by its ID.
     * Returns null if not found.
     *
     * @param flightId — the primary key of the flight
     * @return Flight object or null
     */
    public Flight getFlightById(int flightId) {
        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        Connection connection = null;
        Flight flight = null;

        try {
            connection = DBConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, flightId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // resultSet.next() moves to the first (and only) row if it exists
            if (resultSet.next()) {
                flight = mapRowToFlight(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving flight by ID: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }

        return flight;
    }

    /**
     * Updates the available_seats count for a given flight.
     * Called during booking (decrease seats) and cancellation (increase seats).
     *
     * NOTE: This method does NOT manage its own connection or transaction.
     * It accepts the connection from outside so the CALLER can wrap
     * both the booking INSERT and this UPDATE in a single transaction.
     *
     * @param connection — shared connection from the calling transaction
     * @param flightId   — the flight to update
     * @param seats      — the new available_seats count
     * @return true if the update was successful
     * @throws SQLException — so the caller can rollback if needed
     */
    public boolean updateAvailableSeats(Connection connection, int flightId, int seats) throws SQLException {
        String sql = "UPDATE flights SET available_seats = ? WHERE flight_id = ?";

        // Use the shared connection — do NOT close it here (caller manages it)
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, seats);
        preparedStatement.setInt(2, flightId);

        // executeUpdate() returns the number of rows affected
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected > 0;
    }

    /**
     * Helper method — maps a single ResultSet row to a Flight object.
     * Used by all query methods to avoid repeating column-mapping code.
     *
     * @param resultSet — the current row of the ResultSet cursor
     * @return populated Flight object
     * @throws SQLException if a column name is wrong
     */
    private Flight mapRowToFlight(ResultSet resultSet) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(resultSet.getInt("flight_id"));
        flight.setFlightNumber(resultSet.getString("flight_number"));
        flight.setSource(resultSet.getString("source"));
        flight.setDestination(resultSet.getString("destination"));
        // Convert SQL DATETIME to a readable string
        flight.setDepartureTime(resultSet.getString("departure_time"));
        flight.setTotalSeats(resultSet.getInt("total_seats"));
        flight.setAvailableSeats(resultSet.getInt("available_seats"));
        return flight;
    }
}
