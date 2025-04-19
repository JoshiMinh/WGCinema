package com.joshiminh.wgcinema.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Centralized style definitions and helper methods for the WG Cinema dashboard UI.
 */
public final class UIStyles {
    // Color palette
    public static final Color BACKGROUND_COLOR   = new Color(30, 30, 30);
    public static final Color ACCENT_COLOR       = new Color(70, 130, 180);
    public static final Color COMPONENT_BG_COLOR = new Color(50, 50, 50);
    public static final Color BORDER_COLOR       = new Color(70, 70, 70);

    // Fonts
    public static final Font LABEL_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 22);

    // Padding and spacing
    public static final int FORM_PADDING   = 50;
    public static final int BUTTON_PADDING = 8;
    public static final Insets LABEL_INSETS = new Insets(5, 10, 5, 10);

    private UIStyles() {
        // Prevent instantiation
    }

    /**
     * Apply standard background and font to any Swing component.
     */
    public static void styleComponent(JComponent comp) {
        comp.setBackground(COMPONENT_BG_COLOR);
        comp.setForeground(Color.WHITE);
        comp.setFont(LABEL_FONT);
        comp.setOpaque(true);
    }

    /**
     * Apply standard styling to buttons.
     */
    public static void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(BUTTON_PADDING, 25, BUTTON_PADDING, 25));
    }

    /**
     * Create a border for text inputs.
     */
    public static Border componentBorder() {
        return new CompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(5, 5, 5, 5)
        );
    }

    /**
     * Initialize a JFrame with common settings.
     */
    public static void applyFrameDefaults(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(0, 20));
    }

    /**
     * Create a standardized header label for dialogs.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(TITLE_FONT);
        return label;
    }
}