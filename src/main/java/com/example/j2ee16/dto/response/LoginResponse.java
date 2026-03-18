package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String role;

    public LoginResponse(String accessToken, String refreshToken, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRole() {
        return role;
    }
}
