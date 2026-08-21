# ✈ SkyWay — Flight Booking System
### Java + JDBC + MySQL | College Project by Jayesh

---

## 📋 Project Description

A **Java application** that connects to a **MySQL database using JDBC** to manage flight bookings.

### Features
- ✅ **View Available Flights** — fetched live from MySQL
- ✅ **Search Flights** — by source and destination (case-insensitive)
- ✅ **Book a Ticket** — with passenger name & email, JDBC transaction ensures atomic seat deduction
- ✅ **View a Booking** — full details via SQL JOIN query
- ✅ **View All Bookings** — complete list from MySQL
- ✅ **Cancel a Booking** — deletes from DB and automatically restores the seat count
- ✅ **Auto Seat Management** — `available_seats` updated on every booking/cancellation

---

## 🛠 Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core application language |
| JDBC (Java Database Connectivity) | Java API to connect to MySQL |
| MySQL 8.x | Relational database |
| MySQL Connector/J 8.3.0 | JDBC driver (JAR file) |
| `com.sun.net.httpserver` | Built-in Java HTTP server (no extra dependencies) |

---

## 🚀 How to Run (Step by Step)

### Step 1 — Set up MySQL Database

1. Make sure **MySQL Server** is running on your machine.
2. Open MySQL Workbench or MySQL command line.
3. Run the SQL setup script:
   ```sql
   source /path/to/FlightBookingSystem/database/flight_booking.sql;
   ```
   This creates the `flight_booking_db` database and inserts 10 sample flights.

### Step 2 — Configure your MySQL password

Open this file and set your MySQL password:
```
src/main/java/util/DBConnection.java
```
Change this line:
```java
private static final String PASSWORD = "your_password_here";
```

### Step 3 — Get the MySQL Connector JAR

Download `mysql-connector-j-8.3.0.jar` from:
https://dev.mysql.com/downloads/connector/j/

Place it in the `lib/` folder:
```
FlightBookingSystem/
  └── lib/
       └── mysql-connector-j-8.3.0.jar   ← place here
```

### Step 4 — Run the Web Application

**Double-click `run-web.bat`** (Windows)

This will:
1. Compile all Java source files
2. Start the Java HTTP server (port 8080)
3. Automatically open your browser at `http://localhost:8080`

The beautiful web interface will open — connected to your MySQL database via JDBC!

---

## 📁 Project Structure

```
FlightBookingSystem/
│
├── src/main/java/
│   ├── model/
│   │   ├── Flight.java          ← Flight entity
│   │   └── Booking.java         ← Booking entity
│   │
│   ├── dao/
│   │   ├── FlightDAO.java       ← All flight DB operations (JDBC)
│   │   └── BookingDAO.java      ← All booking DB operations + JDBC Transactions
│   │
│   ├── util/
│   │   └── DBConnection.java    ← JDBC connection setup (DriverManager)
│   │
│   ├── server/
│   │   └── ApiServer.java       ← Java HTTP server + REST API endpoints
│   │
│   └── Main.java                ← Original console-based entry point
│
├── database/
│   └── flight_booking.sql       ← Run this to create DB + insert sample data
│
├── lib/
│   └── (place mysql-connector-j-8.3.0.jar here)
│
├── index.html                   ← Web UI (served by ApiServer)
├── run-web.bat                  ← ⭐ ONE-CLICK: compile + run web server
├── run.bat                      ← Console-only version
└── pom.xml                      ← Maven config (optional)
```

---

## 🔑 Key JDBC Concepts Demonstrated

| Concept | File | Description |
|---|---|---|
| `DriverManager.getConnection()` | `DBConnection.java` | Establishes connection to MySQL |
| `Statement` | `FlightDAO.java` | Executes simple SELECT queries |
| `PreparedStatement` | `FlightDAO.java`, `BookingDAO.java` | Parameterised queries — SQL injection safe |
| `ResultSet` | All DAO files | Iterates over query results |
| `RETURN_GENERATED_KEYS` | `BookingDAO.java` | Gets auto-generated booking_id after INSERT |
| `setAutoCommit(false)` | `BookingDAO.java` | Starts manual JDBC transaction |
| `commit()` | `BookingDAO.java` | Saves booking + seat update together |
| `rollback()` | `BookingDAO.java` | Reverts all changes if anything fails |
| SQL `JOIN` | `BookingDAO.java` | Fetches booking + flight details in one query |

---

## 🗄 Database Schema

```sql
-- flights table
CREATE TABLE flights (
    flight_id       INT PRIMARY KEY AUTO_INCREMENT,
    flight_number   VARCHAR(20)  NOT NULL UNIQUE,
    source          VARCHAR(100) NOT NULL,
    destination     VARCHAR(100) NOT NULL,
    departure_time  DATETIME     NOT NULL,
    total_seats     INT          NOT NULL,
    available_seats INT          NOT NULL
);

-- bookings table
CREATE TABLE bookings (
    booking_id      INT PRIMARY KEY AUTO_INCREMENT,
    flight_id       INT          NOT NULL,
    passenger_name  VARCHAR(100) NOT NULL,
    passenger_email VARCHAR(100) NOT NULL,
    booking_date    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);
```

---

## 🌐 Architecture (Web Version)

```
Browser (index.html)
      ↕  HTTP + JSON
ApiServer.java  ← Java built-in HTTP server
      ↕  JDBC (PreparedStatement, Transactions)
MySQL Database (flight_booking_db)
```

---

## ✅ Requirements Checklist

| Requirement | Status |
|---|---|
| Java application | ✅ |
| Connects to MySQL using JDBC | ✅ |
| View available flights | ✅ |
| Book a ticket | ✅ |
| View a booking | ✅ |
| Cancel a booking | ✅ |
| Auto-update available seats | ✅ |

---

## 👨‍💻 Author

**Jayesh**
College Project — CSA0906 Java Programming
