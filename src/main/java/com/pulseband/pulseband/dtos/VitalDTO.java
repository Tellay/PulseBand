package com.pulseband.pulseband.dtos;

import java.time.LocalDateTime;

public class VitalDTO {
    private int id;
    private int bpm;
    private int sensorStateId;
    private int userId;
    private LocalDateTime recordedAt;

    public VitalDTO(int id, int bpm, int sensorStateId, int userId, LocalDateTime recordedAt) {
        this.id = id;
        this.bpm = bpm;
        this.sensorStateId = sensorStateId;
        this.userId = userId;
        this.recordedAt = recordedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getSensorStateId() {
        return sensorStateId;
    }

    public void setSensorStateId(int sensorStateId) {
        this.sensorStateId = sensorStateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
