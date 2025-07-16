package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class AlertDTO {
    private int id;
    private String message;
    private int severityId;
    private int userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlertDTO() {

    }

    public AlertDTO(int id, String message, int severityId, int userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.severityId = severityId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeverityId() {
        return severityId;
    }

    public void setSeverityId(int severityId) {
        this.severityId = severityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
}
