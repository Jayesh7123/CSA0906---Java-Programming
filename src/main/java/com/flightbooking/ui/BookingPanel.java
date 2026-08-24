package com.flightbooking.ui;

import com.flightbooking.dao.BookingDAO;
import com.flightbooking.model.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel – View all bookings & Cancel a booking.
 * Clean redesign matching the new FlightPanel style.
 */
public class BookingPanel extends JPanel {

    private final BookingDAO bookingDAO = new BookingDAO();

    private JTable            bookingTable;
    private DefaultTableModel bookingTableModel;
    private JTextField        cancelIdField;

    // ── Colour palette ──────────────────────────────────────────────
    private static final Color BG_DARK   = new Color(18, 24, 40);
    private static final Color BG_CARD   = new Color(26, 35, 55);
    private static final Color BG_INPUT  = new Color(36, 48, 72);
    private static final Color BORDER_COL= new Color(55, 72, 105);
    private static final Color TEXT_MAIN = new Color(220, 230, 245);
    private static final Color TEXT_DIM  = new Color(140, 160, 190);
    private static final Color ACCENT    = new Color(167, 139, 250);
    private static final Color SUCCESS   = new Color(52, 211, 153);
    private static final Color DANGER    = new Color(239, 68, 68);
    private static final Color WARN      = new Color(252, 129, 74);

    // ================================================================
    public BookingPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JTabbedPane tabs = buildStyledTabs();
        tabs.addTab("  📋   My Bookings  ",      buildViewBookingsTab());
        tabs.addTab("  ❌   Cancel Booking  ",   buildCancelTab());

