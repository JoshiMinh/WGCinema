package com.joshiminh.wgcinema.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class TransactionDAO extends BaseDAO {

    public static ResultSet fetchTransactionHistory(String connectionString, String email) {
        String sql = "SELECT * FROM transactions WHERE account_email = ? ORDER BY transaction_date DESC";
        return select(connectionString, sql, email);
    }

    public static ResultSet fetchEmployeeTransactions(String connectionString, String employeeEmail) {
        String sql = """
         SELECT amount, seats_preserved
         FROM transactions
         WHERE account_email = ?
         """;
        return select(connectionString, sql, employeeEmail);
    }

    public static int insertTransaction(String connectionString, int movieId, String totalPrice, String selectedSeats, int showroomId, String accountEmail, int showtimeId) {
        String sql = """
         INSERT INTO transactions (movie_id, amount, seats_preserved, showroom_id, account_email, showtime_id)
         VALUES (?, ?, ?, ?, ?, ?)
         """;
        try {
            NumberFormat parser = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            Number parsedNumber = parser.parse(totalPrice.replace("vnđ", "").trim());
            java.math.BigDecimal amount = new java.math.BigDecimal(parsedNumber.doubleValue());
            return update(
                    connectionString,
                    sql,
                    movieId,
                    amount,
                    selectedSeats,
                    showroomId,
                    accountEmail,
                    showtimeId
            );
        } catch (ParseException e) {
            System.err.println("Error parsing price: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static int insertTransactionReturnId(
            String connectionString,
            int movieId,
            String totalPrice,
            String selectedSeats,
            int showroomId,
            String accountEmail,
            int showtimeId) throws SQLException {

        String sql = """
     INSERT INTO transactions
       (movie_id, amount, seats_preserved, showroom_id, account_email, showtime_id)
     VALUES (?, ?, ?, ?, ?, ?)
     """;

        try (
                Connection conn = DriverManager.getConnection(connectionString);
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            NumberFormat parser = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            Number parsedNumber = parser.parse(totalPrice.replace("vnđ", "").trim());
            java.math.BigDecimal amount = new java.math.BigDecimal(parsedNumber.doubleValue());
            ps.setInt(1, movieId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, selectedSeats);
            ps.setInt(4, showroomId);
            ps.setString(5, accountEmail);
            ps.setInt(6, showtimeId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (ParseException e) {
            throw new SQLException("Error parsing price: " + e.getMessage(), e);
        }
    }
}
