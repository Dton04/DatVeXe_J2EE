package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String role;

    public UserProfileResponse(Long userId, String fullName, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
