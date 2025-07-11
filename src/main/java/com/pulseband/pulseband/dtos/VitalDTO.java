package com.pulseband.pulseband.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VitalDTO {
    private int id;
    private int bpm;
    private Integer sensorStateId;
    private SensorStateDTO sensorState;
    private int userId;
    private Integer deviceId;
    private DeviceDTO device;
    private Timestamp recordedAt;
}
