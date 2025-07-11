package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.EmergencyContactDTO;
import com.pulseband.pulseband.dtos.UserDTO;
import com.pulseband.pulseband.dtos.UserTypeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.pulseband.pulseband.utils.ResultSetUtils.getLocalDateTime;

public class UserDAO {
    public UserDTO findByEmail(String email) throws SQLException {
        String query = """
                    SELECT u.*,
                           ec.id AS ec_id, ec.full_name AS ec_full_name, ec.phone AS ec_phone, ec.email AS ec_email,
                           ec.created_at AS ec_created_at, ec.updated_at AS ec_updated_at,
                           ut.id AS ut_id, ut.name AS ut_name, ut.created_at AS ut_created_at, ut.updated_at AS ut_updated_at
                    FROM "user" u
                    LEFT JOIN emergency_contact ec ON u.emergency_contact_id = ec.id
                    JOIN user_type ut ON u.user_type_id = ut.id
                    WHERE u.email = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                EmergencyContactDTO emergencyContact = null;
                int ecId = rs.getInt("ec_id");
                if (!rs.wasNull()) {
                    emergencyContact = new EmergencyContactDTO(
                            ecId,
                            rs.getString("ec_full_name"),
                            rs.getString("ec_phone"),
                            rs.getString("ec_email"),
                            getLocalDateTime(rs, "ec_created_at"),
                            getLocalDateTime(rs, "ec_updated_at")
                    );
                }

                UserTypeDTO userType = new UserTypeDTO(
                        rs.getInt("ut_id"),
                        rs.getString("ut_name"),
                        getLocalDateTime(rs, "ut_created_at"),
                        getLocalDateTime(rs, "ut_updated_at")
                );

                UserDTO user = new UserDTO();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setBirthDate(rs.getDate("birth_date").toLocalDate());
                user.setAdmissionDate(rs.getDate("admission_date").toLocalDate());
                user.setUserTypeId(rs.getInt("user_type_id"));
                user.setUserType(userType);
                user.setEmergencyContactId(rs.getObject("emergency_contact_id") != null ? rs.getInt("emergency_contact_id") : null);
                user.setEmergencyContact(emergencyContact);
                user.setCreatedAt(getLocalDateTime(rs, "created_at"));
                user.setUpdatedAt(getLocalDateTime(rs, "updated_at"));

                return user;
            }
        }
    }
}
