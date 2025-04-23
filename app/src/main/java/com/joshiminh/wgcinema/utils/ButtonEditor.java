package com.joshiminh.wgcinema.utils;

import java.awt.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.table.*;

import com.joshiminh.wgcinema.data.DAO;

@SuppressWarnings("unused")
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton button;
    private final JTable table;
    private final String url;
    private final String tableName;
    private final String primaryKeyColumn;
    private int row;

    public ButtonEditor(String url, JTable table, String tableName, String primaryKeyColumn) {
        this.url = url;
        this.table = table;
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;

        button = new JButton("Delete");
        button.setOpaque(true);
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> {
            fireEditingStopped();
            int confirmation = JOptionPane.showConfirmDialog(
                null, "Are you sure you want to delete this record?", 
                "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirmation == JOptionPane.YES_OPTION) deleteRow();
        });
    }

    private void deleteRow() {
        int id = (int) table.getValueAt(row, 0);
        int result = DAO.deleteRowById(url, tableName, primaryKeyColumn, id);

        if (result > 0) {
            ((DefaultTableModel) table.getModel()).removeRow(row);
            JOptionPane.showMessageDialog(null, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error deleting record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Delete";
    }
}