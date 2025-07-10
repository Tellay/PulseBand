package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.pulseband.pulseband.utils.ResultSetUtils.getLocalDateTime;

public class DriverDAO {
    public List<DriverDTO> findAllDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();

        String query = "SELECT u.*, " +
                "ec.id AS ec_id, ec.full_name AS ec_full_name, ec.phone AS ec_phone, ec.email AS ec_email, " +
                "ec.created_at AS ec_created_at, ec.updated_at AS ec_updated_at " +
                "FROM \"user\" u " +
                "LEFT JOIN emergency_contact ec ON u.emergency_contact_id = ec.id " +
                "WHERE u.user_type_id = 3";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int driverId = rs.getInt("id");

                EmergencyContactDTO emergencyContact = buildEmergencyContact(rs);
                List<VitalDTO> vitals = getVitalsForDriverById(driverId);
                List<AlertDTO> alerts = getAlertsForDriverById(driverId);

                DriverDTO driver = new DriverDTO(
                        driverId,
                        rs.getString("full_name"),
                        rs.getString("password_hash"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        getLocalDateTime(rs, "birth_date"),
                        getLocalDateTime(rs, "admission_date"),
                        rs.getInt("user_type_id"),
                        rs.getInt("emergency_contact_id"),
                        getLocalDateTime(rs, "updated_at"),
                        getLocalDateTime(rs, "created_at"),
                        emergencyContact,
                        vitals,
                        alerts
                );

                drivers.add(driver);
            }
        }

        return drivers;
    }

    private int countDrivers() throws SQLException {
        String query = "SELECT COUNT(*) FROM \"user\" WHERE user_type_id = 3";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if(rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    private EmergencyContactDTO buildEmergencyContact(ResultSet rs) throws SQLException {
        int ecId = rs.getInt("ec_id");
        if (rs.wasNull()) return null;

        return new EmergencyContactDTO(
                ecId,
                rs.getString("ec_full_name"),
                rs.getString("ec_phone"),
                rs.getString("ec_email"),
                getLocalDateTime(rs, "ec_updated_at"),
                getLocalDateTime(rs, "ec_created_at")
        );
    }

    public List<VitalDTO> getVitalsForDriverById(int driverId) throws SQLException {
        List<VitalDTO> vitals = new ArrayList<>();

        String query = "SELECT * FROM \"vital\" WHERE user_id = ? ORDER BY recorded_at DESC";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vitals.add(new VitalDTO(
                            rs.getInt("id"),
                            rs.getInt("bpm"),
                            rs.getInt("sensor_state_id"),
                            rs.getInt("user_id"),
                            getLocalDateTime(rs, "recorded_at")
                    ));
                }
            }
        }

        return vitals;
    }

    public List<AlertDTO> getAlertsForDriverById(int driverId) throws SQLException {
        List<AlertDTO> alerts = new ArrayList<>();

        String query = "SELECT a.*, s.id AS severity_id, s.name AS severity_name, s.created_at AS severity_created_at " +
                "FROM \"alert\" a " +
                "JOIN \"alert_severity\" s ON a.severity_id = s.id " +
                "WHERE a.user_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlertSeverityDTO alertSeverity = new AlertSeverityDTO(
                            rs.getInt("severity_id"),
                            rs.getString("severity_name"),
                            getLocalDateTime(rs, "severity_created_at")
                    );

                    alerts.add(new AlertDTO(
                            rs.getInt("id"),
                            rs.getString("message"),
                            rs.getInt("severity_id"),
                            rs.getInt("user_id"),
                            getLocalDateTime(rs, "created_at"),
                            alertSeverity
                    ));
                }
            }
        }

        return alerts;
    }

    public void addBpmDriver(int driverId, int bpm) throws SQLException {
        String query = "INSERT INTO vital (bpm, sensor_state_id, user_id, recorded_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bpm);
            stmt.setInt(2, 1);
            stmt.setInt(3, driverId);

            stmt.executeUpdate();
        }
    }
}