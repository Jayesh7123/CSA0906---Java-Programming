# Flight Booking Management System

A **Java console-based application** that connects to a **MySQL** database using **JDBC** to manage flight bookings. Built as a college project demonstrating core Java OOP, DAO pattern, JDBC transactions, and MySQL integration — without any heavy frameworks.

---

## Features

- **View Available Flights** — Displays all flights with seats remaining
- **Search Flights** — Search by source and destination (case-insensitive)
- **Book Ticket** — Book a seat with passenger name and email
- **View Booking** — Look up full booking details by Booking ID
- **View All Bookings** — See every booking in the system
- **Cancel Booking** — Cancel a booking and automatically restore the seat
- **Automatic Seat Management** — Seats decrease on booking, restore on cancellation
- **JDBC Transactions** — Booking and seat update happen atomically (all-or-nothing)
- **Input Validation** — Handles invalid inputs gracefully without crashing

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Core application language |
| JDBC | Java API for connecting to MySQL |
| MySQL 8.x | Relational database for persistent storage |
| MySQL Connector/J 8.3.0 | JDBC driver that bridges Java and MySQL |
| Maven | Dependency management and build tool |
| Git / GitHub | Version control |

---

## Project Structure

```
FlightBookingSystem/
│
├── src/
│   └── main/
│       └── java/
│           ├── model/
│           │   ├── Flight.java       ← Flight entity class
│           │   └── Booking.java      ← Booking entity class
│           │
│           ├── dao/
│           │   ├── FlightDAO.java    ← All flight-related DB operations
│           │   └── BookingDAO.java   ← All booking-related DB operations + transactions
│           │
│           ├── util/
│           │   └── DBConnection.java ← JDBC connection setup
│           │
│           └── Main.java             ← Entry point, main menu, user input
│
├── database/
│   └── flight_booking.sql    ← Run this to create DB and insert sample data
│
├── lib/
│   └── (place mysql-connector-j.jar here if not using Maven)
│
├── README.md
├── .gitignore
└── pom.xml                   ← Maven configuration (includes MySQL Connector/J)
```

---

## Requirements

- **Java JDK 17** or higher
- **MySQL Server 8.x**
- **Maven 3.6+** (for dependency management and building)
- Internet connection (first time, Maven downloads MySQL Connector/J automatically)

---

## Database Setup

### Step 1 — Start MySQL Server
Make sure your MySQL server is running.

### Step 2 — Open MySQL client
```bash
mysql -u root -p
```
Enter your MySQL root password when prompted.

### Step 3 — Run the SQL script
```sql
source C:/Users/JAYESH/OneDrive/Desktop/FlightBookingSystem/database/flight_booking.sql;
```
> **Or** open the file in **MySQL Workbench**: File → Open SQL Script → Run

This script will:
- Create the `flight_booking_db` database
- Create the `flights` and `bookings` tables
- Insert 10 sample flights for immediate testing

### Step 4 — Configure credentials
Open `src/main/java/util/DBConnection.java` and update:

```java
private static final String USERNAME = "root";        // Your MySQL username
private static final String PASSWORD = "your_password_here"; // Your MySQL password
```

---

## Running the Application

### Using Maven (Recommended)

```bash
# Navigate to the project root
cd C:/Users/JAYESH/OneDrive/Desktop/FlightBookingSystem

# Compile and run
mvn compile exec:java -Dexec.mainClass="Main"
```

### Build a single runnable JAR

```bash
mvn package
java -jar target/FlightBookingSystem-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Using IntelliJ IDEA

1. Open IntelliJ → **File → Open** → Select the `FlightBookingSystem` folder
2. IntelliJ will detect `pom.xml` and import Maven dependencies automatically
3. Open `src/main/java/Main.java`
4. Click the **Run** button (green triangle) or press `Shift + F10`

### Without Maven (Manual JAR)

1. Download `mysql-connector-j-8.3.0.jar` from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)
2. Place it in the `lib/` folder
3. Compile:
   ```bash
   javac -cp lib/mysql-connector-j-8.3.0.jar -sourcepath src/main/java src/main/java/Main.java -d out/
   ```
4. Run:
   ```bash
   java -cp out/;lib/mysql-connector-j-8.3.0.jar Main
   ```

---

## What is JDBC?

**JDBC (Java Database Connectivity)** is a standard Java API that allows Java programs to interact with relational databases.

Key JDBC classes used in this project:

| Class | Role |
|---|---|
| `DriverManager` | Finds the MySQL driver and creates a Connection |
| `Connection` | Represents an active session with the MySQL database |
| `PreparedStatement` | Executes parameterized SQL queries safely |
| `Statement` | Executes simple SQL without parameters |
| `ResultSet` | Holds the rows returned by a SELECT query |

**Why PreparedStatement instead of Statement?**
PreparedStatement uses `?` placeholders and fills them in safely, preventing SQL injection attacks.

**What is a JDBC Transaction?**
A transaction groups multiple SQL statements so they either all succeed or all fail together:
```java
connection.setAutoCommit(false);  // Start transaction
// ... execute INSERT, UPDATE ...
connection.commit();              // Save all changes
// On error:
connection.rollback();            // Undo all changes
```

---

## How to Test

### Test 1 — View Flights
Choose option **1** → should show 9 available flights (flight AI110 has 0 seats).

### Test 2 — Search Flights
Choose option **2** → enter `Chennai` and `Hyderabad` → should show flight AI101.

### Test 3 — Book a Ticket
Choose option **3** → select a flight → enter name and email → note the Booking ID and that available seats decreased by 1.

### Test 4 — View Your Booking
Choose option **4** → enter the Booking ID from Test 3 → full details shown.

### Test 5 — Cancel the Booking
Choose option **6** → enter the same Booking ID → confirm cancellation → verify seats restored.

### Test 6 — Book a Full Flight
Try to book flight **AI110** (0 seats) → should show "Sorry! No seats available".

### Test 7 — Non-existent Booking
Choose option **4** → enter Booking ID `9999` → should show "Booking not found".

---

## Screenshots

*Add screenshots of the running application here after testing.*

---

## GitHub Upload Instructions

```bash
# Initialize git
git init

# Add all project files
git add .

# First commit
git commit -m "Initial commit: Flight Booking Management System"

# Add your GitHub remote
git remote add origin https://github.com/YOUR_USERNAME/FlightBookingSystem.git

# Push to GitHub
git push -u origin main
```

> **Note:** The `.gitignore` file ensures that `target/`, `.idea/`, `*.class`, and real passwords are NOT uploaded.

---

## Author

**Jayesh**
College Project — Java + MySQL + JDBC
