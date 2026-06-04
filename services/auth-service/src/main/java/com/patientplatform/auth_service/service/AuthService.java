package com.patientplatform.auth_service.service;

import com.patientplatform.auth_service.dto.AuthResponse;
import com.patientplatform.auth_service.dto.LoginRequest;
import com.patientplatform.auth_service.dto.RegisterRequest;
import com.patientplatform.auth_service.exception.CustomException;
import com.patientplatform.auth_service.model.Role;
import com.patientplatform.auth_service.model.User;
import com.patientplatform.auth_service.repository.UserRepository;
import com.patientplatform.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {

        // Check email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered");
        }

        // Validate role
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid role. Must be PATIENT, DOCTOR, or ADMIN");
        }

        // Build and save user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password"));

        // Check account is active
        if (!user.getIsActive()) {
            throw new CustomException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getRole().name(), user.getId());
    }
}
