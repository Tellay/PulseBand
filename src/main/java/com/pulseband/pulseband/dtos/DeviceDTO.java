package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceDTO {
    private int id;
    private String deviceUid;
    private int userId;
    private String deviceName;
    private boolean isActive;
    private LocalDateTime registeredAt;
}
