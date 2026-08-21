package model;

/**
 * Flight model class representing a flight record in the database.
 * This follows basic encapsulation principles with private fields and public getters/setters.
 */
public class Flight {

    // Fields matching the 'flights' table columns
    private int flightId;
    private String flightNumber;
    private String source;
    private String destination;
    private String departureTime;  // Stored as String for simple display
    private int totalSeats;
    private int availableSeats;

    // --- Constructors ---

    // Default no-argument constructor
    public Flight() {}

    // Parameterized constructor (used when retrieving from DB)
    public Flight(int flightId, String flightNumber, String source, String destination,
                  String departureTime, int totalSeats, int availableSeats) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    // --- Getters ---

    public int getFlightId() {
        return flightId;
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

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    // --- Setters ---

    public void setFlightId(int flightId) {
        this.flightId = flightId;
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

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    // --- toString for quick display ---

    @Override
    public String toString() {
        return String.format("Flight[ID=%d, No=%s, %s -> %s, Departure=%s, Available=%d/%d]",
                flightId, flightNumber, source, destination,
                departureTime, availableSeats, totalSeats);
    }
}
