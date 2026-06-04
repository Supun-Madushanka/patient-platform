package com.patientplatform.auth_service.controller;

import com.patientplatform.auth_service.dto.AuthResponse;
import com.patientplatform.auth_service.dto.LoginRequest;
import com.patientplatform.auth_service.dto.RegisterRequest;
import com.patientplatform.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Gateway uses this to validate tokens
    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validate() {
        return ResponseEntity.ok(Map.of("message", "Token is valid"));
    }
}
