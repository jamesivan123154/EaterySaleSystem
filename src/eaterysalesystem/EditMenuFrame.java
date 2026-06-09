package eaterysalesystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                return column != 0;
            }
        };

        loadData();

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnSave = new JButton("Save Changes");
        JButton btnAddFood = new JButton("Add Food");

        btnSave.addActionListener(e -> saveChanges());

        btnAddFood.addActionListener(ev -> {

            String foodName = JOptionPane.showInputDialog(this, "Enter Food Name:");
            if (foodName == null || foodName.trim().isEmpty()) return;

            String priceText = JOptionPane.showInputDialog(this, "Enter Price:");
            if (priceText == null) return;

            try {
                double price = Double.parseDouble(priceText);

                String[] categories = {"Popular", "Niche"};
                String category = (String) JOptionPane.showInputDialog(
                        this,
                        "Select Category:",
                        "Food Category",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        categories,
                        categories[0]
                );
                if (category == null) return;

                // INSERT directly into DB
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sql = "INSERT INTO menu_item (item_name, price, category) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, foodName.trim());
                pst.setDouble(2, price);
                pst.setString(3, category);
                pst.executeUpdate();
                pst.close();
                conn.close();

                reloadTable();

                JOptionPane.showMessageDialog(this, "Food item added successfully!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAddFood);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {

        model.setRowCount(0);

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT item_id, item_name, price, category " +
                         "FROM menu_item ORDER BY item_id";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getDouble("price"),
                    rs.getString("category")
                });
            }

            rs.close();
            pst.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void reloadTable() {
        model.setRowCount(0);
        loadData();
    }

    private void saveChanges() {

        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "UPDATE menu_item SET item_name=?, price=?, category=? WHERE item_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);

            for (int i = 0; i < model.getRowCount(); i++) {
                int itemId = Integer.parseInt(model.getValueAt(i, 0).toString());
                String newFoodName = model.getValueAt(i, 1).toString();
                double newPrice = Double.parseDouble(model.getValueAt(i, 2).toString());
                String category = model.getValueAt(i, 3).toString();

                pst.setString(1, newFoodName);
                pst.setDouble(2, newPrice);
                pst.setString(3, category);
                pst.setInt(4, itemId);
                pst.addBatch();
            }

            pst.executeBatch();
            pst.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Menu updated successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Prices must be numbers only.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        parent.buildPriceLookup();
        parent.refreshItemsGrid();
        super.dispose();
    }
}