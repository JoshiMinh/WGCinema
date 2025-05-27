package com.joshiminh.wgcinema.dashboard.sections;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.joshiminh.wgcinema.dashboard.agents.ShowtimeAgent;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.*;

public class Showtimes {
    private static final Color BACKGROUND_COLOR = new Color(34, 40, 49);
    private static final Color TITLE_COLOR = new Color(57, 62, 70);
    private static final Color BUTTON_COLOR = new Color(0, 173, 181);
    private static final Color TABLE_BG_COLOR = new Color(44, 54, 63);
    private static final Color GRID_COLOR = new Color(60, 70, 80);
    private static final Color SELECTION_BG = new Color(0, 173, 181, 120);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);

    private final JPanel showtimesPanel;

    public Showtimes(String url) {
        showtimesPanel = new JPanel(new BorderLayout());
        showtimesPanel.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(TITLE_COLOR);

        JLabel titleLabel = new JLabel("Show Times", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setBorder(new EmptyBorder(16, 0, 16, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        newButton.setFocusPainted(false);
        newButton.setBackground(BUTTON_COLOR);
        newButton.setForeground(Color.WHITE);
        newButton.setFont(BUTTON_FONT);
        newButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR.darker(), 2, true),
                BorderFactory.createEmptyBorder(10, 22, 10, 22)
        ));
        newButton.addActionListener(_ -> new ShowtimeAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);

        showtimesPanel.add(titlePanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != getColumnCount() - 1;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(TABLE_FONT);
        table.setForeground(Color.WHITE);
        table.setBackground(TABLE_BG_COLOR);
        table.setRowHeight(32);
        table.setGridColor(GRID_COLOR);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(TITLE_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

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
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
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
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        showtimesPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getShowtimesPanel() {
        return showtimesPanel;
    }
}