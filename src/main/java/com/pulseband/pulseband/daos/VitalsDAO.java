package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.VitalDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VitalsDAO {
    public void addBpmDriver(int driverId, int bpm) throws SQLException {
        String query = "INSERT INTO vital (bpm, sensor_state_id, user_id, recorded_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bpm);
            stmt.setInt(2, 1);
            stmt.setInt(3, driverId);

            stmt.executeUpdate();
        }
    }
}
