package com.joshiminh.wgcinema.dashboard;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.joshiminh.wgcinema.utils.table_style;

public class dashboardShowtimes {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private JPanel showtimesPanel;

    public dashboardShowtimes(String url) {
        showtimesPanel = new JPanel(new BorderLayout());
        showtimesPanel.setBackground(BACKGROUND_COLOR);

        // Title panel setup with default font sizes
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Show Times", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        // Removed custom font size so default is used
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        // Using default button font/size
        newButton.addActionListener(e -> new showtimeAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);
        showtimesPanel.add(titlePanel, BorderLayout.NORTH);

        // Create table model without custom large font sizes
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JTable table = new JTable(tableModel);
        new table_style(table); // Assuming table_style now applies default styling
        // Removed explicit font size to use default
        table.setRowHeight(25);

        // Query the "showtimes" table using SELECT *
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM showtimes")) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }
            tableModel.addColumn("Actions");
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount + 1];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                rowData[columnCount] = "Delete";
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading showtimes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column != table.getColumnCount() - 1) {
                    Object updatedValue = table.getValueAt(row, column);
                    Object idValue = table.getValueAt(row, 0);
                    try (Connection connection = DriverManager.getConnection(url);
                         PreparedStatement preparedStatement = connection.prepareStatement(
                                 "UPDATE showtimes SET " + table.getColumnName(column) + " = ? WHERE showtime_id = ?")) {
                        preparedStatement.setObject(1, updatedValue);
                        preparedStatement.setObject(2, idValue);
                        preparedStatement.executeUpdate();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), url, table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        showtimesPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getShowtimesPanel() {
        return showtimesPanel;
    }
}