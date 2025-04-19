package com.joshiminh.wgcinema.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import static com.joshiminh.wgcinema.utils.UIStyles.*;

public class showtimeAgent extends JFrame {
    private String[] showtimeColumns;
    private final String databaseUrl;
    private final List<JComponent> inputComponents;

    public showtimeAgent(String url) {
        databaseUrl = url;
        inputComponents = new ArrayList<>();
        setIconImage(new ImageIcon("images/icon.png").getImage());
        applyFrameDefaults(this, "Add New Showtime", 700, 700);
        setupFrame();
    }

    private void setupFrame() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 0, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(createHeaderLabel("Add New Showtime"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        showtimeColumns = getFilteredColumns();
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(new EmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = LABEL_INSETS;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < showtimeColumns.length; i++) {
            JLabel label = createFormLabel(showtimeColumns[i]);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.25;
            gbc.fill = GridBagConstraints.NONE;
            form.add(label, gbc);
            JComponent input = createInputComponent(showtimeColumns[i]);
            inputComponents.add(input);
            gbc.gridx = 1;
            gbc.weightx = 0.75;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(input, gbc);
        }

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.BOTH;
        formGbc.weightx = 1;
        formGbc.weighty = 1;
        container.add(form, formGbc);
        return container;
    }

    private JLabel createFormLabel(String name) {
        JLabel label = new JLabel(name.replace("_", " ") + ":");
        label.setForeground(Color.WHITE);
        label.setFont(LABEL_FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    @SuppressWarnings("unused")
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JButton saveButton = new JButton("Add Showtime");
        styleButton(saveButton);
        saveButton.addActionListener(e -> performSaveAction());
        panel.add(saveButton);
        return panel;
    }

    private String[] getFilteredColumns() {
        List<String> columns = new ArrayList<>();
        for (String col : getColumnNames(databaseUrl, "showtimes")) {
            if (!List.of("showtime_id", "reserved_chairs", "chairs_booked").contains(col.toLowerCase())) {
                columns.add(col);
            }
        }
        return columns.toArray(new String[0]);
    }

    private static String[] getColumnNames(String url, String table) {
        try (Connection con = DriverManager.getConnection(url)) {
            ResultSet rs = con.getMetaData().getColumns(null, null, table, null);
            List<String> names = new ArrayList<>();
            while (rs.next()) names.add(rs.getString("COLUMN_NAME"));
            return names.toArray(new String[0]);
        } catch (SQLException e) {
            return new String[0];
        }
    }

    private JComponent createInputComponent(String field) {
        if (field.equalsIgnoreCase("date")) return createDateBox();
        if (field.equalsIgnoreCase("showroom_id")) return createShowroomBox();
        if (field.equalsIgnoreCase("movie_id")) return createMovieBox();
        if (field.equalsIgnoreCase("time")) return createTimeBox();
        return createTextField();
    }

    private JComboBox<String> createDateBox() {
        JComboBox<String> box = new JComboBox<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i <= 14; i++) {
            box.addItem(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        styleComponent(box);
        return box;
    }

    private JComboBox<String> createShowroomBox() {
        JComboBox<String> box = new JComboBox<>();
        try (Connection con = DriverManager.getConnection(databaseUrl);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT showroom_id, showroom_name FROM showrooms")) {
            while (rs.next()) {
                box.addItem(rs.getInt("showroom_id") + " - " + rs.getString("showroom_name"));
            }
        } catch (SQLException ignored) {}
        styleComponent(box);
        return box;
    }

    private JComboBox<String> createMovieBox() {
        JComboBox<String> box = new JComboBox<>();
        try (Connection con = DriverManager.getConnection(databaseUrl);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM movies WHERE release_date >= (CURRENT_DATE - INTERVAL 14 DAY) ORDER BY release_date")) {
            while (rs.next()) {
                box.addItem(rs.getInt("id") + " - " + rs.getString("title"));
            }
        } catch (SQLException ignored) {}
        styleComponent(box);
        return box;
    }

    private JComboBox<String> createTimeBox() {
        JComboBox<String> box = new JComboBox<>();
        for (int h = 7; h <= 22; h++) {
            for (int m = 0; m < 60; m += 15) {
                box.addItem(String.format("%02d:%02d", h, m));
            }
        }
        box.addItem("22:30");
        box.setSelectedItem("19:00");
        styleComponent(box);
        return box;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleComponent(field);
        field.setBorder(componentBorder());
        field.setCaretColor(Color.WHITE);
        field.setPreferredSize(new Dimension(0, 30));
        return field;
    }

    private void performSaveAction() {
        addNewShowtime();
    }

    private void addNewShowtime() {
        String[] values = extractValues();
        String query = "INSERT INTO showtimes (" + String.join(", ", showtimeColumns) + ") VALUES (" +
                       "?,".repeat(showtimeColumns.length).replaceAll(",$", "") + ")";
        executeDatabaseOperation(query, values, "Showtime added successfully!", "Failed to add showtime");
    }

    private String[] extractValues() {
        String[] values = new String[inputComponents.size()];
        for (int i = 0; i < inputComponents.size(); i++) {
            JComponent comp = inputComponents.get(i);
            if (comp instanceof JTextField field) {
                values[i] = field.getText();
            } else if (comp instanceof JComboBox box) {
                String selected = (String) box.getSelectedItem();
                values[i] = selected.contains(" - ") ? selected.split(" - ")[0] : selected;
            }
        }
        return values;
    }

    private void executeDatabaseOperation(String query, String[] values, String successMsg, String failMsg) {
        try (Connection con = DriverManager.getConnection(databaseUrl);
             PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) stmt.setString(i + 1, values[i]);
            if (stmt.executeUpdate() > 0) {
                showResultDialog(successMsg, true);
                dispose();
                new showtimeAgent(databaseUrl).setVisible(true);
            } else {
                showResultDialog(failMsg, false);
            }
        } catch (SQLException e) {
            showErrorDialog("Database error: " + e.getMessage());
        }
    }

    private void showResultDialog(String msg, boolean success) {
        JOptionPane.showMessageDialog(this, msg, success ? "Success" : "Warning",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}