package com.pulseband.pulseband.services;

import com.pulseband.pulseband.daos.DriverDAO;
import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DriverService {
    private final DriverDAO driverDAO;

    public DriverService() {
        this.driverDAO = new DriverDAO();
    }

    public List<DriverDTO> getAllDrivers() throws SQLException {
        return driverDAO.findAllDrivers();
    }

    public int getTotalDrivers() throws SQLException {
        return driverDAO.getTotalDrivers();
    }

    public int getActiveDrivers() throws SQLException {
        return driverDAO.getActiveDrivers();
    }

    public int getAverageBpm() throws SQLException {
        return driverDAO.getAverageBpm();
    }

    public void insertDriverBpm(int driverId, int bpm) throws SQLException {
        driverDAO.insertDriverBpm(driverId, bpm);
    }

    public void insertDriverAlert(int driverId, String alert) throws SQLException {
        driverDAO.insertDriverAlert(driverId, alert);
    }

    public void addDriver(DriverDTO driver, EmergencyContactDTO emergencyContact) throws SQLException {
        driverDAO.addDriver(driver, emergencyContact);
    }

    public void editDriver(DriverDTO driver, EmergencyContactDTO emergencyContact) throws SQLException {
        driverDAO.editDriver(driver, emergencyContact);
    }

    public void deleteDriver(int driverId) throws SQLException {
        driverDAO.deleteDriver(driverId);
    }

    public Integer getLastBpm(int driverId) throws SQLException {
        String query = "SELECT bpm FROM vital WHERE user_id = ? ORDER BY recorded_at DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("bpm");
            }
        }
        return null;
    }
}
