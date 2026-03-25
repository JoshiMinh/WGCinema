package com.joshiminh.wgcinema.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowtimeDAO extends BaseDAO {

    public static ResultSet fetchShowtimeDetails(String connectionString, int showtimeId) {
        String sql = "SELECT *, regular_price, vip_price FROM showtimes WHERE showtime_id = ?";
        try {
            ResultSet rs = select(connectionString, sql, showtimeId);
            if (rs != null && rs.next()) {
                try {
                    rs.getString("chairs_selecting");
                    rs.beforeFirst();
                    return rs;
                } catch (SQLException e) {
                    rs.close();
                }
            }
        } catch (SQLException e) {
        }
        return select(connectionString, sql, showtimeId);
    }

    public static String getSelectingSeats(String connectionString, int showtimeId) {
        String sql = "SELECT selected_seats FROM seat_selections WHERE showtime_id = ?";
        StringBuilder allSelectingSeats = new StringBuilder();

        try (ResultSet rs = select(connectionString, sql, showtimeId)) {
            while (rs != null && rs.next()) {
                String seats = rs.getString("selected_seats");
                if (seats != null && !seats.trim().isEmpty()) {
                    if (allSelectingSeats.length() > 0) {
                        allSelectingSeats.append(" ");
                    }
                    allSelectingSeats.append(seats);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting selecting seats: " + e.getMessage());
        }

        return allSelectingSeats.toString();
    }

    public static ResultSet fetchMovieShowtimes(String connectionString, int movieId, String date) {
        String sql = """
         SELECT showtime_id, TIME_FORMAT(time, '%H:%i') AS 'Time (HH:mm)'
         FROM showtimes
         WHERE movie_id = ? AND date = ?
         ORDER BY time
         """;
        return select(connectionString, sql, movieId, date);
    }

    public static ResultSet fetchAllShowtimes(String connectionString) {
        String sql = "SELECT * FROM showtimes";
        return select(connectionString, sql);
    }

    public static int insertShowtime(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO showtimes (" + String.join(", ", columns) + ") VALUES (" +
                "?,".repeat(values.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
    }

    public static int updateShowtimeSeats(String connectionString, int reservedCount, String selectedSeats, int showtimeId) {
        String sql = """
         UPDATE showtimes
         SET reserved_chairs = reserved_chairs + ?, chairs_booked = CONCAT(chairs_booked, ?)
         WHERE showtime_id = ?
         """;
        return update(connectionString, sql, reservedCount, " " + selectedSeats, showtimeId);
    }

    public static int updateShowtimeColumn(String connectionString, String columnName, Object value, Object showtimeId) {
        String sql = "UPDATE showtimes SET " + columnName + " = ? WHERE showtime_id = ?";
        return update(connectionString, sql, value, showtimeId);
    }

    public static int insertSeatSelection(String connectionString, int showtimeId, String userEmail, String selectedSeats) {
        String sql = """
         INSERT INTO seat_selections (showtime_id, user_email, selected_seats)
         VALUES (?, ?, ?)
         ON DUPLICATE KEY UPDATE 
         selected_seats = VALUES(selected_seats),
         selection_timestamp = CURRENT_TIMESTAMP
         """;
        return update(connectionString, sql, showtimeId, userEmail, selectedSeats);
    }

    public static int updateSeatSelection(String connectionString, int showtimeId, String userEmail, String selectedSeats) {
        String updateSql = """
         UPDATE seat_selections 
         SET selected_seats = ?, selection_timestamp = CURRENT_TIMESTAMP
         WHERE showtime_id = ? AND user_email = ?
         """;
        int updated = update(connectionString, updateSql, selectedSeats, showtimeId, userEmail);

        if (updated == 0) {
            return insertSeatSelection(connectionString, showtimeId, userEmail, selectedSeats);
        }
        return updated;
    }

    public static int deleteSeatSelection(String connectionString, int showtimeId, String userEmail) {
        String sql = "DELETE FROM seat_selections WHERE showtime_id = ? AND user_email = ?";
        return update(connectionString, sql, showtimeId, userEmail);
    }

    public static int deleteAllSeatSelections(String connectionString, int showtimeId) {
        String sql = "DELETE FROM seat_selections WHERE showtime_id = ?";
        return update(connectionString, sql, showtimeId);
    }
}
