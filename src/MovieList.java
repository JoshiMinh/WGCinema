import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import javax.swing.*;

public class MovieList extends JFrame {
    private JPanel moviePanel;
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private String connectionString;

    public MovieList(String connectionString) {
        this.connectionString = connectionString;
        setupFrame();
        setupTopBar();
        setupMoviePanel();
        loadMovieList();
    }

    private void setupFrame() {
        setTitle("Movie List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(new ImageIcon("icons/appIcon.png").getImage());
    }

    private void setupTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 20));
        topBar.setBackground(Color.DARK_GRAY);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(topBar, BorderLayout.NORTH);

        JLabel showingMoviesLabel = new JLabel("Showing Movies");
        showingMoviesLabel.setForeground(Color.WHITE);
        showingMoviesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topBar.add(showingMoviesLabel, BorderLayout.WEST);

        JLabel adminLoginLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("icons/appIcon.png");
        Image image = imageIcon.getImage().getScaledInstance(55, 50, Image.SCALE_SMOOTH);
        adminLoginLabel.setIcon(new ImageIcon(image));
        topBar.add(adminLoginLabel, BorderLayout.EAST);
    }

    private void setupMoviePanel() {
        moviePanel = new JPanel(new GridLayout(0, 3, 10, 20));
        moviePanel.setBackground(BACKGROUND_COLOR);
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMovieList() {
        String query = """
            SELECT id, poster, title, age_rating 
            FROM movies
            WHERE release_date >= CURDATE()
            ORDER BY release_date ASC
            """;
        try (Connection connection = DriverManager.getConnection(connectionString);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int movieId = resultSet.getInt("id");
                String imageLink = resultSet.getString("poster");
                String title = resultSet.getString("title");
                String rating = resultSet.getString("age_rating");

                JPanel moviePanel = createMoviePanel(movieId, imageLink, title, rating);
                this.moviePanel.add(moviePanel);
            }
        } catch (SQLException | java.net.MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createMoviePanel(int movieId, String imageLink, String title, String rating) throws java.net.MalformedURLException {
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBackground(BACKGROUND_COLOR);

        JLabel imageLabel = new JLabel(new ImageIcon(new ImageIcon(new URL(imageLink)).getImage().getScaledInstance(180, 270, Image.SCALE_SMOOTH)));
        moviePanel.add(imageLabel, BorderLayout.NORTH);

        JPanel titlePanel = createTitlePanel(title, rating);
        moviePanel.add(titlePanel, BorderLayout.CENTER);

        JButton bookTicketButton = new JButton("Book Ticket");
        bookTicketButton.setFont(new Font("Arial", Font.PLAIN, 14));
        bookTicketButton.setForeground(Color.WHITE);
        bookTicketButton.setBackground(Color.DARK_GRAY);
        bookTicketButton.setPreferredSize(new Dimension(10, 30));
        bookTicketButton.addActionListener(new BookingButtonListener(movieId));
        moviePanel.add(bookTicketButton, BorderLayout.SOUTH);

        return moviePanel;
    }

    private JPanel createTitlePanel(String title, String rating) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        Color ratingColor = switch (rating) {
            case "PG" -> new Color(102, 255, 102);
            case "PG-13" -> new Color(0, 128, 255);
            case "PG-16" -> Color.ORANGE;
            case "R" -> Color.RED;
            default -> Color.WHITE;
        };
        JLabel ratingLabel = new JLabel(rating);
        ratingLabel.setForeground(ratingColor);
        ratingLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(ratingLabel, BorderLayout.SOUTH);

        return titlePanel;
    }

    private class BookingButtonListener implements ActionListener {
        private final int movieId;

        public BookingButtonListener(int movieId) {
            this.movieId = movieId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Booking booking = new Booking(movieId, connectionString);
            booking.setVisible(true);
        }
    }
}