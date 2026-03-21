package com.example.j2ee16.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingLegRequest {
    @NotNull(message = "Trip ID is required")
    @JsonProperty("trip_id")
    private Long tripId;

    @NotBlank(message = "Seat number is required")
    @JsonProperty("seat_number")
    private String seatNumber;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
