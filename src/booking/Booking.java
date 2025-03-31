package booking;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

public class Booking extends JFrame {
    private final int movieId;
    private final Color bgColor = new Color(30, 30, 30);
    private String connectionString;

    public Booking(int movieId, String connectionString) {
        this.movieId = movieId;
        this.connectionString = connectionString;
        setTitle("Booking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("images/icon.png").getImage());
        add(createTabbedPane(), BorderLayout.CENTER);
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(bgColor);
        tabbedPane.setForeground(Color.WHITE);
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            tabbedPane.addTab(i == 0 ? "Today" : calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.getDefault()), createDayPanel(calendar.getTime(), i == 0));
            calendar.add(Calendar.DATE, 1);
        }
        return tabbedPane;
    }

    private JPanel createDayPanel(Date date, boolean isToday) {
        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dayPanel.setBackground(bgColor);
        JPanel showtimesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        showtimesPanel.setBackground(bgColor);
        loadShowtimes(showtimesPanel, date, isToday);
        dayPanel.add(showtimesPanel);
        return dayPanel;
    }

    private void loadShowtimes(JPanel showtimesPanel, Date date, boolean isToday) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT showtime_id, TIME_FORMAT(time, '%H:%i') AS 'Time (HH:mm)' " +
                             "FROM showtimes WHERE movie_id = ? AND date = ? ORDER BY time")) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setString(2, dateFormat.format(date));
            ResultSet resultSet = preparedStatement.executeQuery();
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonPanel.setBackground(bgColor);
            Date currentTime = Calendar.getInstance().getTime();
            while (resultSet.next()) {
                String time = resultSet.getString("Time (HH:mm)");
                Date showTimeDate = timeFormat.parse(time);
                if (isToday && showTimeDate.before(currentTime)) continue;
                buttonPanel.add(createShowtimeButton(time, resultSet.getInt("showtime_id")));
            }
            showtimesPanel.add(buttonPanel);
        } catch (SQLException | java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    private JButton createShowtimeButton(String time, int showtimeId) {
        JButton showtimeButton = new JButton(time);
        showtimeButton.setForeground(Color.WHITE);
        showtimeButton.setBackground(Color.DARK_GRAY);
        showtimeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        showtimeButton.addActionListener(e -> SwingUtilities.invokeLater(() -> new Showrooms(connectionString, showtimeId)));
        return showtimeButton;
    }
}