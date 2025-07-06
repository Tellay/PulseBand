package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class UserDTO {
    private int id;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDateTime birthDate;
    private LocalDateTime admissionDate;
    private int userTypeId;
    private int emergencyContactId;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public UserDTO(int id, String fullName, String password, String phoneNumber, String email, LocalDateTime birthDate, LocalDateTime admissionDate, int userTypeId, int emergencyContactId, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.admissionDate = admissionDate;
        this.userTypeId = userTypeId;
        this.emergencyContactId = emergencyContactId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
