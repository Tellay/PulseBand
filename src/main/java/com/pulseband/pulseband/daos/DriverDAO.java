package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class DriverDAO {
    public List<DriverDTO> findAllDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();

        String query = """
                    SELECT
                      u.id AS user_id,
                      u.full_name,
                      u.phone,
                      u.email,
                      v.bpm AS last_bpm,
                      ec.id AS emergency_contact_id,  -- <== adiciona esta linha
                      ec.full_name AS emergency_contact_name,
                      ec.phone AS emergency_contact_phone,
                      ec.email AS emergency_contact_email
                    FROM "user" u
                    JOIN user_type ut ON u.user_type_id = ut.id
                    LEFT JOIN LATERAL (
                        SELECT bpm
                        FROM vital
                        WHERE user_id = u.id
                        ORDER BY recorded_at DESC
                        LIMIT 1
                    ) v ON true
                    LEFT JOIN emergency_contact ec ON u.emergency_contact_id = ec.id
                    WHERE ut.name = 'Driver';
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");


                int emergencyContactId = rs.getInt("emergency_contact_id");
                String emergencyContactFullName = rs.getString("emergency_contact_name");
                String emergencyContactPhone = rs.getString("emergency_contact_phone");
                String emergencyContactEmail = rs.getString("emergency_contact_email");

                EmergencyContactDTO emergencyContact = new EmergencyContactDTO();
                emergencyContact.setId(emergencyContactId);
                emergencyContact.setFullName(emergencyContactFullName);
                emergencyContact.setPhone(emergencyContactPhone);
                emergencyContact.setEmail(emergencyContactEmail);

                DriverDTO driver = new DriverDTO();
                driver.setId(id);
                driver.setFullName(fullName);
                driver.setPhone(phone);
                driver.setEmail(email);
                driver.setEmergencyContactDTO(emergencyContact);

                int bpm = rs.getInt("last_bpm");
                if (rs.wasNull()) {
                    driver.setLastBpm(null);
                } else {
                    driver.setLastBpm(bpm);
                }

                drivers.add(driver);
            }
        }

        return drivers;
    }

    public int getTotalDrivers() throws SQLException {
        String query = """
                    SELECT COUNT(*) AS total_drivers
                    FROM "user" u
                    JOIN user_type ut ON u.user_type_id = ut.id
                    WHERE ut.name = 'Driver';
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_drivers");
            }
        }

        return 0;
    }

    // ! Change the interval
    public int getActiveDrivers() throws SQLException {
        String query = """
                    SELECT COUNT(DISTINCT user_id) AS active_drivers
                    FROM vital
                    WHERE recorded_at >= NOW() - INTERVAL '24 HOURS';
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("active_drivers");
            }
        }

        return 0;
    }

    public int getAverageBpm() throws SQLException {
        String query = "SELECT ROUND(AVG(bpm)) AS avg_bpm FROM vital;";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("avg_bpm");
            }
        }

        return 0;
    }

    // ! Implement active alerts
}
