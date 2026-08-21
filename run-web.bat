@echo off
:: ============================================================
:: run-web.bat — Build and run the SkyWay Web Server
::
:: This script:
::   1. Compiles ALL Java sources including the new ApiServer
::   2. Starts the HTTP server on http://localhost:8080
::   3. Opens your browser automatically
::
:: BEFORE RUNNING:
::   1. Make sure MySQL is running
::   2. Make sure flight_booking_db exists (run database/flight_booking.sql)
::   3. Verify password in: src\main\java\util\DBConnection.java
::   4. Make sure mysql-connector-j-8.3.0.jar is in lib\
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
    echo [ERROR] MySQL Connector/J not found in lib\ folder.
    echo [INFO]  Please download mysql-connector-j-8.3.0.jar from:
    echo [INFO]    https://dev.mysql.com/downloads/connector/j/
    echo [INFO]  Place it in: %LIB_DIR%
    echo.
    pause
    exit /b 1
)

echo.
echo ============================================================
echo   SKYWAY FLIGHT BOOKING SYSTEM - Web Server
echo   Java JDBC + MySQL Backend
echo ============================================================
echo.

:: Compile ALL Java sources (model, util, dao, server)
echo [1/2] Compiling Java sources...
javac -cp "%JAR_PATH%" -sourcepath "%SRC_DIR%" ^
    "%SRC_DIR%\model\Flight.java" ^
    "%SRC_DIR%\model\Booking.java" ^
    "%SRC_DIR%\util\DBConnection.java" ^
    "%SRC_DIR%\dao\FlightDAO.java" ^
    "%SRC_DIR%\dao\BookingDAO.java" ^
    "%SRC_DIR%\server\ApiServer.java" ^
    -d "%OUT_DIR%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed. Please fix the errors shown above.
    pause
    exit /b 1
)

echo [1/2] Compilation successful!
echo.

:: Open browser after a 2-second delay (server needs a moment to start)
echo [2/2] Starting web server at http://localhost:8080 ...
echo.
echo  Open your browser and go to: http://localhost:8080
echo  Press Ctrl+C in this window to stop the server.
echo.

:: Launch browser after 2s in background
start "" /b cmd /c "timeout /t 2 /nobreak >nul && start http://localhost:8080"

:: Run the web server (stays in foreground)
java -cp "%OUT_DIR%;%JAR_PATH%" server.ApiServer

pause
