-- ==============================================================
-- Flight Booking System - Database Setup Script
-- File: database/flight_booking.sql
--
-- HOW TO RUN IN MySQL Workbench:
--   1. Open MySQL Workbench
--   2. Click: File > Open SQL Script
--   3. Select this file and click Open
--   4. Click the lightning bolt icon (Execute All) to run
-- ==============================================================

-- Step 1: Create the database if it does not already exist
CREATE DATABASE IF NOT EXISTS flight_booking_db;

-- Step 2: Switch to the newly created database
USE flight_booking_db;

-- ==============================================================
-- Step 3: Drop tables if they exist (for clean re-run)
-- bookings must be dropped before flights due to foreign key
-- ==============================================================

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS flights;

-- ==============================================================
-- Step 4: Create the flights table
-- ==============================================================

CREATE TABLE flights (
    flight_id       INT PRIMARY KEY AUTO_INCREMENT,
    flight_number   VARCHAR(20)  NOT NULL UNIQUE,
    source          VARCHAR(100) NOT NULL,
    destination     VARCHAR(100) NOT NULL,
    departure_time  DATETIME     NOT NULL,
    total_seats     INT          NOT NULL,
    available_seats INT          NOT NULL
);

-- ==============================================================
-- Step 5: Create the bookings table
-- ==============================================================

CREATE TABLE bookings (
    booking_id      INT PRIMARY KEY AUTO_INCREMENT,
    flight_id       INT          NOT NULL,
    passenger_name  VARCHAR(100) NOT NULL,
    passenger_email VARCHAR(100) NOT NULL,
    booking_date    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

-- ==============================================================
-- Step 6: Insert 10 sample flights for testing
-- Note: AI110 has 0 available seats to test the no-seat scenario
-- ==============================================================

INSERT INTO flights (flight_number, source, destination, departure_time, total_seats, available_seats)
VALUES
    ('AI101', 'Chennai',   'Hyderabad', '2026-09-10 10:00:00', 50, 45),
    ('AI102', 'Chennai',   'Bangalore', '2026-09-11 14:30:00', 60, 38),
    ('AI103', 'Chennai',   'Delhi',     '2026-09-12 06:45:00', 80, 72),
    ('AI104', 'Hyderabad', 'Mumbai',    '2026-09-13 09:15:00', 55, 50),
    ('AI105', 'Bangalore', 'Delhi',     '2026-09-14 16:00:00', 70, 65),
    ('AI106', 'Delhi',     'Mumbai',    '2026-09-15 08:00:00', 90, 85),
    ('AI107', 'Mumbai',    'Chennai',   '2026-09-16 12:30:00', 50, 48),
    ('AI108', 'Hyderabad', 'Chennai',   '2026-09-17 07:45:00', 60, 60),
    ('AI109', 'Delhi',     'Bangalore', '2026-09-18 19:00:00', 75, 70),
    ('AI110', 'Mumbai',    'Hyderabad', '2026-09-19 11:20:00', 40,  0);

-- ==============================================================
-- Step 7: Verify - display all inserted flights
-- ==============================================================

SELECT * FROM flights;
