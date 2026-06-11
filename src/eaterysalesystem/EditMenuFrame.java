package eaterysalesystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class EditMenuFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private EaterySalesSystemLandingPage parent;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/eaterydb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin123";

    public EditMenuFrame(EaterySalesSystemLandingPage parent) {
        this.parent = parent;

        setTitle("Edit Menu");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(204, 229, 255));

        model = new DefaultTableModel(
                new String[]{"Item ID", "Food Name", "Price", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };

        loadData();

        table = new JTable(model);
        
        String[] categories = {"Popular", "Niche"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(categoryComboBox));

        JScrollPane scrollPane = new JScrollPane(table);
        JButton btnSave = new JButton("Save Changes");
        JButton btnAddFood = new JButton("Add Food");

        btnSave.addActionListener(e -> saveChanges());
        btnAddFood.addActionListener(ev -> addFoodItem());

        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAddFood);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFoodItem() {
        String foodName = JOptionPane.showInputDialog(this, "Enter Food Name:");
        if (foodName == null || foodName.trim().isEmpty()) return;

        String priceText = JOptionPane.showInputDialog(this, "Enter Price:");
        if (priceText == null) return;

        try {
            double price = Double.parseDouble(priceText);
            String[] categories = {"Popular", "Niche"};
            String category = (String) JOptionPane.showInputDialog(this, "Select Category:", "Food Category",
                    JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]);
            
            if (category == null) return;

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pst = conn.prepareStatement("INSERT INTO menu_item (item_name, price, category) VALUES (?, ?, ?)")) {
                pst.setString(1, foodName.trim());
                pst.setDouble(2, price);
                pst.setString(3, category);
                pst.executeUpdate();
            }
            reloadTable();
            JOptionPane.showMessageDialog(this, "Food item added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement("SELECT item_id, item_name, price, category FROM menu_item ORDER BY item_id");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("price"), rs.getString("category")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void reloadTable() {
        loadData();
    }

    private void saveChanges() {
        // Ensure editing stops before saving
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement("UPDATE menu_item SET item_name=?, price=?, category=? WHERE item_id=?")) {

            for (int i = 0; i < model.getRowCount(); i++) {
                pst.setString(1, model.getValueAt(i, 1).toString());
                pst.setDouble(2, Double.parseDouble(model.getValueAt(i, 2).toString()));
                pst.setString(3, model.getValueAt(i, 3).toString());
                pst.setInt(4, Integer.parseInt(model.getValueAt(i, 0).toString()));
                pst.addBatch();
            }
            pst.executeBatch();
            JOptionPane.showMessageDialog(this, "Menu updated successfully!");
            dispose();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }

    @Override
    public void dispose() {
        if (parent != null) {
            parent.buildPriceLookup();
            parent.refreshItemsGrid();
        }
        super.dispose();
    }
}