package com.example.j2ee16.controller;

import com.example.j2ee16.dto.request.ChangePasswordRequest;
import com.example.j2ee16.dto.request.LoginRequest;
import com.example.j2ee16.dto.request.RefreshTokenRequest;
import com.example.j2ee16.dto.request.RegisterRequest;
import com.example.j2ee16.dto.response.ChangePasswordResponse;
import com.example.j2ee16.dto.response.LoginResponse;
import com.example.j2ee16.dto.response.RefreshResponse;
import com.example.j2ee16.dto.response.RegisterResponse;
import com.example.j2ee16.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(authService.changePassword(authentication.getName(), request));
    }
}
