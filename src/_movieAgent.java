import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class _movieAgent extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final String[] MOVIE_COLUMNS = {
        "id", "title", "director", "release_date", "duration", "language", "age_rating", "trailer", "poster", "description"
    };

    public _movieAgent(String url, int id) {
        setIconImage(new ImageIcon("images/icon.png").getImage());
        String[] columnValues = new String[MOVIE_COLUMNS.length];
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

        setTitle("Edit Movie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 15));

        JLabel titleLabel = new JLabel(columnValues[1], SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < MOVIE_COLUMNS.length; i++) {
            JLabel label = new JLabel(MOVIE_COLUMNS[i] + ":");
            label.setForeground(Color.WHITE);

            if (MOVIE_COLUMNS[i].equalsIgnoreCase("age_rating")) {
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"PG", "PG-13", "PG-16", "R"});
                comboBox.setSelectedItem(columnValues[i]);
                comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 25));
                formPanel.add(label);
                formPanel.add(comboBox);
            } else if (MOVIE_COLUMNS[i].equalsIgnoreCase("release_date")) {
                JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
                JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
                dateSpinner.setEditor(dateEditor);
                dateSpinner.setPreferredSize(new Dimension(dateSpinner.getPreferredSize().width, 25));
                try {
                    java.util.Date date = java.sql.Date.valueOf(columnValues[i]);
                    dateSpinner.setValue(date);
                } catch (IllegalArgumentException e) {
                    dateSpinner.setValue(new java.util.Date());
                }
                formPanel.add(label);
                formPanel.add(dateSpinner);
            } else if (MOVIE_COLUMNS[i].equalsIgnoreCase("description")) {
                JTextArea textArea = new JTextArea(columnValues[i]);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setRows(10);
                textArea.setPreferredSize(new Dimension(400, 100));
                formPanel.add(label);
                formPanel.add(textArea);
            } else {
                JTextField textField = new JTextField(columnValues[i]);
                textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 10));
                if (MOVIE_COLUMNS[i].equalsIgnoreCase("id")) {
                    textField.setEditable(false);
                }
                formPanel.add(label);
                formPanel.add(textField);
            }
        }

        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges(url, id, formPanel));
        add(saveButton, BorderLayout.SOUTH);

        getContentPane().setBackground(BACKGROUND_COLOR);
        setVisible(true);
    }

    private void saveChanges(String url, int id, JPanel formPanel) {
        Component[] components = formPanel.getComponents();
        String[] updatedValues = new String[MOVIE_COLUMNS.length];

        for (int i = 0, j = 0; i < components.length; i += 2, j++) {
            Component inputComponent = components[i + 1];
            if (inputComponent instanceof JTextField) {
                updatedValues[j] = ((JTextField) inputComponent).getText();
            } else if (inputComponent instanceof JComboBox) {
                updatedValues[j] = (String) ((JComboBox<?>) inputComponent).getSelectedItem();
            }
        }

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
        new _movieAgent(url, id);
    }
}