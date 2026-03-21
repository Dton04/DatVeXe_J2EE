package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class BookingResponse {
    private Long id;
    
    @JsonProperty("booking_code")
    private String bookingCode;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    
    private BookingStatus status;
    
    @JsonProperty("hold_expires_at")
    private Instant holdExpiresAt;

    public BookingResponse(Long id, String bookingCode, BigDecimal totalAmount, BookingStatus status, Instant holdExpiresAt) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.totalAmount = totalAmount;
        this.status = status;
        this.holdExpiresAt = holdExpiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Instant getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(Instant holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }
}
