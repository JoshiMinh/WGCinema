package com.joshiminh.wgcinema.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.joshiminh.wgcinema.App;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class showtimeAgent extends JFrame { 
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final int FORM_PADDING = 50;
    private static final int BUTTON_PADDING = 8;
    
    private String[] showtimeColumns;
    private final String databaseUrl;
    private final List<JComponent> inputComponents;

    public showtimeAgent(String url) {
        databaseUrl = url;
        inputComponents = new ArrayList<>();
        initializeUI();
        setupFrame();
    }

    private void initializeUI() {
        setIconImage(new ImageIcon("images/icon.png").getImage());
        setTitle("Add New Showtime");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 20));
    }

    private void setupFrame() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Add New Showtime", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createFormPanel() {
        // Get column names for the "showtimes" table and filter out the primary key "showtime_id"
        showtimeColumns = getColumnNames(databaseUrl, "showtimes");
        List<String> filteredColumns = new ArrayList<>();
        for (String column : showtimeColumns) {
            if (!column.equalsIgnoreCase("showtime_id")) {
                filteredColumns.add(column);
            }
        }
        showtimeColumns = filteredColumns.toArray(new String[0]);
        String[] columnValues = new String[showtimeColumns.length];
        for (int i = 0; i < showtimeColumns.length; i++) {
            columnValues[i] = "";
        }
        
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(BACKGROUND_COLOR);
        formContainer.setBorder(new EmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < showtimeColumns.length; i++) {
            JLabel label = createFormLabel(showtimeColumns[i]);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.25;
            gbc.fill = GridBagConstraints.NONE;
            formPanel.add(label, gbc);
            JComponent inputComponent = createInputComponent(showtimeColumns[i], columnValues[i]);
            inputComponents.add(inputComponent);
            gbc.gridx = 1;
            gbc.weightx = 0.75;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(inputComponent, gbc);
        }

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.BOTH;
        formGbc.weightx = 1;
        formGbc.weighty = 1;
        formContainer.add(formPanel, formGbc);
        return formContainer;
    }

    private JLabel createFormLabel(String columnName) {
        JLabel label = new JLabel(columnName.replace("_", " ") + ":");
        label.setForeground(Color.WHITE);
        label.setFont(LABEL_FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        footerPanel.setBackground(BACKGROUND_COLOR);
        JButton saveButton = createSaveButton();
        footerPanel.add(saveButton);
        return footerPanel;
    }

    private JButton createSaveButton() {
        JButton saveButton = new JButton("Add Showtime");
        styleButton(saveButton);
        saveButton.addActionListener(e -> performSaveAction());
        return saveButton;
    }

    private void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, 25, BUTTON_PADDING, 25));
    }

    private static String[] getColumnNames(String url, String tableName) {
        try (Connection connection = DriverManager.getConnection(url)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            List<String> columnNames = new ArrayList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            return columnNames.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private JComponent createInputComponent(String fieldName, String defaultValue) {
        return createTextField(fieldName, defaultValue);
    }

    private JTextField createTextField(String fieldName, String defaultValue) {
        JTextField textField = new JTextField(defaultValue);
        styleComponent(textField);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setCaretColor(Color.WHITE);
        if (fieldName.equalsIgnoreCase("showtime_id")) {
            textField.setEditable(false);
        }
        textField.setPreferredSize(new Dimension(0, 30));
        return textField;
    }

    private void styleComponent(JComponent component) {
        component.setBackground(new Color(50, 50, 50));
        component.setForeground(Color.WHITE);
        component.setFont(LABEL_FONT);
    }

    private void performSaveAction() {
        addNewShowtime();
    }

    private void addNewShowtime() {
        String[] newValues = extractValues(inputComponents);
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO showtimes (");
        queryBuilder.append(String.join(", ", showtimeColumns))
                    .append(") VALUES (")
                    .append("?,".repeat(showtimeColumns.length).replaceAll(",$", ""))
                    .append(")");
        executeDatabaseOperation(queryBuilder.toString(), newValues, "Showtime added successfully!", "Failed to add showtime");
    }

    private void executeDatabaseOperation(String query, String[] values, String successMsg, String failMsg) {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 1, values[i]);
            }
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                showResultDialog(successMsg, true);
                for (Window window : Window.getWindows()) {
                    if (window instanceof Dashboard) {
                        window.dispose();
                    }
                }
                dispose();
                for (Window window : Window.getWindows()) {
                    if (window instanceof App) {
                        window.dispose();
                    }
                }
                new Dashboard(databaseUrl).setVisible(true);
            } else {
                showResultDialog(failMsg, false);
            }
        } catch (SQLException e) {
            showErrorDialog("Database error: " + e.getMessage());
        }
    }

    private String[] extractValues(List<JComponent> components) {
        String[] values = new String[showtimeColumns.length];
        for (int i = 0; i < components.size(); i++) {
            JComponent component = components.get(i);
            if (component instanceof JTextField field) {
                values[i] = field.getText();
            }
        }
        return values;
    }

    private void showResultDialog(String message, boolean success) {
        JOptionPane.showMessageDialog(this, message, success ? "Success" : "Warning",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}