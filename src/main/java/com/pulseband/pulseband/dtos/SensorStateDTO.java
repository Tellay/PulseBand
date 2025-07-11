package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorStateDTO {
    private int id;
    private String stateName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}