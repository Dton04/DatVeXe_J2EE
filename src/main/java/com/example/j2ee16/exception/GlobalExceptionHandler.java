package com.example.j2ee16.exception;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                exception.getErrorCode(),
                exception.getHttpStatus().value(),
                exception.getMessage()
        );
        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = "Validation failed";
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            message = fieldError.getDefaultMessage();
        }

        ApiErrorResponse response = new ApiErrorResponse(
                ErrorCodeConstants.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                message
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
