package com.patientplatform.patient_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String bloodGroup;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDateTime createdAt;
}
