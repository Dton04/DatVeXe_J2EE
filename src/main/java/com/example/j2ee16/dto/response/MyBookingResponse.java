package com.example.j2ee16.dto.response;

import com.example.j2ee16.entity.BookingStatus;
import com.example.j2ee16.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class MyBookingResponse {
    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("booking_code")
    private String bookingCode;

    @JsonProperty("route_name")
    private String routeName;

    @JsonProperty("departure_time")
    private Instant departureTime;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("seat_count")
    private Integer seatCount;

    private BookingStatus status;

    @JsonProperty("payment_status")
    private PaymentStatus paymentStatus;

    public MyBookingResponse(
            Long bookingId,
            String bookingCode,
            String routeName,
            Instant departureTime,
            BigDecimal totalAmount,
            Integer seatCount,
            BookingStatus status,
            PaymentStatus paymentStatus
    ) {
        this.bookingId = bookingId;
        this.bookingCode = bookingCode;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.totalAmount = totalAmount;
        this.seatCount = seatCount;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}

