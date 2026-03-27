package com.joshiminh.wgcinema.ui.dashboard;

import com.joshiminh.wgcinema.dao.AccountDAO;
import com.joshiminh.wgcinema.util.BaseEditorDialog;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.joshiminh.wgcinema.util.AgentStyles.*;

public class EmployeeEditorDialog extends BaseEditorDialog {
    private final JTextField emailField = new JTextField(20);
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private JFormattedTextField dobField;
    private final JCheckBox adminCheckBox = new JCheckBox("Admin Privileges");
    private final String employeeEmail;

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EmployeeEditorDialog(String dbUrl, String employeeEmail, Runnable refreshCallback) {
        super(dbUrl, "Edit Employee: " + employeeEmail, 600, 600, refreshCallback);
        this.employeeEmail = employeeEmail;

        setupFrame();
        loadEmployeeDetails();
    }

    @Override
    protected void setupFrame() {
        add(createHeader("Edit Employee Details"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = LABEL_INSETS;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Email (read-only)
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField.setText(employeeEmail);
        emailField.setEditable(false);
        styleComponent(emailField);
        emailField.setBorder(componentBorder());
        formPanel.add(emailField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        styleComponent(nameField);
        nameField.setBorder(componentBorder());
        formPanel.add(nameField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        styleComponent(genderBox);
        formPanel.add(genderBox, gbc);

        // Date of Birth
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Date of Birth (DD/MM/YYYY):"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dobField = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            dobField = new JFormattedTextField();
        }
        dobField.setColumns(20);
        styleComponent(dobField);
        dobField.setBorder(componentBorder());
        formPanel.add(dobField, gbc);

        // Admin Checkbox
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        adminCheckBox.setForeground(TEXT_COLOR);
        adminCheckBox.setBackground(PRIMARY_BACKGROUND);
        adminCheckBox.setFont(LABEL_FONT);
        formPanel.add(adminCheckBox, gbc);

        add(formPanel, BorderLayout.CENTER);
        add(createFooter("Save Changes", this::handleSave), BorderLayout.SOUTH);
    }

    private void loadEmployeeDetails() {
        try (ResultSet rs = AccountDAO.fetchAccountByEmail(databaseUrl, employeeEmail)) {
            if (rs != null && rs.next()) {
                nameField.setText(rs.getString("name"));
                genderBox.setSelectedItem(rs.getString("gender"));
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) {
                    dobField.setText(dob.toLocalDate().format(INPUT_FORMATTER));
                }
                adminCheckBox.setSelected(rs.getInt("admin") == 1);
            } else {
                showError("Employee details not found.");
                dispose();
            }
        } catch (SQLException e) {
            showError("Database error loading details: " + e.getMessage());
            dispose();
        }
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String dobStr = dobField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();

        if (name.isEmpty() || dobStr.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr, INPUT_FORMATTER);
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use DD/MM/YYYY.");
            return;
        }

        int updatedRows = AccountDAO.updateAccount(databaseUrl, employeeEmail, name, (String) genderBox.getSelectedItem(), java.sql.Date.valueOf(dob.format(DB_FORMATTER)), isAdmin);
        if (updatedRows > 0) {
            showResult("Employee details updated successfully!", true);
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            dispose();
        } else {
            showResult("Failed to update employee details.", false);
        }
    }
}