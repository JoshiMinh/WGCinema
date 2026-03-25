package com.joshiminh.wgcinema.dao;

import java.sql.ResultSet;

public class MovieDAO extends BaseDAO {

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
         SELECT id, poster, title, age_rating, director, duration
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
        String sql = "SELECT id, poster, title, age_rating, director, duration FROM movies WHERE title LIKE ? ORDER BY release_date LIMIT 20";
        return select(connectionString, sql, "%" + titleQuery + "%");
    }

    public static int insertMovie(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO movies (" + String.join(", ", columns) + ") VALUES (" +
                "?,".repeat(values.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
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
}
