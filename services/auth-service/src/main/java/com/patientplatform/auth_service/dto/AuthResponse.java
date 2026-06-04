package com.patientplatform.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String role;
    private Long userId;

    public AuthResponse(String accessToken, String role, Long userId) {
        this.accessToken = accessToken;
        this.role = role;
        this.userId = userId;
    }
}
