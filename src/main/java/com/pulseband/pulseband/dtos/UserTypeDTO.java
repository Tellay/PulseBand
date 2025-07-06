package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class UserTypeDTO {
    private int id;
    private String name;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public UserTypeDTO(int id, String name, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
