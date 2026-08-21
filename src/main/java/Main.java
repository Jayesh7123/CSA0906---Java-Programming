import dao.BookingDAO;
import dao.FlightDAO;
import model.Booking;
import model.Flight;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main.java — Entry point for the Flight Booking Management System.
 *
 * This class:
 *   - Shows the main menu in a loop
 *   - Reads user input using Scanner
 *   - Delegates each operation to FlightDAO or BookingDAO
 *   - Handles exceptions to prevent crashes
 *
 * Run this class to start the application.
 */
public class Main {

    // DAO objects — these handle all database operations
    private static FlightDAO flightDAO = new FlightDAO();
    private static BookingDAO bookingDAO = new BookingDAO();

    // Scanner for reading console input
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Test the database connection once at startup
        System.out.println("Connecting to database...");
        try {
            Connection testConn = DBConnection.getConnection();
            testConn.close();
            System.out.println("Database connected successfully!\n");
        } catch (SQLException e) {
            System.out.println("ERROR: Cannot connect to database.");
            System.out.println("Please check:");
            System.out.println("  1. MySQL server is running");
            System.out.println("  2. Database 'flight_booking_db' exists");
            System.out.println("  3. Username/password in DBConnection.java is correct");
            System.out.println("\nError details: " + e.getMessage());
            return;  // Exit if DB connection fails
        }

