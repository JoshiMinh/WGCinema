import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Checkout extends JFrame {
    private static final int WIDTH = 410, HEIGHT = 700;
    private final JLabel selectedSeatsLabel;
    private int showtimeID, showroomID, movieId;
    private Showrooms showroomsFrame;
    private boolean bookingSuccessful = false;
    private String connectionString;
    private static final int REGULAR_SEAT_PRICE = 80000, VIP_SEAT_PRICE = 85000;

    public Checkout(String connectionString, int showroomID, Time time, int movieId, Date date, String movieTitle, String movieRating, String movieLink, int showtimeID, String selectedSeats, Showrooms showroomsFrame) {
        this.showtimeID = showtimeID;
        this.showroomsFrame = showroomsFrame;
        this.showroomID = showroomID;
        this.movieId = movieId;
        this.connectionString = connectionString;

        setTitle("Checkout");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setBackground(new Color(30, 30, 30));
        setIconImage(new ImageIcon("icons/cards.png").getImage());

        JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.setBackground(new Color(30, 30, 30));
        northPanel.setPreferredSize(new Dimension(WIDTH, 130));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel moviePosterLabel = new JLabel();
        try {
            URL moviePosterUrl = new URL(movieLink);
            ImageIcon moviePosterIcon = new ImageIcon(moviePosterUrl);
            Image scaledMoviePosterImage = moviePosterIcon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            moviePosterLabel.setIcon(new ImageIcon(scaledMoviePosterImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        northPanel.add(moviePosterLabel, gbc);
        gbc.gridheight = 1;

        JLabel movieLabel = new JLabel(movieTitle);
        movieLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        northPanel.add(movieLabel, gbc);

        Color ratingColor;
        switch (movieRating) {
            case "P" -> ratingColor = new Color(50, 220, 100);
            case "K" -> ratingColor = Color.BLUE;
            case "T13" -> ratingColor = Color.YELLOW;
            case "T16" -> ratingColor = Color.ORANGE;
            case "T18" -> ratingColor = Color.RED;
            default -> ratingColor = Color.WHITE;
        }
        JLabel ratingLabel = new JLabel(movieRating);
        ratingLabel.setForeground(ratingColor);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        northPanel.add(ratingLabel, gbc);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        JLabel dateLabel = new JLabel("Date: " + dateFormat.format(date));
        dateLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        northPanel.add(dateLabel, gbc);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        JLabel timeLabel = new JLabel("Time: " + timeFormat.format(time));
        timeLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        northPanel.add(timeLabel, gbc);

        JLabel showroomLabel = new JLabel("Showroom " + showroomID);
        showroomLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        northPanel.add(showroomLabel, gbc);

        selectedSeatsLabel = new JLabel("Selected Seats: " + selectedSeats);
        selectedSeatsLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        northPanel.add(selectedSeatsLabel, gbc);
        add(northPanel, BorderLayout.NORTH);

        JButton bookButton = new JButton("CONFIRM");
        bookButton.setForeground(Color.WHITE);
        bookButton.setBackground(Color.BLUE);
        bookButton.setFont(bookButton.getFont().deriveFont(Font.BOLD, 25));
        bookButton.addActionListener(e -> book());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setPreferredSize(new Dimension(50, 60));
        southPanel.add(bookButton, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        JPanel northCenterPanel = new JPanel();
        northCenterPanel.setBackground(new Color(51, 51, 51));
        northCenterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel priceLabel = new JLabel("Total Price: " + calculateTotalPrice(selectedSeats));
        priceLabel.setForeground(Color.GREEN);
        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD, 15));
        northCenterPanel.add(priceLabel);
        innerPanel.add(northCenterPanel, BorderLayout.NORTH);

        ImageIcon qrIcon = new ImageIcon("images/QR.jpg");
        JLabel qrLabel = new JLabel(qrIcon);
        innerPanel.add(qrLabel, BorderLayout.CENTER);

        JPanel southCenterPanel = new JPanel();
        southCenterPanel.setBackground(new Color(51, 51, 51));
        southCenterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel scanLabel = new JLabel("SCAN TO PAY");
        scanLabel.setForeground(Color.GREEN);
        scanLabel.setFont(scanLabel.getFont().deriveFont(Font.BOLD, 20));
        southCenterPanel.add(scanLabel);
        innerPanel.add(southCenterPanel, BorderLayout.SOUTH);

        add(innerPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (bookingSuccessful) {
                    showroomsFrame.dispose();
                    Showrooms newShowroomsFrame = new Showrooms(connectionString, showtimeID);
                    newShowroomsFrame.setVisible(true);
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static boolean checkBooked(String chairsBooked, String selectedSeats) {
        String[] bookedSeats = chairsBooked.split(" ");
        String[] selectedSeatArray = selectedSeats.split(" ");
        for (String selectedSeat : selectedSeatArray) {
            for (String bookedSeat : bookedSeats) {
                if (selectedSeat.equals(bookedSeat)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String calculateTotalPrice(String selectedSeats) {
        int totalPrice = 0;
        String[] seats = selectedSeats.split(", ");
        for (String seat : seats) {
            char row = seat.charAt(0);
            if (row >= 'A' && row <= 'F') {
                totalPrice += REGULAR_SEAT_PRICE;
            } else if (row >= 'G' && row <= 'L') {
                totalPrice += VIP_SEAT_PRICE;
            }
        }
        return formatPrice(totalPrice);
    }

    private static String formatPrice(int price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return numberFormat.format(price) + "vnđ";
    }

    private void book() {
        try {
            String selectedSeats = selectedSeatsLabel.getText().substring("Selected Seats: ".length()).replaceAll(",", "");
            Connection conn = DriverManager.getConnection(connectionString);
            String chairsQuery = "SELECT Chairs_Booked FROM Showtimes WHERE ShowtimeID = ?";
            PreparedStatement chairsStatement = conn.prepareStatement(chairsQuery);
            chairsStatement.setInt(1, showtimeID);
            ResultSet chairsResult = chairsStatement.executeQuery();

            if (chairsResult.next()) {
                String chairsBooked = chairsResult.getString("Chairs_Booked");
                if (checkBooked(chairsBooked, selectedSeats)) {
                    JOptionPane.showMessageDialog(this, "Some selected seats are already booked!", "Error", JOptionPane.ERROR_MESSAGE);
                    chairsResult.close();
                    chairsStatement.close();
                    conn.close();
                    return;
                }
            } else {
                System.out.println("Showtime not found!");
                chairsResult.close();
                chairsStatement.close();
                conn.close();
                return;
            }

            chairsResult.close();
            chairsStatement.close();

            String updateQuery = "UPDATE Showtimes SET ReservedChairs = ReservedChairs + ?, Chairs_Booked = CONCAT(Chairs_Booked, ?) WHERE ShowtimeID = ?";
            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
            updateStatement.setInt(1, selectedSeats.split(" ").length);
            updateStatement.setString(2, " " + selectedSeats);
            updateStatement.setInt(3, showtimeID);

            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Booking Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showSuccessImage();
                bookingSuccessful = true;
                insertTransactionData(conn, selectedSeats, calculateTotalPrice(selectedSeats));
            } else {
                System.out.println("Booking failed!");
            }

            updateStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertTransactionData(Connection conn, String selectedSeats, String totalPrice) throws SQLException {
        String insertQuery = "INSERT INTO Transactions (TransactionDate, MovieId, Amount, SeatsPreserved, ShowroomID, account_email, ShowtimeID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
        insertStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
        insertStatement.setInt(2, movieId);
        insertStatement.setDouble(3, Double.parseDouble(totalPrice.replaceAll("[^\\d.]", "")));
        insertStatement.setString(4, selectedSeats);
        insertStatement.setInt(5, showroomID);
        insertStatement.setString(6, "Admin");
        insertStatement.setInt(7, showtimeID);
        insertStatement.executeUpdate();
        insertStatement.close();
    }

    private void showSuccessImage() {
        getContentPane().removeAll();
        revalidate();
        repaint();
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(30, 30, 30));
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("images/TicketBooked.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        centerPanel.add(imageLabel, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(30, 30, 30));
        revalidate();
        repaint();
    }
}