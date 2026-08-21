@echo off
:: ============================================================
:: run.bat — Run the Flight Booking System (No Maven required)
:: 
:: This script:
::   1. Downloads mysql-connector-j.jar if not present in lib/
::   2. Compiles all Java source files
::   3. Runs the application
::
:: BEFORE RUNNING:
::   Make sure MySQL server is running and you have updated the
::   password in: src\main\java\util\DBConnection.java
:: ============================================================

setlocal

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src\main\java"
set "OUT_DIR=%PROJECT_DIR%out"
set "LIB_DIR=%PROJECT_DIR%lib"
set "JAR_NAME=mysql-connector-j-8.3.0.jar"
set "JAR_PATH=%LIB_DIR%\%JAR_NAME%"

:: Create output and lib directories if they don't exist
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

:: Check if MySQL connector jar exists
if not exist "%JAR_PATH%" (
    echo.
    echo [INFO] MySQL Connector/J not found in lib\ folder.
    echo [INFO] Please download mysql-connector-j-8.3.0.jar from:
    echo [INFO]   https://dev.mysql.com/downloads/connector/j/
    echo [INFO] Place it in: %LIB_DIR%
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================================
echo   FLIGHT BOOKING SYSTEM — Build and Run
echo ============================================================
echo.

:: Compile all Java source files
echo [1/2] Compiling Java sources...
javac -cp "%JAR_PATH%" -sourcepath "%SRC_DIR%" ^
    "%SRC_DIR%\model\Flight.java" ^
    "%SRC_DIR%\model\Booking.java" ^
    "%SRC_DIR%\util\DBConnection.java" ^
    "%SRC_DIR%\dao\FlightDAO.java" ^
    "%SRC_DIR%\dao\BookingDAO.java" ^
    "%SRC_DIR%\Main.java" ^
    -d "%OUT_DIR%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed. Please fix the errors above.
    pause
    exit /b 1
)

echo [1/2] Compilation successful!
echo.

:: Run the application
echo [2/2] Starting Flight Booking System...
echo.
java -cp "%OUT_DIR%;%JAR_PATH%" Main

pause
