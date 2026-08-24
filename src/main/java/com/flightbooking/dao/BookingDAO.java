package com.flightbooking.dao;

import com.flightbooking.db.DatabaseConnection;
import com.flightbooking.model.Booking;
import com.flightbooking.model.Flight;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the 'bookings' table.
 * Booking and cancellation use JDBC transactions with
 *   setAutoCommit(false) → commit() / rollback()
 */
public class BookingDAO {

    private final FlightDAO flightDAO = new FlightDAO();

    // ----------------------------------------------------------------
    //  Create a new booking  (TRANSACTIONAL)
    //  Returns the generated booking_id, or -1 on failure.
    // ----------------------------------------------------------------
    public int createBooking(int flightId, String passengerName,
                             String passengerContact, int seatsBooked) throws SQLException {

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // --- BEGIN TRANSACTION ---

            // 1. Re-read the flight inside the transaction to get fresh seat count
            Flight flight = getFlightForUpdate(conn, flightId);
            if (flight == null) {
                throw new SQLException("Flight with ID " + flightId + " not found.");
            }
            if (flight.getAvailableSeats() < seatsBooked) {
                throw new SQLException("Not enough seats available. Requested: "
                        + seatsBooked + ", Available: " + flight.getAvailableSeats());
            }

            // 2. Decrease available_seats
            boolean seatsUpdated = flightDAO.decreaseAvailableSeats(conn, flightId, seatsBooked);
            if (!seatsUpdated) {
                throw new SQLException("Could not reserve seats. Please try again.");
            }

            // 3. Calculate total amount
            BigDecimal totalAmount = flight.getFare().multiply(BigDecimal.valueOf(seatsBooked));

            // 4. Insert booking row
            String insertSql = "INSERT INTO bookings "
                    + "(flight_id, passenger_name, passenger_contact, seats_booked, total_amount, booking_date, status) "
                    + "VALUES (?, ?, ?, ?, ?, NOW(), 'ACTIVE')";

            int generatedId = -1;
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, flightId);
                ps.setString(2, passengerName.trim());
                ps.setString(3, passengerContact.trim());
                ps.setInt(4, seatsBooked);
                ps.setBigDecimal(5, totalAmount);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                    }
                }
            }

            conn.commit(); // --- COMMIT ---
            return generatedId;

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex; // re-throw so the UI layer can display the message
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ----------------------------------------------------------------
    //  Cancel an existing booking  (TRANSACTIONAL)
    // ----------------------------------------------------------------
    public void cancelBooking(int bookingId) throws SQLException {

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // --- BEGIN TRANSACTION ---

            // 1. Fetch the booking (with a row-level lock)
            Booking booking = getBookingForUpdate(conn, bookingId);
            if (booking == null) {
                throw new SQLException("Booking ID " + bookingId + " not found.");
            }
            if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
                throw new SQLException("Booking ID " + bookingId + " is already cancelled.");
            }

            // 2. Update booking status to CANCELLED
            String cancelSql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // 3. Restore available_seats to the flight
            boolean seatsRestored = flightDAO.increaseAvailableSeats(
                    conn, booking.getFlightId(), booking.getSeatsBooked());
            if (!seatsRestored) {
                throw new SQLException("Could not restore seats to the flight.");
            }

            conn.commit(); // --- COMMIT ---

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ----------------------------------------------------------------
    //  Get all bookings (joined with flights for display)
    // ----------------------------------------------------------------
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();

        String sql = "SELECT b.booking_id, b.flight_id, b.passenger_name, b.passenger_contact, "
                   + "       b.seats_booked, b.total_amount, b.booking_date, b.status, "
                   + "       f.flight_number, f.source, f.destination, f.flight_date "
                   + "FROM bookings b "
                   + "JOIN flights f ON b.flight_id = f.flight_id "
                   + "ORDER BY b.booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setFlightId(rs.getInt("flight_id"));
                b.setPassengerName(rs.getString("passenger_name"));
                b.setPassengerContact(rs.getString("passenger_contact"));
                b.setSeatsBooked(rs.getInt("seats_booked"));
                b.setTotalAmount(rs.getBigDecimal("total_amount"));
                b.setBookingDate(rs.getTimestamp("booking_date"));
                b.setStatus(rs.getString("status"));
                b.setFlightNumber(rs.getString("flight_number"));
                b.setSource(rs.getString("source"));
                b.setDestination(rs.getString("destination"));
                b.setFlightDate(rs.getString("flight_date"));
                list.add(b);
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    //  Get a single booking by ID (for display / viva demo)
    // ----------------------------------------------------------------
    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT b.booking_id, b.flight_id, b.passenger_name, b.passenger_contact, "
                   + "       b.seats_booked, b.total_amount, b.booking_date, b.status, "
                   + "       f.flight_number, f.source, f.destination, f.flight_date "
                   + "FROM bookings b "
                   + "JOIN flights f ON b.flight_id = f.flight_id "
                   + "WHERE b.booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setFlightId(rs.getInt("flight_id"));
                    b.setPassengerName(rs.getString("passenger_name"));
                    b.setPassengerContact(rs.getString("passenger_contact"));
                    b.setSeatsBooked(rs.getInt("seats_booked"));
                    b.setTotalAmount(rs.getBigDecimal("total_amount"));
                    b.setBookingDate(rs.getTimestamp("booking_date"));
                    b.setStatus(rs.getString("status"));
                    b.setFlightNumber(rs.getString("flight_number"));
                    b.setSource(rs.getString("source"));
                    b.setDestination(rs.getString("destination"));
                    b.setFlightDate(rs.getString("flight_date"));
                    return b;
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    //  Private helper: read flight row with FOR UPDATE lock
    // ----------------------------------------------------------------
    private Flight getFlightForUpdate(Connection conn, int flightId) throws SQLException {
        String sql = "SELECT flight_id, flight_number, source, destination, "
                   + "flight_date, flight_time, total_seats, available_seats, fare "
                   + "FROM flights WHERE flight_id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Flight f = new Flight();
                    f.setFlightId(rs.getInt("flight_id"));
                    f.setFlightNumber(rs.getString("flight_number"));
                    f.setSource(rs.getString("source"));
                    f.setDestination(rs.getString("destination"));
                    f.setFlightDate(rs.getDate("flight_date"));
                    f.setFlightTime(rs.getTime("flight_time"));
                    f.setTotalSeats(rs.getInt("total_seats"));
                    f.setAvailableSeats(rs.getInt("available_seats"));
                    f.setFare(rs.getBigDecimal("fare"));
                    return f;
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    //  Private helper: read booking row with FOR UPDATE lock
    // ----------------------------------------------------------------
    private Booking getBookingForUpdate(Connection conn, int bookingId) throws SQLException {
        String sql = "SELECT booking_id, flight_id, passenger_name, passenger_contact, "
                   + "seats_booked, total_amount, booking_date, status "
                   + "FROM bookings WHERE booking_id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setFlightId(rs.getInt("flight_id"));
                    b.setPassengerName(rs.getString("passenger_name"));
                    b.setPassengerContact(rs.getString("passenger_contact"));
                    b.setSeatsBooked(rs.getInt("seats_booked"));
                    b.setTotalAmount(rs.getBigDecimal("total_amount"));
                    b.setBookingDate(rs.getTimestamp("booking_date"));
                    b.setStatus(rs.getString("status"));
                    return b;
                }
            }
        }
        return null;
    }
}
