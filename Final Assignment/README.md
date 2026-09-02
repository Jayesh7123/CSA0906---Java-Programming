# Canteen Food Ordering and Billing System (Java + Swing + JDBC + MySQL)

A complete assignment-ready Java desktop application for CSA09 Java Programming.

## Features
- Student/Faculty/Staff customer registration and login
- Admin access with protected credentials
- Beautiful Swing GUI with dashboard cards, styled tables and rounded buttons
- Food menu with live stock
- Add to cart, modify quantity, remove items, clear cart
- Automatic billing and invoice popup
- Place orders using multithreading with synchronized stock updates
- Cancel placed orders with stock restoration
- Admin CRUD for food items, prices and stock
- Admin order status management and user listing
- JDBC with MySQL using CREATE, INSERT, SELECT, UPDATE and DELETE
- OOP: encapsulation, inheritance, polymorphism
- Collections: ArrayList, HashMap, HashSet, Hashtable, Iterator and generics
- Custom exception handling for insufficient stock

## Project structure
```text
CanteenFoodOrdering/
├── src/
│   ├── Main.java
│   ├── DBConnection.java
│   ├── User.java
│   ├── FoodItem.java
│   ├── UITheme.java
│   ├── LoginFrame.java
│   ├── CustomerFrame.java
│   └── AdminFrame.java
├── lib/
│   └── PLACE_MYSQL_CONNECTOR_JAR_HERE.txt
├── docs/
│   ├── Pseudocode.txt
│   ├── DatabaseSchema.sql
│   └── AssignmentNotes.txt
└── README.md
```

## Setup (VS Code)
1. Install JDK 17 or later.
2. Install MySQL Server and start it.
3. Copy your `mysql-connector-j-26.7.0.jar` into the `lib` folder.
4. Open the folder in VS Code.
5. Add the JAR to the Java project libraries/classpath.
6. Edit `DBConnection.java` and set your MySQL root password if needed.
7. Run `Main.java`.

## Administrator access
Administrator credentials are not displayed in the application interface.

## Database
The application automatically creates the `canteen_db` database and all required tables on first run.
