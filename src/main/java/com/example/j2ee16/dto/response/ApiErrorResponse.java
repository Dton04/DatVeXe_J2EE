package com.example.j2ee16.dto.response;

public class ApiErrorResponse {
    private String errorCode;
    private int httpStatus;
    private String message;

    public ApiErrorResponse(String errorCode, int httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
