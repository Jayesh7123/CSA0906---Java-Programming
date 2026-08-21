@echo off
:: ============================================================
:: launch.bat — Run Flight Booking System (Faculty Easy Launch)
::
:: BEFORE RUNNING:
::   1. Make sure MySQL Server is running
::   2. Open MySQL Workbench, run database/flight_booking.sql
::   3. Set your MySQL password in DBConnection.java (line 34)
::      then re-build the JAR (or ask student to pre-configure)
:: ============================================================

echo.
echo Starting Flight Booking Management System...
echo.

java -jar FlightBookingSystem.jar

pause
