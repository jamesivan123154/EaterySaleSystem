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
                return column == 1;
            }
        };

        loadData();

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnSave = new JButton("Save Changes");

        btnSave.addActionListener(e -> saveChanges());

        add(scrollPane, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
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

    private void saveChanges() {

    if(table.isEditing()){
        table.getCellEditor().stopCellEditing();
    }

    try {

        for(int i = 0; i < model.getRowCount(); i++) {

            String foodName =
                    model.getValueAt(i, 0).toString();

            int newPrice =
                    Integer.parseInt(
                            model.getValueAt(i, 1).toString()
                    );

            System.out.println(foodName + " -> " + newPrice);

            parent.updatePrice(foodName, newPrice);
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