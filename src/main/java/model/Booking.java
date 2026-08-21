package model;

/**
 * Booking model class representing a booking record in the database.
 * Follows basic OOP encapsulation with private fields and public getters/setters.
 */
public class Booking {

    // Fields matching the 'bookings' table columns
    private int bookingId;
    private int flightId;
    private String passengerName;
    private String passengerEmail;
    private String bookingDate;  // Stored as String for simple display

    // Extra fields for JOIN query results (flight details alongside booking)
    private String flightNumber;
    private String source;
    private String destination;
    private String departureTime;

    // --- Constructors ---

    // Default no-argument constructor
    public Booking() {}

    // Constructor used when creating a new booking (before DB insertion)
    public Booking(int flightId, String passengerName, String passengerEmail) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
    }

    // Full constructor (used when retrieving from DB)
    public Booking(int bookingId, int flightId, String passengerName,
                   String passengerEmail, String bookingDate) {
        this.bookingId = bookingId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.bookingDate = bookingDate;
    }

    // --- Getters ---

    public int getBookingId() {
        return bookingId;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    // --- Setters ---

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    // --- toString for quick display ---

    @Override
    public String toString() {
        return String.format("Booking[ID=%d, FlightID=%d, Passenger=%s, Email=%s, Date=%s]",
                bookingId, flightId, passengerName, passengerEmail, bookingDate);
    }
}
