package com.joshiminh.wgcinema.dashboard;

import java.awt.*;
import javax.swing.*;
import com.joshiminh.wgcinema.App;
import com.joshiminh.wgcinema.dashboard.sections.Movies;
import com.joshiminh.wgcinema.dashboard.sections.Showrooms;
import com.joshiminh.wgcinema.dashboard.sections.Showtimes;
import com.joshiminh.wgcinema.utils.ResourceUtil;

public class Dashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public Dashboard(String url) {
        // Set up the main frame
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(ResourceUtil.loadAppIcon());
        setLayout(new GridBagLayout());

        // Handle window close event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                new App();
            }
        });

        // Configure layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create and configure tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(Color.WHITE);

        // Add tabs for different sections
        tabbedPane.addTab("Movies", new Movies(url).getMoviesSection());
        tabbedPane.addTab("Showrooms", new Showrooms(url).getShowroomsPanel());
        tabbedPane.addTab("Showtimes", new Showtimes(url).getShowtimesPanel());

        // Add tabbed pane to the frame
        add(tabbedPane, gbc);
    }
}