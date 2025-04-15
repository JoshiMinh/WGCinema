package com.joshiminh.wgcinema.dashboard;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.joshiminh.wgcinema.utils.table_style;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class dashboardShowrooms {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private JPanel showroomsPanel;

    public dashboardShowrooms(String url) {
        showroomsPanel = new JPanel(new BorderLayout());
        showroomsPanel.setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Show Rooms", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        newButton.setFont(new Font("Arial", Font.BOLD, 12));
        newButton.addActionListener(e -> new showroomAgent(url).setVisible(true));
        titlePanel.add(newButton, BorderLayout.EAST);
        showroomsPanel.add(titlePanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JTable table = new JTable(tableModel);
        new table_style(table);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM showrooms")) {
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
            JOptionPane.showMessageDialog(null, "Error loading showrooms: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column != table.getColumnCount() - 1) {
                    Object updatedValue = table.getValueAt(row, column);
                    Object idValue = table.getValueAt(row, 0);
                    try (Connection connection = DriverManager.getConnection(url);
                         PreparedStatement preparedStatement = connection.prepareStatement("UPDATE showrooms SET " + table.getColumnName(column) + " = ? WHERE showroom_id = ?")) {
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
        showroomsPanel.add(scrollPane, BorderLayout.CENTER);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT showroom_id, max_chairs FROM showrooms")) {
            while (rs.next()) {
                int showroomId = rs.getInt("showroom_id");
                int maxChairs = rs.getInt("max_chairs");
                dataset.addValue(maxChairs, "Seats", String.valueOf(showroomId));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart barChart = ChartFactory.createBarChart("Seats per Showroom", "Showroom Number", "Number of Seats", dataset, PlotOrientation.VERTICAL, false, true, false);
        Color darkBackground = new Color(30, 30, 30);
        barChart.getTitle().setPaint(Color.WHITE);
        barChart.setBackgroundPaint(darkBackground);
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.setBackgroundPaint(darkBackground);
        plot.setOutlinePaint(new Color(100, 100, 100));
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(darkBackground);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        showroomsPanel.add(chartPanel, BorderLayout.SOUTH);
    }

    public JPanel getShowroomsPanel() {
        return showroomsPanel;
    }
}