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
        String[] columns = {"ID", "Name", "Role", "Active"};
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
        btnDelete.addActionListener(e -> toggleActiveStatus());
        btnClose.addActionListener(e -> { dispose(); parentFrame.setVisible(true); });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        String sql = "SELECT facilitator_id, fullname, role, isactive " +
					 "FROM facilitator " +
					 "ORDER BY isactive DESC, facilitator_id ASC";
        
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

    private void toggleActiveStatus() {
		int row = table.getSelectedRow();

		// Check if a row is selected BEFORE accessing the model
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Please select a staff member first.");
			return;
		}

		// Convert to model index safely
		int modelRow = table.convertRowIndexToModel(row);

		// Extract data using the converted index
		int id = (int) tableModel.getValueAt(modelRow, 0);
		boolean currentStatus = (boolean) tableModel.getValueAt(modelRow, 3);
		boolean newStatus = !currentStatus;

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
			 PreparedStatement ps = conn.prepareStatement("UPDATE facilitator SET isactive = ? WHERE facilitator_id = ?")) {

			ps.setBoolean(1, newStatus);
			ps.setInt(2, id);
			ps.executeUpdate();

			loadStaffData();
			JOptionPane.showMessageDialog(this, "Status updated successfully!");

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
		}
	}

    private void addStaff() { 

    JTextField txtName = new JTextField();
    JTextField txtRole = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JCheckBox chkActive = new JCheckBox("Active", true);

    Object[] fields = {
        "Full Name:", txtName,
        "Role (Owner/Staff):", txtRole,
        "Password:", txtPassword,
        chkActive
    };

    int option = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Add Staff",
            JOptionPane.OK_CANCEL_OPTION
    );

		if (option != JOptionPane.OK_OPTION) return;

		String name = txtName.getText().trim();
		String role = txtRole.getText().trim();
		String password = new String(txtPassword.getPassword()).trim();
		boolean isActive = chkActive.isSelected();

		if (name.isEmpty() || role.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
			return;
		}

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/eaterydb",
				"postgres",
				"admin123")) {

			String sql = "INSERT INTO facilitator (fullName, role, password, isactive) " +
						 "VALUES (?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, name);
			ps.setString(2, role);
			ps.setString(3, password);
			ps.setBoolean(4, isActive);

			ps.executeUpdate();

			JOptionPane.showMessageDialog(this,
					"Staff added successfully!");

			loadStaffData();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this,
					"Error: " + e.getMessage());
		}
	}
	private void editStaff() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Select a staff member first.");
			return;
		}

		int modelRow = table.convertRowIndexToModel(row); 

		int id = (int) tableModel.getValueAt(modelRow, 0);
		String currentName = tableModel.getValueAt(modelRow, 1).toString();
		String currentRole = tableModel.getValueAt(modelRow, 2).toString();

		JTextField txtName = new JTextField(currentName);
		String[] roles = {"Owner", "Staff"};
		JComboBox<String> cbRole = new JComboBox<>(roles);
		cbRole.setSelectedItem(currentRole);
		
		Object[] fields = {
			"Full Name:", txtName,
			"Role:", cbRole
		};

		int option = JOptionPane.showConfirmDialog(
				this,
				fields,
				"Edit Staff",
				JOptionPane.OK_CANCEL_OPTION
		);

		if (option != JOptionPane.OK_OPTION) return;

		String newName = txtName.getText().trim();
		String newRole = (String) cbRole.getSelectedItem(); // Get from dropdown

		if (newName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Name cannot be empty.");
			return;
		}

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/eaterydb", "postgres", "admin123");
			 PreparedStatement ps = conn.prepareStatement(
					 "UPDATE facilitator SET fullname = ?, role = ? WHERE facilitator_id = ?")) {

			ps.setString(1, newName);
			ps.setString(2, newRole);
			ps.setInt(3, id);

			ps.executeUpdate();

			JOptionPane.showMessageDialog(this, "Staff updated successfully!");
			loadStaffData();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
	}
}