package com.pulseband.pulseband.dtos;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DriverDTO extends UserDTO {
    private List<AlertDTO> alerts;
    private List<VitalDTO> vitals;
    private List<DeviceDTO> devices;
}