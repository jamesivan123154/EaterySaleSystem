package eaterysalesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

public class OrderHistoryFrame extends JFrame {

    // Theme Colors
    private static final Color BG_MAIN       = new Color(204, 229, 255);
    private static final Color BG_CARD       = new Color(255, 255, 255);
    private static final Color COLOR_PRIMARY = new Color(128, 0, 0);
    private static final Color COLOR_ACCENT  = new Color(255, 193, 7);

    private JTable table;
    private DefaultTableModel model;

    // PostgreSQL Configuration
    private static final String DB_URL      = "jdbc:postgresql://localhost:5432/eaterydb";
    private static final String DB_USER     = "postgres";
    private static final String DB_PASSWORD = "admin123";

    private int currentFacilitatorId;
	private String currentRole;

	public OrderHistoryFrame(int facilitatorId, String role) {
		this.currentFacilitatorId = facilitatorId;
		this.currentRole = role;

		initializeUI();
		loadOrders();
	}

    private void initializeUI() {

        setTitle("Order History");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Order History");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Table — now includes Facilitator Name
        model = new DefaultTableModel(
                new String[]{
                    "Order ID",
                    "Customer Name",
                    "Facilitator Name",
                    "Total Amount",
                    "Order Date"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
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

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_MAIN);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete  = new JButton("Delete Selected Transaction");
        JButton btnClose   = new JButton("Close");

        btnRefresh.setBackground(COLOR_ACCENT);
        btnDelete.setBackground(COLOR_PRIMARY);
        btnDelete.setForeground(Color.WHITE);
        btnClose.setBackground(COLOR_ACCENT);

        btnRefresh.setFocusPainted(false);
        btnDelete.setFocusPainted(false);
        btnClose.setFocusPainted(false);

        btnRefresh.addActionListener(e -> loadOrders());
        btnDelete.addActionListener(e -> deleteSelectedOrder());
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
	
	private int getOrderOwnerId(int orderId) {
		try (Connection conn = getConnection()) {
			String sql = "SELECT facilitator_id FROM orders WHERE order_id = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, orderId);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getInt("facilitator_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

    private void loadOrders() {

        model.setRowCount(0);

        // JOIN orders + facilitator to get fullName
        String sql =
                "SELECT o.order_id, o.customer_name, f.fullName, " +
                "o.total_amount, o.order_date " +
                "FROM orders o " +
                "JOIN facilitator f ON o.facilitator_id = f.facilitator_id " +
                "ORDER BY o.order_date DESC";

        try (
            Connection conn = getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getString("customer_name"),
                    rs.getString("fullName"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("order_date")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading order history:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedOrder() {

    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this,
            "Please select a transaction first.",
            "No Selection",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int orderId = Integer.parseInt(
        model.getValueAt(selectedRow, 0).toString()
    );

    int orderOwnerId = getOrderOwnerId(orderId);

    // BLOCK if not owner AND not their own transaction
    if (!currentRole.equalsIgnoreCase("Owner")
            && currentFacilitatorId != orderOwnerId) {

        JOptionPane.showMessageDialog(
            this,
            "You can only delete your own transactions.",
            "Access Denied",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Delete transaction #" + orderId + "?",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    try (Connection conn = getConnection()) {

        // Step 1: delete items
        String deleteItems = "DELETE FROM order_item WHERE order_id = ?";
        PreparedStatement pstItems = conn.prepareStatement(deleteItems);
        pstItems.setInt(1, orderId);
        pstItems.executeUpdate();

        // Step 2: delete order
        String deleteOrder = "DELETE FROM orders WHERE order_id = ?";
        PreparedStatement pstOrder = conn.prepareStatement(deleteOrder);
        pstOrder.setInt(1, orderId);

        int rowsAffected = pstOrder.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Transaction deleted successfully.");
            loadOrders();
        }

        pstItems.close();
        pstOrder.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error deleting transaction:\n" + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
}