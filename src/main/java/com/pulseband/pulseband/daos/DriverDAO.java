package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pulseband.pulseband.utils.ResultSetUtils.getLocalDateTime;

public class DriverDAO {
    public List<DriverDTO> getAllDriversBasic() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();

        String query = """
                    SELECT
                      u.id AS user_id,
                      u.full_name,
                      u.phone,
                      u.email,
                      u.birth_date,
                      u.admission_date,
                      ut.id AS user_type_id,
                      ut.name AS user_type_name,
                      ec.id AS emergency_contact_id,
                      ec.full_name AS ec_full_name,
                      ec.phone AS ec_phone,
                      ec.email AS ec_email
                    FROM "user" u
                    INNER JOIN user_type ut ON u.user_type_id = ut.id
                    LEFT JOIN emergency_contact ec ON u.emergency_contact_id = ec.id
                    WHERE ut.name = 'Driver';
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DriverDTO user = new DriverDTO();
                user.setId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setBirthDate(rs.getDate("birth_date").toLocalDate());
                Date admission = rs.getDate("admission_date");
                if (admission != null) user.setAdmissionDate(admission.toLocalDate());

                UserTypeDTO userType = new UserTypeDTO();
                userType.setId(rs.getInt("user_type_id"));
                userType.setName(rs.getString("user_type_name"));
                user.setUserType(userType);

                int ecId = rs.getInt("emergency_contact_id");
                if (!rs.wasNull()) {
                    EmergencyContactDTO ec = new EmergencyContactDTO();
                    ec.setId(ecId);
                    ec.setFullName(rs.getString("ec_full_name"));
                    ec.setPhone(rs.getString("ec_phone"));
                    ec.setEmail(rs.getString("ec_email"));
                    user.setEmergencyContact(ec);
                }

                drivers.add(user);
            }
        }
        return drivers;
    }
//
//    public void addBpmDriver(int driverId, int bpm) throws SQLException {
//        String query = "INSERT INTO vital (bpm, sensor_state_id, user_id, recorded_at) " +
//                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
//
//        try (Connection conn = new DatabaseConnection().getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, bpm);
//            stmt.setInt(2, 1);
//            stmt.setInt(3, driverId);
//
//            stmt.executeUpdate();
//        }
//    }
}