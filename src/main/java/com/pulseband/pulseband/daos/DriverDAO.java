package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.DriverDTO;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    public List<DriverDTO> findAllDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();

        String query = """
                    SELECT
                      u.id AS user_id,
                      u.full_name,
                      u.phone,
                      u.email,
                      u.birth_date,
                      u.admission_date,
                      v.bpm,
                      ec.id AS emergency_contact_id,
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

                Timestamp birthTimestamp = rs.getTimestamp("birth_date");
                Timestamp admissionTimestamp = rs.getTimestamp("admission_date");

                LocalDateTime birthDate = (birthTimestamp != null) ? birthTimestamp.toLocalDateTime() : null;
                LocalDateTime admissionDate = (admissionTimestamp != null) ? admissionTimestamp.toLocalDateTime() : null;


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

                driver.setBirthDate(birthDate);
                driver.setAdmissionDate(admissionDate);
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

    public int getActiveDrivers() throws SQLException {
        String query = """
                    SELECT COUNT(DISTINCT user_id) AS active_drivers
                                        FROM vital
                                        WHERE recorded_at >= NOW() - INTERVAL '20 seconds';
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

    public boolean insertDriverBpm(int userId, int bpm) throws SQLException {
        String query = """
                    INSERT INTO vital (user_id, bpm)
                    VALUES (?, ?);
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setInt(2, bpm);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public boolean insertDriverAlert(int userId, String message) throws SQLException {
        String query = """
                    INSERT INTO alert (user_id, message)
                    VALUES (?, ?);
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, message);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public void addDriver(DriverDTO driver, EmergencyContactDTO emergencyContact) throws SQLException {
        String insertContactSql = """
                INSERT INTO emergency_contact (full_name, phone, email)
                VALUES (?, ?, ?)
            """;

        String insertDriverSql = """
                INSERT INTO "user" (
                    full_name,
                    password_hash,
                    phone,
                    email,
                    birth_date,
                    admission_date,
                    user_type_id,
                    emergency_contact_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        final int DRIVER_USER_TYPE_ID = 3;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int emergencyContactId;
            try (PreparedStatement stmt = conn.prepareStatement(insertContactSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, emergencyContact.getFullName());
                stmt.setString(2, emergencyContact.getPhone());
                stmt.setString(3, emergencyContact.getEmail());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        emergencyContactId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        throw new SQLException("Não foi possível obter o ID do contacto de emergência.");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertDriverSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, driver.getFullName());
                stmt.setString(2, driver.getPasswordHash());
                stmt.setString(3, driver.getPhone());
                stmt.setString(4, driver.getEmail());
                stmt.setObject(5, driver.getBirthDate());
                stmt.setObject(6, driver.getAdmissionDate());
                stmt.setInt(7, DRIVER_USER_TYPE_ID);
                stmt.setInt(8, emergencyContactId);

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        driver.setId(rs.getInt(1));
                    } else {
                        conn.rollback();
                        throw new SQLException("Não foi possível obter o ID do motorista.");
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            throw new SQLException("Erro ao inserir motorista e contacto de emergência: " + e.getMessage(), e);
        }
    }

    public void editDriver(DriverDTO driver, EmergencyContactDTO emergencyContact) throws SQLException {
        String updateUserWithPasswordSql = """
                    UPDATE "user" SET
                        full_name = ?,
                        email = ?,
                        phone = ?,
                        birth_date = ?,
                        admission_date = ?,
                        password_hash = ?
                    WHERE id = ?
                """;

        String updateUserWithoutPasswordSql = """
                    UPDATE "user" SET
                        full_name = ?,
                        email = ?,
                        phone = ?,
                        birth_date = ?,
                        admission_date = ?
                    WHERE id = ?
                """;

        String updateContactSql = """
                    UPDATE emergency_contact SET
                        full_name = ?,
                        phone = ?,
                        email = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement updateUserStmt = conn.prepareStatement(
                            driver.getPasswordHash() != null ? updateUserWithPasswordSql : updateUserWithoutPasswordSql);
                    PreparedStatement updateContactStmt = conn.prepareStatement(updateContactSql)
            ) {
                updateUserStmt.setString(1, driver.getFullName());
                updateUserStmt.setString(2, driver.getEmail());
                updateUserStmt.setString(3, driver.getPhone());
                updateUserStmt.setObject(4, driver.getBirthDate());
                updateUserStmt.setObject(5, driver.getAdmissionDate());

                if (driver.getPasswordHash() != null) {
                    updateUserStmt.setString(6, driver.getPasswordHash());
                    updateUserStmt.setInt(7, driver.getId());
                } else {
                    updateUserStmt.setInt(6, driver.getId());
                }

                updateUserStmt.executeUpdate();

                updateContactStmt.setString(1, emergencyContact.getFullName());
                updateContactStmt.setString(2, emergencyContact.getPhone());
                updateContactStmt.setString(3, emergencyContact.getEmail());
                updateContactStmt.setInt(4, emergencyContact.getId());

                updateContactStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void deleteDriver(int driverId) throws SQLException {
        String deleteSql = "DELETE FROM \"user\" WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, driverId);
            stmt.executeUpdate();
        }
    }
}
