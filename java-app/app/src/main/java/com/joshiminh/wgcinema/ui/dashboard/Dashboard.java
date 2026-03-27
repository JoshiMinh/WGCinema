package com.joshiminh.wgcinema.ui.dashboard;
import com.joshiminh.wgcinema.util.*;

import javax.swing.*;
import java.awt.*;
import com.joshiminh.wgcinema.util.ResourceManager;
import static com.joshiminh.wgcinema.util.AgentStyles.*;

public class Dashboard extends JFrame {
    private final String dbUrl;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public Dashboard(String dbUrl) {
        this.dbUrl = dbUrl;
        setTitle("Admin Dashboard");
        applyFrameDefaults(this, "Admin Dashboard", 1200, 800);
        setIconImage(ResourceManager.loadAppIcon());
        
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Sidebar
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(SECONDARY_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 1.0;

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setForeground(LIGHT_TEXT_COLOR);
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sidebar.add(menuLabel, gbc);
        gbc.gridy++;
        
        // Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(PRIMARY_BACKGROUND);
        
        addSidebarButton(sidebar, gbc, "Movies", "movies");
        addSidebarButton(sidebar, gbc, "Showrooms", "showrooms");
        addSidebarButton(sidebar, gbc, "Showtimes", "showtimes");

        gbc.weighty = 1.0;
        sidebar.add(new Box.Filler(new Dimension(0,0), new Dimension(0,0), new Dimension(0, Integer.MAX_VALUE)), gbc);
        gbc.weighty = 0;
        gbc.gridy++;

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.setBackground(DANGER_RED);
        logoutButton.addActionListener(e -> dispose());
        sidebar.add(logoutButton, gbc);
        
        contentPanel.add(new MoviesPanel(dbUrl).getMoviesSection(), "movies");
        contentPanel.add(new ShowroomsPanel(dbUrl).getShowroomsPanel(), "showrooms");
        contentPanel.add(new ShowtimesPanel(dbUrl).getShowtimesPanel(), "showtimes");
        
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addSidebarButton(JPanel sidebar, GridBagConstraints gbc, String text, String cardName) {
        JButton button = new JButton(text);
        styleButton(button);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sidebar.add(button, gbc);
        gbc.gridy++;
    }
}