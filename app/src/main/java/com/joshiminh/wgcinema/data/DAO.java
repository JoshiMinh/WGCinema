package com.joshiminh.wgcinema.data;

import java.sql.*;

public class DAO {

    // ==========================
    // Movie-related operations
    // ==========================

    public static ResultSet fetchMovieDetails(String connectionString, int movieId) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        return select(connectionString, sql, movieId);
    }

    public static ResultSet fetchAllMovies(String connectionString) {
        String sql = "SELECT id, title, age_rating, release_date FROM movies ORDER BY release_date";
        return select(connectionString, sql);
    }

    public static ResultSet fetchUpcomingMovies(String connectionString) {
        String sql = """
            SELECT *
            FROM movies
            WHERE release_date >= CURDATE()
            ORDER BY release_date ASC
            """;
        return select(connectionString, sql);
    }

    public static int deleteMovieById(String connectionString, int movieId) {
        String sql = "DELETE FROM movies WHERE id = ?";
        return update(connectionString, sql, movieId);
    }

    public static ResultSet fetchAgeRatingCounts(String connectionString) {
        String sql = "SELECT age_rating, COUNT(*) AS count FROM movies GROUP BY age_rating";
        return select(connectionString, sql);
    }

    public static ResultSet fetchLanguageCounts(String connectionString) {
        String sql = "SELECT language, COUNT(*) AS count FROM movies GROUP BY language";
        return select(connectionString, sql);
    }

    // ==========================
    // Showtime-related operations
    // ==========================

    public static ResultSet fetchShowtimeDetails(String connectionString, int showtimeId) {
        String sql = "SELECT * FROM showtimes WHERE showtime_id = ?";
        return select(connectionString, sql, showtimeId);
    }

    public static ResultSet fetchMovieShowtimes(String connectionString, int movieId, String date) {
        String sql = "SELECT showtime_id, TIME_FORMAT(time, '%H:%i') AS 'Time (HH:mm)' " +
                     "FROM showtimes WHERE movie_id = ? AND date = ? ORDER BY time";
        return select(connectionString, sql, movieId, date);
    }

    public static ResultSet fetchAllShowtimes(String connectionString) {
        String sql = "SELECT * FROM showtimes";
        return select(connectionString, sql);
    }

    public static int updateShowtimeSeats(String connectionString, int reservedCount, String selectedSeats, int showtimeId) {
        String sql = "UPDATE showtimes SET reserved_chairs = reserved_chairs + ?, chairs_booked = CONCAT(chairs_booked, ?) WHERE showtime_id = ?";
        return update(connectionString, sql, reservedCount, " " + selectedSeats, showtimeId);
    }

    public static int updateShowtimeColumn(String connectionString, String columnName, Object value, Object showtimeId) {
        String sql = "UPDATE showtimes SET " + columnName + " = ? WHERE showtime_id = ?";
        return update(connectionString, sql, value, showtimeId);
    }

    // ==========================
    // Showroom-related operations
    // ==========================

    public static ResultSet fetchShowroomDetails(String connectionString, int showroomId) {
        String sql = "SELECT * FROM showrooms WHERE showroom_id = ?";
        return select(connectionString, sql, showroomId);
    }

    public static ResultSet fetchAllShowrooms(String connectionString) {
        String sql = "SELECT * FROM showrooms";
        return select(connectionString, sql);
    }

    public static int updateShowroomColumn(String connectionString, String columnName, Object value, Object showroomId) {
        String sql = "UPDATE showrooms SET " + columnName + " = ? WHERE showroom_id = ?";
        return update(connectionString, sql, value, showroomId);
    }

    // ==========================
    // Transaction-related operations
    // ==========================

    public static int insertTransaction(String connectionString, int movieId, String totalPrice, String selectedSeats, int showroomId, String accountEmail, int showtimeId) {
        String sql = "INSERT INTO transactions (movie_id, amount, seats_preserved, showroom_id, account_email, showtime_id) VALUES (?, ?, ?, ?, ?, ?)";
        return update(connectionString, sql, movieId, new java.math.BigDecimal(totalPrice.replaceAll("[^\\d.]", "")), selectedSeats, showroomId, accountEmail, showtimeId);
    }

    // ==========================
    // Utility methods
    // ==========================

    private static ResultSet select(String connectionString, String sql, Object... params) {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int update(String connectionString, String sql, Object... params) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}