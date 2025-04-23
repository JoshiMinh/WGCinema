package com.joshiminh.wgcinema.dashboard.sections;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.joshiminh.wgcinema.dashboard.Dashboard;
import com.joshiminh.wgcinema.dashboard.agents.MovieAgent;
import com.joshiminh.wgcinema.dashboard.agents.MovieSearch;
import com.joshiminh.wgcinema.data.AgeRatingColor;
import com.joshiminh.wgcinema.data.DAO;
import com.joshiminh.wgcinema.utils.*;

@SuppressWarnings("unused")
public class Movies {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private Dashboard dashboardframe;
    private String url;
    private JPanel moviesSection;
    private JPanel moviesPanel;

    public Movies(String url, Dashboard dashboardframe) {
        this.dashboardframe = dashboardframe;
        this.url = url;
        this.moviesSection = new JPanel(new BorderLayout());
        this.moviesSection.setBackground(BACKGROUND_COLOR);
        this.moviesSection.add(createSearchBarPanel(), BorderLayout.NORTH);
        this.moviesPanel = new JPanel();
        this.moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        this.moviesPanel.setBackground(BACKGROUND_COLOR);
        loadMovies();
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.moviesSection.add(scrollPane, BorderLayout.CENTER);
        this.moviesSection.add(createChartPanel(), BorderLayout.SOUTH);
    }

    public JPanel getMoviesSection() {
        return moviesSection;
    }

