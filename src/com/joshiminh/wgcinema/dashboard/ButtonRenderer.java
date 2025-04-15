package com.joshiminh.wgcinema.dashboard;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
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

class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private boolean isPushed;
    private int row;
    private final JTable table;
    private final String url;

    public ButtonEditor(JCheckBox checkBox, String url, JTable table) {
        super(checkBox);
        this.table = table;
        this.url = url;
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> fireEditingStopped());
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
            boolean isSelected, int row, int column) {
        this.row = row;
        button.setText((value == null) ? "Delete" : value.toString());
        isPushed = true;
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int id = (int) table.getValueAt(row, 0);
            int confirmation = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete this record?", "Delete Confirmation", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirmation == JOptionPane.YES_OPTION) {
                try (Connection connection = DriverManager.getConnection(url);
                     PreparedStatement preparedStatement = connection.prepareStatement(
                         "DELETE FROM your_table WHERE id = ?")) {
                    preparedStatement.setInt(1, id);
                    int result = preparedStatement.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Deleted successfully!", "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error deleting record: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        isPushed = false;
        return "Delete";
    }
    
    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
