package com.joshiminh.wgcinema.dashboard.sections;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.joshiminh.wgcinema.dashboard.agents.showtimeAgent;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ButtonEditor;
import com.joshiminh.wgcinema.utils.ButtonRenderer;
import com.joshiminh.wgcinema.utils.TableStyles;

@SuppressWarnings("unused")
public class Showtimes {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private final JPanel showtimesPanel;

    public Showtimes(String url) {
        showtimesPanel = new JPanel(new BorderLayout());
        showtimesPanel.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Show Times", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        newButton.addActionListener(e -> new showtimeAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);

        showtimesPanel.add(titlePanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        JTable table = new JTable(tableModel);
        new TableStyles(table);
        table.setRowHeight(25);

        try (ResultSet resultSet = DAO.fetchAllShowtimes(url)) {
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
                    DAO.updateShowtimeColumn(url, table.getColumnName(column), updatedValue, idValue);
                }
            }
        });

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(url, table, "showtimes", "showtime_id"));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        showtimesPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getShowtimesPanel() {
        return showtimesPanel;
    }
}