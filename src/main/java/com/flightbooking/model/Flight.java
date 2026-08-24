package com.flightbooking.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * Model class representing a row in the 'flights' table.
 */
public class Flight {

    private int        flightId;
    private String     flightNumber;
    private String     source;
    private String     destination;
    private Date       flightDate;
    private Time       flightTime;
    private int        totalSeats;
    private int        availableSeats;
    private BigDecimal fare;

    public Flight() {}

    public Flight(int flightId, String flightNumber, String source, String destination,
                  Date flightDate, Time flightTime, int totalSeats, int availableSeats, BigDecimal fare) {
        this.flightId       = flightId;
        this.flightNumber   = flightNumber;
        this.source         = source;
        this.destination    = destination;
        this.flightDate     = flightDate;
        this.flightTime     = flightTime;
        this.totalSeats     = totalSeats;
        this.availableSeats = availableSeats;
        this.fare           = fare;
    }

    // ---- Getters ----

    public int        getFlightId()       { return flightId; }
    public String     getFlightNumber()   { return flightNumber; }
    public String     getSource()         { return source; }
    public String     getDestination()    { return destination; }
    public Date       getFlightDate()     { return flightDate; }
    public Time       getFlightTime()     { return flightTime; }
    public int        getTotalSeats()     { return totalSeats; }
    public int        getAvailableSeats() { return availableSeats; }
    public BigDecimal getFare()           { return fare; }

    // ---- Setters ----

    public void setFlightId(int flightId)                  { this.flightId       = flightId; }
    public void setFlightNumber(String flightNumber)       { this.flightNumber   = flightNumber; }
    public void setSource(String source)                   { this.source         = source; }
    public void setDestination(String destination)         { this.destination    = destination; }
    public void setFlightDate(Date flightDate)             { this.flightDate     = flightDate; }
    public void setFlightTime(Time flightTime)             { this.flightTime     = flightTime; }
    public void setTotalSeats(int totalSeats)              { this.totalSeats     = totalSeats; }
    public void setAvailableSeats(int availableSeats)      { this.availableSeats = availableSeats; }
    public void setFare(BigDecimal fare)                   { this.fare           = fare; }

    @Override
    public String toString() {
        return flightNumber + " | " + source + " → " + destination
                + " | " + flightDate + " " + flightTime
                + " | Seats: " + availableSeats + " | Fare: ₹" + fare;
    }
}
