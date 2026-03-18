package com.example.j2ee16.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterResponse {
    private String message;

    @JsonProperty("user_id")
    private Long userId;

    public RegisterResponse(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }
}
