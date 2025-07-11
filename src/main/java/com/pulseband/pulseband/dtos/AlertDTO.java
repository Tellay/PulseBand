package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlertDTO {
    private int id;
    private String message;
    private int severityId;
    private AlertSeverityDTO severity;
    private int userId;
    private LocalDateTime createdAt;
}
