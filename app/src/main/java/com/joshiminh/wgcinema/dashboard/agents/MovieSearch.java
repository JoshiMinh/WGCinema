package com.joshiminh.wgcinema.dashboard.agents;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

import com.joshiminh.wgcinema.dashboard.Dashboard;
import com.joshiminh.wgcinema.data.*;
import com.joshiminh.wgcinema.utils.*;

@SuppressWarnings("unused")
public class MovieSearch extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(34, 40, 49);
    private static final Color PANEL_COLOR = new Color(44, 54, 63);
    private static final Color BUTTON_DELETE = new Color(220, 53, 69);
    private static final Color BUTTON_EDIT = new Color(52, 58, 64);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MovieSearch(String url, String query) {
        setTitle("Search: " + query);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 650);
        setLocationRelativeTo(null);
        setIconImage(ResourceUtil.loadAppIcon());
        JScrollPane scrollPane = new JScrollPane(createMoviesPanel(url, query));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        setContentPane(scrollPane);
    }

    private JPanel createMoviesPanel(String url, String query) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BACKGROUND_COLOR);
        boolean found = false;
        try (ResultSet resultSet = DAO.searchMoviesByTitle(url, query)) {
            while (resultSet.next()) {
                found = true;
                listPanel.add(createMovieEntryPanel(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("age_rating"),
                        resultSet.getString("release_date"),
                        url));
                listPanel.add(Box.createRigidArea(new Dimension(0, 14)));
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setForeground(BUTTON_DELETE);
            errorLabel.setFont(TITLE_FONT);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(errorLabel);
        }
        if (!found) {
            JLabel noResult = new JLabel("No movies found.");
            noResult.setForeground(Color.LIGHT_GRAY);
            noResult.setFont(TITLE_FONT);
            noResult.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalGlue());
            listPanel.add(noResult);
            listPanel.add(Box.createVerticalGlue());
        }
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(listPanel, BorderLayout.NORTH);
        return wrapperPanel;
    }

    private JPanel createMovieEntryPanel(int id, String title, String ageRating, String releaseDate, String url) {
        JPanel movieEntryPanel = new JPanel(new BorderLayout(12, 0));
        movieEntryPanel.setBackground(PANEL_COLOR);
        movieEntryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 80), 1, true),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        movieEntryPanel.add(createTextPanel(title, ageRating, releaseDate), BorderLayout.CENTER);
        movieEntryPanel.add(createButtonPanel(id, url), BorderLayout.EAST);
        return movieEntryPanel;
    }

    private JPanel createTextPanel(String title, String ageRating, String releaseDate) {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PANEL_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel ageRatingPanel = createAgeRatingPanel(ageRating);

        JLabel releaseDateLabel = new JLabel("Release Date: " + releaseDate);
        releaseDateLabel.setForeground(new Color(180, 180, 180));
        releaseDateLabel.setFont(LABEL_FONT);
        releaseDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(ageRatingPanel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        textPanel.add(releaseDateLabel);
        return textPanel;
    }

    private JPanel createAgeRatingPanel(String ageRating) {
        JLabel ageRatingLabel = new JLabel("Age Rating: ");
        ageRatingLabel.setForeground(new Color(200, 200, 200));
        ageRatingLabel.setFont(LABEL_FONT);

        JLabel ageRatingValueLabel = new JLabel(ageRating);
        ageRatingValueLabel.setForeground(AgeRatingColor.getColorForRating(ageRating));
        ageRatingValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel ageRatingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageRatingPanel.setBackground(PANEL_COLOR);
        ageRatingPanel.add(ageRatingLabel);
        ageRatingPanel.add(ageRatingValueLabel);
        ageRatingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return ageRatingPanel;
    }

    private JPanel createButtonPanel(int id, String url) {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        buttonPanel.setBackground(PANEL_COLOR);
        JButton deleteButton = createDeleteButton(id, url);
        JButton editButton = createEditButton(id, url);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private JButton createDeleteButton(int id, String url) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(BUTTON_DELETE);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(LABEL_FONT);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        deleteButton.addActionListener(e -> {
            int rowsAffected = DAO.deleteRowById(url, "movies", "id", id);
            if (rowsAffected > 0) {
                dispose();
                new Dashboard(url).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting movie.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return deleteButton;
    }

    private JButton createEditButton(int id, String url) {
        JButton editButton = new JButton("Edit");
        editButton.setBackground(BUTTON_EDIT);
        editButton.setForeground(Color.WHITE);
        editButton.setFont(LABEL_FONT);
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        editButton.addActionListener(e -> new MovieAgent(url, id, false).setVisible(true));
        return editButton;
    }
}