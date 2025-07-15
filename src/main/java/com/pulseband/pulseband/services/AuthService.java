package com.pulseband.pulseband.services;

import com.pulseband.pulseband.exceptions.InvalidCredentialsException;
import com.pulseband.pulseband.exceptions.UnauthorizedUserTypeException;
import com.pulseband.pulseband.dtos.UserDTO;
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

        String userTypeName = user.getUserTypeDTO().getName();
        if (!userTypeName.equals("Administrator") && !userTypeName.equals("HR Staff")) {
            throw new UnauthorizedUserTypeException("Only administrators and RH members can enter.");
        }

        return user;
    }
}
