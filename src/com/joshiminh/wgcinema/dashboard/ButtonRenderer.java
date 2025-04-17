package com.joshiminh.wgcinema.dashboard;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "Delete" : value.toString());
        setBackground(Color.RED);
        setForeground(Color.WHITE);
        return this;
    }
}

class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton button;
    private int row;
    private final JTable table;
    private final String url;
    private final String tableName;
    private final String primaryKeyColumn;

    public ButtonEditor(String url, JTable table, String tableName, String primaryKeyColumn) {
        this.table = table;
        this.url = url;
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;
        
        button = new JButton("Delete");
        button.setOpaque(true);
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        
        button.addActionListener(e -> {
            fireEditingStopped();
            int confirmation = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete this record?", "Delete Confirmation", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                deleteRow();
            }
        });
    }

    private void deleteRow() {
        int id = (int) table.getValueAt(row, 0);
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                 "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?")) {
            
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();
            
            if (result > 0) {
                ((DefaultTableModel) table.getModel()).removeRow(row);
                JOptionPane.showMessageDialog(null, "Deleted successfully!", "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting record: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
            boolean isSelected, int row, int column) {
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Delete";  // Return the button text
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        super.cancelCellEditing();
    }
}