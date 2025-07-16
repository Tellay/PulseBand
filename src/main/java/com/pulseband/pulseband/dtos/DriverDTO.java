package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class DriverDTO extends UserDTO {
    private String temporaryStatus;
    private Integer lastBpm;
    private EmergencyContactDTO emergencyContactDTO;
    private List<AlertDTO> alerts;

    public DriverDTO() {
    }

    public DriverDTO(String temporaryStatus, Integer lastBpm, EmergencyContactDTO emergencyContactDTO, List<AlertDTO> alerts) {
        this.temporaryStatus = temporaryStatus;
        this.lastBpm = lastBpm;
        this.emergencyContactDTO = emergencyContactDTO;
        this.alerts = alerts;
    }

    public DriverDTO(int id, String fullName, String passwordHash, String phone, String email, LocalDateTime birthDate, LocalDateTime admissionDate, int userTypeId, int emergencyContactId, LocalDateTime createdAt, LocalDateTime updatedAt, UserTypeDTO userTypeDTO, String temporaryStatus, Integer lastBpm, EmergencyContactDTO emergencyContactDTO, List<AlertDTO> alerts) {
        super(id, fullName, passwordHash, phone, email, birthDate, admissionDate, userTypeId, emergencyContactId, createdAt, updatedAt, userTypeDTO);
        this.temporaryStatus = temporaryStatus;
        this.lastBpm = lastBpm;
        this.emergencyContactDTO = emergencyContactDTO;
        this.alerts = alerts;
    }

    public String getTemporaryStatus() {
        return temporaryStatus;
    }

    public void setTemporaryStatus(String temporaryStatus) {
        this.temporaryStatus = temporaryStatus;
    }

    public Integer getLastBpm() {
        return lastBpm;
    }

    public void setLastBpm(Integer lastBpm) {
        this.lastBpm = lastBpm;
    }

    public EmergencyContactDTO getEmergencyContactDTO() {
        return emergencyContactDTO;
    }

    public void setEmergencyContactDTO(EmergencyContactDTO emergencyContactDTO) {
        this.emergencyContactDTO = emergencyContactDTO;
    }

    public List<AlertDTO> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
    }
}
