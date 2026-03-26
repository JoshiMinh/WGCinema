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
import java.math.BigDecimal;
import com.joshiminh.wgcinema.util.PriceUtils;

public class TransactionDAO extends BaseDAO {

    public static ResultSet fetchTransactionHistory(String connectionString, String email) {
        String cols = "transaction_id, transaction_date, account_email, showtime_id, total_amount";
        String sql = "SELECT " + cols + " FROM transactions WHERE account_email = ? ORDER BY transaction_date DESC";
        return select(connectionString, sql, email);
    }

    public static ResultSet fetchEmployeeTransactions(String connectionString, String employeeEmail) {
        String sql = "SELECT total_amount FROM transactions WHERE account_email = ?";
        return select(connectionString, sql, employeeEmail);
    }

    public static int insertTransaction(String connectionString, int movieId, String totalPrice, String selectedSeats, int showroomId, String accountEmail, int showtimeId) {
        return recordTransaction(connectionString, showtimeId, accountEmail, totalPrice, selectedSeats) ? 1 : 0;
    }
 
    /**
     * Inserts a transaction and its associated tickets into the database.
     * This method ensures both transaction and tickets are created atomically.
     */
    public static boolean recordTransaction(String connectionString, int showtimeId, String accountEmail, String priceString, String selectedSeats) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            conn.setAutoCommit(false);
            try {
                // Parse Price using PriceUtils
                BigDecimal totalAmount = PriceUtils.parsePrice(priceString);
 
                // 1. Insert Transaction
                String transSql = "INSERT INTO transactions (account_email, showtime_id, total_amount) VALUES (?, ?, ?)";
                int transactionId = -1;
                try (PreparedStatement psTrans = conn.prepareStatement(transSql, Statement.RETURN_GENERATED_KEYS)) {
                    psTrans.setString(1, accountEmail);
                    psTrans.setInt(2, showtimeId);
                    psTrans.setBigDecimal(3, totalAmount);
                    psTrans.executeUpdate();
                    try (ResultSet rs = psTrans.getGeneratedKeys()) {
                        if (rs.next()) {
                            transactionId = rs.getInt(1);
                        }
                    }
                }
 
                if (transactionId == -1) throw new SQLException("Failed to create transaction.");
 
                // 2. Insert Tickets
                String ticketSql = "INSERT INTO tickets (transaction_id, seat_identifier, seat_type, price) VALUES (?, ?, ?, ?)";
                String[] seats = selectedSeats.split(", ");
                try (PreparedStatement psTicket = conn.prepareStatement(ticketSql)) {
                    for (String seat : seats) {
                        psTicket.setInt(1, transactionId);
                        psTicket.setString(2, seat);
                        
                        // Determine seat type and price via PriceUtils
                        String type = PriceUtils.getSeatType(seat);
                        
                        BigDecimal seatPrice = getSeatPriceFromShowtime(conn, showtimeId, type);
                        
                        psTicket.setString(3, type);
                        psTicket.setBigDecimal(4, seatPrice);
                        psTicket.addBatch();
                    }
                    psTicket.executeBatch();
                }
 
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Transaction Record Error: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Connection Error: " + e.getMessage());
            return false;
        }
    }
 
    private static BigDecimal getSeatPriceFromShowtime(Connection conn, int showtimeId, String type) throws SQLException {
        String sql = "SELECT regular_price, vip_price FROM showtimes WHERE showtime_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(type.equals("vip") ? "vip_price" : "regular_price");
                }
            }
        }
        return new BigDecimal(80000); // Fallback
    }
 
    public static int insertTransactionReturnId(String connectionString, int movieId, String totalPrice, String selectedSeats, int showroomId, String accountEmail, int showtimeId) throws SQLException {
        return recordTransaction(connectionString, showtimeId, accountEmail, totalPrice, selectedSeats) ? 1 : 0;
    }
}
