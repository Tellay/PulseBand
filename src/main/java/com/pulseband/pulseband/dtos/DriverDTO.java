package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class DriverDTO extends UserDTO {
    private Integer lastBpm;
    private EmergencyContactDTO emergencyContactDTO;

    public DriverDTO() {
    }

    public DriverDTO(Integer lastBpm, EmergencyContactDTO emergencyContactDTO) {
        this.lastBpm = lastBpm;
        this.emergencyContactDTO = emergencyContactDTO;
    }

    public DriverDTO(int id, String fullName, String passwordHash, String phone, String email, LocalDateTime birthDate, LocalDateTime admissionDate, int userTypeId, int emergencyContactId, LocalDateTime createdAt, LocalDateTime updatedAt, UserTypeDTO userTypeDTO, Integer lastBpm, EmergencyContactDTO emergencyContactDTO) {
        super(id, fullName, passwordHash, phone, email, birthDate, admissionDate, userTypeId, emergencyContactId, createdAt, updatedAt, userTypeDTO);
        this.lastBpm = lastBpm;
        this.emergencyContactDTO = emergencyContactDTO;
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
}
