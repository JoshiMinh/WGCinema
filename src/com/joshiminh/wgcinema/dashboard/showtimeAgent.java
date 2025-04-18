package com.joshiminh.wgcinema.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 0, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JLabel title = new JLabel("Add New Showtime", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.CENTER);
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
        gbc.insets = new Insets(5, 10, 5, 10);
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

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JButton saveButton = createSaveButton();
        panel.add(saveButton);
        return panel;
    }

    private JButton createSaveButton() {
        JButton button = new JButton("Add Showtime");
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(BUTTON_PADDING, 25, BUTTON_PADDING, 25));
        button.addActionListener(e -> performSaveAction());
        return button;
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
             ResultSet rs = stmt.executeQuery(
                "SELECT id, title FROM movies WHERE release_date >= (CURRENT_DATE - INTERVAL 14 DAY) ORDER BY release_date")) {
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
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setCaretColor(Color.WHITE);
        field.setPreferredSize(new Dimension(0, 30));
        return field;
    }

    private void styleComponent(JComponent comp) {
        comp.setBackground(new Color(50, 50, 50));
        comp.setForeground(Color.WHITE);
        comp.setFont(LABEL_FONT);
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