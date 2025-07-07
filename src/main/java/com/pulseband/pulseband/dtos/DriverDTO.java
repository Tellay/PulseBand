package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class DriverDTO extends UserDTO {
    private EmergencyContactDTO emergencyContact;
    private List<VitalDTO> vitals;
    private List<AlertDTO> alerts;
    private String formattedBpms;
    private String formattedAlerts;

    public DriverDTO(int id, String fullName, String password, String phoneNumber, String email, LocalDateTime birthDate, LocalDateTime admissionDate, int userTypeId, int emergencyContactId, LocalDateTime updatedAt, LocalDateTime createdAt, EmergencyContactDTO emergencyContact, List<VitalDTO> vitals, List<AlertDTO> alerts) {
        super(id, fullName, password, phoneNumber, email, birthDate, admissionDate, userTypeId, emergencyContactId, updatedAt, createdAt);
        this.emergencyContact = emergencyContact;
        this.vitals = vitals;
        this.alerts = alerts;
    }

    public EmergencyContactDTO getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(EmergencyContactDTO emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public List<VitalDTO> getVitals() {
        return vitals;
    }

    public void setVitals(List<VitalDTO> vitals) {
        this.vitals = vitals;
    }

    public List<AlertDTO> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
    }

    public String getFormattedBpms() {
        return formattedBpms;
    }

    public void setFormattedBpms(String formattedBpms) {
        this.formattedBpms = formattedBpms;
    }

    public String getFormattedAlerts() {
        return formattedAlerts;
    }

    public void setFormattedAlerts(String formattedAlerts) {
        this.formattedAlerts = formattedAlerts;
    }
}
