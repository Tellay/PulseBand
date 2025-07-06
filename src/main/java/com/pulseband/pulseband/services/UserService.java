package com.pulseband.pulseband.services;

import com.pulseband.pulseband.daos.UserDAO;
import com.pulseband.pulseband.dtos.UserDTO;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Procura um utilizador através do e-mail.
     *
     * @param email Email do utilizador.
     * @return Objeto UserDTO correspondente, ou null se não for encontrado.
     * @throws SQLException se ocorrer erro ao aceder à base de dados.
     */
    public UserDTO getUserByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }
}
