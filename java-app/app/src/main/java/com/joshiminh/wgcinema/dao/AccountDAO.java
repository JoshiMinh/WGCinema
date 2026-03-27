package com.joshiminh.wgcinema.dao;

import java.sql.*;
import java.time.LocalDate;

public class AccountDAO extends BaseDAO {

    public static ResultSet fetchAccountByEmail(String connectionString, String email) {
        String cols = "account_email, name, gender, date_of_birth, membership, password_hash, admin, total_tickets_sold_current_month, total_revenue_current_month, total_tickets_sold_last_month, total_revenue_last_month, last_reset_date";
        String sql = "SELECT " + cols + " FROM accounts WHERE account_email = ?";
        return select(connectionString, sql, email);
    }

    public static ResultSet fetchEmployees(String connectionString) {
        String sql = "SELECT account_email, name FROM accounts ORDER BY name";
        return select(connectionString, sql);
    }

    public static ResultSet fetchEmployeesWithSalesData(String connectionString) {
        String sql = "SELECT account_email, name, total_tickets_sold_current_month, total_revenue_current_month, total_tickets_sold_last_month, total_revenue_last_month, last_reset_date FROM accounts ORDER BY name";
        return select(connectionString, sql);
    }

    public static int updateAccountPassword(String connectionString, String email, String newHashedPassword) {
        String sql = "UPDATE accounts SET password_hash = ? WHERE account_email = ?";
        return update(connectionString, sql, newHashedPassword, email);
    }

    public static int updateAccount(String connectionString, String email, String name, String gender, java.sql.Date dateOfBirth, boolean isAdmin) {
        String sql = "UPDATE accounts SET name = ?, gender = ?, date_of_birth = ?, admin = ? WHERE account_email = ?";
        int adminValue = isAdmin ? 1 : 0;
        return update(connectionString, sql, name, gender, dateOfBirth, adminValue, email);
    }

    public static int updateAccountCurrentMonthSales(String connectionString, String email, int ticketsAdded, double revenueAdded) {
        String sql = "UPDATE accounts SET total_tickets_sold_current_month = total_tickets_sold_current_month + ?, total_revenue_current_month = total_revenue_current_month + ? WHERE account_email = ?";
        return update(connectionString, sql, ticketsAdded, revenueAdded, email);
    }

    public static void resetMonthlySales(String connectionString) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            LocalDate currentDate = LocalDate.now();
            LocalDate previousMonthDate = currentDate.minusMonths(1);

            LocalDate prevMonthStart = previousMonthDate.withDayOfMonth(1);
            LocalDate prevMonthEnd = previousMonthDate.withDayOfMonth(previousMonthDate.lengthOfMonth());

            LocalDate currentMonthStart = currentDate.withDayOfMonth(1);
            LocalDate currentMonthCalcEnd = currentDate;

            String fetchAccountsSql = "SELECT account_email, last_reset_date FROM accounts";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(fetchAccountsSql)) {

                while (rs.next()) {
                    String email = rs.getString("account_email");
                    java.sql.Date lastResetDateSql = rs.getDate("last_reset_date");
                    LocalDate lastResetDate = lastResetDateSql != null ? lastResetDateSql.toLocalDate() : null;

                    boolean needsReset = false;
                    if (lastResetDate == null || lastResetDate.getMonth() != currentDate.getMonth() || lastResetDate.getYear() != currentDate.getYear()) {
                        needsReset = true; 
                    }

                    if (needsReset) {
                        int prevMonthTickets = 0;
                        double prevMonthRevenue = 0.0;
                        String calculatePrevMonthSalesSql = """
                         SELECT COUNT(*) AS total_tickets, SUM(total_amount) AS total_revenue
                         FROM transactions
                         WHERE account_email = ?
                         AND transaction_date >= ? AND transaction_date <= ?
                         """;
                        try (PreparedStatement ps = conn.prepareStatement(calculatePrevMonthSalesSql)) {
                            ps.setString(1, email);
                            ps.setDate(2, java.sql.Date.valueOf(prevMonthStart));
                            ps.setDate(3, java.sql.Date.valueOf(prevMonthEnd));
                            try (ResultSet salesRs = ps.executeQuery()) {
                                if (salesRs.next()) {
                                    prevMonthTickets = salesRs.getInt("total_tickets");
                                    prevMonthRevenue = salesRs.getDouble("total_revenue");
                                }
                            }
                        }

                        int currentMonthTickets = 0;
                        double currentMonthRevenue = 0.0;
                        String calculateCurrentMonthSalesSql = """
                         SELECT COUNT(*) AS total_tickets, SUM(total_amount) AS total_revenue
                         FROM transactions
                         WHERE account_email = ?
                         AND transaction_date >= ? AND transaction_date <= ?
                         """;
                        try (PreparedStatement ps = conn.prepareStatement(calculateCurrentMonthSalesSql)) {
                            ps.setString(1, email);
                            ps.setDate(2, java.sql.Date.valueOf(currentMonthStart));
                            ps.setDate(3, java.sql.Date.valueOf(currentMonthCalcEnd)); 
                            try (ResultSet salesRs = ps.executeQuery()) {
                                if (salesRs.next()) {
                                    currentMonthTickets = salesRs.getInt("total_tickets");
                                    currentMonthRevenue = salesRs.getDouble("total_revenue");
                                }
                            }
                        }

                        String updateAccountSql = """
                         UPDATE accounts
                         SET total_tickets_sold_last_month = ?,
                             total_revenue_last_month = ?,
                             total_tickets_sold_current_month = ?,
                             total_revenue_current_month = ?,
                             last_reset_date = ?
                         WHERE account_email = ?
                         """;
                        try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                            ps.setInt(1, prevMonthTickets);
                            ps.setDouble(2, prevMonthRevenue);
                            ps.setInt(3, currentMonthTickets); 
                            ps.setDouble(4, currentMonthRevenue); 
                            ps.setDate(5, java.sql.Date.valueOf(currentDate));
                            ps.setString(6, email);
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Reset Monthly Sales Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}