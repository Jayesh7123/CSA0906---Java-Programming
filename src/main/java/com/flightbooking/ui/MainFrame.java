package com.flightbooking.ui;

import com.flightbooking.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main application window – clean redesign.
 * Sidebar navigation switches between FlightPanel and BookingPanel.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     contentArea = new JPanel(cardLayout);

    private FlightPanel  flightPanel;
    private BookingPanel bookingPanel;

    private JButton btnFlights;
    private JButton btnBookings;

    // ── Colour palette ──────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(18, 24, 40);
    private static final Color BG_SIDEBAR = new Color(13, 18, 30);
    private static final Color BG_CARD    = new Color(26, 35, 55);
    private static final Color BORDER_COL = new Color(40, 55, 80);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color ACCENT_BTN = new Color(59, 130, 246);
    private static final Color TEXT_MAIN  = new Color(220, 230, 245);
    private static final Color TEXT_DIM   = new Color(100, 120, 150);
    private static final Color SUCCESS    = new Color(52, 211, 153);
    private static final Color DANGER     = new Color(239, 68, 68);

    // ================================================================
    public MainFrame() {
        super("✈  Flight Booking System");
        initUI();
    }

    // ================================================================
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        add(buildSidebar(),   BorderLayout.WEST);
        add(buildContent(),   BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        showPanel("FLIGHTS");
    }

    // ================================================================
    //  Sidebar
    // ================================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL));

        // ── Logo area ────────────────────────────────────────────────
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(BG_SIDEBAR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(32, 20, 24, 20));

        JLabel icon = new JLabel("✈");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icon.setForeground(ACCENT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("Flight Booking");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(TEXT_MAIN);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagLine = new JLabel("Powered by MySQL JDBC");
        tagLine.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagLine.setForeground(TEXT_DIM);
        tagLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(icon);
        logoPanel.add(Box.createVerticalStrut(8));
        logoPanel.add(appName);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(tagLine);
        sidebar.add(logoPanel);

        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        // ── Nav label ────────────────────────────────────────────────
        sidebar.add(makeNavLabel("NAVIGATION"));

        // ── Nav buttons ──────────────────────────────────────────────
        btnFlights  = makeSidebarButton("✈   Flights & Booking", "FLIGHTS");
        btnBookings = makeSidebarButton("📋   My Bookings",       "BOOKINGS");
        sidebar.add(btnFlights);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnBookings);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        // ── Exit ─────────────────────────────────────────────────────
        JButton exitBtn = makeSidebarButton("🚪   Exit Application", null);
        exitBtn.setForeground(new Color(252, 129, 74));
        exitBtn.addActionListener(e -> confirmExit());
        sidebar.add(exitBtn);

        sidebar.add(Box.createVerticalGlue());

        // ── Version ──────────────────────────────────────────────────
        JLabel version = new JLabel("v1.0.0  ·  JDBC + MySQL");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(TEXT_DIM);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        version.setBorder(BorderFactory.createEmptyBorder(10, 0, 16, 0));
        sidebar.add(version);

        return sidebar;
    }

    // ================================================================
    //  Content area (CardLayout)
    // ================================================================
    private JPanel buildContent() {
        contentArea.setBackground(BG_DARK);
        flightPanel  = new FlightPanel();
        bookingPanel = new BookingPanel();
        contentArea.add(flightPanel,  "FLIGHTS");
        contentArea.add(bookingPanel, "BOOKINGS");
        return contentArea;
    }

    // ================================================================
    //  Status bar
    // ================================================================
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_SIDEBAR);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)));

        boolean connected = DatabaseConnection.testConnection();
        String connText   = connected
                ? "●  Connected to MySQL"
                : "●  Not connected — check credentials in DatabaseConnection.java";
        Color connColor   = connected ? SUCCESS : DANGER;

        JLabel connLabel = new JLabel(connText);
        connLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        connLabel.setForeground(connColor);
        bar.add(connLabel, BorderLayout.WEST);

        JLabel info = new JLabel("Java Swing  ·  MySQL JDBC  ·  College Assessment Project");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(TEXT_DIM);
        bar.add(info, BorderLayout.EAST);

        return bar;
    }

    // ================================================================
    //  Panel switching
    // ================================================================
    private void showPanel(String panelName) {
        cardLayout.show(contentArea, panelName);

        btnFlights.setBackground("FLIGHTS".equals(panelName)   ? ACCENT_BTN : BG_SIDEBAR);
        btnFlights.setForeground("FLIGHTS".equals(panelName)   ? Color.WHITE : TEXT_DIM);
        btnBookings.setBackground("BOOKINGS".equals(panelName) ? ACCENT_BTN : BG_SIDEBAR);
        btnBookings.setForeground("BOOKINGS".equals(panelName) ? Color.WHITE : TEXT_DIM);

        if ("BOOKINGS".equals(panelName)) {
            bookingPanel.loadBookingsIntoTable();
        }
    }

    // ================================================================
    //  Exit
    // ================================================================
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) System.exit(0);
    }

    // ================================================================
    //  Sidebar helpers
    // ================================================================
    private JButton makeSidebarButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(BG_SIDEBAR);
        btn.setForeground(TEXT_DIM);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        if (panelName != null) {
            btn.addActionListener((ActionEvent e) -> showPanel(panelName));
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(ACCENT_BTN)) {
                    btn.setBackground(new Color(30, 42, 64));
                    btn.setForeground(TEXT_MAIN);
                }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(ACCENT_BTN)) {
                    btn.setBackground(BG_SIDEBAR);
                    btn.setForeground(TEXT_DIM);
                }
            }
        });

        return btn;
    }

    private JLabel makeNavLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 22, 6, 22));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COL);
        sep.setBackground(BORDER_COL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
