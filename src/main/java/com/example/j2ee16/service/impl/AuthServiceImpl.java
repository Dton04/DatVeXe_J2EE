package com.example.j2ee16.service.impl;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.request.ChangePasswordRequest;
import com.example.j2ee16.dto.request.LoginRequest;
import com.example.j2ee16.dto.request.RefreshTokenRequest;
import com.example.j2ee16.dto.request.RegisterRequest;
import com.example.j2ee16.dto.response.ChangePasswordResponse;
import com.example.j2ee16.dto.response.LoginResponse;
import com.example.j2ee16.dto.response.RefreshResponse;
import com.example.j2ee16.dto.response.RegisterResponse;
import com.example.j2ee16.entity.User;
import com.example.j2ee16.entity.UserRole;
import com.example.j2ee16.exception.ApiException;
import com.example.j2ee16.repository.UserRepository;
import com.example.j2ee16.security.JwtService;
import com.example.j2ee16.security.JwtTokenType;
import com.example.j2ee16.service.AuthService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    ErrorCodeConstants.EMAIL_ALREADY_EXISTS,
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(UserRole.CUSTOMER);
        user.setLocked(false);

        User savedUser = userRepository.save(user);
        return new RegisterResponse("Register success", savedUser.getId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(
                        ErrorCodeConstants.INVALID_CREDENTIALS,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (user.isLocked()) {
            throw new ApiException(ErrorCodeConstants.USER_LOCKED, HttpStatus.FORBIDDEN, "User is locked");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(
                    ErrorCodeConstants.INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken, user.getRole().name());
    }

    @Override
    public RefreshResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        try {
            jwtService.validate(refreshToken);
            if (!jwtService.isTokenType(refreshToken, JwtTokenType.REFRESH)) {
                throw new ApiException(
                        ErrorCodeConstants.INVALID_REFRESH_TOKEN,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"
                );
            }

            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ApiException(
                            ErrorCodeConstants.INVALID_REFRESH_TOKEN,
                            HttpStatus.UNAUTHORIZED,
                            "Invalid refresh token"
                    ));

            if (user.isLocked()) {
                throw new ApiException(ErrorCodeConstants.USER_LOCKED, HttpStatus.FORBIDDEN, "User is locked");
            }

            return new RefreshResponse(jwtService.generateAccessToken(user));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ApiException(
                    ErrorCodeConstants.INVALID_REFRESH_TOKEN,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }
    }

    @Override
    public ChangePasswordResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(
                        ErrorCodeConstants.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (user.isLocked()) {
            throw new ApiException(ErrorCodeConstants.USER_LOCKED, HttpStatus.FORBIDDEN, "User is locked");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new ApiException(
                    ErrorCodeConstants.INVALID_OLD_PASSWORD,
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect"
            );
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ChangePasswordResponse("Success");
    }
}
