package com.joshiminh.wgcinema.dashboard.sections;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.joshiminh.wgcinema.dashboard.agents.ShowroomAgent;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ButtonEditor;
import com.joshiminh.wgcinema.utils.ButtonRenderer;
import com.joshiminh.wgcinema.utils.TableStyles;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("unused")
public class Showrooms {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private final JPanel showroomsPanel;

    public Showrooms(String url) {
        showroomsPanel = new JPanel(new BorderLayout());
        showroomsPanel.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = createTitlePanel(url);
        showroomsPanel.add(titlePanel, BorderLayout.NORTH);

        JTable table = createTable(url);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        showroomsPanel.add(scrollPane, BorderLayout.CENTER);

        ChartPanel chartPanel = createChartPanel(url);
        showroomsPanel.add(chartPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel(String url) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Show Rooms", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        newButton.setFont(new Font("Arial", Font.BOLD, 12));
        newButton.addActionListener(e -> new ShowroomAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);

        return titlePanel;
    }

    private JTable createTable(String url) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        JTable table = new JTable(tableModel);
        new TableStyles(table);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

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
                dataset.addValue(resultSet.getInt("max_chairs"), "Seats", String.valueOf(resultSet.getInt("showroom_id")));
            }
        } catch (SQLException e) {
            showError("Error loading chart data: " + e.getMessage());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Seats per Showroom", "Showroom Number", "Number of Seats",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        styleChart(barChart);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);

        return chartPanel;
    }

    private void styleChart(JFreeChart chart) {
        chart.getTitle().setPaint(Color.WHITE);
        chart.setBackgroundPaint(BACKGROUND_COLOR);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setOutlinePaint(new Color(100, 100, 100));
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getShowroomsPanel() {
        return showroomsPanel;
    }
}