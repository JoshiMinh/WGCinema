import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.border.Border;

public class Showrooms extends JFrame {
    private static final int WIDTH = 1900, HEIGHT = 900, GAP = 3, MAX_SELECTIONS = 8;
    private static int ROWS, COLS, CELL_SIZE, sideWidths;
    private JPanel gridPanel;
    private JLabel infoLabel;
    private final Set<JPanel> selectedCells = new HashSet<>();
    private String connectionString;
    private int showtimeID, showroomID, movieId;
    private String chairsBooked = "", movieTitle, movieRating, movieLink;
    private Time time;
    private java.sql.Date date;

    public Showrooms(String connectionString, int showtimeID) {
        this.showtimeID = showtimeID;
        this.connectionString = connectionString;
        setTitle("Select Seats");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setIconImage(new ImageIcon("images/icon.png").getImage());
        showroomID = getShowroomID(showtimeID);
        fetchMovieInfo();
        setDimensions(showroomID);
        gridPanel = new JPanel(new GridLayout(ROWS, COLS, GAP, GAP));
        gridPanel.setBackground(new Color(30, 30, 30));
        createGridOfBoxes(chairsBooked);
        int gridPanelHeight = gridPanel.getPreferredSize().height;
        JPanel topPanel = createTopPanel(gridPanelHeight);
        JPanel leftPanel = createSidePanel(gridPanelHeight, sideWidths);
        JPanel rightPanel = createSidePanel(gridPanelHeight, sideWidths);
        JPanel bottomInfoPanel = createBottomInfoPanel();
        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(gridPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomInfoPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private int getShowroomID(int showtimeID) {
        String query = "SELECT showroom_id, chairs_booked, time, movie_id, date FROM showtimes WHERE showtime_id = ?";
        try (Connection conn = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    date = rs.getDate("date");
                    chairsBooked = rs.getString("chairs_booked");
                    time = rs.getTime("time");
                    movieId = rs.getInt("movie_id");
                    return rs.getInt("showroom_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private void fetchMovieInfo() {
        String query = "SELECT title, age_rating, poster FROM movies WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    movieTitle = rs.getString("title");
                    movieRating = rs.getString("age_rating");
                    movieLink = rs.getString("poster");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDimensions(int showroomID) {
        switch (showroomID) {
            case 1 -> {
                CELL_SIZE = 60;
                sideWidths = 500;
                ROWS = 10;
                COLS = 10;
            }
            case 2, 3 -> {
                CELL_SIZE = 60;
                sideWidths = 225;
                ROWS = 10;
                COLS = 20;
            }
            case 4 -> {
                CELL_SIZE = 50;
                sideWidths = 175;
                ROWS = 12;
                COLS = 25;
            }
            default -> {
                CELL_SIZE = 60;
                sideWidths = 500;
                ROWS = 10;
                COLS = 10;
            }
        }
    }

    private JPanel createTopPanel(int gridPanelHeight) {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT - gridPanelHeight - 140));
        JLabel screenLabel = new JLabel("SCREEN");
        screenLabel.setFont(new Font(screenLabel.getFont().getName(), Font.BOLD, 35));
        screenLabel.setForeground(new Color(60, 60, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(screenLabel, gbc);
        return topPanel;
    }

    private JPanel createSidePanel(int height, int width) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    private JPanel createBottomInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(35, 35, 35));
        panel.setPreferredSize(new Dimension(WIDTH, 140));
        Border topBorder = BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE);
        panel.setBorder(topBorder);
        JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        westPanel.setOpaque(false);
        ImageIcon posterIcon = null;
        try {
            posterIcon = new ImageIcon(new URL(movieLink));
            Image img = posterIcon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            posterIcon = new ImageIcon(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JLabel posterLabel = new JLabel(posterIcon);
        westPanel.add(posterLabel);
        panel.add(westPanel, BorderLayout.WEST);
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 2.0;
        JPanel movieInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        movieInfoPanel.setOpaque(false);
        JLabel movieTitleLabel = new JLabel(movieTitle);
        movieTitleLabel.setForeground(Color.GRAY);
        movieInfoPanel.add(movieTitleLabel);
        JLabel movieRatingLabel = new JLabel(" " + movieRating);
        movieRatingLabel.setForeground(age_rating_color.getColorForRating(movieRating));
        movieInfoPanel.add(movieRatingLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(movieInfoPanel, gbc);
        JLabel showroomIDLabel = new JLabel("Showroom " + showroomID);
        showroomIDLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(showroomIDLabel, gbc);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(time);
        JLabel timeLabel = new JLabel("Time: " + formattedTime);
        timeLabel.setForeground(Color.WHITE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(this.date);
        gbc.gridy = 2;
        centerPanel.add(timeLabel, gbc);
        JLabel dateLabel = new JLabel("Date: " + formattedDate);
        dateLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(dateLabel, gbc);
        infoLabel = new JLabel(updateMessage());
        infoLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(infoLabel, gbc);
        panel.add(centerPanel, BorderLayout.CENTER);
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(160, 140));
        JButton bookButton = new JButton("Check Out");
        bookButton.setPreferredSize(new Dimension(120, 40));
        bookButton.setFont(new Font("Arial", Font.BOLD, 17));
        bookButton.setBackground(new Color(51, 255, 102));
        bookButton.setForeground(Color.WHITE);
        bookButton.addActionListener(e -> {
            String selectedSeats = selectedCells.stream()
                    .map(cell -> ((JLabel) cell.getComponent(0)).getText())
                    .collect(Collectors.joining(", "));
            if (selectedCells.isEmpty()) {
                JOptionPane.showMessageDialog(Showrooms.this, "Please select at least one seat.", "No Seats Selected", JOptionPane.ERROR_MESSAGE);
            } else {
                new Checkout(connectionString, showroomID, time, movieId, date, movieTitle, movieRating, movieLink, showtimeID, selectedSeats, this);
            }
        });
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.gridx = 0;
        gbcButton.gridy = 0;
        gbcButton.anchor = GridBagConstraints.CENTER;
        gbcButton.insets = new Insets(0, 0, 0, 0);
        eastPanel.add(bookButton, gbcButton);
        panel.add(eastPanel, BorderLayout.EAST);
        return panel;
    }

    private String updateMessage() {
        StringBuilder message = new StringBuilder("No seats selected");
        if (!selectedCells.isEmpty()) {
            ArrayList<String> selectedSeatsList = new ArrayList<>();
            for (JPanel cell : selectedCells) {
                selectedSeatsList.add(((JLabel) cell.getComponent(0)).getText());
            }
            String selectedSeatsString = String.join(", ", selectedSeatsList);
            String sortedSelectedSeats = sortSelectedSeats(selectedSeatsString);
            message = new StringBuilder("You have selected " + selectedCells.size() + " seats: ");
            message.append(sortedSelectedSeats);
        }
        return message.toString();
    }

    public static String sortSelectedSeats(String selectedSeats) {
        String[] seats = selectedSeats.split(", ");
        Arrays.sort(seats, (s1, s2) -> {
            char row1 = s1.charAt(0);
            char row2 = s2.charAt(0);
            if (row1 != row2) {
                return Character.compare(row1, row2);
            } else {
                int number1 = Integer.parseInt(s1.substring(1));
                int number2 = Integer.parseInt(s2.substring(1));
                return Integer.compare(number1, number2);
            }
        });
        return String.join(", ", seats);
    }

    private boolean isSeatBooked(String seat) {
        String pattern = "\\b" + seat + "\\b";
        return chairsBooked.matches(".*" + pattern + ".*");
    }

    private void createGridOfBoxes(String chairsBooked) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JPanel box = new JPanel();
                String seatLabel = getBoxLabel(row, col);
                boolean isBooked = isSeatBooked(seatLabel);
                Color vipBookedColor = new Color(51, 0, 51);
                Color regularBookedColor = new Color(160, 160, 160);
                Color bookedColor = isBooked ? (isVIPRow(row) ? vipBookedColor : regularBookedColor) :
                        (isVIPRow(row) ? new Color(128, 0, 128) : Color.LIGHT_GRAY);
                box.setBackground(bookedColor);
                box.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                JLabel label = new JLabel(seatLabel);
                label.setForeground(isBooked ? Color.GRAY : Color.WHITE);
                label.setHorizontalAlignment(JLabel.CENTER);
                box.add(label);
                if (!isBooked) {
                    box.addMouseListener(new BoxClickListener(box));
                }
                gridPanel.add(box);
            }
        }
    }

    private boolean isVIPRow(int row) {
        return row >= 6 && row <= 11;
    }

    private String getBoxLabel(int row, int col) {
        char rowChar = (char) ('A' + row);
        int colNum = COLS - col;
        return rowChar + String.valueOf(colNum);
    }

    private class BoxClickListener extends MouseAdapter {
        private final JPanel box;

        BoxClickListener(JPanel box) {
            this.box = box;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedCells.contains(box)) {
                selectedCells.remove(box);
                box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            } else if (selectedCells.size() < MAX_SELECTIONS) {
                selectedCells.add(box);
                box.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                JOptionPane.showMessageDialog(Showrooms.this, "You have selected the maximum number of seats (" + MAX_SELECTIONS + ").", "Selection Limit Reached", JOptionPane.WARNING_MESSAGE);
            }
            infoLabel.setText(updateMessage());
        }
    }

    public void restartShowrooms() {
        dispose();
        Showrooms newShowroomsFrame = new Showrooms(connectionString, showtimeID);
        newShowroomsFrame.setVisible(true);
    }
}