    private void loadMovies() {
        moviesPanel.removeAll();
        try (ResultSet resultSet = DAO.fetchAllMovies(url)) {
            while (resultSet != null && resultSet.next()) {
                moviesPanel.add(createMovieEntryPanel(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("age_rating"),
                        resultSet.getString("release_date")
                ));
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            moviesPanel.add(errorLabel);
        }
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    private JPanel createMovieEntryPanel(int id, String title, String ageRating, String releaseDate) {
        JPanel movieEntryPanel = new JPanel(new BorderLayout());
        movieEntryPanel.setBackground(BACKGROUND_COLOR);
        movieEntryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel ageRatingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageRatingPanel.setBackground(BACKGROUND_COLOR);
        JLabel ageRatingLabel = new JLabel("Age Rating: ");
        ageRatingLabel.setForeground(Color.LIGHT_GRAY);
        ageRatingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel ageRatingValueLabel = new JLabel(ageRating);
        ageRatingValueLabel.setForeground(AgeRatingColor.getColorForRating(ageRating));
        ageRatingValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        ageRatingPanel.add(ageRatingLabel);
        ageRatingPanel.add(ageRatingValueLabel);
        ageRatingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel releaseDateLabel = new JLabel("Release Date: " + releaseDate);
        releaseDateLabel.setForeground(Color.LIGHT_GRAY);
        releaseDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        releaseDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(ageRatingPanel);
        textPanel.add(releaseDateLabel);
        movieEntryPanel.add(textPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteMovie(id));
        JButton editButton = new JButton("Edit");
        editButton.setBackground(Color.DARK_GRAY);
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> new MovieAgent(url, id, false, dashboardframe).setVisible(true));
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        movieEntryPanel.add(buttonPanel, BorderLayout.EAST);
        return movieEntryPanel;
    }
    
    private void deleteMovie(int id) {
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to delete this movie?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
    
        if (confirm == JOptionPane.YES_OPTION) {
            int result = DAO.deleteRowById(url, "movies", "id", id);
            if (result > 0) {
                loadMovies();
            } else {
                JOptionPane.showMessageDialog(null, "Error deleting movie.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }      

    private JPanel createSearchBarPanel() {
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setBackground(BACKGROUND_COLOR);
        searchBarPanel.setPreferredSize(new Dimension(0, 45));
        JButton newMovieButton = new JButton("New Movie");
        newMovieButton.addActionListener(e -> new MovieAgent(url, -1, true, dashboardframe).setVisible(true));
        searchBarPanel.add(newMovieButton);
        JTextField searchBar = new JTextField(30);
        searchBarPanel.add(searchBar);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty()) {
                new MovieSearch(url, query, dashboardframe).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a search query.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        searchBar.addActionListener(e -> searchButton.doClick());
        searchBarPanel.add(searchButton);
        return searchBarPanel;
    }

    private JPanel createChartPanel() {
        JPanel chartPanel = new JPanel(new GridLayout(1, 3));
        Color darkBackground = new Color(30, 30, 30);
        chartPanel.setBackground(darkBackground);
    
        DefaultPieDataset ageRatingDataset = new DefaultPieDataset();
        try (ResultSet resultSet = DAO.fetchAgeRatingCounts(url)) {
            while (resultSet != null && resultSet.next()) {
                ageRatingDataset.setValue(resultSet.getString("age_rating"), resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading age rating pie chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JFreeChart ageRatingChart = ChartFactory.createPieChart("Movies by Age Rating", ageRatingDataset, true, true, false);
        ageRatingChart.getTitle().setPaint(Color.WHITE);
        PiePlot agePlot = (PiePlot) ageRatingChart.getPlot();
        agePlot.setBackgroundPaint(darkBackground);
        agePlot.setOutlinePaint(new Color(100, 100, 100));
        agePlot.setLabelBackgroundPaint(new Color(100, 100, 100));
        agePlot.setLabelPaint(Color.WHITE);
        agePlot.setSectionPaint("PG", AgeRatingColor.getColorForRating("PG"));
        agePlot.setSectionPaint("PG-13", AgeRatingColor.getColorForRating("PG-13"));
        agePlot.setSectionPaint("PG-16", AgeRatingColor.getColorForRating("PG-16"));
        agePlot.setSectionPaint("R", AgeRatingColor.getColorForRating("R"));
        ageRatingChart.setBackgroundPaint(darkBackground);
        
        ChartPanel ageChartPanel = new ChartPanel(ageRatingChart);
        ageChartPanel.setPreferredSize(new Dimension(300, 300));
        ageChartPanel.setBackground(darkBackground);
        ageChartPanel.setMouseWheelEnabled(true);
        ageChartPanel.setDomainZoomable(true);
        ageChartPanel.setRangeZoomable(true);
        chartPanel.add(ageChartPanel);
    
        DefaultPieDataset languageDataset = new DefaultPieDataset();
        int totalMovies = 0;
        try (ResultSet resultSet = DAO.fetchLanguageCounts(url)) {
            while (resultSet != null && resultSet.next()) {
                totalMovies += resultSet.getInt("count");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading language pie chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        try (ResultSet resultSet = DAO.fetchLanguageCounts(url)) {
            while (resultSet != null && resultSet.next()) {
                String language = resultSet.getString("language");
                double percentage = (resultSet.getInt("count") * 100.0) / totalMovies;
                languageDataset.setValue(language, percentage);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error calculating language percentages: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JFreeChart languageChart = ChartFactory.createPieChart("Movies by Language", languageDataset, true, true, false);
        languageChart.getTitle().setPaint(Color.WHITE);
        PiePlot languagePlot = (PiePlot) languageChart.getPlot();
        languagePlot.setBackgroundPaint(darkBackground);
        languagePlot.setOutlinePaint(new Color(100, 100, 100));
        languagePlot.setLabelBackgroundPaint(new Color(100, 100, 100));
        languagePlot.setLabelPaint(Color.WHITE);
        languagePlot.setSectionPaint("English", Color.BLUE);
        languagePlot.setSectionPaint("Vietnamese", Color.GREEN);
        languagePlot.setSectionPaint("Other", Color.GRAY);
        languageChart.setBackgroundPaint(darkBackground);
        
        ChartPanel languageChartPanel = new ChartPanel(languageChart);
        languageChartPanel.setPreferredSize(new Dimension(300, 300));
        languageChartPanel.setBackground(darkBackground);
        languageChartPanel.setMouseWheelEnabled(true);
        languageChartPanel.setDomainZoomable(true);
        languageChartPanel.setRangeZoomable(true);
        chartPanel.add(languageChartPanel);
    
        return chartPanel;
    }
}