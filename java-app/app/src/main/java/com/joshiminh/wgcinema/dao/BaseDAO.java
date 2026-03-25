package com.joshiminh.wgcinema.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDAO {
    protected static ResultSet select(String connectionString, String sql, Object... params) {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.err.println("DAO Select Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    protected static int update(String connectionString, String sql, Object... params) {
        try (
                Connection connection = DriverManager.getConnection(connectionString);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("DAO Update/Delete: Rows Affected = " + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("DAO Update/Delete Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

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

    public static int deleteRowById(String connectionString, String tableName, String primaryKeyColumn, Object primaryKeyValue) {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        return update(connectionString, sql, primaryKeyValue);
    }
}
