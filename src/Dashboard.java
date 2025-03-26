import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Dashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final int MOVIES_SECTION_HEIGHT = 400;

    public Dashboard(String url) {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(new ImageIcon("images/icon.png").getImage());
        setLayout(new GridLayout(4, 1));
        add(createSectionPanel("Dashboard", 80, 80));
        add(createMoviesSection(url));
        add(createSectionPanel("Show Rooms", 40, 80));
        add(createSectionPanel("Show Times", 40, 80));
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

    private JScrollPane createMoviesSection(String url) {
        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBackground(BACKGROUND_COLOR);
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, title, age_rating, release_date FROM movies ORDER BY release_date LIMIT 20")) {
            while (resultSet.next()) {
                moviesPanel.add(createMovieEntryPanel(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("age_rating"),
                        resultSet.getString("release_date"),
                        url));
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading movies: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            moviesPanel.add(errorLabel);
        }
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setPreferredSize(new Dimension(400, MOVIES_SECTION_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
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

        JLabel ageRatingLabel = new JLabel("Age Rating: ");
        ageRatingLabel.setForeground(Color.LIGHT_GRAY);
        ageRatingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel ageRatingValueLabel = new JLabel(ageRating);
        ageRatingValueLabel.setForeground(switch (ageRating) {
            case "PG" -> new Color(102, 255, 102);
            case "PG-13" -> new Color(0, 128, 255);
            case "PG-16" -> Color.ORANGE;
            case "R" -> Color.RED;
            default -> Color.WHITE;
        });
        ageRatingValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JPanel ageRatingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageRatingPanel.setBackground(BACKGROUND_COLOR);
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

        JButton editButton = new JButton("Edit");
        editButton.setBackground(Color.DARK_GRAY);
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> new _movieAgent(url, id).setVisible(true));
        movieEntryPanel.add(editButton, BorderLayout.EAST);

        return movieEntryPanel;
    }
}