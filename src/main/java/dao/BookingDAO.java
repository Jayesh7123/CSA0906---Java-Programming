package dao;

import model.Booking;
import model.Flight;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO — Data Access Object for the 'bookings' table.
 *
 * Key concepts demonstrated:
 *   - JDBC Transactions  : Booking and seat-update happen together or not at all
 *   - PreparedStatement  : Used for all INSERT, SELECT, DELETE operations
 *   - SQL JOIN           : Combines bookings and flights tables for full details
 *   - ResultSet          : Iterates over rows returned by SELECT queries
 *   - connection.setAutoCommit(false) : Starts a manual transaction
 *   - connection.commit()             : Saves all changes permanently
 *   - connection.rollback()           : Reverts all changes if an error occurs
 */
public class BookingDAO {

    // FlightDAO is used inside bookTicket and cancelBooking to update seats
    private FlightDAO flightDAO = new FlightDAO();

    /**
     * Books a ticket for a passenger on a given flight.
     *
     * Transaction Flow:
     *   1. Open connection
     *   2. Disable auto-commit (begin manual transaction)
     *   3. Check available seats from DB (read current value)
     *   4. If seats available → INSERT booking row
     *   5. UPDATE flights.available_seats = current - 1
     *   6. If both succeed → commit (save to DB permanently)
     *   7. If any error    → rollback (undo everything)
     *
     * Using a transaction ensures the database stays consistent:
     * you will never have a booking without a corresponding seat deduction.
     *
     * @param booking — the Booking object with flightId, name, and email
     * @return true if booking succeeded, false otherwise
     */
    public boolean bookTicket(Booking booking) {
        Connection connection = null;

        try {
            // Step 1: Get database connection
            connection = DBConnection.getConnection();

            // Step 2: Disable auto-commit to start a manual transaction
            // By default, every SQL statement is committed immediately (auto-commit = true).
            // Setting it to false means changes are only saved when we call commit().
            connection.setAutoCommit(false);

            // Step 3: Check current available seats for this flight
            String checkSeatSql = "SELECT available_seats FROM flights WHERE flight_id = ?";
            PreparedStatement seatCheck = connection.prepareStatement(checkSeatSql);
            seatCheck.setInt(1, booking.getFlightId());
            ResultSet seatResult = seatCheck.executeQuery();

            if (!seatResult.next()) {
                System.out.println("Flight not found.");
                return false;
            }

            int availableSeats = seatResult.getInt("available_seats");

            // Step 4: Check if seats are available
            if (availableSeats <= 0) {
                System.out.println("\nSorry! No seats are available for this flight.");
                return false;
            }

            // Step 5: INSERT the booking record into the bookings table
            // RETURN_GENERATED_KEYS tells JDBC to give us the auto-generated booking_id
            String insertSql = "INSERT INTO bookings (flight_id, passenger_name, passenger_email) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, booking.getFlightId());
            insertStatement.setString(2, booking.getPassengerName());
            insertStatement.setString(3, booking.getPassengerEmail());
            insertStatement.executeUpdate();

            // Retrieve the auto-generated booking_id from MySQL
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                booking.setBookingId(generatedKeys.getInt(1));
            }

            // Step 6: UPDATE the flight's available seats — decrease by 1
            // This uses the shared connection so it's part of the same transaction
            int newSeats = availableSeats - 1;
            flightDAO.updateAvailableSeats(connection, booking.getFlightId(), newSeats);

            // Step 7: Commit the transaction — both INSERT and UPDATE are saved together
            connection.commit();

            return true;

        } catch (SQLException e) {
            // Step 8: If anything went wrong, rollback — undo ALL changes in this transaction
            System.out.println("Booking failed: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                    System.out.println("Transaction rolled back. No changes were saved.");
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;

        } finally {
            // Always restore auto-commit and close connection
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.out.println("Could not restore auto-commit: " + e.getMessage());
            }
            DBConnection.closeConnection(connection);
        }
    }

    /**
     * Retrieves a single booking by its ID using a SQL JOIN.
     *
     * The JOIN combines:
     *   - bookings table   : booking details (passenger, email, date)
     *   - flights table    : flight details (number, source, destination, departure)
     *
     * This gives complete booking information in a single query.
     *
     * @param bookingId — the primary key of the booking
     * @return Booking object with full flight details, or null if not found
     */
    public Booking getBookingById(int bookingId) {
        // JOIN query to fetch booking + flight info in one go
        String sql = "SELECT b.booking_id, b.flight_id, b.passenger_name, b.passenger_email, " +
                     "b.booking_date, f.flight_number, f.source, f.destination, f.departure_time " +
                     "FROM bookings b " +
                     "JOIN flights f ON b.flight_id = f.flight_id " +
                     "WHERE b.booking_id = ?";

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, bookingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapRowToBooking(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving booking: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }

        return null;  // Return null if booking not found
    }

    /**
     * Retrieves all bookings from the database using a JOIN.
     * Returns an empty list if there are no bookings.
     *
     * @return List of all Booking objects with flight details
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT b.booking_id, b.flight_id, b.passenger_name, b.passenger_email, " +
                     "b.booking_date, f.flight_number, f.source, f.destination, f.departure_time " +
                     "FROM bookings b " +
                     "JOIN flights f ON b.flight_id = f.flight_id " +
                     "ORDER BY b.booking_id";

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // Simple Statement — no user input, no parameters needed
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                bookings.add(mapRowToBooking(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving bookings: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(connection);
        }

        return bookings;
    }

    /**
     * Cancels a booking and restores the seat count.
     *
     * Transaction Flow:
     *   1. Find the booking and get its flight_id
     *   2. DELETE the booking row
     *   3. Increase the flight's available_seats by 1
     *   4. Commit — both changes saved together
     *   5. On any error → rollback
     *
     * @param bookingId — the booking to cancel
     * @return true if cancellation succeeded, false if booking not found or error
     */
    public boolean cancelBooking(int bookingId) {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // Step 1: Disable auto-commit to start a transaction
            connection.setAutoCommit(false);

            // Step 2: Find the booking and its associated flight_id
            String selectSql = "SELECT flight_id FROM bookings WHERE booking_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, bookingId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (!resultSet.next()) {
                // Booking does not exist
                return false;
            }

            int flightId = resultSet.getInt("flight_id");

            // Step 3: Get the current available seats for the flight
            String seatSql = "SELECT available_seats FROM flights WHERE flight_id = ?";
            PreparedStatement seatStatement = connection.prepareStatement(seatSql);
            seatStatement.setInt(1, flightId);
            ResultSet seatResult = seatStatement.executeQuery();
            seatResult.next();
            int currentSeats = seatResult.getInt("available_seats");

            // Step 4: DELETE the booking record
            String deleteSql = "DELETE FROM bookings WHERE booking_id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setInt(1, bookingId);
            deleteStatement.executeUpdate();

            // Step 5: Restore the seat count — increase by 1
            int restoredSeats = currentSeats + 1;
            flightDAO.updateAvailableSeats(connection, flightId, restoredSeats);

            // Step 6: Commit the transaction — both DELETE and UPDATE are finalized
            connection.commit();

            return true;

        } catch (SQLException e) {
            // Rollback if anything fails
            System.out.println("Cancellation failed: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                    System.out.println("Transaction rolled back. No changes were saved.");
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;

        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.out.println("Could not restore auto-commit: " + e.getMessage());
            }
            DBConnection.closeConnection(connection);
        }
    }

    /**
     * Helper — maps a JOIN ResultSet row to a Booking object with flight details.
     *
     * @param resultSet — current cursor row from a JOIN query
     * @return fully populated Booking object
     * @throws SQLException if a column name is wrong
     */
    private Booking mapRowToBooking(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(resultSet.getInt("booking_id"));
        booking.setFlightId(resultSet.getInt("flight_id"));
        booking.setPassengerName(resultSet.getString("passenger_name"));
        booking.setPassengerEmail(resultSet.getString("passenger_email"));
        booking.setBookingDate(resultSet.getString("booking_date"));
        // Flight details from the JOIN
        booking.setFlightNumber(resultSet.getString("flight_number"));
        booking.setSource(resultSet.getString("source"));
        booking.setDestination(resultSet.getString("destination"));
        booking.setDepartureTime(resultSet.getString("departure_time"));
        return booking;
    }
}
