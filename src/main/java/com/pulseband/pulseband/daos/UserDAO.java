package com.pulseband.pulseband.daos;

import com.pulseband.pulseband.db.DatabaseConnection;
import com.pulseband.pulseband.dtos.UserDTO;
import com.pulseband.pulseband.dtos.UserTypeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public UserDTO findUserByEmail(String email) throws SQLException {
        String query = """
                    SELECT
                        u.*,
                        ut.id AS user_type_id,
                        ut.name AS user_type_name
                    FROM "user" u
                    INNER JOIN user_type ut ON u.user_type_id = ut.id
                    WHERE u.email = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userTypeId = rs.getInt("user_type_id");
                String userTypeName = rs.getString("user_type_name");

                UserTypeDTO userType = new UserTypeDTO();
                userType.setId(userTypeId);
                userType.setName(userTypeName);

                int userId = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String passwordHash = rs.getString("password_hash");

                UserDTO user = new UserDTO();
                user.setId(userId);
                user.setFullName(fullName);
                user.setPasswordHash(passwordHash);
                user.setUserTypeDTO(userType);

                return user;
            }
        }

        return null;
    }
}
