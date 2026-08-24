package com.flightbooking.dao;

import com.flightbooking.db.DatabaseConnection;
import com.flightbooking.model.Flight;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the 'flights' table.
 * All database interactions related to flights go here.
 */
public class FlightDAO {

    // ----------------------------------------------------------------
    //  Get all flights with available seats > 0
    // ----------------------------------------------------------------
    public List<Flight> getAvailableFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT flight_id, flight_number, source, destination, "
                   + "flight_date, flight_time, total_seats, available_seats, fare "
                   + "FROM flights "
                   + "WHERE available_seats > 0 "
                   + "ORDER BY flight_date, flight_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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
                flights.add(f);
            }
        }
        return flights;
    }

    // ----------------------------------------------------------------
    //  Get all flights (including full ones) – used in booking form
    // ----------------------------------------------------------------
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT flight_id, flight_number, source, destination, "
                   + "flight_date, flight_time, total_seats, available_seats, fare "
                   + "FROM flights "
                   + "WHERE available_seats > 0 "
                   + "ORDER BY flight_date, flight_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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
                flights.add(f);
            }
        }
        return flights;
    }

    // ----------------------------------------------------------------
    //  Get a single flight by ID (used during booking validation)
    // ----------------------------------------------------------------
    public Flight getFlightById(int flightId) throws SQLException {
        String sql = "SELECT flight_id, flight_number, source, destination, "
                   + "flight_date, flight_time, total_seats, available_seats, fare "
                   + "FROM flights WHERE flight_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
        return null; // flight not found
    }

    // ----------------------------------------------------------------
    //  Decrease available_seats by a given count (called inside a transaction)
    // ----------------------------------------------------------------
    public boolean decreaseAvailableSeats(Connection conn, int flightId, int seats) throws SQLException {
        String sql = "UPDATE flights SET available_seats = available_seats - ? "
                   + "WHERE flight_id = ? AND available_seats >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seats);
            ps.setInt(2, flightId);
            ps.setInt(3, seats);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // false means not enough seats
        }
    }

    // ----------------------------------------------------------------
    //  Increase available_seats (called on cancellation inside a transaction)
    // ----------------------------------------------------------------
    public boolean increaseAvailableSeats(Connection conn, int flightId, int seats) throws SQLException {
        String sql = "UPDATE flights SET available_seats = available_seats + ? "
                   + "WHERE flight_id = ? AND (available_seats + ?) <= total_seats";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seats);
            ps.setInt(2, flightId);
            ps.setInt(3, seats);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}
