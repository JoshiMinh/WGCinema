import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

// Booking class for movie tickets
public class Booking extends JFrame {
    private final int movieId;
    private final String password;
    private final Color bgColor = new Color(30, 30, 30);

    public Booking(int movieId, String pwd) {
        this.movieId = movieId;
        this.password = pwd;

        setTitle("Booking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("Icons/WGLogo.png").getImage());

        JTabbedPane tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Create tabbed pane with showtimes for the next 7 days
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(bgColor);
        tabbedPane.setForeground(Color.WHITE);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            JPanel dayPanel = createDayPanel(calendar.getTime(), i == 0);
            String dayLabel = (i == 0) ? "Today" : calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.getDefault());
            tabbedPane.addTab(dayLabel, dayPanel);
            calendar.add(Calendar.DATE, 1);
        }
        return tabbedPane;
    }

    // Create panel for a specific day's showtimes
    private JPanel createDayPanel(Date date, boolean isToday) {
        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dayPanel.setBackground(bgColor);

        JPanel showtimesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        showtimesPanel.setBackground(bgColor);
        loadShowtimes(showtimesPanel, date, isToday);

        dayPanel.add(showtimesPanel);
        return dayPanel;
    }

    // Load and display showtimes for a given date
    private void loadShowtimes(JPanel showtimesPanel, Date date, boolean isToday) {
        String url = "jdbc:sqlserver://JoshiNitro5\\MSSQLSERVER02:1433;database=CinemaData";
        String username = "AdminCinema";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT ShowtimeID, CONVERT(VARCHAR(5), Time, 108) AS 'Time (HH:mm)' " +
                     "FROM Showtimes WHERE MovieId = ? AND Date = ? ORDER BY Time")) {

            preparedStatement.setInt(1, movieId);
            preparedStatement.setString(2, dateFormat.format(date));

            ResultSet resultSet = preparedStatement.executeQuery();
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonPanel.setBackground(bgColor);

            Date currentTime = Calendar.getInstance().getTime();
            while (resultSet.next()) {
                String time = resultSet.getString("Time (HH:mm)");
                Date showTimeDate = timeFormat.parse(time);

                if (isToday && showTimeDate.before(currentTime)) {
                    continue; // Skip past showtimes for today
                }

                JButton showtimeButton = createShowtimeButton(time, resultSet.getInt("ShowtimeID"));
                buttonPanel.add(showtimeButton);
            }
            showtimesPanel.add(buttonPanel);
        } catch (SQLException | java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    // Create a button for a specific showtime
    private JButton createShowtimeButton(String time, int showtimeId) {
        JButton showtimeButton = new JButton(time);
        showtimeButton.setForeground(Color.WHITE);
        showtimeButton.setBackground(Color.DARK_GRAY);
        showtimeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        showtimeButton.addActionListener(e -> new Showrooms(password, showtimeId)); // Assuming Showrooms class exists
        return showtimeButton;
    }
}