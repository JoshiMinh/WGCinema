package com.joshiminh.wgcinema.dashboard.sections;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.joshiminh.wgcinema.dashboard.agents.ShowroomAgent;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Showrooms {
    private static final Color BACKGROUND_COLOR = new Color(24, 26, 32);
    private static final Color TITLE_COLOR = new Color(90, 130, 200); // Light dark blue
    private static final Color BUTTON_COLOR = new Color(60, 63, 65);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private final JPanel showroomsPanel;

    public Showrooms(String url) {
        showroomsPanel = new JPanel(new BorderLayout(0, 12));
        showroomsPanel.setBackground(BACKGROUND_COLOR);
        showroomsPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        showroomsPanel.add(createTitlePanel(url), BorderLayout.NORTH);
        showroomsPanel.add(createTablePanel(url), BorderLayout.CENTER);
        showroomsPanel.add(createChartPanel(url), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel(String url) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Show Rooms", SwingConstants.LEFT);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton newButton = new JButton("New");
        newButton.setFont(BUTTON_FONT);
        newButton.setBackground(BUTTON_COLOR);
        newButton.setForeground(TITLE_COLOR);
        newButton.setFocusPainted(false);
        newButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        newButton.addActionListener(e -> new ShowroomAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel createTablePanel(String url) {
        JTable table = createTable(url);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable(String url) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != getColumnCount() - 1;
            }
        };

        JTable table = new JTable(tableModel);
        new TableStyles(table);
        table.setFont(TABLE_FONT);
        table.setRowHeight(32);
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(36, 38, 45));
        table.setSelectionBackground(new Color(50, 54, 60));
        table.setSelectionForeground(TITLE_COLOR);
        table.getTableHeader().setFont(TABLE_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(BACKGROUND_COLOR);
        table.getTableHeader().setForeground(TITLE_COLOR);

        // Set left padding for all cells
        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer();
        paddedRenderer.setBorder(new EmptyBorder(0, 12, 0, 0)); // 12px left padding
        paddedRenderer.setForeground(Color.WHITE);
        paddedRenderer.setBackground(new Color(36, 38, 45));
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(paddedRenderer);
        }

        try (ResultSet resultSet = DAO.fetchAllShowrooms(url)) {
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
            showError("Error loading showrooms: " + e.getMessage());
        }

        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column != table.getColumnCount() - 1) {
                    updateTableCell(url, table, row, column);
                }
            }
        });

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(url, table, "showrooms", "showroom_id"));

        // Set left padding for all columns except "Actions"
        DefaultTableCellRenderer leftPaddingRenderer = new DefaultTableCellRenderer();
        leftPaddingRenderer.setBorder(new EmptyBorder(0, 12, 0, 0));
        leftPaddingRenderer.setForeground(Color.WHITE);
        leftPaddingRenderer.setBackground(new Color(36, 38, 45));
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (!table.getColumnName(i).equals("Actions")) {
                table.getColumnModel().getColumn(i).setCellRenderer(leftPaddingRenderer);
            }
        }

        return table;
    }

    private void updateTableCell(String url, JTable table, int row, int column) {
        Object updatedValue = table.getValueAt(row, column);
        Object idValue = table.getValueAt(row, 0);
        DAO.updateShowroomColumn(url, table.getColumnName(column), updatedValue, idValue);
    }

    private ChartPanel createChartPanel(String url) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (ResultSet resultSet = DAO.fetchAllShowrooms(url)) {
            while (resultSet.next()) {
                dataset.addValue(resultSet.getInt("max_chairs"), "Seats", "Room " + resultSet.getInt("showroom_id"));
            }
        } catch (SQLException e) {
            showError("Error loading chart data: " + e.getMessage());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Seats per Showroom", "Showroom", "Number of Seats",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        styleChart(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(420, 260));
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);

        return chartPanel;
    }

    private void styleChart(JFreeChart chart) {
        chart.getTitle().setPaint(TITLE_COLOR);
        chart.setBackgroundPaint(BACKGROUND_COLOR);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(36, 38, 45));
        plot.setOutlinePaint(new Color(80, 80, 80));
        plot.setRangeGridlinePaint(new Color(120, 120, 120));
        plot.getDomainAxis().setLabelPaint(TITLE_COLOR);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(TITLE_COLOR);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getShowroomsPanel() {
        return showroomsPanel;
    }
}