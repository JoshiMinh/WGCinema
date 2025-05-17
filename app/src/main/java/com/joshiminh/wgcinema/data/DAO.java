package com.joshiminh.wgcinema.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    // ==========================
    // SELECT Operations
    // ==========================

    // Movie-related SELECT operations
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

    public static ResultSet fetchAgeRatingCounts(String connectionString) {
        String sql = "SELECT age_rating, COUNT(*) AS count FROM movies GROUP BY age_rating";
        return select(connectionString, sql);
    }

    public static ResultSet fetchLanguageCounts(String connectionString) {
        String sql = "SELECT language, COUNT(*) AS count FROM movies GROUP BY language";
        return select(connectionString, sql);
    }

    public static ResultSet searchMoviesByTitle(String connectionString, String titleQuery) {
        String sql = "SELECT id, title, age_rating, release_date FROM movies WHERE title LIKE ? ORDER BY release_date LIMIT 20";
        return select(connectionString, sql, "%" + titleQuery + "%");
    }

    // Showtime-related SELECT operations
    public static ResultSet fetchShowtimeDetails(String connectionString, int showtimeId) {
        String sql = "SELECT * FROM showtimes WHERE showtime_id = ?";
        return select(connectionString, sql, showtimeId);
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

    // Showroom-related SELECT operations
    public static ResultSet fetchShowroomDetails(String connectionString, int showroomId) {
        String sql = "SELECT * FROM showrooms WHERE showroom_id = ?";
        return select(connectionString, sql, showroomId);
    }

    public static ResultSet fetchAllShowrooms(String connectionString) {
        String sql = "SELECT * FROM showrooms";
        return select(connectionString, sql);
    }

    // Account-related SELECT operations
    public static ResultSet fetchAccountByEmail(String connectionString, String email) {
        String sql = "SELECT * FROM accounts WHERE account_email = ?";
        return select(connectionString, sql, email);
    }

    // Utility SELECT operations
    public static String[] getColumnNames(String connectionString, String table) {
        try (Connection con = DriverManager.getConnection(connectionString)) {
            ResultSet rs = con.getMetaData().getColumns(null, null, table, null);
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("COLUMN_NAME"));
            }
            return names.toArray(new String[0]);
        } catch (SQLException e) {
            return new String[0];
        }
    }

    // ==========================
    // INSERT Operations
    // ==========================

    public static int insertTransaction(String connectionString, int movieId, String totalPrice, String selectedSeats, int showroomId, String accountEmail, int showtimeId) {
        String sql = """
            INSERT INTO transactions (movie_id, amount, seats_preserved, showroom_id, account_email, showtime_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        return update(
            connectionString,
            sql,
            movieId,
            new java.math.BigDecimal(totalPrice.replaceAll("[^\\d.]", "")),
            selectedSeats,
            showroomId,
            accountEmail,
            showtimeId
        );
    }

    public static int insertMovie(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO movies (" + String.join(", ", columns) + ") VALUES (" +
                     "?,".repeat(values.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
    }

    public static int insertShowtime(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO showtimes (" + String.join(", ", columns) + ") VALUES (" +
                     "?,".repeat(values.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
    }

    public static int insertShowroom(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO showrooms (" + String.join(", ", columns) + ") VALUES (" +
                     "?,".repeat(columns.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
    }

    // ==========================
    // UPDATE Operations
    // ==========================

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

    public static int updateShowroomColumn(String connectionString, String columnName, Object value, Object showroomId) {
        String sql = "UPDATE showrooms SET " + columnName + " = ? WHERE showroom_id = ?";
        return update(connectionString, sql, value, showroomId);
    }

    public static int updateMovieById(String connectionString, String[] columns, String[] values, int movieId) {
        StringBuilder sql = new StringBuilder("UPDATE movies SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(" WHERE id = ?");
        Object[] params = new Object[values.length + 1];
        System.arraycopy(values, 0, params, 0, values.length);
        params[values.length] = movieId;
        return update(connectionString, sql.toString(), params);
    }

    public static int updateAccountPassword(String connectionString, String email, String newHashedPassword) {
        String sql = "UPDATE accounts SET password_hash = ? WHERE account_email = ?";
        return update(connectionString, sql, newHashedPassword, email);
    }

    // ==========================
    // DELETE Operations
    // ==========================

    public static int deleteRowById(String connectionString, String tableName, String primaryKeyColumn, int id) {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        return update(connectionString, sql, id);
    }

    // ==========================
    // Utility Methods
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
        try (
            Connection connection = DriverManager.getConnection(connectionString);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
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