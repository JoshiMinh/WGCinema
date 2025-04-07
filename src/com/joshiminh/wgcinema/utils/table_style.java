package com.joshiminh.wgcinema.utils;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;

public class table_style {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public table_style(JTable table) {
        table.setFillsViewportHeight(true);
        table.setBackground(BACKGROUND_COLOR);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setBackground(Color.DARK_GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }
}