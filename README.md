# ✈ Flight Booking System

A Java Swing desktop application that uses **JDBC** to connect to **MySQL** and manage flight bookings.  
Built as a college assessment project for *CSA0906 – Java Programming*.

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 11+ | Core language |
| Java Swing | Desktop GUI |
| JDBC (Java Database Connectivity) | Connects Java to MySQL |
| MySQL 8.x | Relational database |
| Maven Wrapper (mvnw) | Build & dependency management — **no Maven install needed** |

---

## Requirements

- **JDK 11 or later** – [Download here](https://adoptium.net/)
- **MySQL Server 8.x** – [Download here](https://dev.mysql.com/downloads/mysql/)
- **Git** (optional, for cloning)

> ✅ **No Maven installation required!** The project includes `mvnw.cmd` (Windows) and `mvnw` (Mac/Linux) — Maven Wrapper that downloads Maven automatically on first run.

---

## Project Structure

```
Case Study/
├── database.sql                    ← Run this FIRST in MySQL
├── pom.xml                         ← Maven build file
├── mvnw.cmd                        ← Maven Wrapper for Windows
├── mvnw                            ← Maven Wrapper for Mac/Linux
├── .gitignore
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── flightbooking/
                    ├── Main.java               ← Entry point
                    ├── db/
                    │   └── DatabaseConnection.java  ← ⚠️ Set your MySQL credentials here
                    ├── model/
                    │   ├── Flight.java
                    │   └── Booking.java
                    ├── dao/
                    │   ├── FlightDAO.java      ← Flight DB operations
                    │   └── BookingDAO.java     ← Booking DB operations
                    └── ui/
                        ├── MainFrame.java      ← Main window
                        ├── FlightPanel.java    ← View flights + book
                        └── BookingPanel.java   ← View + cancel bookings
```

---

## ⚡ Quick Start (3 Steps)

### Step 1 – Download the Project

**Option A – Clone with Git:**
```bash
git clone https://github.com/Jayesh7123/CSA0906---Java-Programming.git
cd "CSA0906---Java-Programming/Case Study"
```

**Option B – Download ZIP:**
- Click the green **"Code"** button on GitHub → **Download ZIP**
- Extract the ZIP → open the `Case Study` folder

---

### Step 2 – Set Up the MySQL Database

1. **Make sure MySQL Server is running**

2. **Run the SQL script** to create the database and tables:

   **In MySQL Workbench:**
   - Go to *File → Open SQL Script*
   - Select `database.sql` from the project folder
   - Click ⚡ **Execute All** (Ctrl+Shift+Enter)

   **Or from the command line:**
   ```bash
   mysql -u root -p < database.sql
   ```

3. This creates `flight_booking_db` with 2 tables and **10 sample flights** ✅

---

### Step 3 – Set Your MySQL Password

Open this file:
```
src/main/java/com/flightbooking/db/DatabaseConnection.java
```

Change **line 17–18** to match your MySQL setup:

```java
private static final String DB_USER     = "root";      // ← your MySQL username
private static final String DB_PASSWORD = "root";      // ← your MySQL password
```

> ℹ️ The default username is `root`. Change the password to whatever you set when installing MySQL.

---

### Step 4 – Run the Application

**On Windows** (open Command Prompt in the project folder):
```cmd
mvnw.cmd exec:java
```

**On Mac / Linux** (open Terminal in the project folder):
```bash
chmod +x mvnw
./mvnw exec:java
```

> 🕐 The first run downloads Maven automatically (~1 min). Subsequent runs are instant.

The **Flight Booking System window** will open on your screen! 🎉

---

## How to Use

### ✈ View Flights
- Click **"✈ Flights & Booking"** in the left sidebar
- The **Available Flights** tab shows all flights from MySQL
- Click **🔄 Refresh** to reload

### 🎫 Book a Ticket
- Go to the **Book a Ticket** tab
- Select a flight from the dropdown
- Enter passenger name, contact (phone/email), and number of seats
- The fare and total amount update automatically
- Click **🎫 Confirm Booking**
- Note your **Booking ID** shown on success!

### 📋 View Bookings
- Click **"📋 My Bookings"** in the sidebar
- All bookings from MySQL are shown with status (**ACTIVE** / **CANCELLED**)

### ❌ Cancel a Booking
- Click **"📋 My Bookings"** → **Cancel Booking** tab
- Enter the **Booking ID**
- Click **🔍 Look Up** to preview, then **❌ Cancel Booking** to cancel
- Seats are automatically restored to the flight

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `Communications link failure` | MySQL Server is not running — start it first |
| `Access denied for user 'root'` | Wrong password in `DatabaseConnection.java` |
| `Unknown database 'flight_booking_db'` | Run `database.sql` first (Step 2) |
| `'mvnw.cmd' is not recognized` | You're not inside the `Case Study` folder — use `cd` to navigate there first |
| No flights shown | Run `database.sql` to insert the sample data |
| Port 3306 refused | MySQL is not listening — check MySQL service status |

---

## Viva Short Notes

### What is JDBC?
JDBC (Java Database Connectivity) is a Java API that allows Java programs to connect to and interact with relational databases like MySQL using SQL queries.

### What is a DAO?
DAO (Data Access Object) is a design pattern that separates database logic from business/UI logic. `FlightDAO` and `BookingDAO` contain all SQL queries.

### What is PreparedStatement?
`PreparedStatement` is a pre-compiled SQL statement that prevents SQL injection and is more efficient than `Statement`.

### What is ResultSet?
`ResultSet` holds the rows returned by a SELECT query. You iterate it with `rs.next()`.

### What is a Transaction?
A group of SQL operations that either ALL succeed or ALL fail together.  
`setAutoCommit(false)` → `commit()` → `rollback()` on error.

### Primary Key vs Foreign Key
- **Primary Key**: Uniquely identifies each row (`flight_id`, `booking_id`)
- **Foreign Key**: Links `bookings.flight_id` → `flights.flight_id` to enforce referential integrity

### Why setAutoCommit(false)?
Without it, each SQL statement is its own transaction. With it, the seat update and booking insert happen atomically — preventing data inconsistency if one fails.

---

## License

This project is created for educational purposes only.
