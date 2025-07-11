package com.pulseband.pulseband.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserDTO {
    private int id;
    private String fullName;
    private String passwordHash;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private LocalDate admissionDate;
    private int userTypeId;
    private UserTypeDTO userType;
    private Integer emergencyContactId;
    private EmergencyContactDTO emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
