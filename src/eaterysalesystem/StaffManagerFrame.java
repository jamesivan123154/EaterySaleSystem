package eaterysalesystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class StaffManagerFrame extends JFrame {

    // --- Same Theme Colors as OrderHistoryFrame ---
    private static final Color BG_MAIN       = new Color(204, 229, 255);
    private static final Color BG_CARD       = new Color(255, 255, 255);
    private static final Color COLOR_PRIMARY = new Color(128, 0, 0);
    private static final Color COLOR_ACCENT  = new Color(255, 193, 7);

    private JTable table;
    private DefaultTableModel tableModel;
    private JFrame parentFrame;

    public StaffManagerFrame(String role, JFrame parent) {
        this.parentFrame = parent;
        setTitle("Staff Manager");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Handle window closing to return to parent
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (parentFrame != null) parentFrame.setVisible(true);
            }
        });

        initUI();
        loadStaffData();
    }

    private void initUI() {
        getContentPane().setBackground(BG_MAIN);

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("Staff Manager");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columns = {"ID", "Name", "Role", "Is Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_CARD);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_MAIN);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_MAIN);
        
        JButton btnAdd = new JButton("Add Staff");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Toggle Active Status");
        JButton btnClose = new JButton("Close");

        // Styling
        JButton[] buttons = {btnAdd, btnEdit, btnDelete, btnClose};
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setBackground(COLOR_ACCENT);
        }
        btnDelete.setBackground(COLOR_PRIMARY);
        btnDelete.setForeground(Color.WHITE);

        btnAdd.addActionListener(e -> addStaff());
        btnEdit.addActionListener(e -> editStaff());
        btnDelete.addActionListener(e -> toggleDelete());
        btnClose.addActionListener(e -> { dispose(); parentFrame.setVisible(true); });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        String sql = "SELECT facilitator_id, fullname, role, isactive FROM facilitator";
        
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("facilitator_id"), 
                    rs.getString("fullname"), 
                    rs.getString("role"), 
                    rs.getBoolean("isactive")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage());
        }
    }

    private void toggleDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member first.");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        boolean newStatus = !(boolean) tableModel.getValueAt(row, 3);
        
        String sql = "UPDATE facilitator SET isactive = ? WHERE facilitator_id = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, newStatus);
            ps.setInt(2, id);
            ps.executeUpdate();
            loadStaffData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void addStaff() { /* Implementation needed */ }
    private void editStaff() { /* Implementation needed */ }
}