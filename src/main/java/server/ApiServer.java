package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dao.BookingDAO;
import dao.FlightDAO;
import model.Booking;
import model.Flight;
import util.DBConnection;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApiServer — A lightweight HTTP server that bridges the web frontend and MySQL.
 *
 * Architecture:
 *   Browser (index.html)
 *       ↕  HTTP / JSON
 *   ApiServer (this class) — com.sun.net.httpserver (built into Java, no extra lib)
 *       ↕  JDBC (via existing DAOs)
 *   MySQL Database
 *
 * Endpoints:
 *   GET  /api/flights              → all flights (available_seats > 0)
 *   GET  /api/flights/all          → ALL flights including full ones
 *   GET  /api/flights/search?from=X&to=Y → search flights
 *   POST /api/bookings             → book a ticket (body: JSON {flightId, name, email})
 *   GET  /api/bookings             → all bookings
 *   GET  /api/bookings/{id}        → booking by id
 *   DELETE /api/bookings/{id}      → cancel booking
 *   GET  /                         → serves index.html (the web UI)
 */
public class ApiServer {

    private static final int PORT = 8080;
    private static final FlightDAO flightDAO = new FlightDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    public static void main(String[] args) throws IOException {

        // ── 1. Test DB connection at startup ──────────────────────────────
        System.out.println("========================================");
        System.out.println("    SKYWAY FLIGHT BOOKING SYSTEM");
        System.out.println("========================================");
        System.out.println("Connecting to MySQL database via JDBC...");
        try {
            Connection test = DBConnection.getConnection();
            test.close();
            System.out.println("✓ MySQL connected successfully!");
        } catch (SQLException e) {
            System.out.println("✗ ERROR: Cannot connect to MySQL.");
            System.out.println("  Please check:");
            System.out.println("  1. MySQL server is running");
            System.out.println("  2. Database 'flight_booking_db' exists");
            System.out.println("  3. Password in DBConnection.java is correct");
            System.out.println("\nError: " + e.getMessage());
            System.exit(1);
        }

        // ── 2. Start HTTP server ──────────────────────────────────────────
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Register route handlers
        server.createContext("/api/flights",  ApiServer::handleFlights);
        server.createContext("/api/bookings", ApiServer::handleBookings);
        server.createContext("/",             ApiServer::handleStatic);

        server.setExecutor(null); // use default executor
        server.start();

        System.out.println("========================================");
        System.out.println("✓ Server started on port " + PORT);
        System.out.println("  Open your browser and go to:");
        System.out.println("  http://localhost:" + PORT);
        System.out.println("========================================");
        System.out.println("Press Ctrl+C to stop the server.\n");
    }

    // =========================================================================
    //  FLIGHTS HANDLER — GET /api/flights and /api/flights/search
    // =========================================================================

