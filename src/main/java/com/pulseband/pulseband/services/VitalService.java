package com.pulseband.pulseband.services;

import com.pulseband.pulseband.daos.UserDAO;
import com.pulseband.pulseband.daos.VitalsDAO;

import java.sql.SQLException;

public class VitalService {
    private final VitalsDAO vitalsDAO;

    public VitalService() {
        this.vitalsDAO = new VitalsDAO();
    }

    public void addVitalToDriver(int driverId, int bpm) throws SQLException {
        vitalsDAO.addBpmDriver(driverId, bpm);
    }
}
