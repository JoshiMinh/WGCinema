package com.joshiminh.wgcinema.data;

import java.sql.*;

public class DAO {

    public static ResultSet fetchMovieShowtimes(String connectionString, int movieId, String date) {
        String sql = "SELECT showtime_id, TIME_FORMAT(time, '%H:%i') AS 'Time (HH:mm)' " +
                     "FROM showtimes WHERE movie_id = ? AND date = ? ORDER BY time";
        return select(connectionString, sql, movieId, date);
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
}