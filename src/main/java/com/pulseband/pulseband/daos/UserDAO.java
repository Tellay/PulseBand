package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.pulseband.pulseband.utils.ResultSetUtils.getLocalDateTime;

public class UserDAO {
    public UserDTO findByEmail(String email) throws SQLException {
        String query = "SELECT u.*, " +
                "ec.id AS ec_id, ec.full_name AS ec_full_name, ec.phone AS ec_phone, ec.email AS ec_email, " +
                "ec.created_at AS ec_created_at, ec.updated_at AS ec_updated_at " +
                "FROM \"user\" u " +
                "LEFT JOIN emergency_contact ec ON u.emergency_contact_id = ec.id " +
                "WHERE u.email = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                return new UserDTO(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("password_hash"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        getLocalDateTime(rs, "birth_date"),
                        getLocalDateTime(rs, "admission_date"),
                        rs.getInt("user_type_id"),
                        rs.getInt("emergency_contact_id"),
                        getLocalDateTime(rs, "updated_at"),
                        getLocalDateTime(rs, "created_at")
                );
            }
        }
    }
}
