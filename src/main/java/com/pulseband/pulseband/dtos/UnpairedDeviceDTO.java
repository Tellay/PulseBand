package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
public class UnpairedDeviceDTO {
    private int id;
    private String deviceUid;
    private LocalDateTime firstSeenAt;
}