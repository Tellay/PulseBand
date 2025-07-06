package com.pulseband.pulseband.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ResultSetUtils {
    public static LocalDateTime getLocalDateTime(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return (timestamp != null) ? timestamp.toLocalDateTime() : null;
    }
}
