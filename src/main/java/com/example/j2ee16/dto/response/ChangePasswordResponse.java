package com.example.j2ee16.dto.response;

public class ChangePasswordResponse {
    private String message;

    public ChangePasswordResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
