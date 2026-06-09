package eaterysalesystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EditMenuFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private EaterySalesSystemLandingPage parent;

    public EditMenuFrame(EaterySalesSystemLandingPage parent) {

        this.parent = parent;

        setTitle("Edit Menu");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(
        new Color(204,229,255)
);

        model = new DefaultTableModel(
                new String[]{"Food Name", "Price"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
            return true;
            }
        };

        loadData();

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnSave = new JButton("Save Changes");
        JButton btnAddFood = new JButton("Add Food");

        btnSave.addActionListener(e -> saveChanges());
        btnAddFood.addActionListener(e -> {

    String foodName = JOptionPane.showInputDialog(
            this,
            "Enter Food Name:"
    );

    if(foodName == null || foodName.trim().isEmpty()) {
        return;
    }

    String priceText = JOptionPane.showInputDialog(
            this,
            "Enter Price:"
    );

    if(priceText == null) {
        return;
    }

    try {

        int price = Integer.parseInt(priceText);

        String[] categories = {
            "Popular",
            "Niche"
        };

        String category =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Select Category:",
                        "Food Category",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        categories,
                        categories[0]
                );

        if(category == null) {
            return;
        }

        parent.addMenuItem(
                category,
                foodName,
                price
        );

        reloadTable();

    } catch(NumberFormatException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Price must be a number."
        );
    }
});

        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(btnAddFood);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {

        for(String[] item : EaterySalesSystemLandingPage.POPULAR_ITEMS){
            model.addRow(new Object[]{
                    item[0],
                    item[1]
            });
        }

        for(String[] item : EaterySalesSystemLandingPage.NICHE_ITEMS){
            model.addRow(new Object[]{
                    item[0],
                    item[1]
            });
        }
    }
    
    private void reloadTable() {

    model.setRowCount(0);

    loadData();
}

    private void saveChanges() {

    if(table.isEditing()){
        table.getCellEditor().stopCellEditing();
    }

    try {

        for(int i = 0; i < model.getRowCount(); i++) {

            String newFoodName = model.getValueAt(i, 0).toString();

        int newPrice = Integer.parseInt(model.getValueAt(i, 1).toString());

        parent.updateMenuItem(
        i,
        newFoodName,
        newPrice
);
        }

        JOptionPane.showMessageDialog(
                this,
                "Menu updated successfully!"
        );

        dispose();

    } catch(NumberFormatException ex){

        JOptionPane.showMessageDialog(
                this,
                "Prices must be numbers only.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
}