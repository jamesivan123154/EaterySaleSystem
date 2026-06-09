package eaterysalesystem;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class SalesReportFrame extends JFrame {

    // ── Colors & Fonts ──
    private static final Color BG_MAIN = new Color(204, 229, 255);
    private static final Color COLOR_PRIMARY = new Color(128, 0, 0);
    private static final Color COLOR_ACCENT = new Color(255, 193, 7);
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 20);
    private static final Font FONT_BTN = new Font("Arial", Font.BOLD, 13);

    private String role;
    private JFrame parent;
    private JPanel pnlDisplay = new JPanel(new BorderLayout());

    public SalesReportFrame(String role, JFrame parent) {
        this.role = role;
        this.parent = parent;

        setTitle("Sales Report - Jomar's Eatery");
        setSize(860, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        pnlDisplay.setBackground(BG_MAIN);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(pnlDisplay, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(COLOR_PRIMARY);
        hdr.setPreferredSize(new Dimension(860, 60));
        hdr.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("JOMAR'S EATERY");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        hdr.add(logo, BorderLayout.WEST);

        JButton btnExit = new JButton("Exit");
        btnExit.setBackground(COLOR_ACCENT);
        btnExit.setPreferredSize(new Dimension(100, 35));
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
        rightPanel.setOpaque(false);
        rightPanel.add(btnExit);
        hdr.add(rightPanel, BorderLayout.EAST);
        
        return hdr;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_MAIN);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnFood = createSideButton("Sales per Food");
        JButton btnEmp = createSideButton("Sales Per Employee");
        JButton btnTotal = createSideButton("Total Sales");

        btnFood.addActionListener(e -> updateReportView("FOOD"));
        btnEmp.addActionListener(e -> updateReportView("EMPLOYEE"));
        btnTotal.addActionListener(e -> loadAllTotalSales());

        sidebar.add(btnFood);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnEmp);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnTotal);

        return sidebar;
    }

    private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setMaximumSize(new Dimension(180, 45));
        return btn;
    }

    private void updateReportView(String type) {
        switch (type) {
            case "FOOD": loadFoodSales(); break;
            case "EMPLOYEE": loadEmployeeSales(); break;
        }
    }

    private void loadFoodSales() {
        String sql = "SELECT m.item_name, SUM(oi.quantity) as total_qty, SUM(oi.subtotal) as revenue " +
                     "FROM order_item oi JOIN menu_item m ON oi.item_id = m.item_id " +
                     "GROUP BY m.item_name ORDER BY revenue DESC";
        updateTable(sql, new String[]{"Item Name", "Quantity Sold", "Total Revenue"});
    }

    private void loadEmployeeSales() {
        String sql = "SELECT f.fullname, COUNT(o.order_id) as orders_handled, SUM(o.total_amount) as total_sales " +
                     "FROM orders o JOIN facilitator f ON o.facilitator_id = f.facilitator_id " +
                     "GROUP BY f.fullname";
        updateTable(sql, new String[]{"Staff Name", "Orders Processed", "Total Sales"});
    }

    private void loadAllTotalSales() {
        pnlDisplay.removeAll();
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_MAIN);

        container.add(createReportSection("Daily Sales Report", "day"));
        container.add(Box.createVerticalStrut(20));
        container.add(createReportSection("Weekly Sales Report", "week"));
        container.add(Box.createVerticalStrut(20));
        container.add(createReportSection("Monthly Sales Report", "month"));

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        pnlDisplay.add(scroll, BorderLayout.CENTER);
        pnlDisplay.revalidate();
        pnlDisplay.repaint();
    }

    private JPanel createReportSection(String title, String interval) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        section.add(lblTitle, BorderLayout.NORTH);

        // Limit set to 12 for professional long-term performance
        String sql = "SELECT DATE_TRUNC('" + interval + "', order_date) as period, SUM(total_amount) as revenue " +
                     "FROM orders GROUP BY period ORDER BY period DESC LIMIT 12";
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Period", "Revenue"}, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
             Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()) model.addRow(new Object[]{rs.getString("period"), "P " + String.format("%.2f", rs.getDouble("revenue"))});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage()); }

        JTable table = new JTable(model);
        
        // Auto-adjust height based on row count
        int tableHeight = (table.getRowCount() * table.getRowHeight()) + table.getTableHeader().getPreferredSize().height;
        table.setPreferredScrollableViewportSize(new Dimension(750, Math.min(tableHeight, 300)));

        section.add(new JScrollPane(table), BorderLayout.CENTER);
        return section;
    }

    private void updateTable(String query, String[] columnNames) {
        pnlDisplay.removeAll();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
             Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while(rs.next()) {
                Object[] row = new Object[columnNames.length];
                for(int i = 0; i < columnNames.length; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage()); }
        
        JTable table = new JTable(model);
        pnlDisplay.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlDisplay.revalidate();
        pnlDisplay.repaint();
    }
}