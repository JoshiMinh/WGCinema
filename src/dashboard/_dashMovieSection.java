package dashboard;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class _dashMovieSection {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);

    private String url;
    private JPanel moviesSection;
    private JPanel moviesPanel;

    public _dashMovieSection(String url) {
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
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, title, age_rating, release_date FROM movies ORDER BY release_date")) {
            while (resultSet.next()) {
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
        ageRatingValueLabel.setForeground(age_rating_color.getColorForRating(ageRating));
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
        editButton.addActionListener(e -> new _movieAgent(url, id, false).setVisible(true));
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        movieEntryPanel.add(buttonPanel, BorderLayout.EAST);
        return movieEntryPanel;
    }

    private void deleteMovie(int id) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM movies WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            loadMovies();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSearchBarPanel() {
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setBackground(BACKGROUND_COLOR);
        searchBarPanel.setPreferredSize(new Dimension(0, 45));
        JButton newMovieButton = new JButton("New Movie");
        newMovieButton.addActionListener(e -> new _movieAgent(url, -1, true).setVisible(true));
        searchBarPanel.add(newMovieButton);
        JTextField searchBar = new JTextField(30);
        searchBarPanel.add(searchBar);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty()) {
                new _searchMovie(url, query).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a search query.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        searchBar.addActionListener(e -> searchButton.doClick());
        searchBarPanel.add(searchButton);
        return searchBarPanel;
    }

    private JPanel createChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(BACKGROUND_COLOR);
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT age_rating, COUNT(*) AS count FROM movies GROUP BY age_rating")) {
            while (resultSet.next()) {
                pieDataset.setValue(resultSet.getString("age_rating"), resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading pie chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JFreeChart pieChart = ChartFactory.createPieChart("Movies by Age Rating", pieDataset, true, true, false);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(Color.DARK_GRAY);
        plot.setLabelPaint(Color.WHITE);
        plot.setSectionPaint("PG", age_rating_color.getColorForRating("PG"));
        plot.setSectionPaint("PG-13", age_rating_color.getColorForRating("PG-13"));
        plot.setSectionPaint("PG-16", age_rating_color.getColorForRating("PG-16"));
        plot.setSectionPaint("R", age_rating_color.getColorForRating("R"));
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanel.add(pieChartPanel, BorderLayout.CENTER);
        return chartPanel;
    }
}