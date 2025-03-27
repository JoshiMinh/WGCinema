import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class _movieAgent extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final String[] MOVIE_COLUMNS = {
        "id", "title", "director", "release_date", "duration", "language", "age_rating", "trailer", "poster", "description"
    };

    public _movieAgent(String url, int id, boolean newMovie) {
        setIconImage(new ImageIcon("images/icon.png").getImage());
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
                } else {
                    JOptionPane.showMessageDialog(this, "No record found with ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
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
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 15));

        JLabel titleLabel = new JLabel(newMovie ? "Add Movie" : columnValues[1], SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < MOVIE_COLUMNS.length; i++) {
            JLabel label = new JLabel(MOVIE_COLUMNS[i] + ":");
            label.setForeground(Color.WHITE);

            JComponent inputComponent;
            if (MOVIE_COLUMNS[i].equalsIgnoreCase("age_rating")) {
                JComboBox<String> comboBox = new JComboBox<>(age_rating_color.getRatings());
                comboBox.setSelectedItem(columnValues[i]);
                inputComponent = comboBox;
            } else if (MOVIE_COLUMNS[i].equalsIgnoreCase("release_date")) {
                JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
                JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
                dateSpinner.setEditor(dateEditor);
                try {
                    if (!newMovie) {
                        java.util.Date date = java.sql.Date.valueOf(columnValues[i]);
                        dateSpinner.setValue(date);
                    }
                } catch (IllegalArgumentException e) {
                    dateSpinner.setValue(new java.util.Date());
                }
                inputComponent = dateSpinner;
            } else if (MOVIE_COLUMNS[i].equalsIgnoreCase("description")) {
                JTextArea textArea = new JTextArea(columnValues[i]);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setRows(10);
                textArea.setPreferredSize(new Dimension(400, 100));
                inputComponent = textArea;
            } else {
                JTextField textField = new JTextField(columnValues[i]);
                if (MOVIE_COLUMNS[i].equalsIgnoreCase("id")) {
                    textField.setEditable(false);
                }
                inputComponent = textField;
            }

            formPanel.add(label);
            formPanel.add(inputComponent);
        }

        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton(newMovie ? "Add Movie" : "Save Changes");
        saveButton.addActionListener(e -> {
            if (newMovie) {
                addNewMovie(url, formPanel);
            } else {
                saveChanges(url, id, formPanel);
            }
        });
        add(saveButton, BorderLayout.SOUTH);

        getContentPane().setBackground(BACKGROUND_COLOR);
        setVisible(true);
    }

    private void addNewMovie(String url, JPanel formPanel) {
        String[] newValues = extractValues(formPanel);

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
        new _movieAgent(url, 0, true);
    }

    private void saveChanges(String url, int id, JPanel formPanel) {
        String[] updatedValues = extractValues(formPanel);

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
        new _movieAgent(url, id, false);
    }

    private String[] extractValues(JPanel formPanel) {
        Component[] components = formPanel.getComponents();
        String[] values = new String[MOVIE_COLUMNS.length];

        for (int i = 0, j = 0; i < components.length; i += 2, j++) {
            Component inputComponent = components[i + 1];

            if (inputComponent instanceof JTextField) {
                values[j] = ((JTextField) inputComponent).getText();
            } else if (inputComponent instanceof JComboBox) {
                values[j] = (String) ((JComboBox<?>) inputComponent).getSelectedItem();
            } else if (inputComponent instanceof JSpinner) {
                java.util.Date dateValue = (java.util.Date) ((JSpinner) inputComponent).getValue();
                values[j] = new java.sql.Date(dateValue.getTime()).toString();
            } else if (inputComponent instanceof JTextArea) {
                values[j] = ((JTextArea) inputComponent).getText();
            }
        }
        return values;
    }
}