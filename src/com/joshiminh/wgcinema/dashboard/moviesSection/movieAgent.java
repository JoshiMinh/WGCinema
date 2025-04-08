package com.joshiminh.wgcinema.dashboard.moviesSection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.joshiminh.wgcinema.utils.*;

public class movieAgent extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static String[] MOVIE_COLUMNS;

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

    public movieAgent(String url, int id, boolean newMovie) {
        setIconImage(new ImageIcon("images/icon.png").getImage());
        MOVIE_COLUMNS = getColumnNames(url, "movies");
        String[] columnValues = new String[MOVIE_COLUMNS.length];

        if (!newMovie) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT " + String.join(", ", MOVIE_COLUMNS) + " FROM movies WHERE id = ?")) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    for (int i = 0; i < MOVIE_COLUMNS.length; i++) {
                        columnValues[i] = resultSet.getString(MOVIE_COLUMNS[i]);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            for (int i = 0; i < MOVIE_COLUMNS.length; i++) {
                columnValues[i] = "";
            }
        }

        setTitle(newMovie ? "Add New Movie" : "Edit Movie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(newMovie ? "Add New Movie" : "Edit: " + columnValues[1], SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(BACKGROUND_COLOR);
        formContainer.setBorder(new EmptyBorder(10, 50, 10, 50));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        List<JComponent> inputComponents = new ArrayList<>();

        for (int i = 0; i < MOVIE_COLUMNS.length; i++) {
            JLabel label = new JLabel(MOVIE_COLUMNS[i].replace("_", " ") + ":");
            label.setForeground(Color.WHITE);
            label.setFont(LABEL_FONT);
            label.setHorizontalAlignment(SwingConstants.RIGHT);

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.25;
            gbc.fill = GridBagConstraints.NONE;
            formPanel.add(label, gbc);

            JComponent inputComponent = createInputComponent(MOVIE_COLUMNS[i], columnValues[i]);
            inputComponents.add(inputComponent);

            gbc.gridx = 1;
            gbc.weightx = 0.75;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(inputComponent, gbc);
        }

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.BOTH;
        formGbc.weightx = 1;
        formGbc.weighty = 1;
        formContainer.add(formPanel, formGbc);
        add(formContainer, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        footerPanel.setBackground(BACKGROUND_COLOR);

        JButton saveButton = new JButton(newMovie ? "Add Movie" : "Save Changes");
        saveButton.setBackground(ACCENT_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(LABEL_FONT);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        saveButton.addActionListener(e -> {
            if (newMovie) {
                addNewMovie(url, inputComponents);
            } else {
                saveChanges(url, id, inputComponents);
            }
        });
        footerPanel.add(saveButton);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JComponent createInputComponent(String fieldName, String defaultValue) {
        JComponent inputComponent;

        if (fieldName.equalsIgnoreCase("age_rating")) {
            JComboBox<String> comboBox = new JComboBox<>(age_rating_color.getRatings());
            comboBox.setSelectedItem(defaultValue);
            comboBox.setBackground(new Color(50, 50, 50));
            comboBox.setForeground(Color.WHITE);
            comboBox.setFont(LABEL_FONT);
            inputComponent = comboBox;
        } else if (fieldName.equalsIgnoreCase("release_date")) {
            JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            dateSpinner.setBackground(new Color(50, 50, 50));
            dateSpinner.setForeground(Color.WHITE);
            dateSpinner.setFont(LABEL_FONT);
            try {
                if (!defaultValue.isEmpty()) {
                    dateSpinner.setValue(java.sql.Date.valueOf(defaultValue));
                }
            } catch (IllegalArgumentException ignored) {}
            inputComponent = dateSpinner;
        } else if (fieldName.equalsIgnoreCase("description")) {
            JTextArea textArea = new JTextArea(defaultValue, 4, 20);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(new Color(50, 50, 50));
            textArea.setForeground(Color.WHITE);
            textArea.setFont(LABEL_FONT);
            textArea.setCaretColor(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(0, 120));  // Increased from 100 to 120
            inputComponent = scrollPane;
        } else {
            JTextField textField = new JTextField(defaultValue);
            textField.setBackground(new Color(50, 50, 50));
            textField.setForeground(Color.WHITE);
            textField.setFont(LABEL_FONT);
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 70)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            textField.setCaretColor(Color.WHITE);
            if (fieldName.equalsIgnoreCase("id")) {
                textField.setEditable(false);
            }
            inputComponent = textField;
        }

        if (!fieldName.equalsIgnoreCase("description")) {
            inputComponent.setPreferredSize(new Dimension(0, 30));
        }
        return inputComponent;
    }

    private void addNewMovie(String url, List<JComponent> inputComponents) {
        String[] newValues = extractValues(inputComponents);
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO movies (");
        for (int i = 1; i < MOVIE_COLUMNS.length; i++) {
            queryBuilder.append(MOVIE_COLUMNS[i]).append(", ");
        }
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(") VALUES (");
        queryBuilder.append("?,".repeat(MOVIE_COLUMNS.length - 1));
        queryBuilder.setLength(queryBuilder.length() - 1);
        queryBuilder.append(")");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 1; i < MOVIE_COLUMNS.length; i++) {
                statement.setString(i, newValues[i]);
            }
            int rowsInserted = statement.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    rowsInserted > 0 ? "Movie added successfully!" : "Failed to add movie.",
                    rowsInserted > 0 ? "Success" : "Error",
                    rowsInserted > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void saveChanges(String url, int id, List<JComponent> inputComponents) {
        String[] updatedValues = extractValues(inputComponents);
        StringBuilder queryBuilder = new StringBuilder("UPDATE movies SET ");
        for (int i = 1; i < MOVIE_COLUMNS.length; i++) {
            queryBuilder.append(MOVIE_COLUMNS[i]).append(" = ?")
                    .append(i < MOVIE_COLUMNS.length - 1 ? ", " : "");
        }
        queryBuilder.append(" WHERE id = ?");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 1; i < MOVIE_COLUMNS.length; i++) {
                statement.setString(i, updatedValues[i]);
            }
            statement.setInt(MOVIE_COLUMNS.length, id);
            int rowsUpdated = statement.executeUpdate();
            JOptionPane.showMessageDialog(this,
                    rowsUpdated > 0 ? "Changes saved successfully!" : "No changes were made.",
                    rowsUpdated > 0 ? "Success" : "Warning",
                    rowsUpdated > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private String[] extractValues(List<JComponent> inputComponents) {
        String[] values = new String[MOVIE_COLUMNS.length];
        for (int i = 0; i < inputComponents.size(); i++) {
            JComponent component = inputComponents.get(i);
            if (component instanceof JTextField) {
                values[i] = ((JTextField) component).getText();
            } else if (component instanceof JComboBox) {
                values[i] = (String) ((JComboBox<?>) component).getSelectedItem();
            } else if (component instanceof JSpinner) {
                java.util.Date dateValue = (java.util.Date) ((JSpinner) component).getValue();
                values[i] = new java.sql.Date(dateValue.getTime()).toString();
            } else if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                values[i] = ((JTextArea) ((JViewport) scrollPane.getComponent(0)).getComponent(0)).getText();
            }
        }
        return values;
    }
}