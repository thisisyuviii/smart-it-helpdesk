package com.helpdesk.sys.controller;

import com.helpdesk.sys.dto.request.LoginRequest;
import com.helpdesk.sys.dto.request.RegisterRequest;
import com.helpdesk.sys.dto.response.JwtAuthResponse;
import com.helpdesk.sys.dto.response.UserSummaryResponse;
import com.helpdesk.sys.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and JWT login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username/email and password to receive JWT token")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new User (EMPLOYEE, AGENT, or ADMIN)")
    public ResponseEntity<UserSummaryResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserSummaryResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
