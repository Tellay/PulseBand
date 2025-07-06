package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class SensorStateDTO {
    private int id;
    private String state;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public SensorStateDTO(int id, String state, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.state = state;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
