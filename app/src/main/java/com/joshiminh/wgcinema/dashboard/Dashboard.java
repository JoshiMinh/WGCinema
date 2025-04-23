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
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                new App();
            }
        });
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(ResourceUtil.loadAppIcon());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(Color.WHITE);

        tabbedPane.addTab("Movies", new Movies(url, this).getMoviesSection());
        tabbedPane.addTab("Showrooms", new Showrooms(url, this).getShowroomsPanel());
        tabbedPane.addTab("Showtimes", new Showtimes(url, this).getShowtimesPanel());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(tabbedPane, gbc);
    }
}