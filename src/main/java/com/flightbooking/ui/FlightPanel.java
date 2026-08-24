package com.flightbooking.ui;

import com.flightbooking.dao.BookingDAO;
import com.flightbooking.dao.FlightDAO;
import com.flightbooking.model.Booking;
import com.flightbooking.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel – View available flights & Book a ticket.
 * Clean redesign: high-contrast labels, bright input fields, centred form.
 */
public class FlightPanel extends JPanel {

    // ── DAOs ────────────────────────────────────────────────────────
    private final FlightDAO  flightDAO  = new FlightDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // ── View-flights table ───────────────────────────────────────────
    private JTable            flightTable;
    private DefaultTableModel flightTableModel;

    // ── Book-ticket form ─────────────────────────────────────────────
    private JComboBox<String> flightComboBox;
    private JTextField        nameField;
    private JTextField        contactField;
    private JTextField        seatsField;
    private JLabel            fareLabel;
    private JLabel            totalLabel;

    private List<Flight> cachedFlights;

    // ── Colour palette (matches MainFrame) ──────────────────────────
    private static final Color BG_DARK   = new Color(18, 24, 40);
    private static final Color BG_CARD   = new Color(26, 35, 55);
    private static final Color BG_INPUT  = new Color(36, 48, 72);
    private static final Color BORDER_COL= new Color(55, 72, 105);
    private static final Color TEXT_MAIN = new Color(220, 230, 245);
    private static final Color TEXT_DIM  = new Color(140, 160, 190);
    private static final Color ACCENT    = new Color(99, 179, 237);
    private static final Color SUCCESS   = new Color(52, 211, 153);
    private static final Color DANGER    = new Color(239, 68, 68);
    private static final Color GOLD      = new Color(251, 191, 36);

    // ================================================================
    public FlightPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JTabbedPane tabs = buildStyledTabs();
        tabs.addTab("  ✈   Available Flights  ", buildViewFlightsTab());
        tabs.addTab("  🎫   Book a Ticket  ",    buildBookTicketTab());

