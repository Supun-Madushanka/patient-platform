package com.patientplatform.api_gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@RequiredArgsConstructor
public class AuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtUtil jwtUtil;

    // These paths don't need a token
    private static final java.util.List<String> PUBLIC_PATHS = java.util.List.of(
            "/api/auth/register",
            "/api/auth/login"
    );

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next)
            throws Exception {

        String path = request.path();

        // Allow public endpoints without token
        if (isPublicPath(path)) {
            return next.handle(request);
        }

        // Check Authorization header exists
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        // Extract and validate token
        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        // Extract user info and forward as headers
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        // Add user info to request headers for downstream services
        ServerRequest mutatedRequest = ServerRequest.from(request)
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .build();

        return next.handle(mutatedRequest);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}