package com.joshiminh.wgcinema.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import static com.joshiminh.wgcinema.util.AgentStyles.*;

/**
 * Base class for editor dialogs to reduce redundancy.
 */
public abstract class BaseEditorDialog extends JFrame {
    protected final String databaseUrl;
    protected final Runnable refreshCallback;

    public BaseEditorDialog(String databaseUrl, String title, int width, int height, Runnable refreshCallback) {
        this.databaseUrl = databaseUrl;
        this.refreshCallback = refreshCallback;

        applyFrameDefaults(this, title, width, height);
    }

    protected abstract void setupFrame();

    protected JPanel createHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        headerPanel.setBackground(PRIMARY_BACKGROUND);

        JLabel titleLabel = createHeaderLabel(title);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    protected JPanel createFooter(String buttonText, Runnable onSave) {
        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        footerPanel.setBackground(PRIMARY_BACKGROUND);

        JButton saveButton = new JButton(buttonText);
        styleButton(saveButton);
        saveButton.addActionListener(e -> onSave.run());
        footerPanel.add(saveButton);

        return footerPanel;
    }

    protected JLabel createFormLabel(String columnName) {
        JLabel label = new JLabel(columnName.replace("_", " ") + ":");
        label.setForeground(TEXT_COLOR);
        label.setFont(LABEL_FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    protected void showResult(String message, boolean success) {
        JOptionPane.showMessageDialog(this, message, success ? "Success" : "Warning",
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected String[] extractComponentValues(List<JComponent> components) {
        String[] values = new String[components.size()];
        for (int i = 0; i < components.size(); i++) {
            JComponent component = components.get(i);
            if (component instanceof JTextField field) {
                values[i] = field.getText();
            } else if (component instanceof JComboBox<?> comboBox) {
                values[i] = comboBox.getSelectedItem().toString();
            } else if (component instanceof JSpinner spinner) {
                Object value = spinner.getValue();
                if (value instanceof java.util.Date date) {
                    values[i] = new java.sql.Date(date.getTime()).toString();
                } else if (value != null) {
                    values[i] = value.toString();
                } else {
                    values[i] = "";
                }
            } else if (component instanceof JScrollPane pane && pane.getViewport().getView() instanceof JTextArea area) {
                values[i] = area.getText();
            } else if (component instanceof JPasswordField pass) {
                values[i] = new String(pass.getPassword());
            }
        }
        return values;
    }
}