        add(tabs, BorderLayout.CENTER);
        loadFlightsIntoTable();
    }

    // ================================================================
    //  TAB 1 – Available Flights
    // ================================================================
    private JPanel buildViewFlightsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        JLabel title = new JLabel("Available Flights");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = createButton("🔄  Refresh", new Color(59, 130, 246));
        refreshBtn.addActionListener(e -> loadFlightsIntoTable());
        JPanel btnHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnHolder.setBackground(BG_DARK);
        btnHolder.add(refreshBtn);
        header.add(btnHolder, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Flight No.", "From", "To", "Date", "Time",
                         "Total Seats", "Avail. Seats", "Fare (₹)"};
        flightTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        flightTable = new JTable(flightTableModel);
        styleTable(flightTable, ACCENT);

        JScrollPane scroll = new JScrollPane(flightTable);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        scroll.setBackground(BG_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ================================================================
    //  TAB 2 – Book a Ticket
    // ================================================================
    private JPanel buildBookTicketTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);

        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));
        card.setPreferredSize(new Dimension(640, 460));

        // Card title
        JLabel title = new JLabel("Book a Ticket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(SUCCESS);
        card.add(title, BorderLayout.NORTH);

        // Form grid
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 8, 10, 8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Select Flight
        flightComboBox = new JComboBox<>();
        flightComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightComboBox.setBackground(BG_INPUT);
        flightComboBox.setForeground(TEXT_MAIN);
        flightComboBox.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        flightComboBox.setPreferredSize(new Dimension(380, 34));
        flightComboBox.addActionListener(e -> updateFareDisplay());
        addRow(form, gbc, 0, "Select Flight:", flightComboBox);

        // Passenger Name
        nameField = createTextField();
        addRow(form, gbc, 1, "Passenger Name:", nameField);

        // Contact
        contactField = createTextField();
        addRow(form, gbc, 2, "Phone / Email:", contactField);

        // Seats
        seatsField = createTextField();
        seatsField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateTotalDisplay(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateTotalDisplay(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotalDisplay(); }
        });
        addRow(form, gbc, 3, "Number of Seats:", seatsField);

        // Fare (read-only)
        fareLabel = makeValueLabel("₹ 0.00", GOLD);
        addRow(form, gbc, 4, "Fare per Seat:", fareLabel);

        // Total (read-only)
        totalLabel = makeValueLabel("₹ 0.00", SUCCESS);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        addRow(form, gbc, 5, "Total Amount:", totalLabel);

        card.add(form, BorderLayout.CENTER);

        // Buttons
        JButton bookBtn  = createButton("🎫  Confirm Booking", new Color(16, 185, 129));
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setPreferredSize(new Dimension(200, 42));
        bookBtn.addActionListener(e -> performBooking());

        JButton clearBtn = createButton("✖  Clear", DANGER);
        clearBtn.setPreferredSize(new Dimension(120, 42));
        clearBtn.addActionListener(e -> clearBookingForm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.add(bookBtn);
        btnRow.add(clearBtn);
        card.add(btnRow, BorderLayout.SOUTH);

        outer.add(card);
        populateFlightComboBox();
        return outer;
    }

    // ================================================================
    //  Data loading
    // ================================================================
    private void loadFlightsIntoTable() {
        try {
            List<Flight> flights = flightDAO.getAvailableFlights();
            flightTableModel.setRowCount(0);
            for (Flight f : flights) {
                flightTableModel.addRow(new Object[]{
                    f.getFlightId(), f.getFlightNumber(),
                    f.getSource(), f.getDestination(),
                    f.getFlightDate(), f.getFlightTime(),
                    f.getTotalSeats(), f.getAvailableSeats(),
                    "₹ " + f.getFare()
                });
            }
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No flights with available seats found.",
                        "No Flights", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            showError("Database error while loading flights:\n" + ex.getMessage());
        }
    }

    private void populateFlightComboBox() {
        flightComboBox.removeAllItems();
        try {
            cachedFlights = flightDAO.getAllFlights();
            for (Flight f : cachedFlights) {
                flightComboBox.addItem(
                        f.getFlightNumber() + "  |  " + f.getSource()
                        + " → " + f.getDestination()
                        + "  |  " + f.getFlightDate()
                        + "  |  Avail: " + f.getAvailableSeats()
                        + "  |  ₹" + f.getFare());
            }
            updateFareDisplay();
        } catch (SQLException ex) {
            showError("Could not load flights for booking:\n" + ex.getMessage());
        }
    }

    // ================================================================
    //  Fare / total helpers
    // ================================================================
    private void updateFareDisplay() {
        int idx = flightComboBox.getSelectedIndex();
        if (idx >= 0 && cachedFlights != null && idx < cachedFlights.size()) {
            fareLabel.setText("₹ " + cachedFlights.get(idx).getFare());
            updateTotalDisplay();
        }
    }

    private void updateTotalDisplay() {
        int idx = flightComboBox.getSelectedIndex();
        if (idx < 0 || cachedFlights == null || idx >= cachedFlights.size()) return;
        try {
            int seats = Integer.parseInt(seatsField.getText().trim());
            if (seats > 0) {
                BigDecimal total = cachedFlights.get(idx).getFare()
                                               .multiply(BigDecimal.valueOf(seats));
                totalLabel.setText("₹ " + total);
            } else {
                totalLabel.setText("₹ 0.00");
            }
        } catch (NumberFormatException ignored) {
            totalLabel.setText("₹ 0.00");
        }
    }

    private void clearBookingForm() {
        nameField.setText("");
        contactField.setText("");
        seatsField.setText("");
        if (flightComboBox.getItemCount() > 0) flightComboBox.setSelectedIndex(0);
        updateFareDisplay();
    }

    // ================================================================
    //  Booking action
    // ================================================================
    private void performBooking() {
        int flightIdx = flightComboBox.getSelectedIndex();
        if (flightIdx < 0 || cachedFlights == null || cachedFlights.isEmpty()) {
            showError("Please select a flight."); return;
        }
        String name    = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String seatStr = seatsField.getText().trim();

        if (name.isEmpty())    { showError("Please enter the passenger name.");       nameField.requestFocus();    return; }
        if (name.length() < 2) { showError("Passenger name must be ≥ 2 characters."); nameField.requestFocus();   return; }
        if (contact.isEmpty()) { showError("Please enter a phone number or email.");   contactField.requestFocus(); return; }
        if (seatStr.isEmpty()) { showError("Please enter the number of seats.");       seatsField.requestFocus();   return; }

        int seats;
        try { seats = Integer.parseInt(seatStr); }
        catch (NumberFormatException e) { showError("Seats must be a valid number."); seatsField.requestFocus(); return; }
        if (seats <= 0)  { showError("Seats must be > 0.");          seatsField.requestFocus(); return; }
        if (seats > 10)  { showError("Max 10 seats per booking.");    seatsField.requestFocus(); return; }

        Flight selected = cachedFlights.get(flightIdx);
        BigDecimal total = selected.getFare().multiply(BigDecimal.valueOf(seats));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm booking?\n\n"
                + "Flight   : " + selected.getFlightNumber() + "\n"
                + "Route    : " + selected.getSource() + " → " + selected.getDestination() + "\n"
                + "Date     : " + selected.getFlightDate() + " " + selected.getFlightTime() + "\n"
                + "Passenger: " + name + "\n"
                + "Contact  : " + contact + "\n"
                + "Seats    : " + seats + "\n"
                + "Total    : ₹" + total,
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int bookingId = bookingDAO.createBooking(
                    selected.getFlightId(), name, contact, seats);
            JOptionPane.showMessageDialog(this,
                    "✅ Booking Successful!\n\n"
                    + "Booking ID : " + bookingId + "\n"
                    + "Flight     : " + selected.getFlightNumber() + "\n"
                    + "Passenger  : " + name + "\n"
                    + "Seats      : " + seats + "\n"
                    + "Total Paid : ₹" + total + "\n\n"
                    + "Save your Booking ID for future reference.",
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            clearBookingForm();
            loadFlightsIntoTable();
            populateFlightComboBox();
        } catch (SQLException ex) {
            showError("Booking failed:\n" + ex.getMessage());
        }
    }

    // ================================================================
    //  Styling helpers
    // ================================================================
    private JTabbedPane buildStyledTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_CARD);
        tabs.setForeground(TEXT_MAIN);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return tabs;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(24);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        tf.setPreferredSize(new Dimension(380, 34));
        return tf;
    }

    private JLabel makeValueLabel(String text, Color colour) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(colour);
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setOpaque(true);
        return btn;
    }

    private void styleTable(JTable table, Color headerColour) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(40, 55, 80));
        table.setSelectionBackground(new Color(59, 130, 246));
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(new Color(18, 24, 40));
        table.getTableHeader().setForeground(headerColour);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBackground(BG_CARD);
        center.setForeground(TEXT_MAIN);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
