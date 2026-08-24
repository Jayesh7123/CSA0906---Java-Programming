-- ============================================================
--  Flight Booking System - Database Setup
--  Run this file in MySQL Workbench or MySQL CLI:
--      mysql -u root -p < database.sql
-- ============================================================

-- Create and use the database
CREATE DATABASE IF NOT EXISTS flight_booking_db;
USE flight_booking_db;

-- Drop tables if they already exist (for re-runs)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS flights;

-- ============================================================
--  FLIGHTS table
-- ============================================================
CREATE TABLE flights (
    flight_id       INT             PRIMARY KEY AUTO_INCREMENT,
    flight_number   VARCHAR(20)     NOT NULL UNIQUE,
    source          VARCHAR(100)    NOT NULL,
    destination     VARCHAR(100)    NOT NULL,
    flight_date     DATE            NOT NULL,
    flight_time     TIME            NOT NULL,
    total_seats     INT             NOT NULL,
    available_seats INT             NOT NULL,
    fare            DECIMAL(10, 2)  NOT NULL,
    CONSTRAINT chk_seats CHECK (available_seats >= 0 AND available_seats <= total_seats),
    CONSTRAINT chk_fare  CHECK (fare > 0)
);

-- ============================================================
--  BOOKINGS table
-- ============================================================
CREATE TABLE bookings (
    booking_id        INT             PRIMARY KEY AUTO_INCREMENT,
    flight_id         INT             NOT NULL,
    passenger_name    VARCHAR(150)    NOT NULL,
    passenger_contact VARCHAR(100)    NOT NULL,
    seats_booked      INT             NOT NULL,
    total_amount      DECIMAL(10, 2)  NOT NULL,
    booking_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status            ENUM('ACTIVE','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
    CONSTRAINT chk_seats_booked CHECK (seats_booked > 0)
);

-- ============================================================
--  SAMPLE FLIGHT DATA
-- ============================================================
INSERT INTO flights (flight_number, source, destination, flight_date, flight_time, total_seats, available_seats, fare) VALUES
('AI-101',  'Chennai',   'Delhi',     '2026-09-05', '06:00:00', 180, 45,  4500.00),
('AI-202',  'Chennai',   'Mumbai',    '2026-09-05', '08:30:00', 160, 80,  3200.00),
('AI-303',  'Bangalore', 'Chennai',   '2026-09-06', '10:00:00', 140, 60,  1800.00),
('AI-404',  'Hyderabad', 'Delhi',     '2026-09-06', '14:15:00', 200, 120, 5500.00),
('AI-505',  'Mumbai',    'Bangalore', '2026-09-07', '07:45:00', 150, 30,  2800.00),
('AI-606',  'Delhi',     'Chennai',   '2026-09-07', '16:00:00', 180, 90,  4200.00),
('AI-707',  'Kolkata',   'Mumbai',    '2026-09-08', '09:30:00', 160, 70,  3800.00),
('AI-808',  'Pune',      'Delhi',     '2026-09-08', '11:00:00', 130, 50,  4800.00),
('AI-909',  'Chennai',   'Kolkata',   '2026-09-09', '13:45:00', 170, 110, 3600.00),
('AI-1010', 'Mumbai',    'Hyderabad', '2026-09-09', '17:30:00', 140, 85,  2500.00);

-- ============================================================
--  Verify
-- ============================================================
SELECT * FROM flights;
