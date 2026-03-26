package com.joshiminh.wgcinema.dao;

import java.sql.ResultSet;

public class ShowroomDAO extends BaseDAO {

    public static ResultSet fetchShowroomDetails(String connectionString, int showroomId) {
        String cols = "showroom_id, showroom_name, max_chairs, row_count, column_count";
        String sql = "SELECT " + cols + " FROM showrooms WHERE showroom_id = ?";
        return select(connectionString, sql, showroomId);
    }

    public static ResultSet fetchAllShowrooms(String connectionString) {
        String cols = "showroom_id, showroom_name, max_chairs, row_count, column_count";
        String sql = "SELECT " + cols + " FROM showrooms";
        return select(connectionString, sql);
    }

    public static int insertShowroom(String connectionString, String[] columns, String[] values) {
        String sql = "INSERT INTO showrooms (" + String.join(", ", columns) + ") VALUES (" +
                "?,".repeat(columns.length).replaceAll(",$", "") + ")";
        return update(connectionString, sql, (Object[]) values);
    }

    public static int updateShowroomColumn(String connectionString, String columnName, Object value, Object showroomId) {
        String sql = "UPDATE showrooms SET " + columnName + " = ? WHERE showroom_id = ?";
        return update(connectionString, sql, value, showroomId);
    }
}
