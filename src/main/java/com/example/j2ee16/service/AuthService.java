package com.example.j2ee16.service;

import com.example.j2ee16.dto.request.LoginRequest;
import com.example.j2ee16.dto.request.RefreshTokenRequest;
import com.example.j2ee16.dto.request.RegisterRequest;
import com.example.j2ee16.dto.request.ChangePasswordRequest;
import com.example.j2ee16.dto.response.ChangePasswordResponse;
import com.example.j2ee16.dto.response.LoginResponse;
import com.example.j2ee16.dto.response.RefreshResponse;
import com.example.j2ee16.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshResponse refresh(RefreshTokenRequest request);

    ChangePasswordResponse changePassword(String email, ChangePasswordRequest request);
}
