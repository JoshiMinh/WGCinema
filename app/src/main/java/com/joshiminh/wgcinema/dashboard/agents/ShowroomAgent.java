package com.joshiminh.wgcinema.dashboard.agents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.ResourceUtil;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static com.joshiminh.wgcinema.utils.AgentStyles.*;

@SuppressWarnings("unused")
public class ShowroomAgent extends JFrame {
    private String[] showroomColumns;
    private final String databaseUrl;
    private final List<JComponent> inputComponents;

    public ShowroomAgent(String url) {
        databaseUrl = url;
        inputComponents = new ArrayList<>();
    
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(ResourceUtil.loadAppIcon());
        applyFrameDefaults(this, "Add New Showroom", 700, 700);
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
        panel.add(createHeaderLabel("Add New Showroom"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        showroomColumns = getFilteredColumns();
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(new EmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = LABEL_INSETS;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < showroomColumns.length; i++) {
            JLabel label = createFormLabel(showroomColumns[i]);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.25;
            gbc.fill = GridBagConstraints.NONE;
            form.add(label, gbc);
            JComponent input = createInputComponent(showroomColumns[i]);
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

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JButton button = new JButton("Add Showroom");
        styleButton(button);
        button.addActionListener(e -> performSaveAction());
        panel.add(button);
        return panel;
    }

    private JLabel createFormLabel(String columnName) {
        JLabel label = new JLabel(columnName.replace("_", " ") + ":");
        label.setForeground(Color.WHITE);
        label.setFont(LABEL_FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JComponent createInputComponent(String fieldName) {
        JTextField field = new JTextField();
        styleComponent(field);
        field.setBorder(componentBorder());
        field.setCaretColor(Color.WHITE);
        field.setPreferredSize(new Dimension(0, 30));
        return field;
    }

    private String[] getFilteredColumns() {
        List<String> columns = new ArrayList<>();
        for (String col : DAO.getColumnNames(databaseUrl, "showrooms")) {
            if (!col.equalsIgnoreCase("showroom_id")) {
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
            e.printStackTrace();
            return new String[0];
        }
    }

    private void performSaveAction() {
        addNewShowroom();
    }

    private void addNewShowroom() {
        String[] values = extractValues();
        int result = DAO.insertShowroom(databaseUrl, showroomColumns, values);
        if (result > 0) {
            showDialog("Showroom added successfully!", true);
            dispose();
        } else {
            showDialog("Failed to add showroom", false);
        }
    }    

    private String[] extractValues() {
        String[] values = new String[inputComponents.size()];
        for (int i = 0; i < inputComponents.size(); i++) {
            JComponent comp = inputComponents.get(i);
            if (comp instanceof JTextField field) {
                values[i] = field.getText();
            }
        }
        return values;
    }

    private void executeDatabaseOperation(String query, String[] values) {
        try (Connection con = DriverManager.getConnection(databaseUrl);
             PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }
            if (stmt.executeUpdate() > 0) {
                showDialog("Showroom added successfully!", true);
                dispose();
            } else {
                showDialog("Failed to add showroom", false);
            }
        } catch (SQLException e) {
            showDialog("Database error: " + e.getMessage(), false);
        }
    }

    private void showDialog(String message, boolean success) {
        JOptionPane.showMessageDialog(this, message, success ? "Success" : "Error",
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}