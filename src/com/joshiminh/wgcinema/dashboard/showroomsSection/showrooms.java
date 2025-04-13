package com.joshiminh.wgcinema.dashboard.showroomsSection;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

import com.joshiminh.wgcinema.dashboard.Dashboard;
import com.joshiminh.wgcinema.utils.*;

// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class showrooms {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private JPanel showroomsPanel;

    public showrooms(String url) {
        showroomsPanel = new JPanel(new BorderLayout());
        showroomsPanel.setBackground(BACKGROUND_COLOR);

        // Create a title panel with a title label and a "New" button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Show Rooms", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        // Add top and bottom margin for the title
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton newButton = new JButton("New");
        newButton.setFont(new Font("Arial", Font.BOLD, 12));
        newButton.addActionListener(e -> {
            // Call new showroomAgent()
            new showroomAgent(url).setVisible(true);
        });
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

        // Increase table text size and row height
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
                         PreparedStatement preparedStatement = connection.prepareStatement(
                                 "UPDATE showrooms SET " + table.getColumnName(column) + " = ? WHERE showroom_id = ?")) {
                        preparedStatement.setObject(1, updatedValue);
                        preparedStatement.setObject(2, idValue);
                        preparedStatement.executeUpdate();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        table.getColumn("Actions").setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
    JButton deleteButton = new JButton("Delete");
    deleteButton.setBackground(Color.RED);
    deleteButton.setForeground(Color.WHITE);
    deleteButton.setFont(new Font("Arial", Font.BOLD, 12));

    deleteButton.addActionListener(e -> {
        int showroomId = (int) table.getValueAt(row, 0);
        int confirmation = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to delete this showroom?", 
            "Delete Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM showrooms WHERE showroom_id = ?")) {
                preparedStatement.setInt(1, showroomId);
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Showroom deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Remove row from table model
                    ((DefaultTableModel) table.getModel()).removeRow(row);

                    // Dispose of current Dashboard frame and reopen a new one
                    Window win = SwingUtilities.getWindowAncestor(table);
                    if (win instanceof JFrame) {
                        ((JFrame) win).dispose(); // Dispose the current Dashboard
                        new Dashboard(url).setVisible(true); // Reopen the Dashboard
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting showroom: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    return deleteButton;
});


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        showroomsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a bar chart for showrooms (Seats per Showroom)
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

JFreeChart barChart = ChartFactory.createBarChart(
        "Seats per Showroom",      // Chart title
        "Showroom Number",         // X-Axis Label
        "Number of Seats",         // Y-Axis Label
        dataset,
        PlotOrientation.VERTICAL,
        false,                     // Include legend
        true,
        false
);

// Define the dark background color
Color darkBackground = new Color(30, 30, 30);

// Set title color and overall background
barChart.getTitle().setPaint(Color.WHITE);
barChart.setBackgroundPaint(darkBackground);

// Style the plot area
CategoryPlot plot = (CategoryPlot) barChart.getPlot();
plot.setBackgroundPaint(darkBackground);
plot.setOutlinePaint(new Color(100, 100, 100));
plot.setRangeGridlinePaint(Color.GRAY);

// Style the domain (X) axis
plot.getDomainAxis().setLabelPaint(Color.WHITE);
plot.getDomainAxis().setTickLabelPaint(Color.WHITE);

// Style the range (Y) axis
plot.getRangeAxis().setLabelPaint(Color.WHITE);
plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

// Create and style the chart panel
ChartPanel chartPanel = new ChartPanel(barChart);
chartPanel.setPreferredSize(new Dimension(400, 300));
chartPanel.setBackground(darkBackground);
chartPanel.setMouseWheelEnabled(true);
chartPanel.setDomainZoomable(true);
chartPanel.setRangeZoomable(true);

// Finally, add the chart panel to your showrooms panel
showroomsPanel.add(chartPanel, BorderLayout.SOUTH);

    }

    public JPanel getShowroomsPanel() {
        return showroomsPanel;
    }
}
