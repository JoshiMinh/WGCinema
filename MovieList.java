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
        setIconImage(new ImageIcon("Icons/WGLogo.png").getImage());
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
        ImageIcon imageIcon = new ImageIcon("Icons/WGLogo.png");
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
                SELECT M.id, M.Link, M.Title, M.Rating 
                FROM (SELECT MovieId 
                      FROM Showtimes 
                      WHERE Date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 6 DAY) 
                      GROUP BY MovieId) AS S 
                INNER JOIN MovieInfo AS M ON S.MovieId = M.id
                """;
        
        try (Connection connection = DriverManager.getConnection(connectionString);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int movieId = resultSet.getInt("id");
                String imageLink = resultSet.getString("Link");
                String title = resultSet.getString("Title");
                String rating = resultSet.getString("Rating");

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
            case "P" -> new Color(50, 220, 100);
            case "K" -> Color.BLUE;
            case "T13" -> Color.YELLOW;
            case "T16" -> Color.ORANGE;
            case "T18" -> Color.RED;
            default -> Color.WHITE;
        };
        JLabel ratingLabel = new JLabel(rating);
        ratingLabel.setForeground(ratingColor);

        JPanel combinedTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        combinedTitlePanel.setBackground(BACKGROUND_COLOR);
        combinedTitlePanel.add(titleLabel);
        combinedTitlePanel.add(ratingLabel);

        titlePanel.add(combinedTitlePanel, BorderLayout.CENTER);

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