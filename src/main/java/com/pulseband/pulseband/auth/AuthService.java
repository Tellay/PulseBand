package com.pulseband.pulseband.auth;

import com.pulseband.pulseband.auth.exceptions.InvalidCredentialsException;
import com.pulseband.pulseband.auth.exceptions.UnauthorizedUserTypeException;
import com.pulseband.pulseband.daos.UserDAO;
import com.pulseband.pulseband.dtos.UserDTO;
import com.pulseband.pulseband.services.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {
    private final UserService userService;

    public AuthService() {
        this.userService = new UserService();
    }

    public UserDTO login(String email, String password) throws InvalidCredentialsException, UnauthorizedUserTypeException {
        UserDTO user;
        try {
            user = userService.getUserByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user in database.", e);
        }

        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Wrong email or password.");
        }

        int userType = user.getUserTypeId();
        if (userType != 1 && userType != 2) {
            throw new UnauthorizedUserTypeException("Only administrators and RH members can enter.");
        }

        return user;
    }
}
