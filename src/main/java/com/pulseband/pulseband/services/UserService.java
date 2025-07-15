package com.pulseband.pulseband.services;

import com.pulseband.pulseband.daos.UserDAO;
import com.pulseband.pulseband.dtos.UserDTO;

import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserDTO getUserByEmail(String email) throws SQLException {
        return userDAO.findUserByEmail(email);
    }
}
