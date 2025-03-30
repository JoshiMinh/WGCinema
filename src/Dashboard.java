import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

public class Dashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final int MOVIES_SECTION_HEIGHT = 400;

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
        getContentPane().setBackground(BACKGROUND_COLOR); // Set the background color
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(new ImageIcon("images/icon.png").getImage());

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Dashboard section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.weightx = 0.7;
        gbc.weighty = 0.3;
        add(createDashboard(url), gbc);

        // Show Rooms section
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        gbc.weighty = 0.3;
        add(createSectionPanel("Show Rooms", 40, 80), gbc);

        // Movies section
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        add(createMoviesSection(url), gbc);

        // Show Times section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        add(createShowTimesSection(url), gbc);

        // Graphs section
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        add(createGraphsSection(url), gbc);
    }

    private JPanel createDashboard(String url) {
        JPanel dashboardPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        dashboardPanel.setBackground(BACKGROUND_COLOR);
        return dashboardPanel;
    }

    private JPanel createGraphsSection(String url) {
        JPanel graphsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        graphsPanel.setBackground(BACKGROUND_COLOR);

        // Bar Chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            String[] tables = {"movies", "accounts", "transactions", "showtimes"};
            for (String table : tables) {
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM " + table);
                if (resultSet.next()) {
                    barDataset.addValue(resultSet.getInt("count"), "Count", table);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bar chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JFreeChart barChart = ChartFactory.createBarChart(
            "Records Count", "Tables", "Count", barDataset, PlotOrientation.VERTICAL, false, true, false);
        graphsPanel.add(new ChartPanel(barChart));

        // Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT age_rating, COUNT(*) AS count FROM movies GROUP BY age_rating")) {
            while (resultSet.next()) {
                pieDataset.setValue(resultSet.getString("age_rating"), resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pie chart data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Movies by Age Rating", pieDataset, true, true, false);
        graphsPanel.add(new ChartPanel(pieChart));

        return graphsPanel;
    }

    private JPanel createShowTimesSection(String url) {
        JPanel showTimesSection = new JPanel(new BorderLayout());
        showTimesSection.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Show Times", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        showTimesSection.add(titleLabel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make all columns except the first (ID) editable
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                 "SELECT * FROM showtimes WHERE date >= CURRENT_DATE ORDER BY date ASC, time ASC");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading showtimes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object updatedValue = table.getValueAt(row, column);
                Object idValue = table.getValueAt(row, 0);

                try (Connection connection = DriverManager.getConnection(url);
                     PreparedStatement preparedStatement = connection.prepareStatement(
                         "UPDATE showtimes SET " + table.getColumnName(column) + " = ? WHERE id = ?")) {
                    preparedStatement.setObject(1, updatedValue);
                    preparedStatement.setObject(2, idValue);
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        showTimesSection.add(scrollPane, BorderLayout.CENTER);

        return showTimesSection;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setBackground(BACKGROUND_COLOR);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setBackground(Color.DARK_GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private JPanel createSectionPanel(String title, int height, int widthPercentage) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        int width = (Toolkit.getDefaultToolkit().getScreenSize().width * widthPercentage) / 100;
        panel.setPreferredSize(new Dimension(width, height));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMoviesSection(String url) {
        JPanel moviesSection = new JPanel(new BorderLayout());
        moviesSection.setBackground(BACKGROUND_COLOR);

        JPanel searchBarPanel = createSearchBarPanel(url);
        moviesSection.add(searchBarPanel, BorderLayout.NORTH);

        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBackground(BACKGROUND_COLOR);

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT id, title, age_rating, release_date FROM movies ORDER BY release_date LIMIT 20")) {
            while (resultSet.next()) {
                moviesPanel.add(createMovieEntryPanel(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("age_rating"),
                    resultSet.getString("release_date"),
                    url
                ));
                moviesPanel.setPreferredSize(new Dimension(0, MOVIES_SECTION_HEIGHT));
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            moviesPanel.add(errorLabel);
        }

        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        moviesSection.add(scrollPane, BorderLayout.CENTER);

        return moviesSection;
    }

    private JPanel createSearchBarPanel(String url) {
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setBackground(BACKGROUND_COLOR);
        searchBarPanel.setPreferredSize(new Dimension(0, 45));

        JButton newMovieButton = new JButton("New Movie");
        newMovieButton.setFont(new Font("Arial", Font.BOLD, 12));
        newMovieButton.addActionListener(e -> {
            try {
                _movieAgent movieAgent = new _movieAgent(url, -1, true);
                movieAgent.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error creating new movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        searchBarPanel.add(newMovieButton);

        JTextField searchBar = new JTextField(30);
        searchBar.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBarPanel.add(searchBar);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty()) {
                new _searchMovie(url, query).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a search query.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        searchBar.addActionListener(e -> searchButton.doClick());
        searchBarPanel.add(searchButton);

        return searchBarPanel;
    }

    private JPanel createMovieEntryPanel(int id, String title, String ageRating, String releaseDate, String url) {
        JPanel movieEntryPanel = new JPanel(new BorderLayout());
        movieEntryPanel.setBackground(BACKGROUND_COLOR);
        movieEntryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        buttonPanel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        editButton.setBackground(Color.DARK_GRAY);
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> new _movieAgent(url, id, false).setVisible(true));
        buttonPanel.add(editButton);

        movieEntryPanel.add(buttonPanel, BorderLayout.EAST);

        return movieEntryPanel;
    }
}