    private static void handleFlights(HttpExchange ex) throws IOException {
        addCorsHeaders(ex);

        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String path = ex.getRequestURI().getPath();
        String query = ex.getRequestURI().getQuery(); // e.g. "from=Chennai&to=Delhi"

        try {
            if (path.equals("/api/flights/search")) {
                // GET /api/flights/search?from=X&to=Y
                Map<String, String> params = parseQuery(query);
                String from = params.getOrDefault("from", "");
                String to   = params.getOrDefault("to",   "");
                List<Flight> results = flightDAO.searchFlights(from, to);
                sendJson(ex, 200, flightsToJson(results));

            } else if (path.equals("/api/flights/all")) {
                // GET /api/flights/all — all flights including full ones
                List<Flight> all = flightDAO.getAllFlightsIncludingFull();
                sendJson(ex, 200, flightsToJson(all));

            } else if (path.matches("/api/flights/\\d+")) {
                // GET /api/flights/{id}
                int id = Integer.parseInt(path.substring("/api/flights/".length()));
                Flight f = flightDAO.getFlightById(id);
                if (f == null) sendJson(ex, 404, "{\"error\":\"Flight not found\"}");
                else           sendJson(ex, 200, flightToJson(f));

            } else {
                // GET /api/flights — only available flights
                List<Flight> flights = flightDAO.getAllFlights();
                sendJson(ex, 200, flightsToJson(flights));
            }
        } catch (Exception e) {
            sendJson(ex, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // =========================================================================
    //  BOOKINGS HANDLER — GET/POST/DELETE /api/bookings
    // =========================================================================

    private static void handleBookings(HttpExchange ex) throws IOException {
        addCorsHeaders(ex);

        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String path   = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod().toUpperCase();

        try {
            // DELETE /api/bookings/{id}  — cancel booking
            if (method.equals("DELETE") && path.matches("/api/bookings/\\d+")) {
                int id = Integer.parseInt(path.substring("/api/bookings/".length()));
                boolean ok = bookingDAO.cancelBooking(id);
                if (ok) sendJson(ex, 200, "{\"success\":true,\"message\":\"Booking cancelled and seat restored.\"}");
                else    sendJson(ex, 404, "{\"success\":false,\"error\":\"Booking not found.\"}");

            // GET /api/bookings/{id}  — booking by id
            } else if (method.equals("GET") && path.matches("/api/bookings/\\d+")) {
                int id = Integer.parseInt(path.substring("/api/bookings/".length()));
                Booking b = bookingDAO.getBookingById(id);
                if (b == null) sendJson(ex, 404, "{\"error\":\"Booking not found\"}");
                else           sendJson(ex, 200, bookingToJson(b));

            // POST /api/bookings  — create booking
            } else if (method.equals("POST") && path.equals("/api/bookings")) {
                String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = parseJsonBody(body);

                int    flightId = Integer.parseInt(data.getOrDefault("flightId", "0"));
                String name     = data.getOrDefault("name", "").trim();
                String email    = data.getOrDefault("email", "").trim();

                if (flightId <= 0 || name.isEmpty() || email.isEmpty()) {
                    sendJson(ex, 400, "{\"error\":\"flightId, name, and email are required.\"}");
                    return;
                }
                if (!email.contains("@") || !email.contains(".")) {
                    sendJson(ex, 400, "{\"error\":\"Invalid email address.\"}");
                    return;
                }

                Booking booking = new Booking(flightId, name, email);
                boolean success = bookingDAO.bookTicket(booking);
                if (success) {
                    sendJson(ex, 201, "{\"success\":true,\"bookingId\":" + booking.getBookingId() + "}");
                } else {
                    sendJson(ex, 409, "{\"success\":false,\"error\":\"No seats available or booking failed.\"}");
                }

            // GET /api/bookings  — all bookings
            } else if (method.equals("GET") && path.equals("/api/bookings")) {
                List<Booking> bookings = bookingDAO.getAllBookings();
                sendJson(ex, 200, bookingsToJson(bookings));

            } else {
                sendJson(ex, 405, "{\"error\":\"Method not allowed\"}");
            }

        } catch (Exception e) {
            sendJson(ex, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    // =========================================================================
    //  STATIC FILE HANDLER — serves index.html for GET /
    // =========================================================================

    private static void handleStatic(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ex.sendResponseHeaders(405, -1);
            return;
        }
        // Look for index.html in the same directory as the JAR / working directory
        File html = new File("index.html");
        if (!html.exists()) {
            String msg = "index.html not found. Run the server from the folder containing index.html.";
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain");
            ex.sendResponseHeaders(404, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.getResponseBody().close();
            return;
        }
        byte[] bytes = java.nio.file.Files.readAllBytes(html.toPath());
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    // =========================================================================
    //  JSON SERIALISERS
    // =========================================================================

    private static String flightsToJson(List<Flight> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(flightToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String flightToJson(Flight f) {
        return "{" +
            "\"id\":"              + f.getFlightId()       + "," +
            "\"number\":\""        + escape(f.getFlightNumber())  + "\"," +
            "\"source\":\""        + escape(f.getSource())        + "\"," +
            "\"destination\":\""   + escape(f.getDestination())   + "\"," +
            "\"departure\":\""     + escape(f.getDepartureTime())  + "\"," +
            "\"totalSeats\":"      + f.getTotalSeats()     + "," +
            "\"availableSeats\":"  + f.getAvailableSeats() +
        "}";
    }

    private static String bookingsToJson(List<Booking> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(bookingToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String bookingToJson(Booking b) {
        return "{" +
            "\"bookingId\":"       + b.getBookingId()       + "," +
            "\"flightId\":"        + b.getFlightId()        + "," +
            "\"flightNumber\":\""  + escape(b.getFlightNumber())   + "\"," +
            "\"source\":\""        + escape(b.getSource())         + "\"," +
            "\"destination\":\""   + escape(b.getDestination())    + "\"," +
            "\"departure\":\""     + escape(b.getDepartureTime())   + "\"," +
            "\"passengerName\":\"" + escape(b.getPassengerName())  + "\"," +
            "\"passengerEmail\":\"" + escape(b.getPassengerEmail()) + "\"," +
            "\"bookingDate\":\""   + escape(b.getBookingDate())    + "\"" +
        "}";
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static void addCorsHeaders(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    /** Parse query string: "from=Chennai&to=Delhi" → {from: Chennai, to: Delhi} */
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) map.put(decode(kv[0]), decode(kv[1]));
        }
        return map;
    }

    private static String decode(String s) {
        try { return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    /**
     * Minimal JSON body parser for simple flat objects.
     * Parses: {"flightId":3,"name":"Jayesh","email":"j@test.com"}
     */
    private static Map<String, String> parseJsonBody(String json) {
        Map<String, String> map = new HashMap<>();
        // Strip outer braces
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Split by comma (naive but works for flat simple JSON)
        for (String token : json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
            token = token.trim();
            int colon = token.indexOf(':');
            if (colon < 0) continue;
            String key = token.substring(0, colon).trim().replaceAll("\"", "");
            String val = token.substring(colon + 1).trim().replaceAll("^\"|\"$", "");
            map.put(key, val);
        }
        return map;
    }

    /** Escape special characters for JSON strings */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
