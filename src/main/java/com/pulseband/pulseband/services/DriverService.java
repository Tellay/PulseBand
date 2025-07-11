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

    public List<DriverDTO> getAllDrivers() throws SQLException {
        return driverDAO.getAllDriversBasic();
    }
}
