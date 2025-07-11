package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlertSeverityDTO {
    private int id;
    private String name;
    private LocalDateTime createdAt;
}