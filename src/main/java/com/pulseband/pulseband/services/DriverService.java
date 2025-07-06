package com.pulseband.pulseband.services;

import com.pulseband.pulseband.daos.DriverDAO;
import com.pulseband.pulseband.dtos.DriverDTO;

import java.sql.SQLException;
import java.util.List;

public class DriverService {
    private final DriverDAO driverDAO;

    public DriverService() {
        this.driverDAO = new DriverDAO();
    }

    /**
     * Obtém todos os motoristas com contactos de emergência, sinais vitais e alertas.
     * @return Lista de DriverDTOs com dados completos.
     * @throws SQLException se houver erro de base de dados.
     */
    public List<DriverDTO> getAllDriversWithDetails() throws SQLException {
        return driverDAO.findAllDrivers();
    }
}
