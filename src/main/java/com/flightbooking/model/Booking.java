package com.flightbooking.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing a row in the 'bookings' table.
 */
public class Booking {

    private int        bookingId;
    private int        flightId;
    private String     passengerName;
    private String     passengerContact;
    private int        seatsBooked;
    private BigDecimal totalAmount;
    private Timestamp  bookingDate;
    private String     status;           // "ACTIVE" or "CANCELLED"

    // Extra fields joined from the flights table for display purposes
    private String     flightNumber;
    private String     source;
    private String     destination;
    private String     flightDate;

    public Booking() {}

    public Booking(int bookingId, int flightId, String passengerName, String passengerContact,
                   int seatsBooked, BigDecimal totalAmount, Timestamp bookingDate, String status) {
        this.bookingId        = bookingId;
        this.flightId         = flightId;
        this.passengerName    = passengerName;
        this.passengerContact = passengerContact;
        this.seatsBooked      = seatsBooked;
        this.totalAmount      = totalAmount;
        this.bookingDate      = bookingDate;
        this.status           = status;
    }

    // ---- Getters ----

    public int        getBookingId()        { return bookingId; }
    public int        getFlightId()         { return flightId; }
    public String     getPassengerName()    { return passengerName; }
    public String     getPassengerContact() { return passengerContact; }
    public int        getSeatsBooked()      { return seatsBooked; }
    public BigDecimal getTotalAmount()      { return totalAmount; }
    public Timestamp  getBookingDate()      { return bookingDate; }
    public String     getStatus()           { return status; }
    public String     getFlightNumber()     { return flightNumber; }
    public String     getSource()           { return source; }
    public String     getDestination()      { return destination; }
    public String     getFlightDate()       { return flightDate; }

    // ---- Setters ----

    public void setBookingId(int bookingId)                  { this.bookingId        = bookingId; }
    public void setFlightId(int flightId)                    { this.flightId         = flightId; }
    public void setPassengerName(String passengerName)       { this.passengerName    = passengerName; }
    public void setPassengerContact(String passengerContact) { this.passengerContact = passengerContact; }
    public void setSeatsBooked(int seatsBooked)              { this.seatsBooked      = seatsBooked; }
    public void setTotalAmount(BigDecimal totalAmount)       { this.totalAmount      = totalAmount; }
    public void setBookingDate(Timestamp bookingDate)        { this.bookingDate      = bookingDate; }
    public void setStatus(String status)                     { this.status           = status; }
    public void setFlightNumber(String flightNumber)         { this.flightNumber     = flightNumber; }
    public void setSource(String source)                     { this.source           = source; }
    public void setDestination(String destination)           { this.destination      = destination; }
    public void setFlightDate(String flightDate)             { this.flightDate       = flightDate; }

    @Override
    public String toString() {
        return "Booking#" + bookingId + " | " + passengerName
                + " | Flight: " + flightNumber
                + " | Seats: " + seatsBooked
                + " | Amount: ₹" + totalAmount
                + " | Status: " + status;
    }
}