        // Main application loop — runs until user chooses Exit
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAvailableFlights();
                    break;
                case 2:
                    searchFlights();
                    break;
                case 3:
                    bookTicket();
                    break;
                case 4:
                    viewBooking();
                    break;
                case 5:
                    viewAllBookings();
                    break;
                case 6:
                    cancelBooking();
                    break;
                case 7:
                    System.out.println("\nThank you for using Flight Booking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please enter a number between 1 and 7.");
            }
        }

        scanner.close();
    }

    // =====================================================================
    //  MENU
    // =====================================================================

    /** Prints the main menu to the console. */
    private static void printMenu() {
        System.out.println("\n========================================");
        System.out.println("        FLIGHT BOOKING SYSTEM");
        System.out.println("========================================");
        System.out.println("  1. View Available Flights");
        System.out.println("  2. Search Flights");
        System.out.println("  3. Book Ticket");
        System.out.println("  4. View Booking");
        System.out.println("  5. View All Bookings");
        System.out.println("  6. Cancel Booking");
        System.out.println("  7. Exit");
        System.out.println("========================================");
    }

    // =====================================================================
    //  OPTION 1 — VIEW AVAILABLE FLIGHTS
    // =====================================================================

    /**
     * Fetches and displays all flights with at least one available seat.
     */
    private static void viewAvailableFlights() {
        System.out.println("\n--- AVAILABLE FLIGHTS ---");

        List<Flight> flights = flightDAO.getAllFlights();

        if (flights.isEmpty()) {
            System.out.println("No available flights at the moment.");
            return;
        }

        printFlightTableHeader();
        for (Flight flight : flights) {
            printFlightRow(flight);
        }
        printTableDivider();
    }

    // =====================================================================
    //  OPTION 2 — SEARCH FLIGHTS
    // =====================================================================

    /**
     * Asks for source and destination, then displays matching flights.
     */
    private static void searchFlights() {
        System.out.println("\n--- SEARCH FLIGHTS ---");

        System.out.print("Enter Source City      : ");
        String source = scanner.nextLine().trim();

        System.out.print("Enter Destination City : ");
        String destination = scanner.nextLine().trim();

        if (source.isEmpty() || destination.isEmpty()) {
            System.out.println("Source and destination cannot be empty.");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(source, destination);

        if (flights.isEmpty()) {
            System.out.println("\nNo available flights found from " + source + " to " + destination + ".");
            return;
        }

        System.out.println("\nFlights from " + source + " to " + destination + ":");
        printFlightTableHeader();
        for (Flight flight : flights) {
            printFlightRow(flight);
        }
        printTableDivider();
    }

    // =====================================================================
    //  OPTION 3 — BOOK TICKET
    // =====================================================================

    /**
     * Guides the user through booking a flight ticket.
     * Validates input before calling bookingDAO.bookTicket().
     */
    private static void bookTicket() {
        System.out.println("\n--- BOOK TICKET ---");

        // First, show available flights for reference
        List<Flight> flights = flightDAO.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights are available for booking right now.");
            return;
        }
        System.out.println("\nAvailable Flights:");
        printFlightTableHeader();
        for (Flight flight : flights) {
            printFlightRow(flight);
        }
        printTableDivider();

        // Get Flight ID from user
        int flightId = readIntInput("\nEnter Flight ID to book: ");

        // Validate: check if flight exists
        Flight selectedFlight = flightDAO.getFlightById(flightId);
        if (selectedFlight == null) {
            System.out.println("Flight with ID " + flightId + " not found.");
            return;
        }

        // Check available seats before asking for passenger details
        if (selectedFlight.getAvailableSeats() <= 0) {
            System.out.println("\nSorry! No seats are available for this flight.");
            return;
        }

        // Get passenger details
        System.out.print("Enter Passenger Name  : ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Passenger name cannot be empty.");
            return;
        }

        System.out.print("Enter Passenger Email : ");
        String email = scanner.nextLine().trim();

        // Basic email validation
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            System.out.println("Please enter a valid email address.");
            return;
        }

        // Create a Booking object and attempt to book
        Booking booking = new Booking(flightId, name, email);
        boolean success = bookingDAO.bookTicket(booking);

        if (success) {
            System.out.println("\n========================================");
            System.out.println("         BOOKING SUCCESSFUL!");
            System.out.println("========================================");
            System.out.println("  Booking ID   : " + booking.getBookingId());
            System.out.println("  Passenger    : " + booking.getPassengerName());
            System.out.println("  Email        : " + booking.getPassengerEmail());
            System.out.println("  Flight       : " + selectedFlight.getFlightNumber());
            System.out.println("  From         : " + selectedFlight.getSource());
            System.out.println("  To           : " + selectedFlight.getDestination());
            System.out.println("  Departure    : " + selectedFlight.getDepartureTime());
            System.out.println("========================================");
        } else {
            System.out.println("\nBooking could not be completed. Please try again.");
        }
    }

    // =====================================================================
    //  OPTION 4 — VIEW BOOKING BY ID
    // =====================================================================

    /**
     * Asks for a Booking ID and displays full booking + flight details.
     */
    private static void viewBooking() {
        System.out.println("\n--- VIEW BOOKING ---");

        int bookingId = readIntInput("Enter Booking ID: ");

        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found. Please check the Booking ID.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("           BOOKING DETAILS");
        System.out.println("========================================");
        System.out.println("  Booking ID     : " + booking.getBookingId());
        System.out.println("  Passenger Name : " + booking.getPassengerName());
        System.out.println("  Email          : " + booking.getPassengerEmail());
        System.out.println("  Flight Number  : " + booking.getFlightNumber());
        System.out.println("  Source         : " + booking.getSource());
        System.out.println("  Destination    : " + booking.getDestination());
        System.out.println("  Departure      : " + booking.getDepartureTime());
        System.out.println("  Booking Date   : " + booking.getBookingDate());
        System.out.println("========================================");
    }

    // =====================================================================
    //  OPTION 5 — VIEW ALL BOOKINGS
    // =====================================================================

    /**
     * Fetches and displays all bookings in a tabular format.
     */
    private static void viewAllBookings() {
        System.out.println("\n--- ALL BOOKINGS ---");

        List<Booking> bookings = bookingDAO.getAllBookings();

        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        // Print table header
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("%-6s %-20s %-25s %-8s %-12s %-12s %-20s%n",
                "BID", "Passenger", "Email", "Flight", "From", "To", "Booking Date");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (Booking b : bookings) {
            System.out.printf("%-6d %-20s %-25s %-8s %-12s %-12s %-20s%n",
                    b.getBookingId(),
                    b.getPassengerName(),
                    b.getPassengerEmail(),
                    b.getFlightNumber(),
                    b.getSource(),
                    b.getDestination(),
                    b.getBookingDate());
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    // =====================================================================
    //  OPTION 6 — CANCEL BOOKING
    // =====================================================================

    /**
     * Cancels an existing booking and restores the flight seat.
     */
    private static void cancelBooking() {
        System.out.println("\n--- CANCEL BOOKING ---");

        int bookingId = readIntInput("Enter Booking ID to cancel: ");

        // First, confirm the booking exists and show its details
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found. Please check the Booking ID.");
            return;
        }

        // Show booking summary before cancelling
        System.out.println("\nBooking found:");
        System.out.println("  Passenger : " + booking.getPassengerName());
        System.out.println("  Flight    : " + booking.getFlightNumber());
        System.out.println("  From      : " + booking.getSource());
        System.out.println("  To        : " + booking.getDestination());

        // Confirmation
        System.out.print("\nAre you sure you want to cancel this booking? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        boolean success = bookingDAO.cancelBooking(bookingId);

        if (success) {
            System.out.println("\nBooking cancelled successfully.");
            System.out.println("Seat has been restored to the flight.");
        } else {
            System.out.println("\nCancellation could not be completed. Please try again.");
        }
    }

    // =====================================================================
    //  DISPLAY HELPERS
    // =====================================================================

    /** Prints the flight table header row. */
    private static void printFlightTableHeader() {
        printTableDivider();
        System.out.printf("%-5s %-10s %-15s %-15s %-22s %-6s%n",
                "ID", "Flight No", "Source", "Destination", "Departure Time", "Seats");
        printTableDivider();
    }

    /** Prints one row of the flight table. */
    private static void printFlightRow(Flight flight) {
        System.out.printf("%-5d %-10s %-15s %-15s %-22s %-6d%n",
                flight.getFlightId(),
                flight.getFlightNumber(),
                flight.getSource(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getAvailableSeats());
    }

    /** Prints a horizontal divider line. */
    private static void printTableDivider() {
        System.out.println("----------------------------------------------------------------------");
    }

    // =====================================================================
    //  INPUT HELPERS
    // =====================================================================

    /**
     * Safely reads an integer from the console.
     * If the user types a non-number, it asks again instead of crashing.
     *
     * @param prompt — the message displayed to the user
     * @return valid integer entered by the user
     */
    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();  // Consume the leftover newline after nextInt()
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();  // Clear the invalid input from the buffer
            }
        }
    }
}
