package com.joshiminh.wgcinema.dashboard.agents;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

import com.joshiminh.wgcinema.dashboard.Dashboard;
import com.joshiminh.wgcinema.utils.*;

public class MovieSearch extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    public MovieSearch(String url, String query) {
        setTitle("Search: " + query);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("images/icon.png").getImage());
        JPanel moviesPanel = createMoviesPanel(url, query);
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setContentPane(scrollPane);
    }

    private JPanel createMoviesPanel(String url, String query) {
        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBackground(BACKGROUND_COLOR);
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT id, title, age_rating, release_date FROM movies WHERE title LIKE ? ORDER BY release_date LIMIT 20")) {
            preparedStatement.setString(1, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                moviesPanel.add(createMovieEntryPanel(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("age_rating"),
                        resultSet.getString("release_date"),
                        url));
                moviesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            moviesPanel.add(errorLabel);
        }
        return moviesPanel;
    }

    private JPanel createMovieEntryPanel(int id, String title, String ageRating, String releaseDate, String url) {
        JPanel movieEntryPanel = new JPanel(new BorderLayout());
        movieEntryPanel.setBackground(BACKGROUND_COLOR);
        movieEntryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        movieEntryPanel.add(createTextPanel(title, ageRating, releaseDate), BorderLayout.CENTER);
        movieEntryPanel.add(createButtonPanel(id, url), BorderLayout.EAST);
        return movieEntryPanel;
    }

    private JPanel createTextPanel(String title, String ageRating, String releaseDate) {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel ageRatingPanel = createAgeRatingPanel(ageRating);
        JLabel releaseDateLabel = new JLabel("Release Date: " + releaseDate);
        releaseDateLabel.setForeground(Color.LIGHT_GRAY);
        releaseDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        releaseDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(ageRatingPanel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(releaseDateLabel);
        return textPanel;
    }

    private JPanel createAgeRatingPanel(String ageRating) {
        JLabel ageRatingLabel = new JLabel("Age Rating: ");
        ageRatingLabel.setForeground(Color.LIGHT_GRAY);
        ageRatingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel ageRatingValueLabel = new JLabel(ageRating);
        ageRatingValueLabel.setForeground(AgeRatingColor.getColorForRating(ageRating));
        ageRatingValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JPanel ageRatingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageRatingPanel.setBackground(BACKGROUND_COLOR);
        ageRatingPanel.add(ageRatingLabel);
        ageRatingPanel.add(ageRatingValueLabel);
        ageRatingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return ageRatingPanel;
    }

    private JPanel createButtonPanel(int id, String url) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton deleteButton = createDeleteButton(id, url);
        buttonPanel.add(deleteButton);
        JButton editButton = createEditButton(id, url);
        buttonPanel.add(editButton);
        return buttonPanel;
    }

    @SuppressWarnings("unused")
    private JButton createDeleteButton(int id, String url) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.addActionListener(e -> {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM movies WHERE id = ?")) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                dispose();
                new Dashboard(url).setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return deleteButton;
    }

    @SuppressWarnings("unused")
    private JButton createEditButton(int id, String url) {
        JButton editButton = new JButton("Edit");
        editButton.setBackground(Color.DARK_GRAY);
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> new MovieAgent(url, id, false).setVisible(true));
        return editButton;
    }
}