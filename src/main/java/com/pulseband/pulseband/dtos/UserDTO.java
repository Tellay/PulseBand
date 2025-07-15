package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class UserDTO {
    private int id;
    private String fullName;
    private String passwordHash;
    private String phone;
    private String email;
    private LocalDateTime birthDate;
    private LocalDateTime admissionDate;
    private int userTypeId;
    private int emergencyContactId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserTypeDTO userTypeDTO;

    public UserDTO() {
    }

    public UserDTO(int id, String fullName, String passwordHash, String phone, String email, LocalDateTime birthDate, LocalDateTime admissionDate, int userTypeId, int emergencyContactId, LocalDateTime createdAt, LocalDateTime updatedAt, UserTypeDTO userTypeDTO) {
        this.id = id;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        this.admissionDate = admissionDate;
        this.userTypeId = userTypeId;
        this.emergencyContactId = emergencyContactId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userTypeDTO = userTypeDTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public int getEmergencyContactId() {
        return emergencyContactId;
    }

    public void setEmergencyContactId(int emergencyContactId) {
        this.emergencyContactId = emergencyContactId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserTypeDTO getUserTypeDTO() {
        return userTypeDTO;
    }

    public void setUserTypeDTO(UserTypeDTO userTypeDTO) {
        this.userTypeDTO = userTypeDTO;
    }
}