        add(tabs, BorderLayout.CENTER);
        loadBookingsIntoTable();
    }

    // ================================================================
    //  TAB 1 – My Bookings
    // ================================================================
    private JPanel buildViewBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        JLabel title = new JLabel("All Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = createButton("🔄  Refresh", new Color(59, 130, 246));
        refreshBtn.addActionListener(e -> loadBookingsIntoTable());
        JPanel btnHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnHolder.setBackground(BG_DARK);
        btnHolder.add(refreshBtn);
        header.add(btnHolder, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {
            "Booking ID", "Passenger Name", "Contact",
            "Flight No.", "From", "To", "Flight Date",
            "Seats", "Booking Date", "Total (₹)", "Status"
        };
        bookingTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        styleTable(bookingTable);
        bookingTable.getColumnModel().getColumn(10).setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ================================================================
    //  TAB 2 – Cancel Booking
    // ================================================================
    private JPanel buildCancelTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(32, 44, 32, 44)));
        card.setPreferredSize(new Dimension(560, 340));

        // Title
        JLabel title = new JLabel("Cancel a Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(WARN);
        card.add(title, BorderLayout.NORTH);

        // Centre content
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(BG_CARD);

        JLabel instruction = new JLabel(
                "<html><center>Enter the <b>Booking ID</b> you received when booking.<br>"
                + "Cancellation will restore seats to the flight.</center></html>");
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instruction.setForeground(TEXT_DIM);
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(instruction);
        centre.add(Box.createVerticalStrut(22));

        // Booking ID row
        JPanel idRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        idRow.setBackground(BG_CARD);

        JLabel idLabel = new JLabel("Booking ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(TEXT_MAIN);
        idRow.add(idLabel);

        cancelIdField = new JTextField(12);
        cancelIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelIdField.setBackground(BG_INPUT);
        cancelIdField.setForeground(TEXT_MAIN);
        cancelIdField.setCaretColor(Color.WHITE);
        cancelIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        cancelIdField.setPreferredSize(new Dimension(160, 38));
        idRow.add(cancelIdField);

        centre.add(idRow);
        card.add(centre, BorderLayout.CENTER);

        // Buttons
        JButton lookupBtn = createButton("🔍  Look Up", new Color(100, 116, 139));
        lookupBtn.setPreferredSize(new Dimension(150, 40));
        lookupBtn.addActionListener(e -> performLookup());

        JButton cancelBtn = createButton("❌  Cancel Booking", DANGER);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setPreferredSize(new Dimension(190, 40));
        cancelBtn.addActionListener(e -> performCancellation());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.add(lookupBtn);
        btnRow.add(cancelBtn);
        card.add(btnRow, BorderLayout.SOUTH);

        outer.add(card);
        return outer;
    }

    // ================================================================
    //  Data loading
    // ================================================================
    public void loadBookingsIntoTable() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            bookingTableModel.setRowCount(0);
            for (Booking b : bookings) {
                bookingTableModel.addRow(new Object[]{
                    b.getBookingId(), b.getPassengerName(), b.getPassengerContact(),
                    b.getFlightNumber(), b.getSource(), b.getDestination(),
                    b.getFlightDate(), b.getSeatsBooked(), b.getBookingDate(),
                    "₹ " + b.getTotalAmount(), b.getStatus()
                });
            }
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No bookings found in the database.",
                        "No Bookings", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            showError("Database error while loading bookings:\n" + ex.getMessage());
        }
    }

    // ================================================================
    //  Look up
    // ================================================================
    private void performLookup() {
        String idStr = cancelIdField.getText().trim();
        if (idStr.isEmpty()) { showError("Please enter a Booking ID."); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idStr); }
        catch (NumberFormatException e) { showError("Booking ID must be a number."); return; }
        try {
            Booking b = bookingDAO.getBookingById(bookingId);
            if (b == null) {
                JOptionPane.showMessageDialog(this,
                        "No booking found with ID: " + bookingId,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Booking Details\n"
                        + "─────────────────────────────\n"
                        + "Booking ID  : " + b.getBookingId() + "\n"
                        + "Passenger   : " + b.getPassengerName() + "\n"
                        + "Contact     : " + b.getPassengerContact() + "\n"
                        + "Flight      : " + b.getFlightNumber() + "\n"
                        + "Route       : " + b.getSource() + " → " + b.getDestination() + "\n"
                        + "Flight Date : " + b.getFlightDate() + "\n"
                        + "Seats       : " + b.getSeatsBooked() + "\n"
                        + "Total Paid  : ₹" + b.getTotalAmount() + "\n"
                        + "Booking Date: " + b.getBookingDate() + "\n"
                        + "Status      : " + b.getStatus(),
                        "Booking #" + bookingId,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            showError("Database error:\n" + ex.getMessage());
        }
    }

    // ================================================================
    //  Cancel booking
    // ================================================================
    private void performCancellation() {
        String idStr = cancelIdField.getText().trim();
        if (idStr.isEmpty()) { showError("Please enter the Booking ID to cancel."); cancelIdField.requestFocus(); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idStr); }
        catch (NumberFormatException e) { showError("Booking ID must be a valid number."); cancelIdField.requestFocus(); return; }
        if (bookingId <= 0) { showError("Booking ID must be a positive number."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel Booking ID: " + bookingId + "?\n"
                + "Seats will be returned to the flight.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bookingDAO.cancelBooking(bookingId);
            JOptionPane.showMessageDialog(this,
                    "✅ Booking ID " + bookingId + " has been cancelled.\n"
                    + "Seats have been restored to the flight.",
                    "Cancellation Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            cancelIdField.setText("");
            loadBookingsIntoTable();
        } catch (SQLException ex) {
            showError("Cancellation failed:\n" + ex.getMessage());
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
        return tabs;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(40, 55, 80));
        table.setSelectionBackground(new Color(139, 92, 246));
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(new Color(18, 24, 40));
        table.getTableHeader().setForeground(ACCENT);
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

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ================================================================
    //  Status column renderer
    // ================================================================
    private static class StatusRenderer extends DefaultTableCellRenderer {
        private static final Color BG_ACTIVE    = new Color(16, 185, 129, 40);
        private static final Color BG_CANCELLED = new Color(239, 68, 68, 40);
        private static final Color FG_ACTIVE    = new Color(52, 211, 153);
        private static final Color FG_CANCELLED = new Color(252, 129, 74);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            if ("ACTIVE".equals(value)) {
                setBackground(isSelected ? new Color(16, 185, 129, 80) : BG_ACTIVE);
                setForeground(FG_ACTIVE);
            } else {
                setBackground(isSelected ? new Color(239, 68, 68, 80) : BG_CANCELLED);
                setForeground(FG_CANCELLED);
            }
            setOpaque(true);
            return this;
        }
    }
}
